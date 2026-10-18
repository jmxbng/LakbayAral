/**
 * compress_all_glb.js
 * Batch-compresses every .glb under app/src/main/assets/models/
 */
const fs   = require('fs');
const path = require('path');
const { optimizeGlb } = require('./compress_glb');

const MODELS_DIR = path.join(__dirname, '..', 'app', 'src', 'main', 'assets', 'models');
const MAX_SIZE   = 1024;
const QUALITY    = 82;

function collectGlbs(dir) {
    const results = [];
    for (const entry of fs.readdirSync(dir, { withFileTypes: true })) {
        const full = path.join(dir, entry.name);
        if (entry.isDirectory()) results.push(...collectGlbs(full));
        else if (entry.isFile() && entry.name.toLowerCase().endsWith('.glb')) results.push(full);
    }
    return results;
}

async function main() {
    const glbFiles = collectGlbs(MODELS_DIR);
    console.log('Found ' + glbFiles.length + ' GLB file(s) to process...\n');
    let totalBefore=0, totalAfter=0, optimized=0, skipped=0;
    const errors=[];
    for (const inputPath of glbFiles) {
        const tmpPath = inputPath + '.tmp';
        const rel = path.relative(MODELS_DIR, inputPath);
        try {
            const result = await optimizeGlb(inputPath, tmpPath, { maxSize: MAX_SIZE, quality: QUALITY, quantize: false });
            totalBefore += result.inputSize;
            if (result.outputSize < result.inputSize) {
                fs.renameSync(tmpPath, inputPath);
                totalAfter += result.outputSize;
                optimized++;
                const saved = ((1 - result.outputSize / result.inputSize) * 100).toFixed(1);
                console.log('OK ' + rel + '  ' + (result.inputSize/1048576).toFixed(2) + 'MB -> ' + (result.outputSize/1048576).toFixed(2) + 'MB  (-' + saved + '%)');
            } else {
                if (fs.existsSync(tmpPath)) fs.unlinkSync(tmpPath);
                totalAfter += result.inputSize;
                skipped++;
                console.log('-- ' + rel + '  already optimal (' + (result.inputSize/1048576).toFixed(2) + 'MB)');
            }
        } catch(err) {
            if (fs.existsSync(tmpPath)) fs.unlinkSync(tmpPath);
            errors.push({ file: rel, message: err.message });
            totalAfter += fs.statSync(inputPath).size;
            console.error('ERR ' + rel + '  ' + err.message);
        }
    }
    console.log('\n--- SUMMARY ---');
    console.log('Total before : ' + (totalBefore/1048576).toFixed(2) + ' MB');
    console.log('Total after  : ' + (totalAfter/1048576).toFixed(2) + ' MB');
    console.log('Saved        : ' + ((totalBefore-totalAfter)/1048576).toFixed(2) + ' MB  (-' + ((1-totalAfter/totalBefore)*100).toFixed(1) + '%)');
    console.log('Optimized    : ' + optimized + '  |  Skipped: ' + skipped + '  |  Errors: ' + errors.length);
    if (errors.length > 0) { errors.forEach(e => console.error('  ERR: ' + e.file + ': ' + e.message)); process.exit(1); }
}

main().catch(err => { console.error('Fatal:', err); process.exit(1); });
