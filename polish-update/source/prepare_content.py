"""Incremental resources over the repaired latest complete content JAR.
Uploaded scripts are never executed. Registry additions are append-only.
"""
import copy,gzip,hashlib,io,json,math,os,sys
from pathlib import Path
from zipfile import ZipFile
sys.path.insert(0,'/workspace/mod-integration/tools/python')
import nbtlib
import numpy as np
from PIL import Image
from native_model import model

ROOT=Path(__file__).resolve().parents[1]
BASE=Path(os.environ.get('SOULBOUND_BASE_JAR',ROOT/'base/soulbound-army-1.20.1-1.8.1-repaired-content.jar'))
OUT=ROOT/'resources';NS='shadowsovereign'
PROFILES=[
 dict(index=54,id='dr_salmawy',source='Mob_53_Dr_Salmawy_Complete',stem='Dr_Salmawy',en='Dr. Salmawy',ar='د. السلماوي',health=620,damage=19,armor=8,speed=.29,width=.7,height=2.1,scale=1,walk='walk',run='run',flies=False,moves=[['punch_attack',.7,0,3.6,1],['counter_attack',.9,5,4,.7],['command',1.15,3,9,.4],['intimidation',1.3,2,8,.5]],template='Salmawy_Secret_Mansion.nbt',site='salmawy_secret_mansion',biome='crimson_shadow',floor=6,altar=[19,7,34]),
 dict(index=55,id='raven_seamstress',source='Mob_64_Raven_Seamstress_Complete',stem='Raven_Seamstress',en='Raven Seamstress',ar='خياطة الغربان',health=1050,damage=24,armor=10,speed=.28,width=1.2,height=3.1,scale=1,walk='walk',run='run',flies=False,moves=[['silver_needle_attack',.8,0,5,1],['raven_swarm',1.2,47,16,.9],['thread_binding',1.3,2,9,.7],['feather_storm',1.5,1,9,1.1],['black_curtain',1.9,5,10,.4]],template='Theater_Of_Black_Curtains.nbt',site='theater_of_black_curtains',biome='moonveil_hollow',floor=10,altar=[22,12,25]),
 dict(index=56,id='clockwork_widow',source='Mob_62_Clockwork_Widow_Complete',stem='Clockwork_Widow',en='Clockwork Widow',ar='الأرملة الآلية',health=1200,damage=26,armor=14,speed=.25,width=.9,height=2.9,scale=1,walk='walk',run='run',flies=False,moves=[['clock_blade_combo',.8,0,5,1.05],['gear_storm',1.4,47,15,.95],['time_lock',1.2,2,9,.6],['six_arm_assault',1.65,1,7,1.15],['broken_hourglass',2.5,6,15,1.2]],template='Palace_Of_Broken_Hours.nbt',site='palace_of_broken_hours',biome='moonveil_hollow',floor=12,altar=[23,3,16]),
]

def write(name,value):
 p=OUT/name;p.parent.mkdir(parents=True,exist_ok=True)
 p.write_bytes(value if isinstance(value,bytes) else (json.dumps(value,ensure_ascii=False,indent=2)+'\n').encode())
def find(root,name):
 paths=[p for p in root.rglob(name) if 'Integration_Optional' not in p.parts]
 if len(paths)!=1:raise ValueError((name,paths))
 return paths[0]
def load_template(data):return nbtlib.File.parse(io.BytesIO(gzip.decompress(data)))
def save_template(name,n):
 b=io.BytesIO();n.write(b);write(name,gzip.compress(b.getvalue(),mtime=0))
