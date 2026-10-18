import argparse
import io
import json
import math
import struct
import tempfile
from pathlib import Path

from PIL import Image


JSON_CHUNK = 0x4E4F534A
BIN_CHUNK = 0x004E4942
FLOAT = 5126
BYTE = 5120
UNSIGNED_SHORT = 5123
TYPE_COMPONENTS = {
    "SCALAR": 1,
    "VEC2": 2,
    "VEC3": 3,
    "VEC4": 4,
    "MAT2": 4,
    "MAT3": 9,
    "MAT4": 16,
}
COMPONENT_SIZES = {
    5120: 1,
    5121: 1,
    5122: 2,
    5123: 2,
    5125: 4,
    5126: 4,
}


class BufferReplacement:
    def __init__(self, data: bytes, byte_stride: int | None = None):
        self.data = data
        self.byte_stride = byte_stride


def align4(data: bytes, pad: bytes = b"\x00") -> bytes:
    padding = (-len(data)) % 4
    return data + pad * padding


def read_glb(path: Path) -> tuple[dict, bytes]:
    data = path.read_bytes()
    magic, version, total_length = struct.unpack_from("<4sII", data, 0)
    if magic != b"glTF" or version != 2:
        raise ValueError(f"{path} is not a GLB v2 file")
    if total_length != len(data):
        raise ValueError(f"{path} has an invalid GLB length")

    offset = 12
    gltf = None
    bin_chunk = b""
    while offset < len(data):
        chunk_length, chunk_type = struct.unpack_from("<II", data, offset)
        offset += 8
        chunk = data[offset : offset + chunk_length]
        offset += chunk_length

        if chunk_type == JSON_CHUNK:
            json_text = chunk.decode("utf-8").rstrip("\x00 \t\r\n")
            gltf = json.loads(json_text)
        elif chunk_type == BIN_CHUNK:
            bin_chunk = chunk

    if gltf is None:
        raise ValueError(f"{path} does not contain a JSON chunk")
    return gltf, bin_chunk


def is_image_buffer_view_exclusive(gltf: dict, buffer_view_index: int) -> bool:
    for accessor in gltf.get("accessors", []):
        if accessor.get("bufferView") == buffer_view_index:
            return False
    for image in gltf.get("images", []):
        if image.get("bufferView") == buffer_view_index:
            continue
    return True


def is_accessor_buffer_view_exclusive(gltf: dict, accessor_index: int) -> bool:
    accessor = gltf.get("accessors", [])[accessor_index]
    buffer_view_index = accessor.get("bufferView")
    if buffer_view_index is None:
        return False

    for index, other_accessor in enumerate(gltf.get("accessors", [])):
        if index != accessor_index and other_accessor.get("bufferView") == buffer_view_index:
            return False
    for image in gltf.get("images", []):
        if image.get("bufferView") == buffer_view_index:
            return False
    return True


def ensure_extension_used(gltf: dict, extension: str) -> None:
    extensions = gltf.setdefault("extensionsUsed", [])
    if extension not in extensions:
        extensions.append(extension)


def accessor_component_count(accessor: dict) -> int:
    return TYPE_COMPONENTS[accessor["type"]]


def accessor_element_size(accessor: dict) -> int:
    return COMPONENT_SIZES[accessor["componentType"]] * accessor_component_count(accessor)


def iter_float_accessor(gltf: dict, bin_chunk: bytes, accessor_index: int):
    accessor = gltf["accessors"][accessor_index]
    if accessor.get("componentType") != FLOAT:
        raise ValueError("Accessor is not float encoded")

    buffer_view = gltf["bufferViews"][accessor["bufferView"]]
    component_count = accessor_component_count(accessor)
    element_size = accessor_element_size(accessor)
    byte_stride = buffer_view.get("byteStride", element_size)
    base = buffer_view.get("byteOffset", 0) + accessor.get("byteOffset", 0)
    fmt = "<" + "f" * component_count

    for index in range(accessor["count"]):
        yield struct.unpack_from(fmt, bin_chunk, base + index * byte_stride)


