const fs = require('node:fs');
const path = require('node:path');
const assert = require('node:assert/strict');
const root = path.resolve(__dirname, '..');
const res = path.join(root, 'src/main/resources');
const data = path.join(res, 'data/pm_equips');
const neo = fs.existsSync(path.join(res, 'META-INF/neoforge.mods.toml'));
const tableDir = neo ? 'loot_table' : 'loot_tables';
const tagDir = neo ? 'tags/item' : 'tags/items';
const recipeDir = neo ? 'recipe' : 'recipes';
const json = file => JSON.parse(fs.readFileSync(file, 'utf8'));
function walk(dir) { return fs.readdirSync(dir, {withFileTypes:true}).flatMap(e => e.isDirectory() ? walk(path.join(dir,e.name)) : [path.join(dir,e.name)]); }
const jsonFiles = walk(res).filter(file => file.endsWith('.json'));
for (const file of jsonFiles) { assert(fs.statSync(file).size > 0, 'Empty JSON: ' + file); json(file); }
const registry = fs.readFileSync(path.join(root, 'src/main/java/net/pm_equips/ItemInit.java'), 'utf8');
const ids = new Set([...registry.matchAll(/ITEMS\.register\("([^"]+)"/g)].map(m => 'pm_equips:' + m[1]));
assert(ids.has('pm_equips:item_finger2_cloth'));
assert(fs.statSync(path.join(res,'assets/pm_equips/textures/item/item_finger2_prescript_cloth.png')).size > 0);
function expandTag(name) {
    const tag = json(path.join(data, tagDir, name + '.json'));
    const result = new Set();
    for (const value of tag.values) {
        const id = typeof value === 'string' ? value : value.id;
        if (id.startsWith('#pm_equips:')) for (const item of expandTag(id.slice(11))) result.add(item);
        else { assert(ids.has(id), 'Unregistered tag item: ' + name + ' / ' + id); result.add(id); }
    }
    return result;
}
for (const name of ['ego_t1_zayin','ego_t2_teth','ego_t3_he','ego_t4_waw','ego_t5_aleph','wings_material','bullet',...(neo ? ['wing_hcorp','wing_kcorp','wing_rcorp','wing_wcorp'] : [])]) expandTag(name);
const recipes = [
    ['finger1_weapon1',5],['finger1_weapon2',6],['finger1_armor1',9],['finger1_armor2',9],['finger1_armor3',9],
    ['finger2_weapon1',2],['finger2_weapon2',3],['finger2_weapon3',3],['finger2_armor1',9],['finger2_armor2',9]
];
for (const [id, count] of recipes) {
    const recipe = json(path.join(data,recipeDir,'equipment',id+'.json'));
    assert.equal(recipe.type,'minecraft:crafting_shapeless');
    assert.equal(recipe.ingredients.length,count,id+' material count');
    assert.equal(recipe.result[neo?'id':'item'],'pm_equips:'+id);
}
const messenger = json(path.join(data,recipeDir,'equipment/finger2_weapon3.json'));
assert(messenger.ingredients.some(i => i.item === (neo?'minecraft:trial_key':'minecraft:gold_ingot')));
function names(name) {
    const pools = json(path.join(data,tableDir,'chests',name+'.json')).pools;
    const result = new Set();
    for (const entry of pools.flatMap(pool => pool.entries)) {
        assert(['minecraft:item','minecraft:tag'].includes(entry.type));
        if (entry.type === 'minecraft:tag') for (const id of expandTag(entry.name.slice(10))) result.add(id);
        else { assert(ids.has(entry.name), 'Unregistered loot: '+entry.name); result.add(entry.name); }
    }
    return result;
}
function exact(name, expected) { assert.deepEqual([...names(name)].sort(), [...expected].sort(), name+' eligibility'); }
function combine(...groups) { return new Set(groups.flatMap(group => typeof group === 'string' ? [group.startsWith('pm_equips:')?group:'pm_equips:'+group] : [...group])); }
const prefix = p => [...ids].filter(id => id.startsWith('pm_equips:'+p));
const ego = (...levels) => combine(...levels.map(level => expandTag(['','ego_t1_zayin','ego_t2_teth','ego_t3_he','ego_t4_waw','ego_t5_aleph'][level])));
const bullet = expandTag('bullet'), materials = expandTag('wings_material');
const thumb = boss => combine('finger1_weapon1','finger1_weapon2','finger1_armor1','finger1_armor2',...(boss?['finger1_armor3']:[]));
const index = proxy => combine('finger2_weapon1','finger2_armor1',...(proxy?['finger2_weapon2','finger2_weapon3','finger2_armor2']:[]));
if (neo) {
    exact('trial_chambers/reward',combine(bullet,materials,'item_enkephalin'));
    exact('trial_chambers/reward_common',combine(thumb(false),index(false),ego(1)));
    exact('trial_chambers/reward_rare',combine(expandTag('wing_hcorp'),expandTag('wing_kcorp'),ego(2)));
    exact('trial_chambers/reward_unique',combine('finger2_weapon2','finger2_weapon3','finger2_armor2','finger1_armor3',expandTag('wing_rcorp'),expandTag('wing_wcorp'),ego(3)));
    exact('trial_chambers/reward_ominous_common',ego(3));
    exact('trial_chambers/reward_ominous_rare',combine(ego(4),'finger2_weapon_sp_caduceus','finger2_armor3'));
    exact('trial_chambers/reward_ominous_unique',combine(ego(5),'head_claw','head_arbiter_armor'));
    for (const name of ['reward','reward_common','reward_rare','reward_unique','reward_ominous_common','reward_ominous_rare','reward_ominous_unique'])
        assert(!fs.existsSync(path.join(res,'data/minecraft/loot_table/chests/trial_chambers',name+'.json')), 'Vanilla override remains: '+name);
} else {
    exact('illager_outpost',combine(thumb(false),ego(2,3),bullet,prefix('wing_rcorp').filter(id=>!/_armor_l[45]$/.test(id)),'item_enkephalin'));
    exact('illager_mansion',combine(thumb(true),ego(2,3,4),bullet,prefix('wing_rcorp'),'item_enkephalin'));
    exact('village_smith',combine(ego(1,2,3),prefix('asc2_'),bullet,materials));
    exact('village_house',combine(ego(1),bullet,prefix('wing_lcorp'),'item_enkephalin'));
    for (const [name, maximumRank] of [['underground_dungeon',2],['underground_mineshaft',4],['underground_stronghold',5]]) {
        const hcorp = prefix('wing_hcorp').filter(id=>!/_armor_l[1-5]$/.test(id)||Number(id.at(-1))<=maximumRank);
        exact(name,combine(bullet,materials,name.endsWith('stronghold')?ego(3,4):ego(2,3),prefix('lccb_'),hcorp,
            index(name.endsWith('stronghold')),name.endsWith('stronghold')?prefix('lce_ego_dimension_shredder'):[]));
        for(const branch of ['mao','si','you']) for(let rank=1;rank<=5;rank++)
            assert.equal(names(name).has('pm_equips:wing_hcorp_'+branch+'_armor_l'+rank),rank<=maximumRank,name+' H armor rank');
    }
    const la = combine(prefix('lca_'),prefix('lcd_'));
    exact('nether_fortress',combine(ego(3,4),thumb(true),la,bullet));
    exact('nether_bastion',combine(ego(3,4,5),thumb(true),la,bullet));
    const regularAmmo = [...bullet].filter(id=>!id.startsWith('pm_equips:ammo_thumbs_'));
    const highIndex = combine('finger2_weapon2','finger2_weapon3','finger2_armor2','finger2_weapon_sp_caduceus','finger2_armor3');
    exact('ancient_city',combine(regularAmmo,ego(4,5),prefix('wing_kcorp'),'head_arbiter_armor',highIndex));
    exact('end_city',combine(regularAmmo,ego(4,5),prefix('wing_wcorp'),'head_claw',highIndex));
    const modifiers = json(path.join(res,'data/forge/loot_modifiers/global_loot_modifiers.json'));
    assert.equal(modifiers.replace,false);
    const equipmentModifiers = modifiers.entries.filter(id=>id.startsWith('pm_equips:equipment/'));
    assert.equal(equipmentModifiers.length,30);
    for (const id of equipmentModifiers) {
        const modifier = json(path.join(data,'loot_modifiers',id.slice(10)+'.json'));
        assert.equal(modifier.type,'pm_equips:equipment_additions');
        assert.equal(modifier.conditions.length,1);
        const table = modifier.conditions[0].loot_table_id;
        assert(table.startsWith('minecraft:chests/'));
        assert(!fs.existsSync(path.join(res,'data/minecraft/loot_tables',table.slice(10)+'.json')), 'Vanilla override remains: '+table);
        names(modifier.table.slice('pm_equips:chests/'.length));
    }
}
console.log(`${neo?'NeoForge':'Forge'}: ${jsonFiles.length} JSON files valid; ten recipes, registered loot/tag IDs, exact eligibility and rarity restrictions verified.`);
