// node scripts/verify-wing-content.cjs [paired-project]
const fs = require('node:fs');
const path = require('node:path');
const assert = require('node:assert/strict');
const root = path.resolve(__dirname, '..');
const paired = process.argv[2] && path.resolve(process.argv[2]);
const resources = path.join(root, 'src/main/resources');
const assets = path.join(resources, 'assets/pm_equips');
const java = path.join(root, 'src/main/java/net/pm_equips');
const neo = fs.existsSync(path.join(resources, 'META-INF/neoforge.mods.toml'));
const read = file => fs.readFileSync(file, 'utf8');
const json = file => JSON.parse(read(file));
const ids = ['item_tcorp_clock', 'head_executioner',
    ...Array.from({length: 4}, (_, i) => `wing_tcorp_weapon_${i + 1}`),
    ...Array.from({length: 5}, (_, i) => `wing_tcorp_armor_l${i + 1}`),
    ...Array.from({length: 5}, (_, i) => `wing_rcorp_armor2_l${i + 1}`),
    'wing_rcorp_armor3', 'wing_rcorp_weapon3', 'wing_rcorp_weapon4'];
const registry = read(path.join(java, 'ItemInit.java'));
const allIds = [...registry.matchAll(/ITEMS\.register\("([^"]+)"/g)].map(match => match[1]);
assert.equal(new Set(allIds).size, allIds.length, 'Registry IDs must be unique');
assert(!allIds.includes('head_claw'), 'Obsolete Claw registration remains');
const sameAssets = new Set(['animations/armor.animation.json',
    'textures/mob_effect/effect_sin_tcorp.png', 'textures/mob_effect/effect_target.png']);
for (const id of ids) {
    assert(/^[a-z0-9_]+$/.test(id) && allIds.includes(id), id);
    const modelFile = `models/item/${id}.json`;
    sameAssets.add(modelFile);
    const model = json(path.join(assets, modelFile));
    const texture = model.textures.layer0.replace('pm_equips:', 'textures/') + '.png';
    assert(fs.existsSync(path.join(assets, texture)), texture);
    sameAssets.add(texture);
    for (const locale of ['ja_jp', 'en_us']) assert(json(path.join(assets, `lang/${locale}.json`))['item.pm_equips.' + id], id + ' localization');
}
const armor = [
    ['HeadExecutioner', 'HeadExecutioner', 'armor_head_executioner', 'head_executioner', ['head_executioner']],
    ['WingTCorpArmor1', 'TCorpArmor1', 'tcorp_1', 'tcorp_1', ['wing_tcorp_armor_l1', 'wing_tcorp_armor_l2']],
    ['WingTCorpArmor2', 'TCorpArmor2', 'tcorp_2', 'tcorp_2', ['wing_tcorp_armor_l3']],
    ['WingTCorpArmor3', 'TCorpArmor3', 'tcorp_3', 'tcorp_3', ['wing_tcorp_armor_l4', 'wing_tcorp_armor_l5']],
    ['WingRCorpArmor2', 'RCorpArmor2', 'rcorp_2', 'rcorp_2', Array.from({length: 5}, (_, i) => `wing_rcorp_armor2_l${i + 1}`)],
    ['WingRCorpArmor3', 'RCorpArmor3', 'rcorp_3', 'rcorp_3', ['wing_rcorp_armor3']]
];
for (const [item, renderer, geometry, texture, registrations] of armor) {
    const itemCode = read(path.join(java, `items/${item}.java`));
    assert(itemCode.includes('extends ArmorItem implements GeoItem') && itemCode.includes('Type.LEGGINGS'), item + ' native leg armor');
    assert(read(path.join(java, `client/renderer/${renderer}R.java`)).includes(`extends FullBodyArmorRenderer<${item}>`), item + ' full-body renderer');
    const model = json(path.join(assets, `geo/${geometry}.geo.json`))['minecraft:geometry'][0];
    const png = fs.readFileSync(path.join(assets, `textures/armor/${texture}.png`));
    assert(png.subarray(0, 8).equals(Buffer.from([137,80,78,71,13,10,26,10])), texture + ' PNG signature');
    assert.equal(png.readUInt32BE(16), model.description.texture_width, texture + ' width');
    assert.equal(png.readUInt32BE(20), model.description.texture_height, texture + ' height');
    for (const part of ['armorHead','armorBody','armorRightArm','armorLeftArm','armorRightLeg','armorLeftLeg'])
        assert(model.bones.some(bone => bone.name === part), geometry + ' missing ' + part);
    sameAssets.add(`geo/${geometry}.geo.json`);
    sameAssets.add(`textures/armor/${texture}.png`);
    sameAssets.add(`animmodels/pm_equips_armor/${geometry}.json`);
    for (const id of registrations) {
        const file = `animmodels/armor/${id}.json`;
        const epic = json(path.join(assets, file));
        assert.equal(epic.parent, `pm_equips:pm_equips_armor/${geometry}`);
        assert.equal(epic.render_properties.texture_path, `pm_equips:textures/armor/${texture}.png`);
        sameAssets.add(file);
    }
}
for (const name of ['WingTCorpWeapon3', 'WingRCorpWeapon3'])
    assert(read(path.join(java, `items/${name}.java`)).includes('extends ProjectileWeaponItem'), name + ' projectile API');
const tagDir = neo ? 'item' : 'items';
assert(json(path.join(resources, `data/pm_equips/tags/${tagDir}/wings_material.json`)).values.includes('pm_equips:item_tcorp_clock'));
const swordTag = json(path.join(resources, `data/minecraft/tags/${tagDir}/swords.json`)).values;
assert(!swordTag.includes('pm_equips:head_claw') && !swordTag.includes('pm_equips:head_executioner'), 'Armor is not a crafting sword');
for (const file of ['items/HeadClawGauntlet.java', 'client/models/HeadClawM.java', 'client/renderer/HeadClawR.java',
    'items/WingTCorpArmorBase.java', 'items/WingRCorpArmorBase.java']) assert(!fs.existsSync(path.join(java, file)), file + ' must be retired');
for (const file of ['models/item/head_claw.json', 'geo/head_claw.geo.json', 'textures/item/head_claw.png', 'animations/head_claw.animation.json'])
    assert(!fs.existsSync(path.join(assets, file)), file + ' must be retired');
if (paired) for (const file of sameAssets) {
    const other = path.join(paired, 'src/main/resources/assets/pm_equips', file);
    assert(fs.readFileSync(path.join(assets, file)).equals(fs.readFileSync(other)), 'Version asset mismatch: ' + file);
}
console.log(`${neo ? 'NeoForge' : 'Forge'}: ${ids.length} item IDs, 12 leg armors, full-body/Epic Fight resources and ${sameAssets.size} shared assets verified.`);
