from pathlib import Path
import zipfile,json,hashlib,subprocess,os,shutil
R=Path(__file__).resolve().parents[1];manifest=json.loads((R/'repairs/overlay-manifest.json').read_text());changed=set(manifest['changed']);removed=set(manifest['removed_independent_legendary_sets'])
with zipfile.ZipFile(R/'original-1.8.1.jar') as original,zipfile.ZipFile(R/'reviewed-1.8.1.jar')as fixed:
 assert fixed.testzip()is None;before=set(original.namelist());after=set(fixed.namelist());assert after==(before-removed)|changed
 for n in before-changed-removed:assert original.read(n)==fixed.read(n),n
 assert not any(n.startswith('dev/soulboundqa/') for n in after)
 for n in removed:assert n not in after
 atlas=json.loads(fixed.read('assets/minecraft/atlases/blocks.json'))['sources'];assert len(atlas)==444
 for frame in atlas:
  assert frame['type']=='minecraft:single';ns,path=frame['resource'].split(':');assert f'assets/{ns}/textures/{path}.png' in after
 learning=[n for n in after if n.startswith('data/shadowsovereign/advancements/learning/')and n.endswith('.json')];assert len(learning)==500
 corrected=0
 for n in learning:
  d=json.loads(fixed.read(n))
  for c in d['criteria'].values():
   if c['trigger']=='minecraft:placed_block':
    predicate=c['conditions']['location'];assert isinstance(predicate,list)and len(predicate)==2;assert predicate[0]['condition']=='minecraft:block_state_property';corrected+=1
 assert corrected==160
 sites=json.loads(fixed.read('data/shadowsovereign/mythic_sites.json'));names={'shadowsovereign:'+s['structure']for s in sites};sets=[json.loads(fixed.read(n))for n in after if n.startswith('data/shadowsovereign/worldgen/structure_set/') and n.endswith('.json')]
 assert len(names)==52
 for name in names:assert sum(v['structure']==name for s in sets for v in s['structures'])==1,name
 assert hashlib.sha256((R/'reviewed-1.8.1.jar').read_bytes()).hexdigest()==manifest['output_sha256']
print('REVIEWED AUDIT PASS: all unmodified entries byte-identical;160 repaired placement criteria;500 lessons retained;444 exact original texture frames explicitly atlased;52 unique shared habitats;no QA classes;JAR CRC and hash verified')
cache=Path(os.environ.get('GRADLE_USER_HOME',str(Path.home()/'.gradle')))/'caches/modules-2/files-2.1/org.ow2.asm';cp=':'.join(str(next((cache/name).rglob('*.jar')))for name in ['asm','asm-tree'])
subprocess.run([str(Path(os.environ['JAVA_HOME'])/'bin/java') if os.environ.get('JAVA_HOME') else (shutil.which('java') or 'java'),'--class-path',cp,str((R/'repository/integration/OwnLinkageTest.java') if (R/'repository/integration/OwnLinkageTest.java').exists() else R.parent/'integration/OwnLinkageTest.java'),str(R/'reviewed-1.8.1.jar')],check=True)
