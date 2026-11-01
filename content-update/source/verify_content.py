"""Focused asset/overlay checks. Does not claim engine codec/render execution."""
import gzip
import hashlib
import io
import json
import math
from pathlib import Path
import sys
import unittest
from zipfile import ZipFile
sys.path.insert(0,'/workspace/mod-integration/tools/python')
import nbtlib
from PIL import Image
from prepare_content import ROOT,OUT,BASE,BASE_SHA,PROFILES,NS,model_bounds

class ContentResources(unittest.TestCase):
 @classmethod
 def setUpClass(cls):
  cls.base=ZipFile(BASE);cls.spec=json.loads((ROOT/'content-spec.json').read_text())
 @classmethod
 def tearDownClass(cls):cls.base.close()
 def read(self,name):return (OUT/name).read_bytes() if (OUT/name).exists() else self.base.read(name)
 def template(self,name):return nbtlib.File.parse(io.BytesIO(gzip.decompress(self.read(name))))
 def test_reviewed_baseline_and_critical_fixes_are_preserved(self):
  self.assertEqual(hashlib.sha256(BASE.read_bytes()).hexdigest(),BASE_SHA)
  forbidden=['META-INF/mods.toml','assets/minecraft/atlases/blocks.json','dev/shadowsovereign/expansion/OpenMythics.class','dev/shadowsovereign/expansion/X1Hooks.class']
  for name in forbidden:self.assertFalse((OUT/name).exists(),name)
  self.assertFalse((OUT/'data/shadowsovereign/advancements/learning').exists())
  before=json.loads(self.base.read('assets/shadowsovereign/sounds.json'));after=json.loads(self.read('assets/shadowsovereign/sounds.json'))
  for key in before:self.assertEqual(before[key],after[key])
 def test_original_bones_cubes_and_all_authored_clips_retained(self):
  for p in PROFILES:
   source=ROOT/'inputs'/p['source'];geo=json.loads(next(source.rglob(p['stem']+'.geo.json')).read_text())['minecraft:geometry'][0]
   supplied=json.loads(next(source.rglob(p['stem']+'.animation.json')).read_text())['animations']
   model=json.loads(self.read(f'assets/{NS}/mythic/{p["id"]}.json'))
   self.assertEqual([b['name'] for b in geo['bones']],[b['name'] for b in model['bones']])
   self.assertEqual(sum(len(b.get('cubes',[])) for b in geo['bones']),sum(len(b['cubes']) for b in model['bones']))
   for name,clip in supplied.items():
    converted=model['animations'][name.split('.')[-1]];self.assertEqual(converted['length'],clip['animation_length'])
    for track in converted['tracks']:
     self.assertEqual(len({k[0] for k in track['keys']}),len(track['keys']))
     self.assertEqual(sorted(track['keys']),track['keys'])
     self.assertTrue(all(math.isfinite(v) for k in track['keys'] for v in k))
 def test_yuta_two_texture_regions_preserve_original_pixels(self):
  source=ROOT/'inputs/Mob_61_Yuta_Okkotsu_Complete'
  for suffix,first,second in [('.png','Yuta_Okkotsu_Texture.png','Yuta_Okkotsu_Rika_Texture.png'),('_glow.png','Yuta_Okkotsu_Texture_glowmask.png','Yuta_Okkotsu_Rika_Texture_glowmask.png')]:
   atlas=Image.open(io.BytesIO(self.read(f'assets/{NS}/textures/entity/yuta_okkotsu{suffix}'))).convert('RGBA')
   self.assertEqual(atlas.crop((0,0,128,128)).tobytes(),Image.open(source/first).convert('RGBA').tobytes())
   self.assertEqual(atlas.crop((128,0,256,128)).tobytes(),Image.open(source/second).convert('RGBA').tobytes())
  model=json.loads(self.read(f'assets/{NS}/mythic/yuta_okkotsu.json'))
  for bone in model['bones']:
   for c in bone['cubes']:
    for uv in c['faces'].values():
     if bone['name'].startswith(('rika_','fx_')):self.assertTrue(min(uv[0],uv[2])>=128)
     else:self.assertTrue(max(uv[0],uv[2])<=128)
 def test_new_templates_1201_and_actual_guardian_clearance(self):
  newer={'minecraft:vault','minecraft:trial_spawner','minecraft:copper_bulb','minecraft:tuff_bricks','minecraft:heavy_core','minecraft:short_grass'}
  for p in PROFILES:
   n=self.template(self.spec['templates'][str(p['index'])]['path']);self.assertEqual(int(n['DataVersion']),3465)
   self.assertFalse(newer&{str(s['Name']) for s in n['palette']})
   bypos={tuple(map(int,b['pos'])):b for b in n['blocks']}
   x,y,z=p['altar'];width=p['width'];height=p['height']
   for X in range(math.floor(x+.5-width/2),math.ceil(x+.5+width/2)):
    for Z in range(math.floor(z+.5-width/2),math.ceil(z+.5+width/2)):
     for Y in range(y,math.ceil(y+height)):
      block=bypos.get((X,Y,Z));self.assertIsNotNone(block,(p['id'],X,Y,Z))
      state=str(n['palette'][int(block['state'])]['Name']);self.assertIn(state,{'minecraft:air','minecraft:cave_air'},(p['id'],X,Y,Z,state))
     floor=bypos.get((X,y-1,Z));self.assertIsNotNone(floor)
     self.assertNotIn(str(n['palette'][int(floor['state'])]['Name']),{'minecraft:air','minecraft:cave_air','minecraft:water','minecraft:lava'})
 def test_shared_habitats_append_without_altering_old_sites(self):
  before=json.loads(self.base.read(f'data/{NS}/mythic_sites.json'));after=json.loads(self.read(f'data/{NS}/mythic_sites.json'))
  self.assertEqual(after[:len(before)],before);self.assertEqual(len(after),55)
  names=[s['structure'] for s in after];self.assertEqual(len(set(names)),55)
  shared=json.loads(self.read(f'data/{NS}/worldgen/structure_set/reviewed_mythic_habitats.json'))
  self.assertEqual({s['structure'] for s in shared['structures']},{NS+':'+n for n in names})
  original=json.loads(self.base.read(f'data/{NS}/worldgen/structure_set/reviewed_mythic_habitats.json'))
  self.assertEqual(shared['placement'],original['placement'])
  max_span=0
  for name in names:
   structure=json.loads(self.read(f'data/{NS}/worldgen/structure/{name}.json'));pool=json.loads(self.read('data/'+structure['start_pool'].replace(':','/worldgen/template_pool/')+'.json'))
   for entry in pool['elements']:
    location=entry['element']['location'];n=self.template('data/'+location.replace(':','/structures/')+'.nbt');max_span=max(max_span,int(n['size'][0]),int(n['size'][2]))
  min_axis=(shared['placement']['separation']+1)*16
  self.assertLess(max_span+16,min_axis)
 def test_all_60_decor_models_recipes_and_real_placements(self):
  entries=self.spec['blocks'];placements=self.spec['decor_placements']
  self.assertEqual(len(entries),60);self.assertEqual(len(placements),60)
  self.assertEqual({e['id'] for e in entries},{p['id'] for p in placements})
  recipes=set()
  for b in entries:
   d=json.loads(self.read(f'assets/{NS}/models/block/{b["id"]}.json'))
   for e in d['elements']:
    self.assertTrue(all(-16<=v<=32 for v in e['from']+e['to']))
   recipe=json.loads(self.read(f'data/{NS}/recipes/content/{b["id"]}.json'));signature=json.dumps([recipe['pattern'],recipe['key']],sort_keys=True)
   self.assertNotIn(signature,recipes);recipes.add(signature)
   unlock=json.loads(self.read(f'data/{NS}/advancements/recipes/content/{b["id"]}.json'))
   self.assertEqual(unlock['rewards']['recipes'],[NS+':content/'+b['id']])
   self.assertEqual(unlock['criteria']['materials']['trigger'],'minecraft:inventory_changed')
  reserved={}
  for p in placements:
   n=self.template(p['template']);bypos={tuple(map(int,b['pos'])):b for b in n['blocks']};block=bypos[tuple(p['position'])]
   self.assertEqual(str(n['palette'][int(block['state'])]['Name']),NS+':'+p['id'])
   occupied={tuple(v) for v in p['reserved_cells']};prior=reserved.setdefault(p['template'],set());self.assertFalse(prior&occupied);prior.update(occupied)
 def test_old_template_solids_and_loot_not_erased(self):
  paths={p['template'] for p in self.spec['decor_placements']}
  for path in paths:
   if path not in self.base.namelist():continue
   old=nbtlib.File.parse(io.BytesIO(gzip.decompress(self.base.read(path))));new=self.template(path)
   bypos={tuple(map(int,b['pos'])):b for b in new['blocks']}
   self.assertEqual(list(old['size']),list(new['size']))
   for b in old['blocks']:
    state=old['palette'][int(b['state'])]
    if str(state['Name']) in {'minecraft:air','minecraft:cave_air','minecraft:void_air'}:continue
    other=bypos[tuple(map(int,b['pos']))]
    self.assertEqual(state.snbt(),new['palette'][int(other['state'])].snbt(),path)
    if 'nbt' in b:self.assertEqual(b['nbt'].snbt(),other['nbt'].snbt(),path)
 def test_x2_sprites_and_only_existing_biome_bindings(self):
  config=json.loads(self.read(f'assets/{NS}/content/x2.json'));self.assertEqual(len(config['effects']),20)
  images=set()
  for effect in config['effects']:
   name=effect['id'];definition=json.loads(self.read(f'assets/{NS}/particles/{name}.json'))
   self.assertEqual(len(definition['textures']),effect['texture']['frameCount'])
   for sprite in definition['textures']:
    namespace,path=sprite.split(':');name=f'assets/{namespace}/textures/particle/{path}.png';self.assertTrue((OUT/name).is_file());images.add(name)
  self.assertEqual(len(images),182)
  for biome in config['biomes']:
   namespace,name=biome['biomeRegistryName'].split(':');self.assertIn(f'data/{namespace}/worldgen/biome/{name}.json',self.base.namelist())
  self.assertEqual(len(config['biomes']),5)

if __name__=='__main__':unittest.main(verbosity=2)
