"""Place supplied props in matching existing/new habitats without destroying blocks.

Only clear template space is occupied. Sparse templates get explicit air around
the new prop within their existing bounds; authored solids are never erased.
Conservatively reserve the entire visual extent,
keep guardian clearance, attach wall/ceiling models and leave old worlds alone:
these are template edits for future generated structures, not save edits.
"""
import collections
import json
import math
from zipfile import ZipFile
import nbtlib
from prepare_content import ROOT,OUT,BASE,NS,load_template,save_template

AIR={'minecraft:air','minecraft:cave_air','minecraft:void_air'}

def cells(pos,bounds):
 low,high=bounds
 ranges=[range(math.floor(pos[i]+low[i]/16+1e-6),math.ceil(pos[i]+high[i]/16-1e-6)) for i in range(3)]
 return {(x,y,z) for x in ranges[0] for y in ranges[1] for z in ranges[2]}

def place(n,entries,altar):
 palette=n['palette'];bypos={tuple(map(int,b['pos'])):b for b in n['blocks']}
 size=list(map(int,n['size']));sparse=len(bypos)<math.prod(size)
 def state(pos):
  b=bypos.get(pos)
  if b is not None:return str(palette[int(b['state'])]['Name'])
  if sparse and all(0<=pos[i]<size[i] for i in range(3)):return 'minecraft:air'
  return None
 def solid(pos):
  s=state(pos)
  if not s or s in AIR:return False
  if not s.startswith('minecraft:') and not any(token in s for token in ['stone','brick','slate','quartz','concrete']):return False
  if any(w in s for w in ['air','water','lava','torch','lantern','door','fence','pane','bars','stairs','slab','carpet','button','plate','rail','chain','leaves','log','chest','barrel','bed','pot','sign','cauldron','vine','ladder','candle','bamboo']):return False
  return True
 reserved=set();placements=[]
 for entry in entries:
  bounds=entry['visual_bounds'];mount=entry['mount'];choices=[]
  candidates=set(bypos)
  if sparse:
   for x,y,z in bypos:
    candidates.add((x,y+1,z));candidates.add((x,y,z-1))
    candidates.add((x,y-math.ceil(bounds[1][1]/16),z))
  for x,y,z in candidates:
   if state((x,y,z)) not in AIR:continue
   # Keep full guardian footprint, player approach, and its near combat circle free.
   if (x-altar[0])**2+(z-altar[2])**2<7**2:continue
   if mount=='ceiling':
    ceiling=math.ceil(y+bounds[1][1]/16-1e-6)
    if not solid((x,ceiling,z)):continue
   elif mount=='wall':
    if not solid((x,y,z+1)):continue
   elif not solid((x,y-1,z)):continue
   clear_bounds=[list(v) for v in bounds]
   # Authored wall plaques can recess <=1px into their supporting wall.
   # Keep that wall intact; require air only in front of its attachment plane.
   if mount=='wall' and 16<clear_bounds[1][2]<=17:clear_bounds[1][2]=16
   occupied=cells((x,y,z),clear_bounds)
   if occupied&reserved or any(state(p) not in AIR for p in occupied):continue
   # Reject doorways and stair routes even when the supplied prop has no collision.
   nearby=[state((x+dx,y+dy,z+dz)) or '' for dx in range(-2,3) for dz in range(-2,3) for dy in [0,1]]
   if any('door' in s or 'stairs' in s or 'ladder' in s for s in nearby):continue
   # Prefer wall-adjacent display alcoves over the centre of open paths.
   wall=min([d for d in range(1,5) if any(solid((x+dx*d,y,z+dz*d)) for dx,dz in [(1,0),(-1,0),(0,1),(0,-1)])] or [6])
   choices.append(((wall,abs(y-altar[1]),x,z,y),(x,y,z),occupied))
  if not choices:raise ValueError('No conservative clear '+mount+' position for '+entry['id'])
  _,pos,occupied=min(choices,key=lambda c:c[0]);reserved|=occupied
  palette.append(nbtlib.Compound({'Name':nbtlib.String(NS+':'+entry['id']),'Properties':nbtlib.Compound({'facing':nbtlib.String('north')})}))
  if sparse:
   air=next((i for i,p in enumerate(palette) if str(p['Name'])=='minecraft:air'),None)
   if air is None:palette.append(nbtlib.Compound({'Name':nbtlib.String('minecraft:air')}));air=len(palette)-1
   for cell in sorted(occupied):
    if cell not in bypos:
     b=nbtlib.Compound({'pos':nbtlib.List[nbtlib.Int](cell),'state':nbtlib.Int(air)})
     n['blocks'].append(b);bypos[cell]=b
  bypos[pos]['state']=nbtlib.Int(next(i for i,p in enumerate(palette) if str(p['Name'])==NS+':'+entry['id']));bypos[pos].pop('nbt',None)
  placements.append({'id':entry['id'],'position':list(pos),'mount':mount,'reserved_cells':[list(p) for p in sorted(occupied)]})
 return placements

def main():
 spec=json.loads((ROOT/'content-spec.json').read_text())
 groups=collections.defaultdict(list)
 for entry in spec['blocks']:groups[entry['kind']].append(entry)
 with ZipFile(BASE) as base:
  sites=json.loads((OUT/f'data/{NS}/mythic_sites.json').read_text());bykind={s['kind']:s for s in sites}
  for kind,entries in groups.items():
   site=bykind[kind]
   fresh=str(kind) in spec['templates']
   if fresh:path=spec['templates'][str(kind)]['path']
   else:
    structure=json.loads(base.read(f'data/{NS}/worldgen/structure/{site["structure"]}.json'))
    pool=json.loads(base.read('data/'+structure['start_pool'].replace(':','/worldgen/template_pool/')+'.json'))
    location=pool['elements'][0]['element']['location'];path='data/'+location.replace(':','/structures/')+'.nbt'
   n=load_template((OUT/path).read_bytes() if fresh else base.read(path))
   placements=place(n,entries,site['altar'])
   for p in placements:p['template']=path;p['kind']=kind
   spec['decor_placements'].extend(placements);save_template(path,n)
   print('Placed',len(entries),'props at',site['structure'])
 (ROOT/'content-spec.json').write_text(json.dumps(spec,ensure_ascii=False,indent=2)+'\n')
 assert len(spec['decor_placements'])==20
 print('PASS: all twenty Eclipse props have conservative, supported habitat placements')

if __name__=='__main__':main()