def model_bounds(d):
 points=[]
 for e in d['elements']:
  r=e.get('rotation');angle=math.radians(r['angle']) if r else 0
  for x in [e['from'][0],e['to'][0]]:
   for y in [e['from'][1],e['to'][1]]:
    for z in [e['from'][2],e['to'][2]]:
     v=[x,y,z]
     if r:
      a,b=[i for i in range(3) if i!='xyz'.index(r['axis'])];o=r['origin'];u,w=v[a]-o[a],v[b]-o[b]
      v[a]=o[a]+u*math.cos(angle)-w*math.sin(angle);v[b]=o[b]+u*math.sin(angle)+w*math.cos(angle)
     points.append(v)
 return [[min(v[i] for v in points) for i in range(3)],[max(v[i] for v in points) for i in range(3)]]
def normalize(n):
 n['DataVersion']=nbtlib.Int(3465)
 for state in n['palette']:
  if str(state['Name'])=='minecraft:short_grass':state['Name']=nbtlib.String('minecraft:grass')
  if str(state['Name'])=='minecraft:structure_block':
   state['Name']=nbtlib.String('minecraft:air');state.pop('Properties',None)
 for b in n['blocks']:
  state=str(n['palette'][int(b['state'])]['Name'])
  if state.endswith(('chest','barrel')) and not b.get('nbt',{}).get('LootTable') and not b.get('nbt',{}).get('Items'):
   b['nbt']=nbtlib.Compound({'id':nbtlib.String('minecraft:barrel' if state.endswith('barrel') else 'minecraft:chest'),'LootTable':nbtlib.String('minecraft:chests/stronghold_library' if 'library' in state else 'minecraft:chests/abandoned_mineshaft')})
  if state=='minecraft:air' and 'nbt' in b:b.pop('nbt')
 return n
def add_structure(jar,shared,name,path,n,biome,floor):
 save_template(path,n)
 structure=json.loads(jar.read(f'data/{NS}/worldgen/structure/expansion_hanan_cairo_house.json'))
 structure.update(biomes=NS+':'+biome,start_pool=NS+':'+name,start_height={'absolute':-floor},terrain_adaptation='beard_box')
 pool=json.loads(jar.read(f'data/{NS}/worldgen/template_pool/expansion_hanan_cairo_house.json'))
 pool['name']=NS+':'+name;pool['elements'][0]['element']['location']=NS+':'+path.split('/structures/')[1][:-4]
 write(f'data/{NS}/worldgen/structure/{name}.json',structure);write(f'data/{NS}/worldgen/template_pool/{name}.json',pool)
 shared['structures'].append({'structure':NS+':'+name,'weight':1})
def egg(jar,id,index):
 d=json.loads(jar.read(f'assets/{NS}/models/item/hanan_spawn_egg.json'))
 d['textures']={'egg':NS+':item/polish_eggs','particle':NS+':item/polish_eggs'}
 d['elements'][0]['faces']['north']['uv']=[index*4,0,(index+1)*4,16]
 d['elements'][0]['faces']['south']['uv']=[(index+1)*4,0,index*4,16]
 write(f'assets/{NS}/models/item/{id}_spawn_egg.json',d)
def native_creature(root,stem,id):
 geo=json.loads(find(root,stem+'.geo.json').read_text());anim=json.loads(find(root,stem+'.animation.json').read_text())
 native=model(geo,anim)
 for clip in native['animations'].values():
  for t in clip['tracks']:
   if t['channel']=='scale':
    for k in t['keys']:k[1:]=[.001 if abs(v)<1e-6 else v for v in k[1:]]
 write(f'assets/{NS}/mythic/{id}.json',native)
 texture=find(root,stem+'_Texture.png');write(f'assets/{NS}/textures/entity/{id}.png',texture.read_bytes())
 glows=[p for p in root.rglob('*.png') if 'glow' in p.name.lower() and 'preview' not in p.name.lower()]
 if len(glows)>1:raise ValueError(glows)
 if glows:glow=glows[0].read_bytes()
 else:
  b=io.BytesIO();Image.new('RGBA',Image.open(texture).size).save(b,format='PNG');glow=b.getvalue()
 write(f'assets/{NS}/textures/entity/{id}_glow.png',glow)
 return native
