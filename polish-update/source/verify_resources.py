"""Focused checks of the new resources and affected presentation/template paths."""
import copy,json,math
from zipfile import ZipFile
from prepare_content import ROOT,OUT,BASE,NS,PROFILES,load_template,write

def main():
 spec=json.loads((ROOT/'content-spec.json').read_text());assert len(spec['profiles'])==3 and len(spec['blocks'])==20 and len(spec['biome_blocks'])==20
 assert len(spec['decor_placements'])==20
 with ZipFile(BASE) as before:
  resources={p.relative_to(OUT).as_posix():p.read_bytes() for p in OUT.rglob('*') if p.is_file()}
  allfiles=set(before.namelist())|resources.keys()
  def read(path):return resources[path] if path in resources else before.read(path)
  for name,data in resources.items():
   if name.endswith('.json'):json.loads(data)
  for p in PROFILES:
   t=spec['templates'][str(p['index'])];n=load_template(read(t['path']));bypos={tuple(map(int,b['pos'])):str(n['palette'][int(b['state'])]['Name']) for b in n['blocks']};x,y,z=p['altar']
   assert bypos.get((x,y-1,z)) not in [None,'minecraft:air','minecraft:water'],p['id']
   for X in range(math.floor(x+.5-p['width']/2),math.ceil(x+.5+p['width']/2)):
    for Z in range(math.floor(z+.5-p['width']/2),math.ceil(z+.5+p['width']/2)):
     for Y in range(y,math.ceil(y+p['height'])):assert bypos.get((X,Y,Z)) in ['minecraft:air','minecraft:cave_air'],(p['id'],X,Y,Z,bypos.get((X,Y,Z)))
   native=json.loads(read(f'assets/{NS}/mythic/{p["id"]}.json'));assert all(m[0] in native['animations'] and m[1]<=native['animations'][m[0]]['length'] for m in p['moves'])
  dim=f'data/{NS}/dimension/echo_realms.json';old=json.loads(before.read(dim))['generator']['biome_source']['biomes'];new=json.loads(read(dim))['generator']['biome_source']['biomes'];assert new[:-1]==old and len(new)==11
  atlas=f'assets/minecraft/atlases/blocks.json';assert atlas not in resources
  icons=json.loads(read(f'assets/{NS}/models/item/x1_visual.json'));oldicons=json.loads(before.read(f'assets/{NS}/models/item/x1_visual.json'));assert icons['overrides'][:len(oldicons['overrides'])]==oldicons['overrides']
  for path in resources:
   if '/advancements/' in path and path in before.namelist():
    old=json.loads(before.read(path));new=json.loads(read(path));old['display']['icon']=new['display']['icon'];assert old==new,('Criteria/reward regression',path)
   if path.endswith('.nbt') and path in before.namelist():
    old=load_template(before.read(path));new=load_template(read(path));positions={tuple(map(int,b['pos'])):b for b in new['blocks']}
    for b in old['blocks']:
     pos=tuple(map(int,b['pos']));state=old['palette'][int(b['state'])]
     if str(state['Name']) not in {'minecraft:air','minecraft:cave_air','minecraft:void_air'}:
      other=positions[pos];assert new['palette'][int(other['state'])]==state and other.get('nbt')==b.get('nbt'),('Existing block/loot overwritten',path,pos)
  # Every texture/model reference from the new block models resolves, including
  # parent's external vanilla assets which are supplied by the engine.
  for name in resources:
   if '/models/' not in name or not name.endswith('.json'):continue
   d=json.loads(resources[name])
   for e in d.get('elements',[]):
    assert all(-16<=v<=32 for v in e['from']+e['to']),('Illegal Java model coordinates',name)
    if 'rotation' in e:assert e['rotation']['angle'] in [-45,-22.5,0,22.5,45] and e['rotation']['axis'] in ['x','y','z'],name
   for key,value in d.get('textures',{}).items():
    if value.startswith(NS+':'):assert 'assets/'+value.replace(':','/textures/')+'.png' in allfiles,(name,key,value)
  shared=json.loads(read(f'data/{NS}/worldgen/structure_set/reviewed_mythic_habitats.json'));oldshared=json.loads(before.read(f'data/{NS}/worldgen/structure_set/reviewed_mythic_habitats.json'));assert shared['placement']==oldshared['placement'] and shared['structures'][:-7]==oldshared['structures']
  for state in spec['biome_blocks']:
   id=state['id'];assert f'assets/{NS}/models/item/{id}.json' in allfiles
  def recipe_signature(d):
   if d.get('type')!='minecraft:crafting_shaped':return None
   rows=d['pattern'];width=max(map(len,rows));rows=[r.ljust(width) for r in rows]
   while rows and not rows[0].strip():rows=rows[1:]
   while rows and not rows[-1].strip():rows=rows[:-1]
   if not rows:return None
   low=min(i for r in rows for i,c in enumerate(r) if c!=' ');high=max(i for r in rows for i,c in enumerate(r) if c!=' ')
   grid=tuple(tuple(json.dumps(d['key'][c],sort_keys=True) if c!=' ' else '' for c in r[low:high+1]) for r in rows)
   return min(grid,tuple(tuple(reversed(r)) for r in grid))
  seen={}
  for path in before.namelist():
   if '/recipes/' in path and path.endswith('.json') and '/advancements/' not in path:
    signature=recipe_signature(json.loads(before.read(path)))
    if signature:seen[signature]=path
  for path,data in resources.items():
   if '/recipes/polish/' in path and '/advancements/' not in path:
    signature=recipe_signature(json.loads(data))
    if signature:
     assert signature not in seen,('Uncraftable ambiguous recipe',path,seen.get(signature));seen[signature]=path
 # Metadata includes the actual supported placements, available to the native QA mod.
 write(f'assets/{NS}/content/polish.json',spec)
 report={'groups':9,'new_legendary':3,'new_decor':20,'new_biome_blocks':20,'habitat_placements':20,'new_biome_structures':4,'new_tree_templates':4,'preview_models':len(json.loads((ROOT/'visual-report.json').read_text())['preview_bounds']),'graphical_client_tested':False}
 (ROOT/'RESOURCE-CHECKS.json').write_text(json.dumps(report,indent=2)+'\n');print('PASS: 9 focused resource/preservation groups',report)
if __name__=='__main__':main()
