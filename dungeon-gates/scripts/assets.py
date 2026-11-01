"""Reviewed, deterministic asset import. Never executes scripts from the uploaded packs."""
from pathlib import Path
import sys,json,shutil,io,gzip,hashlib,math
sys.path.insert(0,str(Path(__file__).resolve().parents[1]/'tools/python'))
import nbtlib as n
from PIL import Image
R=Path(__file__).resolve().parents[1];O=R/'src/main/resources';A=O/'assets/dungeongates';D=O/'data/dungeongates'
def dump(p,d):p.parent.mkdir(parents=True,exist_ok=True);p.write_text(json.dumps(d,ensure_ascii=False,indent=2)+'\n')
def remap(v):
 if isinstance(v,str):return v.replace('soulbound_decor:','dungeongates:').replace('soulbound:','dungeongates:')
 if isinstance(v,list):return [remap(x) for x in v]
 if isinstance(v,dict):return {k:remap(x) for k,x in v.items()}
 return v
def copytree(src,dst):
 for p in src.rglob('*'):
  if not p.is_file():continue
  q=dst/p.relative_to(src);q.parent.mkdir(parents=True,exist_ok=True)
  if p.suffix=='.json':dump(q,remap(json.loads(p.read_text())))
  else:shutil.copyfile(p,q)
copytree(R/'input/emerald/Blocks_And_Decor/assets/soulbound_decor',A)
for folder in ['Mobs/MossboundSkulker','Mobs/CrystalSentinel','Boss/EmeraldRootWarden','Trophy']:
 copytree(R/'input/emerald'/folder/'assets/soulbound',A)
copytree(R/'input/frozen/Resources/assets/soulbound',A)
copytree(R/'input/frozen/Resources/data/soulbound',D)
# Retain all authored portal geo/clips and effects; render strip frames explicitly.
copytree(R/'input/emerald/Portal/assets/soulbound',A)
portal=A/'textures/gate';portal.mkdir(parents=True,exist_ok=True)
for p in (R/'input/emerald/Portal/assets/soulbound/textures/entity/portal').glob('*.png'):
 shutil.copyfile(p,portal/('emerald_'+p.name))
for p in (R/'input/frozen/Portal').rglob('*.png'):
 if 'preview' not in p.name.lower() and 'MOCKUP' not in p.name:shutil.copyfile(p,portal/('frozen_'+p.name))
# Geo glow masks use GeckoLib's documented _glowing suffix.
for p in (A/'textures/entity').glob('*_glowmask.png'):shutil.copyfile(p,p.with_name(p.name.replace('_glowmask','_glowing')))
# GeckoLib expected normalized resource paths for Emerald.
for id in ['mossbound_skulker','crystal_sentinel','emerald_root_warden']:
 for folder,ext in [('geo','.geo.json'),('animations','.animation.json')]:
  p=A/folder/(id+ext);q=A/folder/'entity'/(id+ext);q.parent.mkdir(exist_ok=True);shutil.copyfile(p,q)
# Preserve exact arbitrary-angle faces via registered Forge geometry loader (no rounding).
for p in [A/'models/block/frozen_crystal_cluster.json',A/'models/item/frozen_kings_crown.json']:
 d=json.loads(p.read_text());d['loader']='dungeongates:authored_mesh';dump(p,d)
# Fix authored rectangular strips with explicit width/height for atlas-based consumers.
for p in (A/'textures').rglob('*.png.mcmeta'):
 d=json.loads(p.read_text());im=Image.open(str(p)[:-7]);w,h=im.size
 if p.name.startswith(('portal_surface','portal_border','portal_distortion','portal_entry_flash')) and w==96:fh=144
 elif 'runes' in p.name and w==128:fh=16
 elif 'energy_strand' in p.name and w==8:fh=16
 else:continue
 d['animation'].update(width=w,height=fh);dump(p,d)
