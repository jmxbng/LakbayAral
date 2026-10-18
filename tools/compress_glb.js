const fs = require('fs');
const path = require('path');
const sharp = require('sharp');

const JSON_CHUNK = 0x4E4F534A;
const BIN_CHUNK = 0x004E4942;
const FLOAT = 5126;
const BYTE = 5120;
const UNSIGNED_SHORT = 5123;

const TYPE_COMPONENTS = {
    SCALAR: 1,
    VEC2: 2,
    VEC3: 3,
    VEC4: 4,
    MAT2: 4,
    MAT3: 9,
    MAT4: 16,
};

function align4(buf, padByte = 0) {
    const pad = (4 - (buf.length % 4)) % 4;
    if (pad === 0) return buf;
    const padded = Buffer.alloc(buf.length + pad, padByte);
    buf.copy(padded);
    return padded;
}

function readGlb(filePath) {
    const data = fs.readFileSync(filePath);
    const magic = data.toString('utf8', 0, 4);
    const version = data.readUInt32LE(4);
    const totalLength = data.readUInt32LE(8);
    if (magic !== 'glTF' || version !== 2) {
        throw new Error(`${filePath} is not a GLB v2 file`);
    }
    if (totalLength !== data.length) {
        throw new Error(`${filePath} invalid total length`);
    }

    let offset = 12;
    let gltf = null;
    let binChunk = Buffer.alloc(0);

    while (offset < data.length) {
        const chunkLength = data.readUInt32LE(offset);
        const chunkType = data.readUInt32LE(offset + 4);
        offset += 8;
        const chunk = data.subarray(offset, offset + chunkLength);
        offset += chunkLength;

        if (chunkType === JSON_CHUNK) {
            const jsonText = chunk.toString('utf8').replace(/\0+$/, '').trim();
            gltf = JSON.parse(jsonText);
        } else if (chunkType === BIN_CHUNK) {
            binChunk = chunk;
        }
    }

    if (!gltf) throw new Error(`${filePath} missing JSON chunk`);
    return { gltf, binChunk };
}

function writeGlb(filePath, gltf, binChunk) {
    const jsonStr = JSON.stringify(gltf);
    let jsonBuf = Buffer.from(jsonStr, 'utf8');
    jsonBuf = align4(jsonBuf, 0x20); // space padding for JSON

    const alignedBin = align4(binChunk, 0x00); // zero padding for BIN

    const totalLength = 12 + 8 + jsonBuf.length + 8 + alignedBin.length;
    const header = Buffer.alloc(12);
    header.write('glTF', 0, 4, 'ascii');
    header.writeUInt32LE(2, 4);
    header.writeUInt32LE(totalLength, 8);

    const jsonHeader = Buffer.alloc(8);
    jsonHeader.writeUInt32LE(jsonBuf.length, 0);
    jsonHeader.writeUInt32LE(JSON_CHUNK, 4);

    const binHeader = Buffer.alloc(8);
    binHeader.writeUInt32LE(alignedBin.length, 0);
    binHeader.writeUInt32LE(BIN_CHUNK, 4);

    const out = Buffer.concat([header, jsonHeader, jsonBuf, binHeader, alignedBin]);
    fs.writeFileSync(filePath, out);
}

function isImageBufferViewExclusive(gltf, bvIndex) {
    for (const acc of (gltf.accessors || [])) {
        if (acc.bufferView === bvIndex) return false;
    }
    return true;
}

function isAccessorBufferViewExclusive(gltf, accIndex) {
    const targetBv = (gltf.accessors || [])[accIndex]?.bufferView;
    if (targetBv === undefined) return false;
    for (let i = 0; i < (gltf.accessors || []).length; i++) {
        if (i !== accIndex && gltf.accessors[i].bufferView === targetBv) return false;
    }
    for (const img of (gltf.images || [])) {
        if (img.bufferView === targetBv) return false;
    }
    return true;
}