def quantize_position_accessor(gltf: dict, bin_chunk: bytes, accessor_index: int) -> tuple[BufferReplacement, dict]:
    accessor = gltf["accessors"][accessor_index]
    values = iter_float_accessor(gltf, bin_chunk, accessor_index)
    mins = accessor.get("min")
    maxs = accessor.get("max")

    if mins is None or maxs is None:
        first = next(values)
        mins = list(first)
        maxs = list(first)
        for value in values:
            for axis in range(3):
                mins[axis] = min(mins[axis], value[axis])
                maxs[axis] = max(maxs[axis], value[axis])

    scales = []
    qmax = []
    for axis in range(3):
        extent = float(maxs[axis]) - float(mins[axis])
        if abs(extent) < 1e-12:
            scales.append(1.0)
            qmax.append(0)
        else:
            scales.append(extent / 65535.0)
            qmax.append(65535)

    packed = bytearray()
    for value in iter_float_accessor(gltf, bin_chunk, accessor_index):
        for axis in range(3):
            if qmax[axis] == 0:
                quantized = 0
            else:
                quantized = round((value[axis] - mins[axis]) / scales[axis])
                quantized = max(0, min(65535, quantized))
            packed.extend(struct.pack("<H", quantized))
        packed.extend(b"\x00\x00")

    accessor["componentType"] = UNSIGNED_SHORT
    accessor["min"] = [0, 0, 0]
    accessor["max"] = qmax
    accessor.pop("normalized", None)
    accessor["byteOffset"] = 0

    report = {
        "accessor": accessor_index,
        "semantic": "POSITION",
        "before": accessor["count"] * 12,
        "after": len(packed),
        "min": [float(value) for value in mins],
        "scale": scales,
    }
    return BufferReplacement(bytes(packed), byte_stride=8), report


def quantize_normal_accessor(gltf: dict, bin_chunk: bytes, accessor_index: int) -> tuple[BufferReplacement, dict]:
    accessor = gltf["accessors"][accessor_index]
    packed = bytearray()
    for value in iter_float_accessor(gltf, bin_chunk, accessor_index):
        for axis in range(3):
            quantized = round(max(-1.0, min(1.0, value[axis])) * 127.0)
            packed.extend(struct.pack("<b", max(-127, min(127, quantized))))
        packed.extend(b"\x00")

    accessor["componentType"] = BYTE
    accessor["normalized"] = True
    accessor["byteOffset"] = 0
    accessor.pop("min", None)
    accessor.pop("max", None)

    report = {
        "accessor": accessor_index,
        "semantic": "NORMAL",
        "before": accessor["count"] * 12,
        "after": len(packed),
    }
    return BufferReplacement(bytes(packed), byte_stride=4), report


def quantize_texcoord_accessor(gltf: dict, bin_chunk: bytes, accessor_index: int) -> tuple[BufferReplacement, dict] | None:
    accessor = gltf["accessors"][accessor_index]
    values = list(iter_float_accessor(gltf, bin_chunk, accessor_index))
    min_uv = [min(value[axis] for value in values) for axis in range(2)]
    max_uv = [max(value[axis] for value in values) for axis in range(2)]
    if min(min_uv) < -0.0001 or max(max_uv) > 1.0001:
        return None

    packed = bytearray()
    for value in values:
        for axis in range(2):
            quantized = round(max(0.0, min(1.0, value[axis])) * 65535.0)
            packed.extend(struct.pack("<H", max(0, min(65535, quantized))))

    accessor["componentType"] = UNSIGNED_SHORT
    accessor["normalized"] = True
    accessor["byteOffset"] = 0
    accessor.pop("min", None)
    accessor.pop("max", None)

    report = {
        "accessor": accessor_index,
        "semantic": "TEXCOORD_0",
        "before": accessor["count"] * 8,
        "after": len(packed),
    }
    return BufferReplacement(bytes(packed), byte_stride=None), report


def identity_matrix() -> list[list[float]]:
    return [
        [1.0, 0.0, 0.0, 0.0],
        [0.0, 1.0, 0.0, 0.0],
        [0.0, 0.0, 1.0, 0.0],
        [0.0, 0.0, 0.0, 1.0],
    ]