# Invisible tick anchor: only the floating energy and label renderer are visible.
for id in ['emerald_trial_gate','frozen_grave_gate']:
 dump(A/'blockstates'/f'{id}.json',{'variants':{'':{'model':'minecraft:block/air'}}})
 tex='dungeongates:item/frozen_grave_gate' if id.startswith('frozen') else 'dungeongates:item/emerald_gate_icon'
 if id.startswith('emerald'):
  im=Image.open(portal/'emerald_portal_surface_anim.png');im.crop((0,0,96,144)).save(A/'textures/item/emerald_gate_icon.png')
 dump(A/'models/item'/f'{id}.json',{'parent':'minecraft:item/generated','textures':{'layer0':tex}})
# All six supplied mobs have distinct eggs and the actual provided textures remain unchanged.
ids=['mossbound_skulker','crystal_sentinel','emerald_root_warden','frostgrave_prowler','chainbound_sentinel','frozen_gravekeeper']
for id in ids:dump(A/'models/item'/f'{id}_spawn_egg.json',{'parent':'minecraft:item/template_spawn_egg'})
# Simple block drops; custom crates use actual inventories, separate from their supplied static meshes.
for p in (A/'blockstates').glob('*.json'):
 if p.stem.endswith('_gate'):continue
 dump(D/'loot_tables/blocks'/f'{p.stem}.json',{'type':'minecraft:block','pools':[{'rolls':1,'entries':[{'type':'minecraft:item','name':'dungeongates:'+p.stem}],'conditions':[{'condition':'minecraft:survives_explosion'}]}]})
# Adapt actual Emerald template, retain every room, replace floor data markers with support.
src=R/'input/emerald/Dungeon/structures/p1_1_emerald_sanctuary_FULL_64x28x64.nbt';nb=n.load(src)
for s in nb['palette']:s['Name']=n.String(remap(str(s['Name'])))
nb['palette'].append(n.Compound({'Name':n.String('dungeongates:polished_sanctuary_stone')}));floor=len(nb['palette'])-1
for b in nb['blocks']:
 state=nb['palette'][int(b['state'])]
 if str(state['Name'])=='minecraft:structure_block':b['state']=n.Int(floor);b.pop('nbt',None)
 if str(state['Name']).endswith('ancient_emerald_chest'):
  b['nbt']=n.Compound({'id':n.String('dungeongates:treasure_crate'),'LootTable':n.String('minecraft:chests/simple_dungeon')})
# Clear a misplaced authored crystal at the boss entrance; retain the rest of the template.
air=next(i for i,s in enumerate(nb['palette']) if str(s['Name'])=='minecraft:air')
for b in nb['blocks']:
 if list(map(int,b['pos']))==[48,3,21]:b['state']=n.Int(air)
(D/'structures').mkdir(parents=True,exist_ok=True);nb.save(D/'structures/emerald_sanctuary.nbt',gzipped=True)
# Frozen pack contains no NBT. Author a connected royal crypt using ALL supplied blocks.
size=(64,28,64);pal=[];lookup={};cells={}
def state(name,props=None):
 key=(name,tuple(sorted((props or {}).items())))
 if key not in lookup:
  s=n.Compound({'Name':n.String(name)})
  if props:s['Properties']=n.Compound({k:n.String(v) for k,v in props.items()})
  lookup[key]=len(pal);pal.append(s)
 return lookup[key]
def put(x,y,z,name,props=None):cells[x,y,z]=state(name,props)
for x in range(64):
 for y in range(28):
  for z in range(64):put(x,y,z,'minecraft:bedrock' if x in [0,63] or y in [0,27] or z in [0,63] else 'dungeongates:frostgrave_stone')
def room(x1,z1,x2,z2,height=10):
 for x in range(x1,x2+1):
  for z in range(z1,z2+1):
   put(x,2,z,'dungeongates:royal_frost_marble' if (x+z)%3 else 'dungeongates:polished_frostgrave_stone')
   for y in range(3,height+3):put(x,y,z,'minecraft:air')
   put(x,height+3,z,'dungeongates:cracked_icebound_bricks')