function ensureExtensionUsed(gltf, ext) {
    if (!gltf.extensionsUsed) gltf.extensionsUsed = [];
    if (!gltf.extensionsUsed.includes(ext)) gltf.extensionsUsed.push(ext);
}

// Matrix helper routines (4x4 column-major)
function identityMatrix() {
    return [
        [1, 0, 0, 0],
        [0, 1, 0, 0],
        [0, 0, 1, 0],
        [0, 0, 0, 1]
    ];
}

function multiplyMatrix(a, b) {
    const res = [
        [0, 0, 0, 0],
        [0, 0, 0, 0],
        [0, 0, 0, 0],
        [0, 0, 0, 0]
    ];
    for (let r = 0; r < 4; r++) {
        for (let c = 0; c < 4; c++) {
            let sum = 0;
            for (let k = 0; k < 4; k++) {
                sum += a[r][k] * b[k][c];
            }
            res[r][c] = sum;
        }
    }
    return res;
}

function translationMatrix(t) {
    const m = identityMatrix();
    m[0][3] = t[0];
    m[1][3] = t[1];
    m[2][3] = t[2];
    return m;
}

function scaleMatrix(s) {
    const m = identityMatrix();
    m[0][0] = s[0];
    m[1][1] = s[1];
    m[2][2] = s[2];
    return m;
}

function quaternionMatrix(q) {
    let [x, y, z, w] = q;
    const len = Math.sqrt(x * x + y * y + z * z + w * w);
    if (len <= 0) return identityMatrix();
    x /= len; y /= len; z /= len; w /= len;
    const xx = x * x, yy = y * y, zz = z * z;
    const xy = x * y, xz = x * z, yz = y * z;
    const wx = w * x, wy = w * y, wz = w * z;
    return [
        [1 - 2 * (yy + zz), 2 * (xy - wz), 2 * (xz + wy), 0],
        [2 * (xy + wz), 1 - 2 * (xx + zz), 2 * (yz - wx), 0],
        [2 * (xz - wy), 2 * (yz + wx), 1 - 2 * (xx + yy), 0],
        [0, 0, 0, 1]
    ];
}

function nodeMatrix(node) {
    if (node.matrix) {
        const raw = node.matrix;
        const m = identityMatrix();
        for (let col = 0; col < 4; col++) {
            for (let row = 0; row < 4; row++) {
                m[row][col] = raw[col * 4 + row];
            }
        }
        return m;
    }
    const t = translationMatrix(node.translation || [0, 0, 0]);
    const r = quaternionMatrix(node.rotation || [0, 0, 0, 1]);
    const s = scaleMatrix(node.scale || [1, 1, 1]);
    return multiplyMatrix(multiplyMatrix(t, r), s);
}

function writeNodeMatrix(node, m) {
    const raw = [];
    for (let col = 0; col < 4; col++) {
        for (let row = 0; row < 4; row++) {
            raw.push(m[row][col]);
        }
    }
    node.matrix = raw;
    delete node.translation;
    delete node.rotation;
    delete node.scale;
}

function appendNodeDequantization(gltf, meshIdx, mins, scales) {
    const dequant = multiplyMatrix(translationMatrix(mins), scaleMatrix(scales));
    for (const node of (gltf.nodes || [])) {
        if (node.mesh === meshIdx) {
            writeNodeMatrix(node, multiplyMatrix(nodeMatrix(node), dequant));
        }
    }
}

function iterFloatAccessor(gltf, binChunk, accIdx) {
    const acc = gltf.accessors[accIdx];
    const bv = gltf.bufferViews[acc.bufferView];
    const compCount = TYPE_COMPONENTS[acc.type];
    const elemSize = compCount * 4;
    const stride = bv.byteStride || elemSize;
    const base = (bv.byteOffset || 0) + (acc.byteOffset || 0);

    const values = [];
    for (let i = 0; i < acc.count; i++) {
        const itemOffset = base + i * stride;
        const compValues = [];
        for (let c = 0; c < compCount; c++) {
            compValues.push(binChunk.readFloatLE(itemOffset + c * 4));
        }
        values.push(compValues);
    }
    return values;
}

