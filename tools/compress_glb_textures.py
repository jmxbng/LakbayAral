import argparse
import io
import json
import struct
from pathlib import Path

from PIL import Image


JSON_CHUNK = 0x4E4F534A
BIN_CHUNK = 0x004E4942


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
    gltf, bin_chunk = read_glb(input_path)
    buffer_views = gltf.get("bufferViews", [])

    replacements: dict[int, bytes] = {}
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

        replacements[buffer_view_index] = optimized
        image["mimeType"] = mime_type
        image.pop("uri", None)
        image_reports.append(
            {
                "name": image.get("name", ""),
                "dimensions": dimensions,
                "before": len(original),
                "after": len(optimized),
                "mimeType": mime_type,
            }
        )

    rebuilt = bytearray()
    for index, buffer_view in enumerate(buffer_views):
        if buffer_view.get("buffer", 0) != 0:
            continue

        if index in replacements:
            chunk = replacements[index]
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
    }


def main() -> None:
    parser = argparse.ArgumentParser(description="Compress embedded GLB textures for mobile builds.")
    parser.add_argument("input", type=Path)
    parser.add_argument("output", type=Path)
    parser.add_argument("--max-size", type=int, default=1024)
    parser.add_argument("--quality", type=int, default=82)
    args = parser.parse_args()

    report = compress_glb(args.input, args.output, args.max_size, args.quality)
    print(json.dumps(report, indent=2))


if __name__ == "__main__":
    main()
