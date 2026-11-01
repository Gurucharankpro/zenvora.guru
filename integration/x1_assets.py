"""Play supplied frames using bounded, temporary vanilla ItemDisplay entities.
No globally synchronized animated sprite is used: each instance starts at frame 0.
"""
import argparse,json,copy
from pathlib import Path
NS='shadowsovereign'

def merge_frame_atlas(out, resources):
 """ItemDisplay models use the blocks atlas, not arbitrary texture folders.

 Keep existing sources, including sources owned by unrelated future additions.
 Explicit single sources also remain inspectable in resource-pack overrides.
 """
 p=out/'assets/minecraft/atlases/blocks.json'
 atlas=json.loads(p.read_text()) if p.exists() else {'sources':[]}
 sources=atlas.setdefault('sources',[])
 known={s.get('resource') for s in sources if s.get('type')=='minecraft:single'}
 for resource in sorted(set(resources)):
  if resource not in known:sources.append({'type':'minecraft:single','resource':resource});known.add(resource)
 p.parent.mkdir(parents=True,exist_ok=True);p.write_text(json.dumps(atlas,indent=2)+'\n')

def build(inputs,out):
 root=inputs/'X1_Shadow_Awakening';spec=json.loads((out/'integration-spec.json').read_text())
 def write(path,data):
  p=out/path;p.parent.mkdir(parents=True,exist_ok=True);p.write_bytes(data if isinstance(data,bytes) else (json.dumps(data,ensure_ascii=False,indent=2)+'\n').encode())
 def fn(path,lines):write(f'data/{NS}/functions/x1/{path}.mcfunction',('\n'.join(lines)+'\n').encode())
 overrides=[];layers=[];effects=[];number=1;dispatch=[];atlas_frames=[]
 presets=json.loads((root/'Integration/performance_presets.json').read_text())['effects']
 for file in sorted((root/'Effects').glob('*/effect.json')):
  effect=json.loads(file.read_text());id=effect['id'];used=[];layer_commands={}
  selected=presets[id]['MEDIUM']['layers']
  if id=='13':selected=['base','dome_view']
  # Static burst presentation of looping auras. No perpetual entity emitters.
  for layer in effect['layers']:
   if layer['name'] not in selected:continue
   name=layer['name'];key='e'+id+'_'+name;frames=sorted((file.parent/name).glob('*.png'))
   assert len(frames)==layer['frames'],(key,len(frames),layer['frames'])
   tpf=layer['ticks_per_frame'];duration=min(100,layer['duration_ticks']);start=number
   ground=layer['render_mode']=='ground';size=6 if id=='14' else 4 if id=='03' else 2.8
   for i,frame in enumerate(frames):
    texture=f'x1/{key}_{i:03d}';modelpath=f'item/x1/frame_{number:04d}'
    write(f'assets/{NS}/textures/{texture}.png',frame.read_bytes())
    atlas_frames.append(NS+':'+texture)
    element={'from':[0,7.99,0],'to':[16,8.01,16],'faces':{'up':{'texture':'#v','uv':[0,0,16,16]},'down':{'texture':'#v','uv':[0,0,16,16]}}} if ground else {'from':[0,0,7.99],'to':[16,16,8.01],'faces':{'north':{'texture':'#v','uv':[0,0,16,16]},'south':{'texture':'#v','uv':[16,0,0,16]}}}
    element['shade']=False
    write(f'assets/{NS}/models/{modelpath}.json',{'gui_light':'front','textures':{'v':NS+':'+texture,'particle':NS+':'+texture},'elements':[element]})
    overrides.append({'predicate':{'custom_model_data':number},'model':NS+':'+modelpath});number+=1
   # One cycle only, including supplied transparent end frames. The source
   # loop flag is retained in review metadata, not interpreted as infinite life.
   lines=[]
   for i in range(len(frames)):
    lo=1+i*tpf;hi=min(duration,(i+1)*tpf)
    if lo<=hi:lines.append(f'execute if score @s sbx1_age matches {lo}..{hi} run data modify entity @s item.tag.CustomModelData set value {start+i}')
   lines.append(f'execute if score @s sbx1_age matches {duration}.. run function {NS}:x1/end')
   fn('layer/'+key,lines);dispatch.append(f'execute if entity @s[tag=sbx1_{key}] run function {NS}:x1/layer/{key}')
   nbt={'Tags':['sb_x1_fx','sb_x1_new','sbx1_'+key],'billboard':'fixed' if ground else 'vertical','item_display':'none','brightness':{'block':15,'sky':15},'view_range':.75,'shadow_radius':0.0,'shadow_strength':0.0,'Invulnerable':True,'item':{'id':NS+':x1_visual','Count':1,'tag':{'CustomModelData':start}},'transformation':{'translation':[0,.035 if ground else size/2,0],'scale':[size,1 if ground else size,size if ground else 1],'left_rotation':[0,0,0,1],'right_rotation':[0,0,0,1]}}
   # JSON is a valid SNBT subset here; booleans are serialized as byte literals.
   for transform in nbt['transformation']:
    nbt['transformation'][transform]=[float(v) for v in nbt['transformation'][transform]]
   snbt=json.dumps(nbt,separators=(',',':')).replace('true','1b')
   spawn=['execute if score #active sbx1_count matches ..23 run summon minecraft:item_display ~ ~ ~ '+snbt,'scoreboard players set @e[type=minecraft:item_display,tag=sb_x1_new] sbx1_age 0','tag @e[type=minecraft:item_display,tag=sb_x1_new] remove sb_x1_new','scoreboard players add #active sbx1_count 1']
   layer_commands[name]=spawn;used+=spawn
   layers.append(dict(key=key,frames=len(frames),ticks_per_frame=tpf,duration=duration,start=start,loop_in_source=layer['loop']))
  if id=='11':
   for name,commands in layer_commands.items():fn('event/11_'+name,['execute store result score #active sbx1_count if entity @e[type=minecraft:item_display,tag=sb_x1_fx]']+commands)
   used=layer_commands['slash_horizontal']
  if id=='06':used=layer_commands['ground']+layer_commands['ally_marker']
  event=['execute store result score #active sbx1_count if entity @e[type=minecraft:item_display,tag=sb_x1_fx]']+used
  sound={'01':'shadow_extraction','02':'shadow_summon','03':'shadow_summon','05':'shadow_rank_promotion','12':'shadow_magic_impact'}.get(id)
  if sound:event.append(f'playsound {NS}:x1.{sound} master @a[distance=..40] ~ ~ ~ 0.45 1')
  fn('event/'+id,event);effects.append(id)
 merge_frame_atlas(out,atlas_frames)
 write(f'assets/{NS}/models/item/x1_visual.json',{'parent':NS+':'+overrides[0]['model'].split(':')[1],'overrides':overrides})
 fn('init',['execute unless data storage shadowsovereign:x1 initialized run scoreboard objectives add sbx1_age dummy','execute unless data storage shadowsovereign:x1 initialized run scoreboard objectives add sbx1_count dummy','data modify storage shadowsovereign:x1 initialized set value 1b'])
 fn('tick_world',['execute as @e[type=minecraft:item_display,tag=sb_x1_fx] run scoreboard players add @s sbx1_age 1','execute as @e[type=minecraft:item_display,tag=sb_x1_fx] run function shadowsovereign:x1/dispatch'])
 fn('dispatch',dispatch)
 fn('end',['execute if entity @s[type=minecraft:item_display,tag=sb_x1_fx] run scoreboard players reset @s sbx1_age','execute if entity @s[type=minecraft:item_display,tag=sb_x1_fx] run kill @s'])
 sounds=json.loads((out/f'assets/{NS}/sounds.json').read_text()) if (out/f'assets/{NS}/sounds.json').exists() else {}
 for audio in (root/'Audio').glob('*.ogg'):
  write(f'assets/{NS}/sounds/x1/{audio.name}',audio.read_bytes());sounds['x1.'+audio.stem]={'sounds':[{'name':NS+':x1/'+audio.stem,'stream':False}]}
 # Preserve all baseline sound declarations; base fallback is merged by patcher.
 write('x1-sounds.json',sounds)
 for loc in ['en_us','ar_sa']:
  p=out/f'assets/{NS}/lang/{loc}.json';d=json.loads(p.read_text());d['item.shadowsovereign.x1_visual']='Internal X1 visual' if loc=='en_us' else 'مؤثر بصري داخلي';write(str(p.relative_to(out)),d)
 spec['x1']={'effects':effects,'layers':layers,'frame_models':number-1,'budget_per_dimension':24,'presentation':'vanilla item displays; finite one-cycle aura bursts; full-bright alpha quads, not additive dome meshes','unbound_assets':['UI badges and widgets','Blockbench dome and ring meshes','continuous persistent aura loops','directional rally wave (not used in default rally)','barrier tile mesh (sprite fallback only)']}
 write('integration-spec.json',spec);print(f'Prepared X1: {len(effects)} effect families, {len(layers)} layers, {number-1} independently clocked frame models.')
if __name__=='__main__':
 p=argparse.ArgumentParser();p.add_argument('--inputs',type=Path,required=True);p.add_argument('--resources',type=Path,required=True);a=p.parse_args();build(a.inputs,a.resources)