def creatures(jar,langs,spec,shared):
 sites=json.loads(jar.read(f'data/{NS}/mythic_sites.json'))
 for i,p in enumerate(PROFILES):
  root=ROOT/'inputs'/p['source'];native=native_creature(root,p['stem'],p['id']);egg(jar,p['id'],i)
  assert all(m[0] in native['animations'] for m in p['moves'])
  n=normalize(nbtlib.load(find(root,p['template'])));name='expansion_'+p['site'];path=f'data/{NS}/structures/polish/{p["site"]}.nbt'
  if p['id']=='dr_salmawy':
   bypos={tuple(map(int,b['pos'])):b for b in n['blocks']}
   air=next(i for i,s in enumerate(n['palette']) if str(s['Name'])=='minecraft:air')
   # Open the furnished fence bottleneck at the dining-room entrance.
   for pos in [(28,6,6),(28,7,6)]:
    assert str(n['palette'][int(bypos[pos]['state'])]['Name']) in {'minecraft:dark_oak_fence','minecraft:dark_oak_pressure_plate'}
    bypos[pos]['state']=nbtlib.Int(air);bypos[pos].pop('nbt',None)
   # Complete the final descent step, preserving the false bookshelf secret.
   stairs=nbtlib.Compound({'Name':nbtlib.String('minecraft:stone_brick_stairs'),'Properties':nbtlib.Compound({k:nbtlib.String(v) for k,v in dict(facing='north',half='bottom',shape='straight',waterlogged='false').items()})})
   assert str(n['palette'][int(bypos[(9,1,26)]['state'])]['Name'])=='minecraft:air'
   n['palette'].append(stairs);bypos[(9,1,26)]['state']=nbtlib.Int(len(n['palette'])-1)
  add_structure(jar,shared,name,path,n,p['biome'],p['floor'])
  sites.append({'kind':p['index'],'structure':name,'altar':p['altar'],'rootOffset':[0,0,0]})
  spec['templates'][p['index']]={'path':path,'altar':p['altar'],'source_dimensions':list(map(int,n['size']))}
  spec['profiles'].append({k:p[k] for k in ['index','id','health','damage','armor','speed','width','height','scale','walk','run','flies','moves']})
  for loc,d in langs.items():
   label=p['ar'] if loc=='ar_sa' else p['en'];d[f'entity.{NS}.{p["id"]}']=label;d[f'item.{NS}.{p["id"]}_spawn_egg']=('بيضة استدعاء ' if loc=='ar_sa' else 'Spawn Egg: ')+label
   d[f'grimoire.{NS}.lore.{p["id"]}']=('اهزم الحارس في موطنه لتضم روحه لجيشك. الهروب ينهي المواجهة ويعيد صحته كاملة.' if loc=='ar_sa' else 'Defeat this guardian in its habitat to earn its soul. Retreat ends the encounter and restores its full health.')
   d[f'grimoire.{NS}.abilities.{p["id"]}']=('قدرات: ' if loc=='ar_sa' else 'Abilities: ')+', '.join(m[0].replace('_',' ') for m in p['moves'])
 write(f'data/{NS}/mythic_sites.json',sites)
