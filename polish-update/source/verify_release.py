import hashlib,json,sys
from pathlib import Path
from zipfile import ZipFile
from prepare_content import ROOT,OUT,BASE
def main():
 path=Path(sys.argv[1]);allowed={f'dev/shadowsovereign/{n}.class' for n in ['mythic/MythicProfiles','mythic/MythicExpedition','expansion/CreativeGroups','network/ShadowNetwork','client/DevourerModel','client/MythicGrimoireScreen','content/ContentBootstrap','content/ContentConfig']}
 resources={p.relative_to(OUT).as_posix():p.read_bytes() for p in OUT.rglob('*') if p.is_file()}
 with ZipFile(BASE) as old,ZipFile(path) as new:
  assert new.testzip() is None;original=set(old.namelist());final=set(new.namelist());assert len(final)==len(new.namelist()) and original<=final
  changed={n for n in original if old.read(n)!=new.read(n)};assert changed<=allowed|resources.keys(),changed-allowed-resources.keys()
  for n in ['META-INF/mods.toml','assets/minecraft/atlases/blocks.json','dev/shadowsovereign/expansion/OpenMythics.class','dev/shadowsovereign/expansion/X1Hooks.class','dev/shadowsovereign/gloom/GloomContent.class']:
   assert old.read(n)==new.read(n),n
  for n in original:
   if '/advancements/learning/' in n or '/textures/x1/' in n:assert old.read(n)==new.read(n),n
  for n,data in resources.items():assert new.read(n)==data,n
  for n in final-original:assert n in resources or n.startswith('dev/shadowsovereign/content/'),n
  assert not any(n.startswith(('net/','dev/soulboundqa/')) for n in final)
 report={'baseline_sha256':hashlib.sha256(BASE.read_bytes()).hexdigest(),'output_sha256':hashlib.sha256(path.read_bytes()).hexdigest(),'output_bytes':path.stat().st_size,'modified':sorted(changed),'added':sorted(final-original),'removed':[],'all_undeclared_entries_byte_identical':True}
 (path.parent/'CONTENT-MANIFEST.json').write_text(json.dumps(report,indent=2)+'\n');print('PASS: final CRC, fingerprint, reviewed fixes and unchanged entries')
if __name__=='__main__':main()
