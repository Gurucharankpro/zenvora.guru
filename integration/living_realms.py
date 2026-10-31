"""Native biome settlements and one-time advancement tutorial rewards."""
import argparse,json,gzip,io,copy
from pathlib import Path
import nbtlib

NS='shadowsovereign'
def build(out):
 spec=json.loads((out/'integration-spec.json').read_text())
 def write(name,d):
  p=out/name;p.parent.mkdir(parents=True,exist_ok=True)
  p.write_bytes(d if isinstance(d,bytes) else (json.dumps(d,ensure_ascii=False,indent=2)+'\n').encode())
 biomes=['gloomwood','ashen_weald','echo_haven','bound_soul_bound','ashen_monolith','moonveil_hollow','crimson_shadow','shattered_sky','eternal_abyss','sunken_kingdom']
 groups=[spec['decor_packs'][k] for k in ['mob1','hollow_reaper','soul_devourer']]
 # Round-robin each collection so every region has a mixture of all three styles.
 ids=[groups[j][i] for i in range(20) for j in range(3)]
 layouts=[]
 source='hollow_reaper_reliquary'
 proto=nbtlib.File.parse(io.BytesIO(gzip.decompress((out/f'data/{NS}/structures/expansion/{source}.nbt').read_bytes())))
 blockspec={b['id']:b for b in spec['blocks']}
 for index,biome in enumerate(biomes):
  name='waystation_'+biome;sid='expansion_'+name;n=copy.deepcopy(proto)
  # Preserve spacious architecture, replace display furniture with a regional selection.
  for state in n['palette']:
   if str(state['Name']).startswith(NS+':'):state.clear();state['Name']=nbtlib.String('minecraft:air')
   elif str(state['Name'])=='minecraft:polished_deepslate':state['Name']=nbtlib.String('minecraft:prismarine' if biome=='sunken_kingdom' else 'minecraft:polished_blackstone' if 'ashen' in biome else 'minecraft:deepslate_tiles' if biome=='eternal_abyss' else 'minecraft:stone_bricks')
  bypos={tuple(map(int,b['pos'])):b for b in n['blocks']}
  selected=ids[index*6:(index+1)*6]
  for i,id in enumerate(selected):
   b=blockspec[id];mount=b.get('mount','floor');x=3+(i%3)*6;z=3+(i//3)*10;y=6 if mount=='ceiling' else 1
   if mount=='wall':
    for Y in range(1,5):
     n['palette'].append(nbtlib.Compound({'Name':nbtlib.String('minecraft:stone_bricks')}));bypos[(x,Y,z+1)]['state']=nbtlib.Int(len(n['palette'])-1)
   n['palette'].append(nbtlib.Compound({'Name':nbtlib.String(NS+':'+id),'Properties':nbtlib.Compound({'facing':nbtlib.String('north')})}))
   bypos[(x,y,z)]['state']=nbtlib.Int(len(n['palette'])-1)
   spec['decor_placements'].append(dict(id=id,structure=name,position=[x,y,z],mount=mount))
  # Reward chest and ambient light make these landmarks useful exploration stops.
  n['palette'].append(nbtlib.Compound({'Name':nbtlib.String('minecraft:chest'),'Properties':nbtlib.Compound({'facing':nbtlib.String('north'),'type':nbtlib.String('single'),'waterlogged':nbtlib.String('false')})}))
  chest=bypos[(21,1,23)];chest['state']=nbtlib.Int(len(n['palette'])-1);chest['nbt']=nbtlib.Compound({'id':nbtlib.String('minecraft:chest'),'LootTable':nbtlib.String(NS+':chests/waystation')})
  n['palette'].append(nbtlib.Compound({'Name':nbtlib.String('minecraft:sea_lantern')}))
  for x in (6,12,18):
   for z in (6,16):bypos[(x,7,z)]['state']=nbtlib.Int(len(n['palette'])-1)
  buf=io.BytesIO();n.write(buf);write(f'data/{NS}/structures/expansion/{name}.nbt',gzip.compress(buf.getvalue(),mtime=0))
  for kind in ['structure','template_pool','structure_set']:
   d=json.loads((out/f'data/{NS}/worldgen/{kind}/expansion_{source}.json').read_text())
   if kind=='structure':
    d.update(biomes=NS+':'+biome,start_pool=NS+':'+sid)
    if biome=='sunken_kingdom':d['project_start_to_heightmap']='OCEAN_FLOOR_WG'
   elif kind=='template_pool':d['name']=NS+':'+sid;d['elements'][0]['element']['location']=NS+':expansion/'+name
   else:d['structures'][0]['structure']=NS+':'+sid;d['placement'].update(salt=872300+index,spacing=28,separation=10)
   write(f'data/{NS}/worldgen/{kind}/{sid}.json',d)
  spec['structures'].append(sid);layouts.append(dict(biome=biome,structure=name,decor=selected))
 loot={'type':'minecraft:chest','pools':[{'rolls':4,'entries':[{'type':'minecraft:item','name':'minecraft:'+id,'weight':w,'functions':[{'function':'minecraft:set_count','count':{'type':'minecraft:uniform','min':1,'max':count}}]} for id,w,count in [('bread',5,6),('torch',5,16),('iron_ingot',3,4),('emerald',2,3),('golden_apple',1,1)]]}]}
 write(f'data/{NS}/loot_tables/chests/waystation.json',loot)
 quests=[];langs={loc:json.loads((out/f'assets/{NS}/lang/{loc}.json').read_text()) for loc in ['en_us','ar_sa']}
 def quest(id,title,ar,desc,ardesc,criteria,parent='welcome',xp=50):
  key='quest.shadowsovereign.'+id
  for loc,t,d in [('en_us',title,desc),('ar_sa',ar,ardesc)]:langs[loc][key+'.title']=t;langs[loc][key+'.description']=d
  q={'display':{'icon':{'item':'minecraft:compass'},'title':{'translate':key+'.title'},'description':{'translate':key+'.description'},'frame':'task','show_toast':True,'announce_to_chat':False,'hidden':False},'criteria':criteria,'rewards':{'experience':xp,'function':NS+':journey/'+id}}
  if parent:q['parent']=NS+':journey/'+parent
  else:q['display']['background']='minecraft:textures/block/deepslate_tiles.png'
  write(f'data/{NS}/advancements/journey/{id}.json',q)
  write(f'data/{NS}/functions/journey/{id}.mcfunction',b'give @s minecraft:bread 8\ngive @s minecraft:torch 16\ngive @s minecraft:emerald 2\n')
  quests.append(id)
 quest('welcome','Your first expedition','رحلتك الأولى','Explore landmarks for supplies. You can flee guardians, summon allies and return later.','استكشف المعالم للمؤن. يمكنك الهروب من الحراس واستدعاء الرفاق والعودة لاحقًا.',{'enter':{'trigger':'minecraft:location','conditions':{'location':{'dimension':NS+':echo_realms'}}}},parent=None,xp=100)
 for biome in biomes:
  label=biome.replace('_',' ').title()
  quest('visit_'+biome,'Discover '+label,'اكتشف '+langs['ar_sa'].get('biome.shadowsovereign.'+biome,label),'Find a waystation, collect supplies and study the local landmarks.','ابحث عن استراحة واجمع المؤن وتعرّف على معالم المنطقة.',{'visit':{'trigger':'minecraft:location','conditions':{'location':{'dimension':NS+':echo_realms','biome':NS+':'+biome}}}})
 for pack,items in spec['decor_packs'].items():
  quest('collect_'+pack,'Relic collector: '+pack.replace('_',' '),'جامع الآثار: '+pack.replace('_',' '),'Collect any relic from this collection. Decorate your camp; props are safe to recover.','اجمع أي زخرفة من هذه المجموعة وزيّن معسكرك. يمكنك استرجاع الزخارف بكسرها.',{'collect':{'trigger':'minecraft:inventory_changed','conditions':{'items':[{'items':[NS+':'+id for id in items]}]}}},xp=100)
 quest('cartographer','Know every realm','خبير العوالم','Visit all ten biomes. One-time exploration rewards help fund your next expedition.','زر البايومات العشرة. جوائز الاستكشاف تُمنح مرة واحدة لدعم رحلتك التالية.',{'visit_'+b:{'trigger':'minecraft:location','conditions':{'location':{'dimension':NS+':echo_realms','biome':NS+':'+b}}} for b in biomes},xp=300)
 write(f'data/{NS}/functions/journey/visit_sunken_kingdom.mcfunction',b'give @s minecraft:bread 8\ngive @s minecraft:torch 16\ngive @s minecraft:emerald 2\neffect give @s minecraft:water_breathing 300 0 true\n')
 for loc,d in langs.items():write(f'assets/{NS}/lang/{loc}.json',d)
 spec.update(regional_settlements=layouts,journey_quests=quests)
 write('integration-spec.json',spec)
 print('Added 10 regional decorated waystations, all 60 designs, 15 one-time tutorial quests and supply rewards.')
if __name__=='__main__':
 p=argparse.ArgumentParser();p.add_argument('--resources',type=Path,required=True);build(p.parse_args().resources)