function quantizePositionAccessor(gltf, binChunk, accIdx) {
    const acc = gltf.accessors[accIdx];
    const values = iterFloatAccessor(gltf, binChunk, accIdx);

    let mins = acc.min ? [...acc.min] : null;
    let maxs = acc.max ? [...acc.max] : null;

    if (!mins || !maxs) {
        mins = [...values[0]];
        maxs = [...values[0]];
        for (const v of values) {
            for (let a = 0; a < 3; a++) {
                mins[a] = Math.min(mins[a], v[a]);
                maxs[a] = Math.max(maxs[a], v[a]);
            }
        }
    }

    const scales = [];
    const qmax = [];
    for (let a = 0; a < 3; a++) {
        const extent = maxs[a] - mins[a];
        if (Math.abs(extent) < 1e-12) {
            scales.push(1.0);
            qmax.push(0);
        } else {
            scales.push(extent / 65535.0);
            qmax.push(65535);
        }
    }

    const packed = Buffer.alloc(acc.count * 8);
    let pOffset = 0;
    for (const v of values) {
        for (let a = 0; a < 3; a++) {
            let q = qmax[a] === 0 ? 0 : Math.round((v[a] - mins[a]) / scales[a]);
            q = Math.max(0, Math.min(65535, q));
            packed.writeUInt16LE(q, pOffset);
            pOffset += 2;
        }
        packed.writeUInt16LE(0, pOffset); // 2-byte alignment padding
        pOffset += 2;
    }

    acc.componentType = UNSIGNED_SHORT;
    acc.min = [0, 0, 0];
    acc.max = qmax;
    delete acc.normalized;
    acc.byteOffset = 0;

    return {
        replacement: { data: packed, byteStride: 8 },
        report: { accIdx, semantic: 'POSITION', min: mins, scale: scales }
    };
}

function quantizeNormalAccessor(gltf, binChunk, accIdx) {
    const acc = gltf.accessors[accIdx];
    const values = iterFloatAccessor(gltf, binChunk, accIdx);
    const packed = Buffer.alloc(acc.count * 4);
    let pOffset = 0;

    for (const v of values) {
        for (let a = 0; a < 3; a++) {
            const clamped = Math.max(-1.0, Math.min(1.0, v[a]));
            const q = Math.max(-127, Math.min(127, Math.round(clamped * 127.0)));
            packed.writeInt8(q, pOffset);
            pOffset += 1;
        }
        packed.writeInt8(0, pOffset); // 1-byte padding for 4-byte stride
        pOffset += 1;
    }

    acc.componentType = BYTE;
    acc.normalized = true;
    acc.byteOffset = 0;
    delete acc.min;
    delete acc.max;

    return {
        replacement: { data: packed, byteStride: 4 },
        report: { accIdx, semantic: 'NORMAL' }
    };
}

function quantizeTexcoordAccessor(gltf, binChunk, accIdx) {
    const acc = gltf.accessors[accIdx];
    const values = iterFloatAccessor(gltf, binChunk, accIdx);

    let minU = Infinity, maxU = -Infinity, minV = Infinity, maxV = -Infinity;
    for (const v of values) {
        minU = Math.min(minU, v[0]);
        maxU = Math.max(maxU, v[0]);
        minV = Math.min(minV, v[1]);
        maxV = Math.max(maxV, v[1]);
    }

    if (minU < -0.0001 || maxU > 1.0001 || minV < -0.0001 || maxV > 1.0001) {
        return null; // UV outside [0, 1] cannot be normalized uint16
    }

    const packed = Buffer.alloc(acc.count * 4);
    let pOffset = 0;
    for (const v of values) {
        for (let a = 0; a < 2; a++) {
            const clamped = Math.max(0.0, Math.min(1.0, v[a]));
            const q = Math.round(clamped * 65535.0);
            packed.writeUInt16LE(Math.max(0, Math.min(65535, q)), pOffset);
            pOffset += 2;
        }
    }

    acc.componentType = UNSIGNED_SHORT;
    acc.normalized = true;
    acc.byteOffset = 0;
    delete acc.min;
    delete acc.max;

    return {
        replacement: { data: packed, byteStride: null },
        report: { accIdx, semantic: 'TEXCOORD_0' }
    };
}

