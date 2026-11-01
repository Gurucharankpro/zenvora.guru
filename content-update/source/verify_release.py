"""Verify a built distribution against the reviewed input and explicit overlay.
Does not certify rendering, codecs or multiplayer; native compilation is required.
"""
import hashlib
import json
from pathlib import Path
import sys
from zipfile import ZipFile
from prepare_content import BASE,BASE_SHA,OUT,ROOT,NS

def verify(path):
 assert hashlib.sha256(BASE.read_bytes()).hexdigest()==BASE_SHA
 allowed={f'dev/shadowsovereign/{name}.class' for name in [
  'mythic/MythicProfiles','mythic/MythicExpedition','gloom/GloomContent',
  'expansion/CreativeGroups','ShadowSovereign','army/ShadowGoal',
  'network/ShadowNetwork','client/DevourerModel']}
 resources={p.relative_to(OUT).as_posix():p.read_bytes() for p in OUT.rglob('*') if p.is_file()}
 with ZipFile(BASE) as before,ZipFile(path) as after:
  assert after.testzip() is None
  old=set(before.namelist());new=set(after.namelist())
  assert len(new)==len(after.namelist()),'Duplicate entries'
  assert old<=new,'An original entry was removed'
  changed={name for name in old if before.read(name)!=after.read(name)}
  assert changed<=allowed|set(resources),changed-allowed-set(resources)
  assert changed&{n for n in old if n.endswith('.class')}==allowed
  for name in ['META-INF/mods.toml','assets/minecraft/atlases/blocks.json','dev/shadowsovereign/expansion/OpenMythics.class','dev/shadowsovereign/expansion/X1Hooks.class']:
   assert before.read(name)==after.read(name),'Protected fix/version changed: '+name
  for name in old:
   if name.startswith('data/shadowsovereign/advancements/learning/') or name.startswith('assets/shadowsovereign/textures/x1/'):
    assert before.read(name)==after.read(name),name
  for name,data in resources.items():assert after.read(name)==data,'Resource lost or replaced: '+name
  added=new-old
  for name in added:
   assert name in resources or name.startswith('dev/shadowsovereign/content/'),name
  assert not any(name.startswith(('net/','dev/soulboundqa/')) for name in new),'Fixture or QA leak'
  report={'baseline_sha256':BASE_SHA,'output_sha256':hashlib.sha256(path.read_bytes()).hexdigest(),'output_bytes':path.stat().st_size,'modified':sorted(changed),'added':sorted(added),'removed':[],'untouched_entries_byte_identical':True,'runtime_verified':False}
 (path.parent/'CONTENT-MANIFEST.json').write_text(json.dumps(report,indent=2)+'\n')
 print('PASS: distribution CRC, explicit changes, untouched resources, protected reviewed fixes and artifact hash')

if __name__=='__main__':verify(Path(sys.argv[1]))
