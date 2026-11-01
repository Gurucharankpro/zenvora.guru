"""Import the five audited latest uploads without replacing previous assets."""
import argparse,copy,json,gzip,io,struct
from pathlib import Path
import nbtlib
from decor_packs import geometry_bounds
from prepare import transparent_png
NS='shadowsovereign'
def build(inputs,out):
 spec=json.loads((out/'integration-spec.json').read_text());added=[]
 langs={l:json.loads((out/f'assets/{NS}/lang/{l}.json').read_text()) for l in ['en_us','ar_sa']}
 def write(path,value):
  p=out/path;p.parent.mkdir(parents=True,exist_ok=True);p.write_bytes(value if isinstance(value,bytes) else (json.dumps(value,ensure_ascii=False,indent=2)+'\n').encode())
 def add(original,prefix,model,texture,glow,title,biome):
  id=prefix+original;assert id not in spec['decor_ids']
  d=json.loads(model.read_text());d['textures']={k:f'{NS}:block/{id}' for k in d['textures']};d['render_type']='minecraft:cutout'
  bounds=geometry_bounds(d);raw=texture.read_bytes();w,h=struct.unpack('>II',raw[16:24]);assert w==h and w in (64,128)
  write(f'assets/{NS}/models/block/{id}.json',d);write(f'assets/{NS}/models/item/{id}.json',{'parent':f'{NS}:block/{id}'})
  write(f'assets/{NS}/blockstates/{id}.json',{'variants':{f'facing={direction}':{'model':f'{NS}:block/{id}','y':angle} for direction,angle in [('north',180 if prefix=='relic_' else 0),('east',270 if prefix=='relic_' else 90),('south',0 if prefix=='relic_' else 180),('west',90 if prefix=='relic_' else 270)]}})
  write(f'assets/{NS}/textures/block/{id}.png',raw);write(f'assets/{NS}/textures/block/{id}_e.png',glow.read_bytes() if glow and glow.exists() else transparent_png(w,h))
  light=10 if any(x in id for x in ['lantern','crystal','brazier','heart','spore','pearl']) else 3
  entry=dict(id=id,category='decor',light=light,hardness=1.5,render_layer='cutout',visual_bounds=bounds,mount='floor',pack=prefix.rstrip('_'),biome=biome)
  spec['blocks'].append(entry);spec['decor_ids'].append(id);added.append(entry)
  for l in langs:langs[l]['block.'+NS+'.'+id]=title
  write(f'data/{NS}/loot_tables/blocks/{id}.json',{'type':'minecraft:block','pools':[{'rolls':1,'entries':[{'type':'minecraft:item','name':NS+':'+id}],'conditions':[{'condition':'minecraft:survives_explosion'}]}]})
 root=inputs/'Decor_Pack_05';manifest=json.loads((root/'asset_manifest.json').read_text())
 for a in manifest['assets']:
  id=a['id'];add(id,'butcher_',root/a['model'],root/f'assets/soulbound/textures/block/decor/{id}.png',root/a['emissive_mask'],a['title'],'crimson_shadow')
 root=inputs/'Decor_Pack_03_Variant/Decor_Pack_03_Soul_Devourer/assets/decor_pack_03'
 for model in sorted((root/'models/block').glob('*.json')):
  id=model.stem;add(id,'devourer_v2_',model,root/f'textures/block/{id}.png',root/f'textures/block/{id}_e.png',id.replace('_',' ').title()+' II','gloomwood')
 manifest=next((inputs/'Decor_Pack_10').rglob('asset_manifest.json'));root=manifest.parent.parent
 biomes={1:'crimson_shadow',2:'moonveil_hollow',3:'shattered_sky',4:'eternal_abyss',5:'sunken_kingdom'}
 for a in json.loads(manifest.read_text())['assets']:
  add(a['asset_id'],'relic_',root/a['files']['java_block_model'],root/a['files']['texture'],None,a['display_name'],biomes[a['biome_index']])
 assert len(added)==60
 groups=[('butcher_court','crimson_shadow',[b for b in added if b['pack']=='butcher']),('devourer_second_sanctum','gloomwood',[b for b in added if b['pack']=='devourer_v2'])]
 groups +=[('relic_site_'+biome,biome,[b for b in added if b['pack']=='relic' and b['biome']==biome]) for biome in biomes.values()]
 proto=nbtlib.File.parse(io.BytesIO(gzip.decompress((out/f'data/{NS}/structures/expansion/hollow_reaper_reliquary.nbt').read_bytes())))
 for index,(name,biome,entries) in enumerate(groups):
  n=copy.deepcopy(proto);bypos={tuple(map(int,b['pos'])):b for b in n['blocks']}
  air=next(i for i,s in enumerate(n['palette']) if str(s['Name'])=='minecraft:air')
  for pos,b in bypos.items():
   x,y,z=pos
   if 1<=x<=23 and 1<=z<=25 and 1<=y<=6:b['state']=nbtlib.Int(air);b.pop('nbt',None)
  for i,b in enumerate(entries):
   row,col=divmod(i,4);pos=(3+col*6,1,3+row*5)
   n['palette'].append(nbtlib.Compound({'Name':nbtlib.String(NS+':'+b['id']),'Properties':nbtlib.Compound({'facing':nbtlib.String('north')})}));bypos[pos]['state']=nbtlib.Int(len(n['palette'])-1)
   spec['decor_placements'].append(dict(id=b['id'],structure=name,position=list(pos),mount='floor'))
  buf=io.BytesIO();n.write(buf);write(f'data/{NS}/structures/expansion/{name}.nbt',gzip.compress(buf.getvalue(),mtime=0))
  sid='expansion_'+name
  for kind in ['structure','template_pool','structure_set']:
   d=json.loads((out/f'data/{NS}/worldgen/{kind}/expansion_hollow_reaper_reliquary.json').read_text())
   if kind=='structure':
    d.update(biomes=NS+':'+biome,start_pool=NS+':'+sid)
    if biome=='sunken_kingdom':d['project_start_to_heightmap']='OCEAN_FLOOR_WG'
   elif kind=='template_pool':d['name']=NS+':'+sid;d['elements'][0]['element']['location']=NS+':expansion/'+name
   else:d['structures'][0]['structure']=NS+':'+sid;d['placement'].update(salt=872400+index)
   write(f'data/{NS}/worldgen/{kind}/{sid}.json',d)
  spec['structures'].append(sid)
 labels={'soulbound_army':('Other mod items','بقية أغراض المود'),'creatures':('Spawn eggs','بيض المخلوقات'),'nature':('Decorations','الديكور'),'workshops':('Workstations','محطات العمل')}
 for key,(en,ar) in labels.items():
  langs['en_us']['itemGroup.shadowsovereign.'+key]=en;langs['ar_sa']['itemGroup.shadowsovereign.'+key]=ar
 for loc,d in langs.items():write(f'assets/{NS}/lang/{loc}.json',d)
 # Plain JVM helper; it adds no inferred Minecraft API dependencies.
 java='package dev.shadowsovereign.expansion;\npublic final class CreativeGroups {\n static final java.util.Set<String> DECOR=java.util.Set.of('+','.join(json.dumps(id) for id in spec['decor_ids'])+');\n public static int category(String path,int legacy,boolean block){if(DECOR.contains(path))return 2;if(legacy==1)return 1;if(legacy==3 || path.equals("monolith_enchanting_table") || block&&path.startsWith("eclipse_"))return 3;return 0;}\n}\n'
 write('creative-groups-source.txt',java.encode());spec['latest_decor_ids']=[b['id'] for b in added]
 write('integration-spec.json',spec);print('Imported 60 distinct new decor designs with 7 themed sites and four creative group labels.')
if __name__=='__main__':
 p=argparse.ArgumentParser();p.add_argument('--inputs',type=Path,required=True);p.add_argument('--resources',type=Path,required=True);a=p.parse_args();build(a.inputs,a.resources)
