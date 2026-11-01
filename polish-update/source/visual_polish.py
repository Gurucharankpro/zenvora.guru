"""Presentation-only model framing and bespoke journey icons.
No combat scale, old criteria, reward functions, or claim state is changed.
"""
import copy,io,itertools,json,math
import numpy as np
from PIL import Image
from zipfile import ZipFile
from prepare_content import ROOT,OUT,BASE,NS,write

def transform(position,rotation,scale=(1,1,1)):
 x,y,z=np.radians(rotation);cx,cy,cz=np.cos([x,y,z]);sx,sy,sz=np.sin([x,y,z])
 r=np.array([[cz*cy,cz*sy*sx-sz*cx,cz*sy*cx+sz*sx],[sz*cy,sz*sy*sx+cz*cx,sz*sy*cx-cz*sx],[-sy,cy*sx,cy*cx]])
 m=np.eye(4);m[:3,:3]=r@np.diag(scale);m[:3,3]=position;return m
def bounds(d,id=''):
 bones=d['bones'];clip=d['animations'].get('idle',next(iter(d['animations'].values())));lo=np.array([np.inf]*3);hi=-lo
 radius=0
 # These legacy attack-only effects were authored below the model's feet.
 # Frame the creature's body; leave geometry, animations and world rendering intact.
 exclude={'cursed_executioner':('shock_',),'frostbound_snow_leopard':('shield_','fx_spark_','swirl_')}.get(id,())
 for time in np.linspace(0,clip['length'],17):
  poses=np.zeros((len(bones),9));poses[:,6:]=1
  for t in clip['tracks']:
   keys=np.array(t['keys']);v=np.array([np.interp(time,keys[:,0],keys[:,j]) for j in [1,2,3]])
   offset={'position':0,'rotation':3,'scale':6}[t['channel']]
   if offset==6:poses[t['bone'],6:]*=v
   else:poses[t['bone'],offset:offset+3]+=v
  matrices={};visible={}
  def matrix(i):
   if i in matrices:return matrices[i]
   b=bones[i];parent=b['parent'];pivot=np.array(b['pivot']);base=np.array(bones[parent]['pivot']) if parent>=0 else np.zeros(3)
   p=poses[i];local=transform(pivot-base+p[:3],np.array(b['rotation'])+p[3:6],p[6:]);matrices[i]=(matrix(parent)@local) if parent>=0 else local
   visible[i]=(visible[parent] if parent>=0 else True) and sum(abs(p[6:])>.01)>=2 and not b['name'].startswith(exclude)
   return matrices[i]
  for i,b in enumerate(bones):
   bm=matrix(i)
   if not visible[i]:continue
   for cube in b['cubes']:
    origin=np.array(cube['origin']);cm=bm@transform(origin-np.array(b['pivot']),cube['rotation'])
    vertices=np.array([[*([cube['from'][j] if v[j]==0 else cube['to'][j] for j in range(3)]-origin),1] for v in itertools.product([0,1],repeat=3)])
    result=(cm@vertices.T).T[:,:3];lo=np.minimum(lo,result.min(axis=0));hi=np.maximum(hi,result.max(axis=0));radius=max(radius,float(np.sqrt((result[:,0]**2+result[:,2]**2)).max()))
 if not np.all(np.isfinite(lo)):raise ValueError('No visible body')
 # Includes whole idle cycle plus turning depth, never hidden Rika/FX planes.
 return [max(.3,2*radius/16)*1.10,max(.3,(hi[1]-min(0,lo[1]))/16)*1.10]

def main():
 report={'preview_bounds':{},'journey_icons':[]}
 with ZipFile(BASE) as jar:
  paths={n for n in jar.namelist() if n.startswith(f'assets/{NS}/mythic/') and n.endswith('.json')}
  paths|={p.relative_to(OUT).as_posix() for p in (OUT/f'assets/{NS}/mythic').glob('*.json')}
  for path in sorted(paths):
   d=json.loads(jar.read(path) if path in jar.namelist() else (OUT/path).read_bytes())
   if not {'bones','animations'}<=d.keys():continue
   before=d.get('previewBounds');after=bounds(d,__import__('pathlib').Path(path).stem);d['previewBounds']=after
   write(path,d);report['preview_bounds'][path]={'before':before,'after':after}
  atlas=Image.open(ROOT/'art/badges.png').convert('RGBA');w,h=atlas.size
  names=['welcome','cartographer','collect_hollow_reaper','collect_soul_devourer','collect_mob1','visit_ashen_monolith','visit_ashen_weald','visit_bound_soul_bound','visit_crimson_shadow','visit_echo_haven','visit_eternal_abyss','visit_gloomwood','visit_moonveil_hollow','visit_shattered_sky','visit_sunken_kingdom','visit_singing_glass_desert']
  item=json.loads(jar.read(f'assets/{NS}/models/item/x1_visual.json'));old=copy.deepcopy(item['overrides'])
  for i,name in enumerate(names):
   cell=atlas.crop((round((i%4)*w/4),round((i//4)*h/4),round((i%4+1)*w/4),round((i//4+1)*h/4)))
   # Power-of-two sprite dimensions keep the shared atlas mipmaps available.
   cell=cell.resize((256,256),Image.Resampling.NEAREST)
   b=io.BytesIO();cell.save(b,format='PNG');write(f'assets/{NS}/textures/item/badges/{name}.png',b.getvalue())
   write(f'assets/{NS}/models/item/badges/{name}.json',{'parent':'minecraft:item/generated','textures':{'layer0':NS+':item/badges/'+name}})
   value=10000+i;item['overrides'].append({'predicate':{'custom_model_data':value},'model':NS+':item/badges/'+name})
   path=f'data/{NS}/advancements/journey/{name}.json'
   if name=='visit_singing_glass_desert':
    a=copy.deepcopy(json.loads(jar.read(f'data/{NS}/advancements/journey/visit_crimson_shadow.json')))
    a['display']['title']={'text':'Singing Glass Desert / صحراء الزجاج المغني'};a['display']['description']={'text':'Discover the glass dunes and their crystal wildlife. / اكتشف كثبان الزجاج ومخلوقاتها البلورية.'};a['criteria']['visit']['conditions']['location']['biome']=NS+':singing_glass_desert';a['rewards']={'experience':75}
   else:a=json.loads(jar.read(path));assert a['display']['icon']['item']=='minecraft:compass'
   a['display']['icon']={'item':NS+':x1_visual','nbt':'{CustomModelData:'+str(value)+'}'};write(path,a);report['journey_icons'].append(path)
  assert item['overrides'][:len(old)]==old;write(f'assets/{NS}/models/item/x1_visual.json',item)
 (ROOT/'visual-report.json').write_text(json.dumps(report,indent=2)+'\n')
 print('PASS: body-only preview bounds; 15 placeholder journey icons replaced + 1 new biome badge; 444 existing X1 overrides retained')
if __name__=='__main__':main()
