"""Static asset and graph checks. Does not claim to validate Forge codecs or actual rendering."""
from pathlib import Path
import sys,json,collections,math
sys.path.insert(0,str(Path(__file__).resolve().parents[1]/'tools/python'))
import nbtlib
from PIL import Image
R=Path(__file__).resolve().parents[1];A=R/'src/main/resources/assets/dungeongates';D=R/'src/main/resources/data/dungeongates';checks=[]
def check(b,message):
 if not b:raise AssertionError(message)
checks.append('all JSON parses')
for p in R.glob('src/main/resources/**/*.json'):json.loads(p.read_text())
# Actual vanilla resource references, including inherited parents and face texture bindings.
for p in (A/'models').rglob('*.json'):
 d=json.loads(p.read_text())
 for k,v in d.get('textures',{}).items():
  if v.startswith('dungeongates:'):
   image=A/'textures'/(v.split(':')[1]+'.png');check(image.exists(),f'{p}: missing {v}');im=Image.open(image)
   check(im.width==im.height or Path(str(image)+'.mcmeta').exists(),f'{p}: rectangular atlas sprite requires explicit frame dimensions')
 parent=d.get('parent','')
 if parent.startswith('dungeongates:'):check((A/'models'/(parent.split(':')[1]+'.json')).exists(),f'{p}: missing parent {parent}')
 for e in d.get('authored_elements',d.get('elements',[])):
  check(all(-16<=x<=32 for a in ['from','to'] for x in e[a]),f'illegal model coordinates {p}')
  if 'rotation' in e and d.get('loader')!='dungeongates:authored_mesh':check(e['rotation']['angle'] in [-45,-22.5,0,22.5,45],f'illegal vanilla rotation {p}')
checks.append('resource model/texture refs and actual vanilla coordinate/angle limits')
ids=['mossbound_skulker','crystal_sentinel','emerald_root_warden','frostgrave_prowler','chainbound_sentinel','frozen_gravekeeper'];clips={}
for id in ids:
 geo=json.loads((A/f'geo/entity/{id}.geo.json').read_text())['minecraft:geometry'][0];anim=json.loads((A/f'animations/entity/{id}.animation.json').read_text());bones={b['name'] for b in geo['bones']};c=anim['animations'];clips[id]=len(c)
 for name,a in c.items():check(set(a.get('bones',{}))<=bones,f'{id} unknown animated bones {name}')
 check((A/f'textures/entity/{id}.png').exists() and (A/f'textures/entity/{id}_glowing.png').exists(),f'{id} glow routing')
checks.append('six actual authored geometry/animation bone sets and glow routes')
# Verify actual final NBT walkable graph, not only declared source coordinates.
for id in ['emerald_sanctuary','frozen_crypt']:
 n=nbtlib.load(D/f'structures/{id}.nbt');check(list(map(int,n['size']))==[64,28,64],'NBT size');palette=n['palette'];names=[str(s['Name']) for s in palette];cells={tuple(map(int,b['pos'])):names[int(b['state'])] for b in n['blocks']};check('minecraft:structure_block' not in {cells[p] for p in cells},'unprocessed marker');check(not any('soulbound' in v for v in names),'old namespace')
 nonsolid={'minecraft:air','minecraft:cave_air','dungeongates:emerald_hanging_vines','dungeongates:winterveil_vines','dungeongates:forgotten_garden_flower','dungeongates:ghostfrost_flower','minecraft:water'}
 def walk(p):
  x,y,z=p
  return 0<x<63 and 0<z<63 and 1<y<26 and cells.get(p) in nonsolid and cells.get((x,y+1,z)) in nonsolid and cells.get((x,y-1,z)) not in nonsolid
 start=(10,3,50);check(walk(start),f'{id} arrival');queue=collections.deque([start]);seen={start}
 while queue:
  x,y,z=queue.popleft()
  for dx,dz in [(1,0),(-1,0),(0,1),(0,-1)]:
   for dy in [0,1,-1]:
    p=(x+dx,y+dy,z+dz)
    if p not in seen and walk(p):seen.add(p);queue.append(p)
 targets=[(14,3,7),(48,3,12)] if id=='emerald_sanctuary' else [(14,3,7),(46,3,15)]
 for p in targets:check(p in seen,f'{id} target unreachable {p}')
 print(id,'reachable standing cells',len(seen))
checks.append('actual final NBT BFS arrival-to-boss-and-return connectivity')
for p in (A/'textures').rglob('*.png.mcmeta'):
 im=Image.open(str(p)[:-7]);a=json.loads(p.read_text())['animation'];w=a.get('width',min(im.size));h=a.get('height',min(im.size));check(im.width%w==0 and im.height%h==0,f'invalid animation frames {p}')
checks.append('sprite frame dimensions')
result={'static_checks_passed':checks,'clips_per_creature':clips,'native_engine_tests':False,'graphical_tests':False};(R/'reports/STATIC-CHECKS.json').write_text(json.dumps(result,indent=2)+'\n');print(result)