function quantizeMeshAttributes(gltf, binChunk, replacements) {
    const meshAttributes = {};
    for (let meshIdx = 0; meshIdx < (gltf.meshes || []).length; meshIdx++) {
        const mesh = gltf.meshes[meshIdx];
        const attrs = {};
        for (const prim of (mesh.primitives || [])) {
            for (const [semantic, accIdx] of Object.entries(prim.attributes || {})) {
                if (attrs[semantic] !== undefined && attrs[semantic] !== accIdx) {
                    attrs[semantic] = -1; // conflicting accessor
                } else {
                    attrs[semantic] = accIdx;
                }
            }
        }
        meshAttributes[meshIdx] = attrs;
    }

    let modified = false;
    const processedAccs = new Set();
    const positionReports = {};

    for (const [meshIdxStr, attrs] of Object.entries(meshAttributes)) {
        const meshIdx = parseInt(meshIdxStr, 10);
        const posAccIdx = attrs['POSITION'];
        if (posAccIdx === undefined || posAccIdx < 0) continue;
        const acc = gltf.accessors[posAccIdx];
        const bvIdx = acc.bufferView;
        if (
            acc.componentType === FLOAT &&
            acc.type === 'VEC3' &&
            bvIdx !== undefined &&
            isAccessorBufferViewExclusive(gltf, posAccIdx)
        ) {
            const { replacement, report } = quantizePositionAccessor(gltf, binChunk, posAccIdx);
            replacements[bvIdx] = replacement;
            processedAccs.add(posAccIdx);
            positionReports[meshIdx] = report;
            modified = true;
        }
    }

    for (const [meshIdxStr, report] of Object.entries(positionReports)) {
        const meshIdx = parseInt(meshIdxStr, 10);
        appendNodeDequantization(gltf, meshIdx, report.min, report.scale);
    }

    for (const attrs of Object.values(meshAttributes)) {
        for (const semantic of ['NORMAL', 'TEXCOORD_0']) {
            const accIdx = attrs[semantic];
            if (accIdx === undefined || accIdx < 0 || processedAccs.has(accIdx)) continue;
            const acc = gltf.accessors[accIdx];
            const bvIdx = acc.bufferView;
            if (acc.componentType !== FLOAT || bvIdx === undefined || !isAccessorBufferViewExclusive(gltf, accIdx)) {
                continue;
            }

            if (semantic === 'NORMAL' && acc.type === 'VEC3') {
                const { replacement } = quantizeNormalAccessor(gltf, binChunk, accIdx);
                replacements[bvIdx] = replacement;
                processedAccs.add(accIdx);
                modified = true;
            } else if (semantic === 'TEXCOORD_0' && acc.type === 'VEC2') {
                const res = quantizeTexcoordAccessor(gltf, binChunk, accIdx);
                if (res) {
                    replacements[bvIdx] = res.replacement;
                    processedAccs.add(accIdx);
                    modified = true;
                }
            }
        }
    }

    if (modified) {
        ensureExtensionUsed(gltf, 'KHR_mesh_quantization');
    }
}