rooms=[(4,45,19,59,9),(4,34,15,46,6),(7,23,32,35,11),(35,27,58,43,12),(35,5,58,24,18),(6,5,24,17,10)]
for args in rooms:room(*args)
for args in [(12,32,20,40,5),(28,28,39,32,6),(45,21,49,30,7),(21,11,38,15,6)]:room(*args)
for x,z in [(7,48),(17,56),(10,26),(29,32),(38,30),(55,40),(38,8),(55,21),(8,8),(22,15)]:
 for y in range(3,7):put(x,y,z,'dungeongates:silver_chain_column')
 put(x,7,z,'dungeongates:royal_ice_lantern',{'hanging':'false'})
for x,z in [(8,50),(18,54),(12,28),(28,25),(39,36),(53,32),(40,18),(54,10),(10,14)]:
 put(x,3,z,'dungeongates:frozen_crystal_cluster',{'facing':'up'});put(x,2,z,'dungeongates:glowing_blue_ice_crystal')
for x,z in [(8,47),(18,58),(13,25),(30,33),(37,28),(56,42),(36,6),(57,23)]:put(x,3,z,'dungeongates:ghostfrost_flower')
for x,z in [(7,46),(18,46),(8,24),(31,24),(36,28),(57,28),(36,6),(57,6)]:put(x,3,z,'dungeongates:frozen_memorial_statue',{'facing':'south'})
for x,y,z in [(4,11,50),(7,13,29),(35,14,32),(35,20,15)]:put(x,y,z,'dungeongates:winterveil_vines',{'up':'true','north':'false','east':'false','south':'false','west':'true'})
chest=(9,3,8);put(*chest,'dungeongates:winter_royal_chest',{'facing':'east'})
blocks=[]
for p,s in cells.items():
 b=n.Compound({'pos':n.List[n.Int](p),'state':n.Int(s)})
 if p==chest:b['nbt']=n.Compound({'id':n.String('dungeongates:treasure_crate'),'LootTable':n.String('dungeongates:chests/winter_vault')})
 blocks.append(b)