def multiply_matrix(left: list[list[float]], right: list[list[float]]) -> list[list[float]]:
    return [
        [
            sum(left[row][inner] * right[inner][column] for inner in range(4))
            for column in range(4)
        ]
        for row in range(4)
    ]


def translation_matrix(value: list[float]) -> list[list[float]]:
    matrix = identity_matrix()
    matrix[0][3], matrix[1][3], matrix[2][3] = value[0], value[1], value[2]
    return matrix


def scale_matrix(value: list[float]) -> list[list[float]]:
    matrix = identity_matrix()
    matrix[0][0], matrix[1][1], matrix[2][2] = value[0], value[1], value[2]
    return matrix


def quaternion_matrix(quaternion: list[float]) -> list[list[float]]:
    x, y, z, w = quaternion
    length = math.sqrt(x * x + y * y + z * z + w * w)
    if length <= 0:
        return identity_matrix()
    x, y, z, w = x / length, y / length, z / length, w / length
    xx, yy, zz = x * x, y * y, z * z
    xy, xz, yz = x * y, x * z, y * z
    wx, wy, wz = w * x, w * y, w * z
    return [
        [1.0 - 2.0 * (yy + zz), 2.0 * (xy - wz), 2.0 * (xz + wy), 0.0],
        [2.0 * (xy + wz), 1.0 - 2.0 * (xx + zz), 2.0 * (yz - wx), 0.0],
        [2.0 * (xz - wy), 2.0 * (yz + wx), 1.0 - 2.0 * (xx + yy), 0.0],
        [0.0, 0.0, 0.0, 1.0],
    ]


def node_matrix(node: dict) -> list[list[float]]:
    if "matrix" in node:
        raw = node["matrix"]
        return [[float(raw[column * 4 + row]) for column in range(4)] for row in range(4)]

    translation = translation_matrix(node.get("translation", [0.0, 0.0, 0.0]))
    rotation = quaternion_matrix(node.get("rotation", [0.0, 0.0, 0.0, 1.0]))
    scale = scale_matrix(node.get("scale", [1.0, 1.0, 1.0]))
    return multiply_matrix(multiply_matrix(translation, rotation), scale)


def write_node_matrix(node: dict, matrix: list[list[float]]) -> None:
    node["matrix"] = [
        float(matrix[row][column])
        for column in range(4)
        for row in range(4)
    ]
    node.pop("translation", None)
    node.pop("rotation", None)
    node.pop("scale", None)


def append_node_dequantization(gltf: dict, mesh_index: int, mins: list[float], scales: list[float]) -> None:
    dequant = multiply_matrix(translation_matrix(mins), scale_matrix(scales))
    for node in gltf.get("nodes", []):
        if node.get("mesh") != mesh_index:
            continue
        write_node_matrix(node, multiply_matrix(node_matrix(node), dequant))


def collect_mesh_attributes(gltf: dict) -> dict[int, dict[str, int]]:
    mesh_attributes: dict[int, dict[str, int]] = {}
    for mesh_index, mesh in enumerate(gltf.get("meshes", [])):
        attributes: dict[str, int] = {}
        for primitive in mesh.get("primitives", []):
            for semantic, accessor_index in primitive.get("attributes", {}).items():
                existing = attributes.get(semantic)
                if existing is not None and existing != accessor_index:
                    attributes[semantic] = -1
                else:
                    attributes[semantic] = accessor_index
        mesh_attributes[mesh_index] = attributes
    return mesh_attributes