async function optimizeGlb(inputPath, outputPath, options = {}) {
    const maxSize = options.maxSize || 1024;
    const quality = options.quality || 82;
    const quantize = options.quantize !== false;

    const { gltf, binChunk } = readGlb(inputPath);
    const bufferViews = gltf.bufferViews || [];
    const replacements = {};

    // 1. Process textures
    for (const image of (gltf.images || [])) {
        const bvIdx = image.bufferView;
        if (bvIdx === undefined || !isImageBufferViewExclusive(gltf, bvIdx)) continue;
        const bv = bufferViews[bvIdx];
        const origOffset = bv.byteOffset || 0;
        const origData = binChunk.subarray(origOffset, origOffset + bv.byteLength);

        try {
            const meta = await sharp(origData).metadata();
            let pipeline = sharp(origData);

            if (Math.max(meta.width, meta.height) > maxSize) {
                pipeline = pipeline.resize({ width: maxSize, height: maxSize, fit: 'inside' });
            }

            let optimized;
            let mimeType;
            if (meta.hasAlpha) {
                optimized = await pipeline.png({ compressionLevel: 9, effort: 7 }).toBuffer();
                mimeType = 'image/png';
            } else {
                optimized = await pipeline.jpeg({ quality, mozjpeg: false }).toBuffer();
                mimeType = 'image/jpeg';
            }

            if (optimized.length < origData.length) {
                replacements[bvIdx] = { data: optimized, byteStride: null };
                image.mimeType = mimeType;
                delete image.uri;
            }
        } catch (e) {
            console.warn(`Warning: Failed to optimize image in ${inputPath}:`, e.message);
        }
    }

    // 2. Quantize geometry
    if (quantize) {
        quantizeMeshAttributes(gltf, binChunk, replacements);
    }

    // 3. Rebuild binary chunk
    const chunks = [];
    let currentOffset = 0;

    for (let i = 0; i < bufferViews.length; i++) {
        const bv = bufferViews[i];
        let chunk;
        if (replacements[i]) {
            const rep = replacements[i];
            chunk = rep.data;
            if (rep.byteStride === null) {
                delete bv.byteStride;
            } else {
                bv.byteStride = rep.byteStride;
            }
        } else {
            const s = bv.byteOffset || 0;
            chunk = binChunk.subarray(s, s + bv.byteLength);
        }

        // Align each bufferView to 4-byte boundary
        const padding = (4 - (currentOffset % 4)) % 4;
        if (padding > 0) {
            chunks.push(Buffer.alloc(padding, 0));
            currentOffset += padding;
        }

        bv.byteOffset = currentOffset;
        bv.byteLength = chunk.length;
        chunks.push(chunk);
        currentOffset += chunk.length;
    }

    // Pad end of binary chunk to 4 bytes
    const finalPad = (4 - (currentOffset % 4)) % 4;
    if (finalPad > 0) {
        chunks.push(Buffer.alloc(finalPad, 0));
        currentOffset += finalPad;
    }

    const rebuiltBin = Buffer.concat(chunks);
    if (gltf.buffers && gltf.buffers[0]) {
        gltf.buffers[0].byteLength = rebuiltBin.length;
    }

    writeGlb(outputPath, gltf, rebuiltBin);

    // Verify output file
    readGlb(outputPath);

    return {
        inputSize: fs.statSync(inputPath).size,
        outputSize: fs.statSync(outputPath).size
    };
}

module.exports = { optimizeGlb, readGlb, writeGlb };

if (require.main === module) {
    const args = process.argv.slice(2);
    if (args.length < 1) {
        console.log("Usage: node compress_glb.js <input.glb> [output.glb]");
        process.exit(1);
    }
    const input = args[0];
    const output = args[1] || input;
    const tempOut = output + '.tmp';

    optimizeGlb(input, tempOut)
        .then(res => {
            if (res.outputSize < res.inputSize) {
                fs.renameSync(tempOut, output);
                console.log(`Optimized ${path.basename(input)}: ${(res.inputSize/1048576).toFixed(2)}MB -> ${(res.outputSize/1048576).toFixed(2)}MB (-${(((res.inputSize-res.outputSize)/res.inputSize)*100).toFixed(1)}%)`);
            } else {
                fs.unlinkSync(tempOut);
                console.log(`Kept original ${path.basename(input)}: already optimal (${(res.inputSize/1048576).toFixed(2)}MB)`);
            }
        })
        .catch(err => {
            if (fs.existsSync(tempOut)) fs.unlinkSync(tempOut);
            console.error("Error optimizing GLB:", err);
            process.exit(1);
        });
}