def decorations(jar,langs,spec):
 root=ROOT/'inputs/Decor_Pack_08_Eclipse_Syndicate_Java/Decor_Pack_08_Eclipse_Syndicate_Java'
 manifest=json.loads((root/'Documentation/asset_manifest.json').read_text())
 targets=[53,53,56,56,6,6,55,55,50,50,54,54,47,47,52,52,48,48,47,47]
 names=['مكتب عقود الأرواح','نصب العقد بأربع أيد','ساعة الأرملة الآلية','عرش الساعة الرملية المحطمة','نصب الفارس الأجوف','درع الحصن','طاولة خياطة ملكة الغربان','ثريا ألف غراب','تمثال الساموراي البلوري','مزار النصل المرآتي','تجربة الفطر العملاق','خزانة نباتات الكيميائي','نصب الأسطورة 67','العرش الملكي 67','تمثال دوقة الخزف','مرآة الزينة الملكية','مجسم 69 المقلوب','نصب وهم الجاذبية','عرش الإحراج المطلق','تمثال ملك الميمز']
 centers='paper emerald clock sand iron_sword shield string feather amethyst_shard glass red_mushroom flower_pot gold_block diamond white_terracotta glass_pane ender_eye purple_dye nether_star cake'.split()
 for a,kind,ar,center in zip(manifest['decorations'],targets,names,centers):
  id='syndicate_'+a['id'];d=json.loads((root/a['java_block_model']).read_text())
  d['textures']={k:NS+':block/'+id for k in d['textures']};d['render_type']='minecraft:translucent' if a.get('translucent_materials') else 'minecraft:cutout';d['ambientocclusion']=False
  for e in d['elements']:assert all(-16<=v<=32 for v in e['from']+e['to']),id
  bounds=model_bounds(d);mount='ceiling' if 'chandelier' in id else 'floor'
  b={'id':id,'category':'decor','hardness':1.5,'light':5 if a.get('emissive_materials') else 0,'visual_bounds':bounds,'mount':mount,'kind':kind,'pack':'syndicate'};spec['blocks'].append(b)
  write(f'assets/{NS}/models/block/{id}.json',d);write(f'assets/{NS}/models/item/{id}.json',{'parent':NS+':block/'+id})
  write(f'assets/{NS}/textures/block/{id}.png',(root/a['texture']).read_bytes())
  write(f'assets/{NS}/blockstates/{id}.json',{'variants':{f'facing={f}':{'model':NS+':block/'+id,'y':r} for f,r in [('north',0),('east',90),('south',180),('west',270)]}})
  block_loot(id)
  write(f'data/{NS}/recipes/polish/{id}.json',{'type':'minecraft:crafting_shaped','pattern':['SCS',' G ','S S'],'key':{'S':{'item':'minecraft:blackstone'},'C':{'item':'minecraft:'+center},'G':{'item':'minecraft:gold_nugget'}},'result':{'item':NS+':'+id}})
  write(f'data/{NS}/advancements/recipes/polish/{id}.json',{'parent':'minecraft:recipes/root','criteria':{'materials':{'trigger':'minecraft:inventory_changed','conditions':{'items':[{'items':['minecraft:'+center]}]}}},'rewards':{'recipes':[NS+':polish/'+id]}})
  langs['en_us'][f'block.{NS}.{id}']=a['title'];langs['ar_sa'][f'block.{NS}.{id}']=ar
 old=json.loads(jar.read(f'assets/{NS}/content/blocks.json'));old['blocks'].extend(spec['blocks']);write(f'assets/{NS}/content/blocks.json',old)
