// Original 64x64 player-skin assets: office uniform with two hair/tie palettes.
const fs = require('node:fs'), zlib = require('node:zlib'), path = require('node:path');
const crc = bytes => { let n = 0xffffffff; for (const b of bytes) { n ^= b; for (let i=0;i<8;i++) n=(n>>>1)^((n&1)?0xedb88320:0); } return (n^0xffffffff)>>>0; };
function chunk(type, bytes) { const t=Buffer.from(type), n=Buffer.alloc(4), c=Buffer.alloc(4); n.writeUInt32BE(bytes.length); c.writeUInt32BE(crc(Buffer.concat([t,bytes]))); return Buffer.concat([n,t,bytes,c]); }
for (let variant=1; variant<=2; variant++) {
 const pixels=Buffer.alloc(64*64*4);
 const fill=(x,y,w,h,color)=>{for(let j=y;j<y+h;j++)for(let i=x;i<x+w;i++){const p=(j*64+i)*4; pixels[p]=color[0];pixels[p+1]=color[1];pixels[p+2]=color[2];pixels[p+3]=255;}};
 const skin=variant===1?[220,177,142]:[238,199,163], hair=variant===1?[40,30,27]:[106,65,42], suit=[35,40,49], pants=[23,26,34], shirt=[219,224,227], tie=variant===1?[103,158,186]:[164,56,62];
 fill(0,0,32,16,skin); fill(0,0,32,8,hair); fill(0,8,8,8,hair);fill(24,8,8,8,hair);fill(8,8,8,2,hair);
 fill(9,11,2,1,[31,40,51]);fill(13,11,2,1,[31,40,51]); fill(11,14,2,1,[153,97,83]);
 fill(16,16,24,16,suit);fill(20,20,8,3,shirt);fill(23,22,2,8,tie);fill(20,30,8,1,[57,62,71]);
 for(const [x,y] of [[40,16],[32,48]]){fill(x,y,16,16,suit);fill(x,y+13,16,3,skin);fill(x,y+12,16,1,shirt);}
 for(const [x,y] of [[0,16],[16,48]]){fill(x,y,16,16,pants);fill(x,y+13,16,3,[15,17,22]);}
 fill(26,23,1,2,[211,187,95]);
 const raw=Buffer.alloc(64*(1+64*4));for(let y=0;y<64;y++)pixels.copy(raw,y*257+1,y*256,(y+1)*256);
 const header=Buffer.alloc(13);header.writeUInt32BE(64,0);header.writeUInt32BE(64,4);header[8]=8;header[9]=6;
 const out=Buffer.concat([Buffer.from('89504e470d0a1a0a','hex'),chunk('IHDR',header),chunk('IDAT',zlib.deflateSync(raw)),chunk('IEND',Buffer.alloc(0))]);
 const target=path.join(__dirname,'../src/main/resources/assets/pm_equips/textures/entity',variant===1?'npc_default_1.png':'npc_default_2.slim.png');fs.mkdirSync(path.dirname(target),{recursive:true});fs.writeFileSync(target,out);
}
