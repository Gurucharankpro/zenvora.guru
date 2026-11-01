"""Overlay focused reviewed fixes onto the hash-pinned latest binary; never rebuild old mod sources."""
from pathlib import Path
import zipfile,json,hashlib,re
R=Path(__file__).resolve().parents[1];original=R/'original-1.8.1.jar';assert hashlib.sha256(original.read_bytes()).hexdigest()=='beeedcd097ebbfc8dc212f34b52d6fd3f6d09504aacced44bfd249aa7a16cf3d'
changes={};removed=set()
with zipfile.ZipFile(original) as z:
 for name in z.namelist():
  if name.startswith('data/shadowsovereign/advancements/learning/') and name.endswith('.json'):
   d=json.loads(z.read(name));dirty=False
   for criterion in d.get('criteria',{}).values():
    if criterion.get('trigger')=='minecraft:placed_block':
     c=criterion['conditions'];block=c.pop('block');location=c.pop('location');assert isinstance(location,dict)
     c['location']=[{'condition':'minecraft:block_state_property','block':block},{'condition':'minecraft:location_check','predicate':location}];dirty=True
   if dirty:changes[name]=(json.dumps(d,ensure_ascii=False,indent=2)+'\n').encode()
 assert len(changes)==160
 frames=[n[len('assets/shadowsovereign/textures/'):-4] for n in z.namelist() if n.startswith('assets/shadowsovereign/textures/x1/') and n.endswith('.png')]
 assert len(frames)==444
 changes['assets/minecraft/atlases/blocks.json']=(json.dumps({'sources':[{'type':'minecraft:single','resource':'shadowsovereign:'+n} for n in sorted(frames)]},indent=2)+'\n').encode()
 sites=json.loads(z.read('data/shadowsovereign/mythic_sites.json'));names={'shadowsovereign:'+s['structure'] for s in sites};assert len(names)==52
 for name in z.namelist():
  if name.startswith('data/shadowsovereign/worldgen/structure_set/') and name.endswith('.json'):
   d=json.loads(z.read(name));ids={v['structure'] for v in d['structures']}
   if ids&names:
    assert ids<=names,(name,ids-names);removed.add(name)
 changes['data/shadowsovereign/worldgen/structure_set/reviewed_mythic_habitats.json']=(json.dumps({'structures':[{'structure':n,'weight':1} for n in sorted(names)],'placement':{'type':'minecraft:random_spread','spacing':48,'separation':32,'salt':181030}},indent=2)+'\n').encode()
 for lang,line in [('en_us','Encounter ended. This guardian returns at full health.'),('ar_sa','انتهت المواجهة. سيعود الحارس بكامل صحته عند رجوعك.')]:
  name=f'assets/shadowsovereign/lang/{lang}.json';d=json.loads(z.read(name));d['review.shadowsovereign.retreat']=line;changes[name]=(json.dumps(d,ensure_ascii=False,indent=2)+'\n').encode()
 name='META-INF/mods.toml';s=z.read(name).decode().replace('version="1.8.1-shadow-awakening-fix"','version="1.8.1-reviewed"');changes[name]=s.encode()
 with zipfile.ZipFile(R/'qa/build/libs/soulbound-181-qa-1.0-repair.jar') as patch:
  for n in patch.namelist():
   if n.endswith('.class'):assert n.startswith('dev/shadowsovereign/expansion/');changes[n]=patch.read(n)
 out=R/'reviewed-1.8.1.jar'
 with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED,compresslevel=6) as dst:
  for n in z.namelist():
   if n not in removed and n not in changes:dst.writestr(n,z.read(n))
  for n,data in changes.items():dst.writestr(n,data)
with zipfile.ZipFile(out) as z:assert z.testzip() is None
report={'baseline_sha256':hashlib.sha256(original.read_bytes()).hexdigest(),'output_sha256':hashlib.sha256(out.read_bytes()).hexdigest(),'output_bytes':out.stat().st_size,'changed':sorted(changes),'removed_independent_legendary_sets':sorted(removed),'all_other_entries_byte_identical':True}
(R/'repairs/overlay-manifest.json').write_text(json.dumps(report,indent=2)+'\n');print('Overlay built:',out,report['output_bytes'],report['output_sha256'])
