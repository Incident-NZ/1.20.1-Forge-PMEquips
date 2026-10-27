// Run with Node.js from the project root. No packages or network are required.
const fs = require('node:fs');
const path = require('node:path');
const assert = require('node:assert/strict');
const root = path.resolve(__dirname, '..');
const resources = path.join(root, 'src/main/resources');
const assets = path.join(resources, 'assets/pm_equips');
const java = path.join(root, 'src/main/java/net/pm_equips');
function walk(dir) {
    return fs.readdirSync(dir, {withFileTypes: true}).flatMap(entry => {
        const file = path.join(dir, entry.name);
        return entry.isDirectory() ? walk(file) : [file];
    });
}
const jsonFiles = walk(resources).filter(file => file.endsWith('.json'));
for (const file of jsonFiles) {
    assert(fs.statSync(file).size > 0, `Empty JSON: ${file}`);
    JSON.parse(fs.readFileSync(file, 'utf8'));
}
const itemIDs = ['mob_npc_1_spawn_egg', 'head_arbiter_armor', 'finger2_weapon_sp_caduceus',
    'finger1_weapon1', 'finger1_weapon2', 'finger2_weapon1', 'finger2_weapon2', 'finger2_weapon3',
    'finger1_armor1', 'finger1_armor2', 'finger1_armor3', 'finger2_armor1', 'finger2_armor2', 'finger2_armor3',
    'ammo_thumbs_if', 'ammo_thumbs_fr', 'ammo_thumbs_ap', 'ammo_thumbs_kb'];
const registrations = fs.readFileSync(path.join(java, 'ItemInit.java'), 'utf8');
for (const id of itemIDs) {
    assert(/^[a-z0-9_]+$/.test(id));
    assert(registrations.includes(`register("${id}"`), `Missing item: ${id}`);
    assert(fs.existsSync(path.join(assets, 'models/item', id + '.json')), `Missing model: ${id}`);
}
const languages = ['ja_jp', 'en_us'].map(locale => JSON.parse(fs.readFileSync(path.join(assets, 'lang', locale + '.json'))));
for (const lang of languages) {
    for (const id of itemIDs) assert(lang['item.pm_equips.' + id], `Missing translation: ${id}`);
    for (const id of ['mob_npc_1', 'p_arbiter_pillar']) assert(lang['entity.pm_equips.' + id]);
    for (const id of ['effect_freeze', 'effect_unlocked']) assert(lang['effect.pm_equips.' + id]);
}
const bullet = JSON.parse(fs.readFileSync(path.join(resources, 'data/pm_equips/tags/items/bullet.json')));
for (const id of itemIDs.filter(id => id.startsWith('ammo_'))) assert(bullet.values.includes('pm_equips:' + id));
const featureFiles = walk(java).filter(file => /(?:MobNPC[123]|PArbiterPillar[MR]?|Finger[12](?:Armor|Weapon).*|ItemAmmoThumbs.*|HeadArbiterArmor[MR]?|Arbiter.*|ForgeFeature.*|Feature.*Mixin|ItemStackData|ParticleInit)\.java$/.test(path.basename(file)));
for (const file of featureFiles) {
    const text = fs.readFileSync(file, 'utf8');
    assert(!/net\.neoforged|DataComponents|EquipmentSlotGroup|ItemAttributeModifiers/.test(text), `Newer API: ${file}`);
    for (const match of text.matchAll(/new ResourceLocation\("pm_equips",\s*"([^"\n]+\.(?:png|json))"\)/g)) {
        assert(fs.existsSync(path.join(assets, match[1])), `Missing resource ${match[1]} in ${file}`);
    }
}
for (const file of walk(path.join(assets, 'models/item')).filter(file => /(?:finger[12]_|ammo_thumbs_|head_arbiter_)/.test(path.basename(file)))) {
    const model = JSON.parse(fs.readFileSync(file));
    for (const texture of Object.values(model.textures || {})) {
        if (texture.startsWith('pm_equips:')) assert(fs.existsSync(path.join(assets, 'textures', texture.slice(10) + '.png')), `Missing texture: ${texture}`);
    }
}
const sounds = JSON.parse(fs.readFileSync(path.join(assets, 'sounds.json')));
for (const [name, entry] of Object.entries(sounds).filter(([name]) => name.includes('arbiter'))) {
    for (const sound of entry.sounds) {
        const id = typeof sound === 'string' ? sound : sound.name;
        assert(fs.existsSync(path.join(assets, 'sounds', id.replace(/^pm_equips:/, '') + '.ogg')), `Missing sound: ${name}`);
    }
}
const packets = fs.readFileSync(path.join(java, 'network/ForgeFeaturePackets.java'), 'utf8');
const ids = [...packets.matchAll(/register\([^,]+,(\d+),NetworkDirection/g)].map(match => Number(match[1]));
assert.equal(ids.length, 5);
assert.equal(new Set(ids).size, ids.length);
assert(ids.every(id => id > 6 && id < 256), 'Forge packet discriminators must fit in one byte');
console.log(`Verified ${jsonFiles.length} JSON files, ${itemIDs.length} items, ${featureFiles.length} feature Java files, localization, assets, ammo tags, sounds and five packet IDs.`);