def block_loot(id):write(f'data/{NS}/loot_tables/blocks/{id}.json',{'type':'minecraft:block','pools':[{'rolls':1,'entries':[{'type':'minecraft:item','name':NS+':'+id}],'conditions':[{'condition':'minecraft:survives_explosion'}]}]})
def biome(jar,langs,spec,shared):
 root=ROOT/'inputs/Biome_06_Singing_Glass_Desert_Java_Complete'
 manifest=json.loads((root/'Biome_Assets/Asset_Manifest.json').read_text())
 ar=['رمل الكثبان البنفسجي','رمل الزجاج الذهبي','حجر الصحراء الرملي العتيق','حجر الصحراء الرملي المتشقق','حجر السراب المصقول','خام البلور المغني','بلوك البلور المغني','جذع الخشب الزجاجي','أوراق الخشب الزجاجي','شتلة الخشب الزجاجي','جذع نخيل السراب','أوراق نخيل السراب','شتلة نخيل السراب','عشب الصحراء الهامس','صبار البلور','لوتس السراب','سرخس الكثبان الذهبي','شجيرة شوك الزجاج','عنقود البلور المغني','فطر رمل القمر']
 assets=root/'Biome_Assets/assets/soulbound'
 for p in assets.rglob('*'):
  if not p.is_file() or '/items/' in p.as_posix() or '/lang/' in p.as_posix():continue
  rel=p.relative_to(assets).as_posix();data=p.read_bytes()
  if p.suffix=='.json':
   d=json.loads(data.decode().replace('soulbound:',NS+':'))
   if rel.startswith('models/block/'):
    id=p.stem;entry=next(a for a in manifest['assets'] if a['registry_name'].endswith(':'+id));d['render_type']='minecraft:'+('cutout' if entry['properties']['layer']=='cutout_mipped' else entry['properties']['layer'])
   write('assets/'+NS+'/'+rel,d)
  else:write('assets/'+NS+'/'+rel,data)
 for a,label in zip(manifest['assets'],ar):
  id=a['registry_name'].split(':')[1];spec['biome_blocks'].append(dict(id=id,type=a['type'],**a['properties']));block_loot(id)
  langs['en_us'][f'block.{NS}.{id}']=a['name'];langs['ar_sa'][f'block.{NS}.{id}']=label
 tags={'mineable/shovel':['violet_dune_sand','golden_glass_sand'],'mineable/axe':['glasswood_log','mirage_palm_log'],'mineable/pickaxe':['ancient_desert_sandstone','cracked_desert_sandstone','polished_mirage_stone','singing_crystal_ore','singing_crystal_block','singing_crystal_cluster'],'needs_iron_tool':['singing_crystal_ore'],'logs':['glasswood_log','mirage_palm_log'],'logs_that_burn':['glasswood_log','mirage_palm_log'],'leaves':['glasswood_leaves','mirage_palm_leaves']}
 for tag,ids in tags.items():
  path=f'data/minecraft/tags/blocks/{tag}.json';d=json.loads(jar.read(path)) if path in jar.namelist() else {'replace':False,'values':[]};d['values']+= [NS+':'+id for id in ids];write(path,d)
 for wood in ['glasswood','mirage_palm']:
  leaf=NS+':'+wood+'_leaves';sapling=NS+':'+wood+'_sapling'
  children=[{'type':'minecraft:item','name':leaf,'conditions':[{'condition':'minecraft:match_tool','predicate':{'items':['minecraft:shears']}}]},
            {'type':'minecraft:item','name':leaf,'conditions':[{'condition':'minecraft:match_tool','predicate':{'enchantments':[{'enchantment':'minecraft:silk_touch','levels':{'min':1}}]}}]},
            {'type':'minecraft:item','name':sapling,'conditions':[{'condition':'minecraft:random_chance','chance':.08}]}]
  write(f'data/{NS}/loot_tables/blocks/{wood}_leaves.json',{'type':'minecraft:block','pools':[{'rolls':1,'entries':[{'type':'minecraft:alternatives','children':children}],'conditions':[{'condition':'minecraft:survives_explosion'}]}]})
 for tree in sorted((root/'Tree_Designs').glob('*.nbt')):
  n=normalize(nbtlib.load(tree))
  for s in n['palette']:s['Name']=nbtlib.String(str(s['Name']).replace('soulbound:',NS+':'))
  save_template(f'data/{NS}/structures/polish/{tree.stem.lower()}.nbt',n)
 subst={'minecraft:purple_terracotta':'violet_dune_sand','minecraft:yellow_terracotta':'golden_glass_sand','minecraft:sandstone':'ancient_desert_sandstone','minecraft:cut_sandstone':'cracked_desert_sandstone','minecraft:purpur_block':'polished_mirage_stone','minecraft:glowstone':'singing_crystal_block','minecraft:amethyst_cluster':'singing_crystal_cluster'}
 for name,floor in [('Cathedral_of_Glass_Harp',9),('Buried_Mirage_Observatory',16),('Lost_Caravanserai',5),('Thunderglass_Labyrinth',7)]:
  n=normalize(nbtlib.load(root/'Structures'/f'{name}.nbt'))
  for s in n['palette']:
   if str(s['Name']) in subst:s['Name']=nbtlib.String(NS+':'+subst[str(s['Name'])]);s.pop('Properties',None)
  id='glass_desert_'+name.lower();path=f'data/{NS}/structures/polish/{name.lower()}.nbt';add_structure(jar,shared,id,path,n,'singing_glass_desert',floor)
  spec['biome_structures'].append({'id':id,'path':path,'floor':floor,'size':list(map(int,n['size']))})
 native_creature(root/'Creature','Prism_Jerboa','prism_jerboa');egg(jar,'prism_jerboa',3)
 for loc,d in langs.items():
  d[f'entity.{NS}.prism_jerboa']='يربوع المنشور' if loc=='ar_sa' else 'Prism Jerboa';d[f'item.{NS}.prism_jerboa_spawn_egg']='بيضة يربوع المنشور' if loc=='ar_sa' else 'Prism Jerboa Spawn Egg';d[f'biome.{NS}.singing_glass_desert']='صحراء الزجاج المغني' if loc=='ar_sa' else 'Singing Glass Desert'
 dim=json.loads(jar.read(f'data/{NS}/dimension/echo_realms.json'));params=copy.deepcopy(dim['generator']['biome_source']['biomes'][0]['parameters']);params.update(temperature=.85,humidity=-.55);dim['generator']['biome_source']['biomes'].append({'biome':NS+':singing_glass_desert','parameters':params});write(f'data/{NS}/dimension/echo_realms.json',dim)
 noise=json.loads(jar.read(f'data/{NS}/worldgen/noise_settings/echo_expanses.json'))
 rule={'type':'minecraft:condition','if_true':{'type':'minecraft:biome','biome_is':[NS+':singing_glass_desert']},'then_run':{'type':'minecraft:sequence','sequence':[
  {'type':'minecraft:condition','if_true':{'type':'minecraft:stone_depth','offset':0,'add_surface_depth':False,'secondary_depth_range':0,'surface_type':'floor'},'then_run':{'type':'minecraft:sequence','sequence':[{'type':'minecraft:condition','if_true':{'type':'minecraft:noise_threshold','noise':'minecraft:surface','min_threshold':.35,'max_threshold':1000},'then_run':{'type':'minecraft:block','result_state':{'Name':NS+':golden_glass_sand'}}},{'type':'minecraft:block','result_state':{'Name':NS+':violet_dune_sand'}}]}},
  {'type':'minecraft:condition','if_true':{'type':'minecraft:stone_depth','offset':4,'add_surface_depth':False,'secondary_depth_range':0,'surface_type':'floor'},'then_run':{'type':'minecraft:block','result_state':{'Name':NS+':ancient_desert_sandstone'}}}]}}
 old=noise['surface_rule'];noise['surface_rule']={'type':'minecraft:sequence','sequence':[rule,old]};write(f'data/{NS}/worldgen/noise_settings/echo_expanses.json',noise)
 b=json.loads(jar.read(f'data/{NS}/worldgen/biome/sunken_kingdom.json'));b.update(temperature=1.6,downfall=0,features=[[] for _ in range(11)])
 b['effects'].update({k:int(v,16) for k,v in dict(sky_color='d9783a',fog_color='b46cc4',water_color='3fd6c8',water_fog_color='1b7f86',grass_color='b9a2d6',foliage_color='d8b64a').items()});b['effects'].pop('particle',None)
 b['spawners']={'creature':[{'type':NS+':prism_jerboa','weight':6,'minCount':1,'maxCount':3}],'ambient':[],'monster':[]};b['features'][9]=[NS+':singing_desert_ecology'];b['features'][6]=[NS+':singing_crystal_ore'];write(f'data/{NS}/worldgen/biome/singing_glass_desert.json',b)
 for key in ['singing_desert_ecology','glasswood_tree','mirage_palm_tree']:
  write(f'data/{NS}/worldgen/configured_feature/{key}.json',{'type':NS+':'+key,'config':{}})
 write(f'data/{NS}/worldgen/placed_feature/singing_desert_ecology.json',{'feature':NS+':singing_desert_ecology','placement':[{'type':'minecraft:biome'}]})
 write(f'data/{NS}/worldgen/configured_feature/singing_crystal_ore.json',{'type':'minecraft:ore','config':{'size':6,'discard_chance_on_air_exposure':.2,'targets':[{'target':{'predicate_type':'minecraft:tag_match','tag':'minecraft:stone_ore_replaceables'},'state':{'Name':NS+':singing_crystal_ore'}},{'target':{'predicate_type':'minecraft:block_match','block':NS+':ancient_desert_sandstone'},'state':{'Name':NS+':singing_crystal_ore'}}]}})
 write(f'data/{NS}/worldgen/placed_feature/singing_crystal_ore.json',{'feature':NS+':singing_crystal_ore','placement':[{'type':'minecraft:count','count':8},{'type':'minecraft:in_square'},{'type':'minecraft:height_range','height':{'type':'minecraft:uniform','min_inclusive':{'absolute':8},'max_inclusive':{'absolute':78}}},{'type':'minecraft:biome'}]})
 x2=json.loads(jar.read(f'assets/{NS}/content/x2.json'));profiles=list((ROOT.parent/'content-update/inputs/X2_The_Living_Dimension_Forge_1.20.1/Biome_Profiles').glob('*singing*'))
 if len(profiles)!=1:raise ValueError(profiles)
 d=json.loads(profiles[0].read_text());d['biomeRegistryName']=NS+':singing_glass_desert';x2['biomes'].append(d);write(f'assets/{NS}/content/x2.json',x2)
 write(f'data/{NS}/recipes/polish/smelt_golden_sand.json',{'type':'minecraft:smelting','ingredient':{'item':NS+':golden_glass_sand'},'result':'minecraft:glass','experience':.1,'cookingtime':200})

