// node scripts/verify-neo-parity.cjs [NeoForge project]
const assert = require('node:assert/strict');
const fs = require('node:fs');
const path = require('node:path');
const root = path.resolve(__dirname, '..');
const neo = path.resolve(process.argv[2] || path.join(root, '../1.21.1-pm-equips-auto'));
const java = 'src/main/java/net/pm_equips';
const resources = 'src/main/resources';
const assets = resources + '/assets';
const read = file => fs.readFileSync(file, 'utf8');
const json = file => JSON.parse(read(file));
function walk(dir, relative = '') {
    return fs.readdirSync(path.join(dir, relative), {withFileTypes: true}).flatMap(entry =>
        entry.isDirectory() ? walk(dir, path.join(relative, entry.name)) : [path.join(relative, entry.name)]);
}
function sorted(value) {
    if (Array.isArray(value)) return value.map(sorted);
    if (value && typeof value === 'object') return Object.fromEntries(Object.keys(value).sort().map(key => [key, sorted(value[key])]));
    return value;
}
const equalJson = (a, b, label) => assert.deepEqual(sorted(a), sorted(b), label);
const registeredItems = new Set(['ItemInit.java', 'BlockInit.java'].flatMap(file =>
    [...read(path.join(root, java, file)).matchAll(/\.register\(\s*"([^"]+)"/g)].map(match => 'pm_equips:' + match[1])));
function recipeItems(value, file) {
    if (Array.isArray(value)) { for (const entry of value) recipeItems(entry, file); }
    else if (value && typeof value === 'object') for (const [key, entry] of Object.entries(value)) {
        if (['item', 'id'].includes(key) && typeof entry === 'string' && entry.startsWith('pm_equips:'))
            assert(registeredItems.has(entry), 'Unregistered recipe item: ' + entry + ' in ' + file);
        recipeItems(entry, file);
    }
}
function materials(project) {
    const source = read(path.join(project, java, 'items/materials/ArmorEquips.java'));
    const result = {};
    for (const match of source.matchAll(/^    ([A-Z][A-Z0-9_]*)\(/gm)) {
        const start = match.index + match[0].length;
        let end = start, depth = 1;
        for (; end < source.length && depth; end++) {
            if (source[end] === '(') depth++;
            if (source[end] === ')') depth--;
        }
        result[match[1]] = source.slice(start, end - 1).replace(/\s/g, '');
    }
    return result;
}
equalJson(materials(root), materials(neo), 'Armor material values and repairs');
assert(read(path.join(root, java, 'items/materials/ArmorEquips.java')).includes('case LEGGINGS -> protectionAmounts[1] + protectionAmounts[2]'),
    'Native armor defense must use the same slot mapping as NeoForge');
let itemCount = 0;
for (const name of ['ItemInit', 'MobEffectInit', 'EntityInit']) {
    const ids = project => [...read(path.join(project, java, name + '.java'))
        .matchAll(/(?:ITEMS|MOB_EFFECTS|ENTITY_TYPES)\.register\(\s*"([^"]+)"/g)].map(match => match[1]).sort();
    const expected = ids(neo), actual = ids(root);
    assert.equal(new Set(actual).size, actual.length, name + ' duplicate IDs');
    assert.deepEqual(actual, expected, name + ' registry parity');
    if (name === 'ItemInit') itemCount = actual.length;
}
const tab = project => [...read(path.join(project, java, 'tabs/PMETabs.java')).matchAll(/ItemInit\.(\w+)\.get\(\)/g)].map(match => match[1]);
assert.deepEqual(tab(root), tab(neo), 'Creative tab order');

// Raw models/textures/sounds match NeoForge. Epic Fight meshes are derived from
// the current Geo models by generate-epic-fight-armor.cjs, using Forge's renderer APIs.
let sharedAssets = 0;
for (const relative of walk(path.join(neo, assets))) {
    const portable = relative.replaceAll('\\', '/');
    if (portable.includes('/animmodels/')) continue;
    const a = path.join(neo, assets, relative), b = path.join(root, assets, relative);
    assert(fs.existsSync(b), 'Missing shared asset: ' + relative);
    if (portable.includes('/lang/')) equalJson(json(b), json(a), 'Translation text: ' + relative);
    else assert(fs.readFileSync(a).equals(fs.readFileSync(b)), 'Asset differs: ' + relative);
    sharedAssets++;
}
for (const relative of walk(path.join(root, assets)).filter(file => file.endsWith('.json'))) {
    assert(fs.statSync(path.join(root, assets, relative)).size > 0, 'Empty JSON: ' + relative);
    json(path.join(root, assets, relative));
}
for (const file of walk(path.join(root, java, 'client/models')).filter(file => file.endsWith('.java'))) {
    for (const match of read(path.join(root, java, 'client/models', file))
            .matchAll(/new ResourceLocation\("pm_equips",\s*"([^"\n]+\.(?:png|json))"\)/g))
        assert(fs.existsSync(path.join(root, assets, 'pm_equips', match[1])), 'Model resource: ' + match[1]);
}
for (const file of walk(path.join(root, assets, 'pm_equips/animmodels/armor'))) {
    const model = json(path.join(root, assets, 'pm_equips/animmodels/armor', file));
    if (model.parent?.startsWith('pm_equips:'))
        assert(fs.existsSync(path.join(root, assets, 'pm_equips/animmodels', model.parent.slice(10) + '.json')), 'Epic Fight parent: ' + file);
    assert(fs.existsSync(path.join(root, assets, 'pm_equips', model.render_properties.texture_path.slice(10))), 'Epic Fight texture: ' + file);
}

let recipes = 0;
for (const relative of walk(path.join(neo, resources, 'data/pm_equips/recipe'))) {
    const expected = json(path.join(neo, resources, 'data/pm_equips/recipe', relative));
    if (expected.result?.id) { expected.result.item = expected.result.id; delete expected.result.id; }
    const converted = JSON.parse(JSON.stringify(expected).replaceAll('minecraft:trial_key', 'minecraft:gold_ingot')
        .replaceAll('minecraft:wind_charge', 'minecraft:feather'));
    recipeItems(converted, relative);
    equalJson(json(path.join(root, resources, 'data/pm_equips/recipes', relative)), converted, 'Recipe parity: ' + relative);
    recipes++;
}
for (const file of walk(path.join(neo, resources, 'data/pm_equips/tags/item')))
    equalJson(json(path.join(root, resources, 'data/pm_equips/tags/items', file)),
        json(path.join(neo, resources, 'data/pm_equips/tags/item', file)), 'Tag parity: ' + file);
for (const relative of ['curios/slots/core_page.json', 'curios/slots/badge.json', 'curios/entities/players.json'])
    equalJson(json(path.join(root, resources, 'data/pm_equips', relative)), json(path.join(neo, resources, 'data/pm_equips', relative)), relative);

// Classes were renamed without changing the registered item IDs or losing a tier.
for (const name of ['HeadArbiter', 'WingHCorpArmor1', 'WingHCorpArmor2', 'WingHCorpArmor3',
        'WingHCorpWeapon1', 'WingHCorpWeapon2', 'WingHCorpWeapon3', 'WingKCorpArmor1', 'WingKCorpArmor2',
        'WingLCorpWeapon1', 'WingLCorpWeapon2', 'WingRCorpArmor1', 'WingRCorpWeapon1', 'WingRCorpWeapon2'])
    assert(fs.existsSync(path.join(root, java, 'items', name + '.java')), name);
console.log(`Verified ${itemCount} item IDs, creative order, ${sharedAssets} shared assets, ${recipes} recipes, tags, translations and Curios slots against current NeoForge.`);
