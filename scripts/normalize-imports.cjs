const fs=require('node:fs'),path=require('node:path');
const root=path.resolve(__dirname,'..','src/main/java');
function files(d){return fs.readdirSync(d,{withFileTypes:true}).flatMap(e=>e.isDirectory()?files(path.join(d,e.name)):[path.join(d,e.name)]);}
function mask(s){let out='',state='code';for(let i=0;i<s.length;i++){const c=s[i],next=s[i+1];
 if(state==='line'){if(c==='\n'){state='code';out+='\n';}else out+=' ';continue;}
 if(state==='block'){if(c==='*'&&next==='/'){out+='  ';i++;state='code';}else out+=c==='\n'?'\n':' ';continue;}
 if(state==='string'||state==='char'){if(c==='\\'){out+='  ';i++;}else if((state==='string'&&c==='"')||(state==='char'&&c==="'")){out+=' ';state='code';}else out+=c==='\n'?'\n':' ';continue;}
 if(c==='/'&&next==='/'){out+='  ';i++;state='line';}else if(c==='/'&&next==='*'){out+='  ';i++;state='block';}else if(c==='"'){out+=' ';state='string';}else if(c==="'"){out+=' ';state='char';}else out+=c;
 }return out;}
const known={EquipmentSlot:'net.minecraft.world.entity.EquipmentSlot',InteractionHand:'net.minecraft.world.InteractionHand',LivingEntity:'net.minecraft.world.entity.LivingEntity',Projectile:'net.minecraft.world.entity.projectile.Projectile',ItemStack:'net.minecraft.world.item.ItemStack',Item:'net.minecraft.world.item.Item',ResourceLocation:'net.minecraft.resources.ResourceLocation',Holder:'net.minecraft.core.Holder'};
let changed=0,conflicts=[];
for(const file of files(root).filter(f=>f.endsWith('.java'))){
 const original=fs.readFileSync(file,'utf8').replace(/^\uFEFF/,'');
 const pkg=/^package ([^;]+);/m.exec(original)?.[1];if(!pkg)continue;
 const imports=new Set([...original.matchAll(/^import ([^;]+);[^\n]*$/gm)].map(m=>m[1]).filter(n=>n.includes('.')));
 let body=original.replace(/^package [^;]+;\r?\n/m,'').replace(/^import [^;]+;[^\n]*$/gm,'').trim();
 const existing=new Map([...imports].filter(n=>!n.endsWith('.*')&&!n.startsWith('static ')).map(n=>[n.split('.').at(-1),n]));
 const edits=[];
 for(const m of mask(body).matchAll(/\b((?:net|software|com|org|java)(?:\.[a-z_][a-z0-9_]*)+\.([A-Z][A-Za-z0-9_]*))/g)){
  const name=m[1],simple=m[2],prior=existing.get(simple);
  if(prior&&prior!==name){conflicts.push(path.basename(file)+': '+name+' conflicts with '+prior);continue;}
  existing.set(simple,name);if(!name.startsWith(pkg+'.'))imports.add(name);
  edits.push({start:m.index,end:m.index+name.length,text:simple});
 }
 for(const e of edits.reverse())body=body.slice(0,e.start)+e.text+body.slice(e.end);
 const code=mask(body);
 for(const [simple,qualified] of Object.entries(known)){
  if(new RegExp('\\b'+simple+'\\b').test(code)&&!existing.has(simple)&&!new RegExp('(?:class|interface|record|enum)\\s+'+simple+'\\b').test(code)){
   imports.add(qualified);existing.set(simple,qualified);
  }
 }
 for(const imported of [...imports]){
  if(imported.endsWith('.*'))continue;
  const simple=imported.split('.').at(-1);
  const wildcard=imported.slice(0,imported.lastIndexOf('.'))+'.*';
  if(!new RegExp('\\b'+simple+'\\b').test(code)||imported===pkg+'.'+simple||imports.has(wildcard))imports.delete(imported);
 }
 const text='package '+pkg+';\n\n'+[...imports].sort().map(n=>'import '+n+';').join('\n')+'\n\n'+body+'\n';
 if(text!==original){fs.writeFileSync(file,text);changed++;}
}
console.log('Normalized imports in '+changed+' Java files.');
if(conflicts.length){console.log([...new Set(conflicts)].join('\n'));process.exitCode=1;}