def quantize_mesh_attributes(gltf: dict, bin_chunk: bytes, replacements: dict[int, BufferReplacement]) -> list[dict]:
    reports = []
    buffer_views = gltf.get("bufferViews", [])
    mesh_attributes = collect_mesh_attributes(gltf)
    processed_accessors: set[int] = set()
    position_dequant_by_mesh: dict[int, dict] = {}

    for mesh_index, attributes in mesh_attributes.items():
        position_accessor = attributes.get("POSITION")
        if position_accessor is None or position_accessor < 0:
            continue
        accessor = gltf["accessors"][position_accessor]
        buffer_view_index = accessor.get("bufferView")
        if (
            accessor.get("componentType") == FLOAT
            and accessor.get("type") == "VEC3"
            and buffer_view_index is not None
            and buffer_views[buffer_view_index].get("buffer", 0) == 0
            and is_accessor_buffer_view_exclusive(gltf, position_accessor)
        ):
            replacement, report = quantize_position_accessor(gltf, bin_chunk, position_accessor)
            replacements[buffer_view_index] = replacement
            reports.append(report)
            processed_accessors.add(position_accessor)
            position_dequant_by_mesh[mesh_index] = report

    for mesh_index, report in position_dequant_by_mesh.items():
        append_node_dequantization(gltf, mesh_index, report["min"], report["scale"])

    for attributes in mesh_attributes.values():
        for semantic in ("NORMAL", "TEXCOORD_0"):
            accessor_index = attributes.get(semantic)
            if accessor_index is None or accessor_index < 0 or accessor_index in processed_accessors:
                continue
            accessor = gltf["accessors"][accessor_index]
            buffer_view_index = accessor.get("bufferView")
            if (
                accessor.get("componentType") != FLOAT
                or buffer_view_index is None
                or buffer_views[buffer_view_index].get("buffer", 0) != 0
                or not is_accessor_buffer_view_exclusive(gltf, accessor_index)
            ):
                continue

            if semantic == "NORMAL" and accessor.get("type") == "VEC3":
                replacement, report = quantize_normal_accessor(gltf, bin_chunk, accessor_index)
            elif semantic == "TEXCOORD_0" and accessor.get("type") == "VEC2":
                result = quantize_texcoord_accessor(gltf, bin_chunk, accessor_index)
                if result is None:
                    continue
                replacement, report = result
            else:
                continue

            replacements[buffer_view_index] = replacement
            reports.append(report)
            processed_accessors.add(accessor_index)

    if reports:
        ensure_extension_used(gltf, "KHR_mesh_quantization")
    return reports


def compress_image(data: bytes, max_size: int, quality: int) -> tuple[bytes, str, str]:
    image = Image.open(io.BytesIO(data))
    image.load()
    original_size = image.size

    if max(image.size) > max_size:
        image.thumbnail((max_size, max_size), Image.Resampling.LANCZOS)

    if image.mode in ("RGBA", "LA") or (image.mode == "P" and "transparency" in image.info):
        output = io.BytesIO()
        image.save(output, format="PNG", optimize=True)
        mime_type = "image/png"
    else:
        output = io.BytesIO()
        image.convert("RGB").save(
            output,
            format="JPEG",
            quality=quality,
            optimize=True,
            progressive=False,
            subsampling=1,
        )
        mime_type = "image/jpeg"

    return output.getvalue(), mime_type, f"{original_size[0]}x{original_size[1]}->{image.size[0]}x{image.size[1]}"


def write_glb(path: Path, gltf: dict, bin_chunk: bytes) -> None:
    json_bytes = json.dumps(gltf, separators=(",", ":")).encode("utf-8")
    json_bytes = align4(json_bytes, b" ")
    bin_chunk = align4(bin_chunk, b"\x00")
    total_length = 12 + 8 + len(json_bytes) + 8 + len(bin_chunk)

    with path.open("wb") as out:
        out.write(struct.pack("<4sII", b"glTF", 2, total_length))
        out.write(struct.pack("<II", len(json_bytes), JSON_CHUNK))
        out.write(json_bytes)
        out.write(struct.pack("<II", len(bin_chunk), BIN_CHUNK))
        out.write(bin_chunk)


def compress_glb(input_path: Path, output_path: Path, max_size: int, quality: int) -> dict:
    return optimize_glb(input_path, output_path, max_size, quality, quantize=False)


