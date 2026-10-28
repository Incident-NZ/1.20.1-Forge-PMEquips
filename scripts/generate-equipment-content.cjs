const fs = require('node:fs');
const path = require('node:path');
const root = path.resolve(__dirname, '..');
const neo = fs.existsSync(path.join(root, 'src/main/resources/META-INF/neoforge.mods.toml'));
const resources = path.join(root, 'src/main/resources');
const data = path.join(resources, 'data/pm_equips');
const java = path.join(root, 'src/main/java/net/pm_equips');
const tableDir = neo ? 'loot_table' : 'loot_tables';
const tagDir = neo ? 'tags/item' : 'tags/items';
const recipeDir = neo ? 'recipe' : 'recipes';
const readJSON = file => JSON.parse(fs.readFileSync(file, 'utf8'));
function writeJSON(file, value) {
    fs.mkdirSync(path.dirname(file), {recursive: true});
    fs.writeFileSync(file, JSON.stringify(value, null, 2) + '\n');
}
const registry = fs.readFileSync(path.join(java, 'ItemInit.java'), 'utf8');
const ids = [...registry.matchAll(/ITEMS\.register\("([a-z0-9_]+)"/g)].map(match => match[1]);
const item = name => ({item: name.includes(':') ? name : 'pm_equips:' + name});
const tag = name => ({tag: name.includes(':') ? name : 'pm_equips:' + name});
const repeat = (value, count) => Array.from({length: count}, () => value);
const cloth = item('item_finger2_cloth');
const wood = tag('minecraft:planks'), iron = item('minecraft:iron_ingot');
const gold = item('minecraft:gold_ingot'), leather = item('minecraft:leather');
const recipes = {
    finger1_weapon1: [...repeat(wood, 3), ...repeat(iron, 2)],
    finger1_weapon2: [...repeat(wood, 3), ...repeat(iron, 2), item('minecraft:iron_sword')],
    finger1_armor1: [...repeat(leather, 6), ...repeat(gold, 2), item('ammo_large')],
    finger1_armor2: [item('finger1_armor1'), ...repeat(gold, 8)],
    finger1_armor3: [item('finger1_armor2'), ...repeat(gold, 8)],
    finger2_weapon1: [tag('minecraft:swords'), cloth],
    finger2_weapon2: [tag('minecraft:swords'), item('minecraft:chain'), cloth],
    finger2_weapon3: [tag('minecraft:swords'), item(neo ? 'minecraft:trial_key' : 'minecraft:gold_ingot'), cloth],
    finger2_armor1: [...repeat(cloth, 4), ...repeat(leather, 2), ...repeat(gold, 3)],
    finger2_armor2: [item('finger2_armor1'), ...repeat(cloth, 6), leather, gold]
};
for (const [id, ingredients] of Object.entries(recipes)) {
    if (ingredients.length > 9) throw Error('Too many crafting slots: ' + id);
    writeJSON(path.join(data, recipeDir, 'equipment', id + '.json'), {
        type: 'minecraft:crafting_shapeless', category: 'equipment', ingredients,
        result: neo ? {id: 'pm_equips:' + id, count: 1} : {item: 'pm_equips:' + id, count: 1}
    });
}
writeJSON(path.join(resources, 'assets/pm_equips/models/item/item_finger2_cloth.json'), {
    parent: 'minecraft:item/generated', textures: {layer0: 'pm_equips:item/item_finger2_prescript_cloth'}
});
for (const locale of ['ja_jp', 'en_us']) {
    const file = path.join(resources, 'assets/pm_equips/lang', locale + '.json');
    const lang = readJSON(file);
    lang['item.pm_equips.item_finger2_cloth'] = locale === 'ja_jp' ? '人差し指 - 指令の布' : 'The Index -  Presctipt Cloth';
    lang['tooltip.pm_equips.claw_controls'] = locale === 'ja_jp'
        ? 'Shift+V: 血清を切り替え / 右クリック: 使用（共通CT 60秒）'
        : 'Shift+V: cycle serum / Right-click: use (shared cooldown: 60 seconds)';
    lang['tooltip.pm_equips.claw_serum'] = locale === 'ja_jp' ? '選択中: 血清%s' : 'Selected: Serum %s';
    lang['tooltip.pm_equips.claw_pending'] = locale === 'ja_jp' ? '次の一撃: 攻撃力%s（特異点の補正込み）' : 'Next strike: %s attack damage (including Singularity modifiers)';
    writeJSON(file, lang);
}
// Bring all registered ammunition into the shared bullet tag, including shotgun shells.
writeJSON(path.join(data, tagDir, 'bullet.json'), {replace: false, values: ids.filter(id => id.startsWith('ammo_')).map(id => 'pm_equips:' + id)});
if (neo) {
    for (const wing of ['wcorp', 'rcorp', 'kcorp', 'hcorp']) {
        const values = ids.filter(id => id.startsWith('wing_' + wing));
        if (!values.length) throw Error('Empty wing: ' + wing);
        writeJSON(path.join(data, tagDir, 'wing_' + wing + '.json'), {replace: false, values: values.map(id => 'pm_equips:' + id)});
    }
}
// Extend vanilla's sword tag with this mod's actual SwordItem subclasses.
const classParents = new Map();
for (const file of fs.readdirSync(path.join(java, 'items')).filter(file => file.endsWith('.java'))) {
    const text = fs.readFileSync(path.join(java, 'items', file), 'utf8');
    const match = /class\s+(\w+)\s+extends\s+(?:net\.minecraft\.world\.item\.)?(\w+)/.exec(text);
    if (match) classParents.set(match[1], match[2]);
}
function swordClass(name, seen = new Set()) {
    if (name === 'SwordItem') return true;
    if (seen.has(name)) return false;
    seen.add(name);
    return classParents.has(name) && swordClass(classParents.get(name), seen);
}
const swords = [];
for (const match of registry.matchAll(/ITEMS\.register\("([a-z0-9_]+)",\s*(?:\(\)\s*->\s*new\s+)?(\w+)(?:::new|\()/g)) {
    if (swordClass(match[2])) swords.push('pm_equips:' + match[1]);
}
const swordTagFile = path.join(resources, neo ? 'data/minecraft/tags/item/swords.json' : 'data/minecraft/tags/items/swords.json');
const previousSwords = fs.existsSync(swordTagFile) ? readJSON(swordTagFile).values : [];
writeJSON(swordTagFile, {replace: false, values: [...new Set([...previousSwords, ...swords])]});
const entryItem = id => ({type: 'minecraft:item', name: id.includes(':') ? id : 'pm_equips:' + id, weight: 1});
const entryTag = id => ({type: 'minecraft:tag', name: 'pm_equips:' + id, expand: true, weight: 1});
const entries = (...groups) => groups.flat();
const equipment = list => list.map(entryItem);
const allAmmo = ids.filter(id => id.startsWith('ammo_'));
const normalAmmo = allAmmo.filter(id => !id.startsWith('ammo_thumbs_'));
const ammoEntries = list => list.map(id => ({...entryItem(id), weight: 3,
    functions: [{function: 'minecraft:set_count', count: {type: 'minecraft:uniform', min: 4, max: 16}}]}));
const tiers = (...numbers) => numbers.map(number => entryTag(['', 'ego_t1_zayin', 'ego_t2_teth', 'ego_t3_he', 'ego_t4_waw', 'ego_t5_aleph'][number]));
const thumb = upper => equipment(['finger1_weapon1', 'finger1_weapon2', 'finger1_armor1', 'finger1_armor2', ...(upper ? ['finger1_armor3'] : [])]);
const index = upper => equipment(['finger2_weapon1', 'finger2_armor1', ...(upper ? ['finger2_weapon2', 'finger2_weapon3', 'finger2_armor2'] : [])]);
const wing = prefix => ids.filter(id => id.startsWith('wing_' + prefix));
const additions = {};
const bindings = {};
function group(name, tables, content) { additions[name] = content; for (const table of tables) bindings[table] = name; }
if (neo) {
    const trial = 'chests/trial_chambers/';
    group('trial_chambers/reward', [trial + 'reward'], entries([entryTag('bullet'), entryTag('wings_material'), entryItem('item_enkephalin')]));
    group('trial_chambers/reward_common', [trial + 'reward_common'], entries(equipment(['finger1_weapon1','finger1_armor1','finger1_weapon2','finger1_armor2','finger2_weapon1','finger2_armor1']), tiers(1)));
    group('trial_chambers/reward_rare', [trial + 'reward_rare'], entries([entryTag('wing_hcorp'),entryTag('wing_kcorp')], tiers(2)));
    group('trial_chambers/reward_unique', [trial + 'reward_unique'], entries(equipment(['finger2_weapon2','finger2_weapon3','finger2_armor2','finger1_armor3']), [entryTag('wing_rcorp'),entryTag('wing_wcorp')], tiers(3)));
    group('trial_chambers/reward_ominous_common', [trial + 'reward_ominous_common'], tiers(3));
    group('trial_chambers/reward_ominous_rare', [trial + 'reward_ominous_rare'], entries(tiers(4), equipment(['finger2_weapon_sp_caduceus','finger2_armor3'])));
    group('trial_chambers/reward_ominous_unique', [trial + 'reward_ominous_unique'], entries(tiers(5), equipment(['head_claw','head_arbiter_armor'])));
} else {
    const enkephalin = entryItem('item_enkephalin');
    const materials = entryTag('wings_material');
    group('illager_outpost', ['chests/pillager_outpost'], entries(thumb(false), tiers(2,3), ammoEntries(allAmmo), equipment(wing('rcorp').filter(id => !/armor_l[45]$/.test(id))), [enkephalin]));
    group('illager_mansion', ['chests/woodland_mansion'], entries(thumb(true), tiers(2,3,4), ammoEntries(allAmmo), equipment(wing('rcorp')), [enkephalin]));
    group('village_smith', ['chests/village/village_armorer','chests/village/village_toolsmith','chests/village/village_weaponsmith'], entries(tiers(1,2,3), equipment(ids.filter(id => id.startsWith('asc2_'))), ammoEntries(allAmmo), [materials]));
    const houses = ['butcher','cartographer','desert_house','fisher','fletcher','mason','plains_house','savanna_house','shepherd','snowy_house','tannery','taiga_house','temple'];
    group('village_house', houses.map(id => 'chests/village/village_' + id), entries(tiers(1), ammoEntries(allAmmo), equipment(wing('lcorp')), [enkephalin]));
    const lccb = equipment(ids.filter(id => id.startsWith('lccb_')));
    const hcorp = maximumRank => equipment(wing('hcorp').filter(id => !/armor_l[1-5]$/.test(id) || Number(id.at(-1)) <= maximumRank));
    group('underground_dungeon', ['chests/simple_dungeon'], entries(ammoEntries(allAmmo), [materials], tiers(2,3), lccb, hcorp(2), index(false)));
    group('underground_mineshaft', ['chests/abandoned_mineshaft'], entries(ammoEntries(allAmmo), [materials], tiers(2,3), lccb, hcorp(4), index(false)));
    group('underground_stronghold', ['chests/stronghold_corridor','chests/stronghold_crossing','chests/stronghold_library'], entries(ammoEntries(allAmmo), [materials], tiers(3,4), lccb, hcorp(5), equipment(ids.filter(id => id.startsWith('lce_ego_dimension_shredder'))), index(true)));
    const lcaLCD = equipment(ids.filter(id => id.startsWith('lca_') || id.startsWith('lcd_')));
    group('nether_fortress', ['chests/nether_bridge'], entries(tiers(3,4), thumb(true), lcaLCD, ammoEntries(allAmmo)));
    group('nether_bastion', ['chests/bastion_bridge','chests/bastion_hoglin_stable','chests/bastion_other','chests/bastion_treasure'], entries(tiers(3,4,5), thumb(true), lcaLCD, ammoEntries(allAmmo)));
    const highIndex = equipment(['finger2_weapon2','finger2_weapon3','finger2_armor2','finger2_weapon_sp_caduceus','finger2_armor3']);
    group('ancient_city', ['chests/ancient_city'], entries(ammoEntries(normalAmmo), tiers(4,5), equipment(wing('kcorp')), [entryItem('head_arbiter_armor')], highIndex));
    group('end_city', ['chests/end_city_treasure'], entries(ammoEntries(normalAmmo), tiers(4,5), equipment(wing('wcorp')), [entryItem('head_claw')], highIndex));
}
for (const [name, content] of Object.entries(additions)) {
    for (const entry of content) if (entry.type === 'minecraft:item' && entry.name.startsWith('pm_equips:') && !ids.includes(entry.name.slice(10))) throw Error('Unknown item: ' + entry.name);
    writeJSON(path.join(data, tableDir, 'chests', name + '.json'), {
        type: 'minecraft:chest', pools: [{name: 'pm_equips_equipment', rolls: neo ? 1 : {type: 'minecraft:uniform', min: 1, max: 3}, bonus_rolls: 0, entries: content}]
    });
}
if (!neo) {
    const modifierIDs = [];
    for (const [table, name] of Object.entries(bindings)) {
        const id = 'equipment/' + table.replace(/^chests\//, '');
        modifierIDs.push('pm_equips:' + id);
        writeJSON(path.join(data, 'loot_modifiers', id + '.json'), {
            type: 'pm_equips:equipment_additions', table: 'pm_equips:chests/' + name,
            conditions: [{condition: 'forge:loot_table_id', loot_table_id: 'minecraft:' + table}]
        });
    }
    const file = path.join(resources, 'data/forge/loot_modifiers/global_loot_modifiers.json');
    const previous = fs.existsSync(file) ? readJSON(file).entries : [];
    writeJSON(file, {replace: false, entries: [...new Set([...previous, ...modifierIDs])]});
}
// Remove only the superseded override files, after saving their exact contents locally.
for (const table of Object.keys(bindings)) {
    const file = path.resolve(resources, 'data/minecraft', tableDir, table + '.json');
    if (!file.startsWith(resources + path.sep)) throw Error('Invalid removal path');
    if (!fs.existsSync(file)) continue;
    const backup = path.join(root, 'build/equipment-loot-originals', table + '.json');
    fs.mkdirSync(path.dirname(backup), {recursive: true});
    if (!fs.existsSync(backup)) fs.copyFileSync(file, backup);
    fs.unlinkSync(file);
}
writeJSON(path.join(root, 'build/equipment-content-manifest.json'), {neo, recipes, bindings, additions});
console.log(`Generated ${Object.keys(recipes).length} recipes and ${Object.keys(additions).length} addition tables for ${Object.keys(bindings).length} vanilla tables (${neo ? 'NeoForge' : 'Forge'}).`);
