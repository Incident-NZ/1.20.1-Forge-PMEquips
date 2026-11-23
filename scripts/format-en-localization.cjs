// Keep every English translation unchanged while following the Japanese key order and sections.
// node scripts/format-en-localization.cjs [--check]
const fs = require('node:fs');
const path = require('node:path');
const assert = require('node:assert/strict');
const directory = path.join(__dirname, '../src/main/resources/assets/pm_equips/lang');
const japaneseFile = path.join(directory, 'ja_jp.json');
const englishFile = path.join(directory, 'en_us.json');
const japaneseSource = fs.readFileSync(japaneseFile, 'utf8');
const englishSource = fs.readFileSync(englishFile, 'utf8');
const japaneseText = JSON.parse(japaneseSource.replace(/^\uFEFF/, ''));

function entries(source) {
    const parsed = JSON.parse(source.replace(/^\uFEFF/, ''));
    const result = [];
    let separate = false;
    for (const line of source.split(/\r?\n/)) {
        if (!line.trim()) separate = true;
        const match = /^\s*("(?:[^"\\]|\\.)*")\s*:\s*("(?:[^"\\]|\\.)*")\s*,?\s*$/.exec(line);
        if (!match) continue;
        result.push({key: JSON.parse(match[1]), rawKey: match[1], rawValue: match[2], separate});
        separate = false;
    }
    assert.equal(result.length, Object.keys(parsed).length, 'Expected unique, flat translation entries');
    assert.equal(new Set(result.map(entry => entry.key)).size, result.length, 'Duplicate translation key');
    return result;
}
function section(key) {
    if (!key.startsWith('item.pm_equips.')) return key.split('.')[0];
    const id = key.slice('item.pm_equips.'.length);
    if (id === 'item_lament_yisang') return 'limbus';
    if (id.startsWith('item_')) return 'materials';
    if (id.startsWith('ammo_')) return 'ammunition';
    if (id.startsWith('head_')) return 'head';
    const ego = /^lobego(\d)_(weapon|suit)_/.exec(id);
    if (ego) {
        // Some legacy IDs retain a different tier number; follow the Japanese label.
        const grade = /\b(ZAYIN|TETH|HE|WAW|ALEPH)\b/.exec(japaneseText[key] ?? '')?.[1] ?? ego[1];
        return `ego_${ego[2]}_${grade}`;
    }
    const wing = /^wing_([^_]+)/.exec(id);
    if (wing) return 'wing_' + wing[1].replace(/^rcorp\d+$/, 'rcorp');
    const finger = /^finger(\d)_/.exec(id);
    if (finger) return 'finger_' + finger[1];
    if (id.startsWith('asc')) return 'associations';
    if (id.startsWith('fixer_')) return 'fixers';
    return 'item_' + id.split('_')[0];
}
const japanese = entries(japaneseSource);
const english = entries(englishSource);
const byKey = new Map(english.map(entry => [entry.key, entry]));
const ordered = japanese.filter(entry => byKey.has(entry.key)).map(entry => ({...byKey.get(entry.key), separate: entry.separate}));
const included = new Set(ordered.map(entry => entry.key));
for (const entry of english.filter(entry => !included.has(entry.key))) {
    // Retain EN-only entries beside their category without disturbing the shared JP order.
    let after = -1;
    for (let index = 0; index < ordered.length; index++)
        if (section(ordered[index].key) === section(entry.key)) after = index;
    ordered.splice(after < 0 ? ordered.length : after + 1, 0, {...entry, separate: false});
}
const lines = ['{'];
for (let index = 0; index < ordered.length; index++) {
    const entry = ordered[index];
    if (index && (entry.separate || section(entry.key) !== section(ordered[index - 1].key))) lines.push('');
    lines.push(`  ${entry.rawKey}: ${entry.rawValue}${index === ordered.length - 1 ? '' : ','}`);
}
lines.push('}', '');
const newline = japaneseSource.includes('\r\n') ? '\r\n' : '\n';
const result = (englishSource.startsWith('\uFEFF') ? '\uFEFF' : '') + lines.join(newline);
const before = JSON.parse(englishSource.replace(/^\uFEFF/, ''));
const after = JSON.parse(result.replace(/^\uFEFF/, ''));
assert.deepEqual(after, before, 'Translation keys and text must remain unchanged');
if (process.argv.includes('--check')) assert.equal(englishSource, result, 'EN ordering/formatting needs updating');
else fs.writeFileSync(englishFile, result);
assert.equal(fs.readFileSync(japaneseFile, 'utf8'), japaneseSource, 'Japanese file must not change');
console.log(`${process.argv.includes('--check') ? 'Verified' : 'Formatted'} ${ordered.length} EN entries; translation text and Japanese file unchanged.`);
