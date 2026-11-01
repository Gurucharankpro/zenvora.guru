"""Focused resource regression checks, explicitly not Minecraft parser tests."""
import importlib.util
import json
from pathlib import Path
import tempfile
import unittest
from zipfile import ZipFile

REPO=Path('/workspace/zenvora.guru/integration')
BASE=Path('/workspace/soulbound-current/soulbound-army-1.20.1-1.8.1-reviewed.jar')
HISTORICAL_INPUT=Path('/workspace/mod-integration/resources.6eniMZ')
X1_INPUT=Path('/workspace/mod-integration/inputs')

def module(name):
 spec=importlib.util.spec_from_file_location(name,REPO/(name+'.py'))
 result=importlib.util.module_from_spec(spec);spec.loader.exec_module(result);return result

class GeneratorRepairs(unittest.TestCase):
 def test_500_generated_lessons_equal_reviewed_engine_fixed_resources(self):
  generator=module('learning_quests')
  with tempfile.TemporaryDirectory() as directory,ZipFile(BASE) as jar:
   out=Path(directory)
   for name in ['integration-spec.json','assets/shadowsovereign/lang/en_us.json','assets/shadowsovereign/lang/ar_sa.json']:
    target=out/name;target.parent.mkdir(parents=True,exist_ok=True);target.write_bytes((HISTORICAL_INPUT/name).read_bytes())
   generator.build(out)
   generated=list((out/'data/shadowsovereign/advancements/learning').glob('*.json'))
   self.assertEqual(len(generated),500)
   for p in generated:
    # Equal IDs, rewards, display, block predicates AND dimension/biome predicates.
    self.assertEqual(json.loads(p.read_text()),json.loads(jar.read(p.relative_to(out).as_posix())),p.name)

 def test_x1_generator_explicitly_atlases_all_444_frames_and_keeps_foreign_sources(self):
  generator=module('x1_assets')
  with tempfile.TemporaryDirectory() as directory,ZipFile(BASE) as jar:
   out=Path(directory)
   for name,data in [('integration-spec.json',b'{}'),('assets/shadowsovereign/lang/en_us.json',b'{}'),('assets/shadowsovereign/lang/ar_sa.json',b'{}')]:
    p=out/name;p.parent.mkdir(parents=True,exist_ok=True);p.write_bytes(data)
   atlas_path=out/'assets/minecraft/atlases/blocks.json';atlas_path.parent.mkdir(parents=True,exist_ok=True)
   foreign={'type':'minecraft:directory','source':'preserved','prefix':'preserved/'}
   atlas_path.write_text(json.dumps({'sources':[foreign]}))
   generator.build(X1_INPUT,out)
   atlas=json.loads(atlas_path.read_text())['sources']
   expected=json.loads(jar.read('assets/minecraft/atlases/blocks.json'))['sources']
   self.assertEqual(len(expected),444)
   self.assertEqual(atlas,[foreign,*expected])
   for s in expected:
    namespace,path=s['resource'].split(':')
    name=f'assets/{namespace}/textures/{path}.png'
    self.assertEqual((out/name).read_bytes(),jar.read(name),name)
   generator.merge_frame_atlas(out,[s['resource'] for s in expected])
   self.assertEqual(json.loads(atlas_path.read_text())['sources'],atlas)

if __name__=='__main__':unittest.main(verbosity=2)