def optimize_glb(
    input_path: Path,
    output_path: Path,
    max_size: int,
    quality: int,
    quantize: bool = False,
) -> dict:
    gltf, bin_chunk = read_glb(input_path)
    buffer_views = gltf.get("bufferViews", [])

    replacements: dict[int, BufferReplacement] = {}
    image_reports = []
    for image in gltf.get("images", []):
        buffer_view_index = image.get("bufferView")
        if buffer_view_index is None:
            continue
        if not is_image_buffer_view_exclusive(gltf, buffer_view_index):
            continue

        buffer_view = buffer_views[buffer_view_index]
        if buffer_view.get("buffer", 0) != 0:
            continue

        start = buffer_view.get("byteOffset", 0)
        end = start + buffer_view["byteLength"]
        original = bin_chunk[start:end]
        optimized, mime_type, dimensions = compress_image(original, max_size, quality)
        should_replace = len(optimized) < len(original)

        if should_replace:
            replacements[buffer_view_index] = BufferReplacement(optimized)
            image["mimeType"] = mime_type
            image.pop("uri", None)
        image_reports.append(
            {
                "name": image.get("name", ""),
                "dimensions": dimensions,
                "before": len(original),
                "after": len(optimized) if should_replace else len(original),
                "mimeType": mime_type if should_replace else image.get("mimeType", ""),
                "changed": should_replace,
            }
        )

    attribute_reports = quantize_mesh_attributes(gltf, bin_chunk, replacements) if quantize else []

    rebuilt = bytearray()
    for index, buffer_view in enumerate(buffer_views):
        if buffer_view.get("buffer", 0) != 0:
            continue

        if index in replacements:
            replacement = replacements[index]
            chunk = replacement.data
            if replacement.byte_stride is None:
                buffer_view.pop("byteStride", None)
            else:
                buffer_view["byteStride"] = replacement.byte_stride
        else:
            start = buffer_view.get("byteOffset", 0)
            chunk = bin_chunk[start : start + buffer_view["byteLength"]]

        rebuilt.extend(b"\x00" * ((-len(rebuilt)) % 4))
        buffer_view["byteOffset"] = len(rebuilt)
        buffer_view["byteLength"] = len(chunk)
        rebuilt.extend(chunk)

    rebuilt.extend(b"\x00" * ((-len(rebuilt)) % 4))
    if gltf.get("buffers"):
        gltf["buffers"][0]["byteLength"] = len(rebuilt)

    output_path.parent.mkdir(parents=True, exist_ok=True)
    write_glb(output_path, gltf, bytes(rebuilt))

    return {
        "file": input_path.name,
        "before": input_path.stat().st_size,
        "after": output_path.stat().st_size,
        "images": image_reports,
        "attributes": attribute_reports,
    }


def optimize_in_place(input_path: Path, max_size: int, quality: int, quantize: bool) -> dict:
    with tempfile.NamedTemporaryFile(suffix=".glb", delete=False) as temp_file:
        temp_path = Path(temp_file.name)
    try:
        report = optimize_glb(input_path, temp_path, max_size, quality, quantize=quantize)
        read_glb(temp_path)
        if temp_path.stat().st_size < input_path.stat().st_size:
            temp_path.replace(input_path)
            report["written"] = True
        else:
            report["after"] = input_path.stat().st_size
            report["written"] = False
        return report
    finally:
        if temp_path.exists():
            temp_path.unlink()


def main() -> None:
    parser = argparse.ArgumentParser(description="Compress embedded GLB textures for mobile builds.")
    parser.add_argument("input", type=Path)
    parser.add_argument("output", type=Path, nargs="?")
    parser.add_argument("--max-size", type=int, default=1024)
    parser.add_argument("--quality", type=int, default=82)
    parser.add_argument("--quantize", action="store_true", help="Quantize float mesh attributes with KHR_mesh_quantization.")
    parser.add_argument("--in-place", action="store_true", help="Overwrite the input only when the optimized GLB is smaller.")
    args = parser.parse_args()

    if args.in_place:
        report = optimize_in_place(args.input, args.max_size, args.quality, args.quantize)
    else:
        if args.output is None:
            parser.error("output is required unless --in-place is used")
        report = optimize_glb(args.input, args.output, args.max_size, args.quality, quantize=args.quantize)
    print(json.dumps(report, indent=2))


if __name__ == "__main__":
    main()