def main():
 assert hashlib.sha256(BASE.read_bytes()).hexdigest()=='f88cac49bae0d10961a277ab2277738892cb5ba4aaaae596fc5b0c17d64edb04'
 spec={'profiles':[],'blocks':[],'biome_blocks':[],'templates':{},'biome_structures':[],'decor_placements':[]}
 with ZipFile(BASE) as jar:
  langs={loc:json.loads(jar.read(f'assets/{NS}/lang/{loc}.json')) for loc in ['en_us','ar_sa']}
  shared=json.loads(jar.read(f'data/{NS}/worldgen/structure_set/reviewed_mythic_habitats.json'))
  creatures(jar,langs,spec,shared);decorations(jar,langs,spec);biome(jar,langs,spec,shared)
  write(f'data/{NS}/worldgen/structure_set/reviewed_mythic_habitats.json',shared)
  for loc,d in langs.items():write(f'assets/{NS}/lang/{loc}.json',d)
  # Standard game-asset sizing: square cells and mipmap-compatible dimensions.
  # The generated originals remain in art/ for review.
  raw=Image.open(ROOT/'art/eggs.png').convert('RGBA');atlas=Image.new('RGBA',(1024,256))
  for i in range(4):
   cell=raw.crop((round(i*raw.width/4),0,round((i+1)*raw.width/4),raw.height));cell.thumbnail((240,240),Image.Resampling.NEAREST)
   atlas.paste(cell,(i*256+(256-cell.width)//2,(256-cell.height)//2))
  buf=io.BytesIO();atlas.save(buf,format='PNG');write(f'assets/{NS}/textures/item/polish_eggs.png',buf.getvalue())
 write(f'assets/{NS}/content/polish.json',spec)
 (ROOT/'content-spec.json').write_text(json.dumps(spec,ensure_ascii=False,indent=2)+'\n')
 print('Prepared 3 legendary creatures, 20 Eclipse decorations, biome with 20 assets, 4 buildings, 4 trees and Prism Jerboa')
if __name__=='__main__':main()
