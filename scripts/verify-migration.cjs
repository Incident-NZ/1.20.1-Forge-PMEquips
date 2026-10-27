const fs=require('node:fs'),path=require('node:path');
const root=path.resolve(__dirname,'..');
function files(d){return fs.existsSync(d)?fs.readdirSync(d,{withFileTypes:true}).flatMap(e=>e.isDirectory()?files(path.join(d,e.name)):[path.join(d,e.name)]):[];}
const java=files(path.join(root,'src/main/java')).filter(f=>f.endsWith('.java'));
const baseline=JSON.parse(fs.readFileSync(path.join(__dirname,'source-baseline.json'),'utf8'));
const errors=[],warnings=[];
const items=new Set(),blocks=new Set(),entities=new Set();
for(const file of java){
 const s=fs.readFileSync(file,'utf8').replace(/^\uFEFF/,''),rel=path.relative(root,file).replaceAll('\\','/');
 const ids=[...s.matchAll(/\b(?:ITEMS|BLOCKS|BLOCK_ITEMS|ENTITY_TYPES|PARTICLE_TYPES|MOB_EFFECTS|SOUNDS|MENUS|BLOCK_ENTITIES)\.register\(\s*"([^"]+)"/g)].map(m=>m[1]);
 const featureIDs=new Set(['mob_npc_1','mob_npc_1_spawn_egg','p_arbiter_pillar','head_arbiter_armor','finger2_weapon_sp_caduceus','effect_unlocked','effect_freeze',
  'ammo_thumbs_if','ammo_thumbs_fr','ammo_thumbs_ap','ammo_thumbs_kb','finger1_weapon1','finger1_weapon2','finger2_weapon1','finger2_weapon2','finger2_weapon3',
  'finger1_armor1','finger1_armor2','finger1_armor3','finger2_armor1','finger2_armor2','finger2_armor3']);
 if(baseline.registrations[rel]&&JSON.stringify(ids.filter(id=>!featureIDs.has(id)))!==JSON.stringify(baseline.registrations[rel]))errors.push('Changed registration IDs: '+rel);
 for(const id of ids)if(!/^[a-z0-9/._-]+$/.test(id))errors.push('Invalid registry ID: '+id);
 if(file.endsWith('ItemInit.java'))for(const id of ids)items.add(id);
 if(file.endsWith('BlockInit.java')){
  for(const m of s.matchAll(/BLOCKS\.register\(\s*"([^"]+)"/g))blocks.add(m[1]);
  for(const m of s.matchAll(/BLOCK_ITEMS\.register\(\s*"([^"]+)"/g))items.add(m[1]);
 }
 if(path.basename(file)==='EntityInit.java')for(const id of ids)entities.add(id);
 const code=s.replace(/^import [^\n]*$/gm,'').replace(/\/\*[\s\S]*?\*\//g,'').replace(/\/\/[^\n]*/g,'').replace(/"(?:\\.|[^"\\])*"/g,'""');
 if(/\bnet\.minecraft\./.test(code))errors.push('Fully qualified Minecraft type outside imports: '+rel);
 if(/net\.minecraftforge|ForgeCapabilities|Bus\.FORGE|NetworkHooks|RegistryObject|\.getOrCreateTag\(/.test(code))errors.push('Legacy API: '+rel);
 for(const m of s.matchAll(/\bclass\s+(\w+)(?:<[^\n{]+>)?\s+extends\s+([\w.]+)/g)){
  const previous=baseline.inheritance[m[1]],parent=m[2].split('.').at(-1);
  const featureParents={MobNPC1:'TamableAnimal',MobNPC2:'HumanoidMobRenderer',NPCActivityGoal:'Goal',
   PArbiterPillar:'Projectile',PArbiterPillarM:'GeoModel',PArbiterPillarR:'GeoEntityRenderer',ArbiterParticle:'TextureSheetParticle',
   HeadArbiterArmor:'ArmorItem',HeadArbiterArmorM:'GeoModel',HeadArbiterArmorR:'GeoArmorRenderer',Finger2WeaponSP:'Item',UnlockedEffect:'MobEffect',FreezeEffect:'MobEffect',
   ItemAmmoThumbsIF:'Item',ItemAmmoThumbsFR:'Item',ItemAmmoThumbsAP:'Item',ItemAmmoThumbsKB:'Item',Finger1Weapon1:'ProjectileWeaponItem',Finger1Weapon2:'ProjectileWeaponItem',
   Finger2Weapon1:'SwordItem',Finger2Weapon2:'SwordItem',Finger2Weapon3:'SwordItem',
   Finger1Armor1M:'GeoModel',Finger1Armor2M:'GeoModel',Finger1Armor3M:'GeoModel',Finger2Armor1M:'GeoModel',Finger2Armor2M:'GeoModel',Finger2Armor3M:'GeoModel',
   Finger1Armor1R:'GeoArmorRenderer',Finger1Armor2R:'GeoArmorRenderer',Finger1Armor3R:'GeoArmorRenderer',Finger2Armor1R:'GeoArmorRenderer',Finger2Armor2R:'GeoArmorRenderer',Finger2Armor3R:'GeoArmorRenderer',
   Finger1Armor1:'ArmorItem',Finger1Armor2:'ArmorItem',Finger1Armor3:'ArmorItem',Finger2Armor1:'ArmorItem',Finger2Armor2:'ArmorItem',Finger2Armor3:'ArmorItem'};
  if(!previous&&featureParents[m[1]]!==parent)errors.push('New inherited class: '+m[1]+' extends '+parent);
  else if(previous&&previous.split('.').at(-1)!==parent)errors.push('Changed superclass: '+m[1]+' '+previous+' -> '+parent);
 }
}
const resourceRoots=['src/main/resources','src/generated/resources'].map(p=>path.join(root,p)).filter(p=>fs.existsSync(p));
const jsonFiles=resourceRoots.flatMap(files).filter(f=>f.endsWith('.json')||f.endsWith('.mcmeta'));
const parsed=new Map();
for(const file of jsonFiles){try{parsed.set(file,JSON.parse(fs.readFileSync(file,'utf8').replace(/^\uFEFF/,'')));}catch(e){errors.push('JSON syntax: '+path.relative(root,file)+': '+e.message);}}
function resourceExists(namespace,relative){return resourceRoots.some(r=>fs.existsSync(path.join(r,'assets',namespace,relative)));}
function tagExists(namespace,type,id){return resourceRoots.some(r=>fs.existsSync(path.join(r,'data',namespace,'tags',type,id+'.json')));}
function checkItem(id,file){if(id.startsWith('pm_equips:')&&!items.has(id.slice(10)))errors.push('Unknown item '+id+' in '+path.relative(root,file));}
function walk(obj,file){if(!obj||typeof obj!=='object')return;
 if(typeof obj.item==='string')checkItem(obj.item,file);
 if(obj.type==='minecraft:item'&&typeof obj.name==='string')checkItem(obj.name,file);
 if(obj.result&&typeof obj.result.id==='string')checkItem(obj.result.id,file);
 for(const value of Object.values(obj))if(Array.isArray(value))for(const entry of value)walk(entry,file);else walk(value,file);
}
for(const [file,json] of parsed){
 const rel=path.relative(root,file).replaceAll('\\','/');
 if(rel.includes('/data/')){
  if(/\/(recipes|advancements|loot_tables)\//.test(rel)||/\/tags\/(items|blocks|entity_types)\//.test(rel))errors.push('Old data-pack directory: '+rel);
  walk(json,file);
  const tag=/\/data\/([^/]+)\/tags\/(item|block|entity_type)\/(.+)\.json$/.exec(rel);
  if(tag){for(const entry of json.values||[]){const id=typeof entry==='string'?entry:entry.id;if(!id||entry.required===false)continue;
    if(id.startsWith('#')){const [ns,p]=id.slice(1).split(':');if(ns==='pm_equips'&&!tagExists(ns,tag[2],p))errors.push('Unknown tag '+id+' in '+rel);}
    else if(id.startsWith('pm_equips:')){const registry=tag[2]==='block'?blocks:tag[2]==='entity_type'?entities:items;if(!registry.has(id.slice(10)))errors.push('Unknown '+tag[2]+' '+id+' in '+rel);}
  }}
  if(rel.includes('/recipe/')&&json.type?.startsWith('minecraft:')&&json.result?.item)errors.push('Old recipe result field: '+rel);
 }
 if(rel.includes('/assets/')&&rel.includes('/models/')){
  if(typeof json.parent==='string'&&json.parent.startsWith('pm_equips:')&&!resourceExists('pm_equips','models/'+json.parent.slice(10)+'.json'))errors.push('Unknown model parent '+json.parent+' in '+rel);
  for(const texture of Object.values(json.textures||{}))if(typeof texture==='string'&&texture.startsWith('pm_equips:')&&!resourceExists('pm_equips','textures/'+texture.slice(10)+'.png'))errors.push('Unknown model texture '+texture+' in '+rel);
 }
 if(rel.includes('/assets/')&&rel.includes('/particles/'))for(const texture of json.textures||[]){
  const [namespace,id]=texture.split(':');
  if(namespace==='pm_equips'&&!resourceExists(namespace,'textures/particle/'+id+'.png'))errors.push('Missing particle texture '+texture+' in '+rel);
 }
}
for(const id of items)if(!resourceExists('pm_equips','models/item/'+id+'.json'))errors.push('Missing item model: '+id);
const soundsFile=path.join(root,'src/main/resources/assets/pm_equips/sounds.json');
const sounds=parsed.get(soundsFile)||{};
const language=parsed.get(path.join(root,'src/main/resources/assets/pm_equips/lang/en_us.json'))||{};
const soundSource=fs.readFileSync(path.join(root,'src/main/java/net/pm_equips/SoundInit.java'),'utf8');
for(const match of soundSource.matchAll(/register\("([^"]+)"\)/g))if(!sounds[match[1]])errors.push('Missing sound event: '+match[1]);
for(const [id,entry] of Object.entries(sounds)){
 if(entry.subtitle&&!language[entry.subtitle])errors.push('Missing subtitle translation: '+entry.subtitle);
 for(const sound of entry.sounds||[]){const value=typeof sound==='string'?sound:sound.name;if(value?.startsWith('pm_equips:')&&!resourceExists('pm_equips','sounds/'+value.slice(10)+'.ogg'))errors.push('Missing sound resource: '+value);}
}
for(const file of java){const s=fs.readFileSync(file,'utf8');for(const m of s.matchAll(/"((?:geo|animations|textures)\/[a-zA-Z0-9_/.-]+\.(?:json|png))"/g)){
 if(!resourceExists('pm_equips',m[1]))errors.push('Missing asset '+m[1]+' in '+path.relative(root,file));
}}
const summary={javaFiles:java.length,items:items.size,blocks:blocks.size,entities:entities.size,jsonFiles:parsed.size,recipes:[...parsed.keys()].filter(f=>f.replaceAll('\\','/').includes('/recipe/')).length,errors:[...new Set(errors)],warnings:[...new Set(warnings)]};
fs.mkdirSync(path.join(root,'build'),{recursive:true});fs.writeFileSync(path.join(root,'build/migration-verification.json'),JSON.stringify(summary,null,2)+'\n');
console.log(JSON.stringify(summary,null,2));if(summary.errors.length)process.exitCode=1;