nb=n.File({'DataVersion':n.Int(3465),'size':n.List[n.Int](size),'palette':n.List[n.Compound](pal),'blocks':n.List[n.Compound](blocks),'entities':n.List[n.Compound]([])})
nb.save(D/'structures/frozen_crypt.nbt',gzipped=True)
# Shared isolated void dimension, not a dimension per gate.
dump(D/'dimension_type/dungeons.json',{'ultrawarm':False,'natural':False,'piglin_safe':False,'respawn_anchor_works':False,'bed_works':False,'has_raids':False,'has_skylight':False,'has_ceiling':False,'coordinate_scale':1,'ambient_light':.18,'logical_height':256,'min_y':0,'height':256,'infiniburn':'#minecraft:infiniburn_overworld','effects':'minecraft:the_end','monster_spawn_block_light_limit':0,'monster_spawn_light_level':0,'fixed_time':18000})
dump(D/'dimension/dungeons.json',{'type':'dungeongates:dungeons','generator':{'type':'minecraft:flat','settings':{'biome':'minecraft:the_void','lakes':False,'features':False,'layers':[],'structure_overrides':[]}}})
dump(D/'worldgen/configured_feature/floating_gates.json',{'type':'dungeongates:floating_gates','config':{}})
dump(D/'worldgen/placed_feature/floating_gates.json',{'feature':'dungeongates:floating_gates','placement':[{'type':'minecraft:biome'}]})
dump(D/'forge/biome_modifier/floating_gates.json',{'type':'forge:add_features','biomes':'#minecraft:is_overworld','features':'dungeongates:floating_gates','step':'top_layer_modification'})
# Tags keep normal pickaxe mining / explicit no-tool decorative plants.
solid=[p.stem for p in (A/'blockstates').glob('*.json') if not p.stem.endswith('_gate')]
dump(O/'data/minecraft/tags/blocks/mineable/pickaxe.json',{'replace':False,'values':['dungeongates:'+x for x in solid]})
lang={}
for id in solid:lang['block.dungeongates.'+id]=id.replace('_',' ').title()
for id in ids:lang['entity.dungeongates.'+id]=id.replace('_',' ').title();lang['item.dungeongates.'+id+'_spawn_egg']=id.replace('_',' ').title()+' Spawn Egg'
lang.update({'itemGroup.dungeongates':'Dungeon Gates','item.dungeongates.emerald_wardens_crown':'Emerald Warden\'s Crown','item.dungeongates.frozen_kings_crown':'Frozen King\'s Crown','block.dungeongates.emerald_trial_gate':'Emerald Trial Gate','block.dungeongates.frozen_grave_gate':'Frozen Grave Gate','dg.locked':'Defeat this dungeon\'s guardian to unlock the return gate.','dg.reward_locked':'The guardian seals this treasure.','dg.cleared':'Dungeon cleared! Your trophy is ready at the return gate.','dg.level':'LEVEL %s','dg.gate':'GATE %s/5','dg.category':'CATEGORY: %s','dg.name.emerald_trial':'EMERALD TRIAL','dg.name.frozen_grave':'FROZEN GRAVE'})
dump(A/'lang/en_us.json',lang)
ar=dict(lang);ar.update({'itemGroup.dungeongates':'بوابات الدنجن','dg.locked':'اهزم حارس الدنجن لفتح بوابة العودة.','dg.reward_locked':'الحارس يغلق هذا الكنز.','dg.cleared':'اكتمل الدنجن! جائزتك عند بوابة العودة.','dg.level':'المستوى %s','dg.gate':'البوابة %s/5','dg.category':'الفئة: %s','dg.name.emerald_trial':'اختبار الزمرد','dg.name.frozen_grave':'القبر المتجمد'})
for k,v in [('mossbound_skulker','المتربص الطحلبي'),('crystal_sentinel','الحارس البلوري'),('emerald_root_warden','حارس الجذور الزمردي'),('frostgrave_prowler','متربص القبر المتجمد'),('chainbound_sentinel','حارس السلاسل'),('frozen_gravekeeper','حارس القبر المتجمد')]:ar['entity.dungeongates.'+k]=v
ar['block.dungeongates.emerald_trial_gate']='بوابة اختبار الزمرد';ar['block.dungeongates.frozen_grave_gate']='بوابة القبر المتجمد'
dump(A/'lang/ar_sa.json',ar)
# Metadata permits future gate packages without reserving unusable registrations.
dump(R/'reports/ASSET-AUDIT.json',{'active_gates':2,'future_gates_not_installed':['P1-3','P1-4','P1-5','P2-1'],'supplied_creatures':ids,'supplied_blocks':24,'emerald_dungeon':'supplied NBT, marker/namespace/chest adaptations','frozen_dungeon':'new connected crypt authored from actual supplied blocks; ZIP had no NBT','fixed':['both legacy namespaces','rectangular animated strip dimensions','frozen arbitrary-angle mesh loader','frameless surface replaces supplied solid arch','dynamic CATEGORY labels replace baked difficulty','boss entrance crystal clearance','functional inventories replace static chest models']})
print('Imported all supplied runtime assets; two 64x28x64 playable dungeons')
particle_source={'emerald_spark':portal/'emerald_particle_emerald_spark.png','green_mist':portal/'emerald_particle_green_mist.png','energy_strand':portal/'emerald_particle_energy_strand.png','activation_burst':R/'input/frozen/Portal/Effects/portal_activation_burst_strip.png','entry_vortex':R/'input/frozen/Portal/Effects/portal_entry_vortex_strip.png'}
for id,src in particle_source.items():
 target=A/'textures/particle'/f'{id}.png';target.parent.mkdir(parents=True,exist_ok=True);shutil.copyfile(src,target)
 im=Image.open(src);w,h=im.size;fh=16 if id=='energy_strand' else w
 dump(Path(str(target)+'.mcmeta'),{'animation':{'frametime':2,'width':w,'height':fh,'interpolate':True}})
 dump(A/'particles'/f'{id}.json',{'textures':['dungeongates:'+id]})
