"""Append-only assets over the reviewed JAR. Does not run historical generators.

Input scripts are never executed. Native geometry conversion operates only on
supplied numeric tracks. Production Java compilation is a separate required step.
"""
import copy
import gzip
import hashlib
import io
import json
import math
import os
from pathlib import Path
import sys
from zipfile import ZipFile

sys.path.insert(0, '/workspace/mod-integration/tools/python')
import nbtlib
from PIL import Image
from native_model import model

ROOT=Path(__file__).resolve().parents[1]
BASE=Path(os.environ.get('SOULBOUND_BASE_JAR',str(ROOT.parent/'soulbound-army-1.20.1-1.8.1-reviewed.jar')))
BASE_SHA='2b9f00f36e7d1228fe31838daf6c147b5f5c719211d3f8467fedac88eba4da26'
NS='shadowsovereign'
OUT=ROOT/'resources'

PROFILES=[
 dict(index=51,id='morsi',source='Mob_57_Morsi_Complete',stem='Morsi',en='Morsi',ar='مرسي',health=420,damage=16,armor=6,speed=.30,width=.65,height=1.95,scale=1,walk='walk',run='run',flies=False,
      moves=[['punch_attack',.65,0,3.6,1],['defensive_counter',.85,5,0,0],['argument',1.3,3,7,.35]],
      template='Morsi_Mobile_Shop.nbt',site='morsi_mobile_shop',biome='crimson_shadow',floor=4,altar=[15,5,14]),
 dict(index=52,id='porcelain_duchess',source='Mob_68_Porcelain_Duchess_Complete',stem='Porcelain_Duchess',en='Porcelain Duchess',ar='دوقة الخزف',health=1000,damage=24,armor=12,speed=.25,width=1.4,height=3.6,scale=1,walk='walk',run='run',flies=False,
      moves=[['porcelain_fan_attack',.7,0,5,1],['mirror_shard_storm',1.35,47,16,.9],['crimson_ribbon_attack',1.5,2,9,.85],['royal_waltz',1.85,1,7,1],['shattered_coronation',2.1,6,16,1.15]],
      template='Palace_Of_Silent_Duchess.nbt',site='palace_of_silent_duchess',biome='moonveil_hollow',floor=8,altar=[24,9,17]),
 dict(index=53,id='yuta_okkotsu',source='Mob_61_Yuta_Okkotsu_Complete',stem='Yuta_Okkotsu',en='Yuta Okkotsu',ar='يوتا أوكوتسو',health=850,damage=23,armor=10,speed=.31,width=.65,height=1.95,scale=1,walk='walk',run='run',flies=False,
      moves=[['katana_combo',.65,0,4.6,1.05],['cursed_slash',.95,47,14,1],['rika_manifestation',1.2,3,9,.5],['rika_strike',1.1,1,7,1.1],['reverse_cursed_energy',1.45,46,8,0]],
      template='Abandoned_Jujutsu_Academy.nbt',site='abandoned_jujutsu_academy',biome='crimson_shadow',floor=8,altar=[22,9,16]),
]

AR_DECOR=[
 'عرش الملك بلا تاج','نصب الغراب القرمزي','صنم الظل بستة أذرع','مزار القمر الدموي','نصب عين الشارينغان','فانوس غراب إيتاتشي','عمود الرينغان','مذبح المسارات الستة','تمثال ملاك الأوريغامي','جدار ألف زهرة ورقية','إطار دوامة كاموي','أثر القناع الحلزوني','نصب مروحة مادارا','تمثال حارس السوسانو','تمثال التنين الطيني','معرض فن الطين','معرض الدمية القرمزية','نصب ذيل العقرب','راية السحابة القرمزية','طاولة مجلس الظلال',
 'نصب الظبي البلوري','نافورة الدموع الضائعة','صدفة الكاتدرائية المصغرة','جرس الأرواح المنسية','تمثال عثة القمر','ثريا فراشات القمر','نافورة لؤلؤ القضاعة','طاحونة الماء المصغرة','تمثال البومة الفلكية','كرة النجوم','تمثال آكل النمل الخزفي','برج الجرس الياقوتي','تمثال الباندا النائمة','مجسم فهد الثلج النائم','حوض الأكسولوتل البلوري','نصب لؤلؤة القلب المكسور','صندوق موسيقى الفيل الذهبي','دوامة الأحلام المنسية','مزار فانوس الخيزران','عرش البلور الملكي المتجمد',
 'كرسي رفاعي الأسطوري','تمثال رفاعي التذكاري','مكتب ناصر التنفيذي','لوحة الأخوين','خزانة أرشيف السلماوي','طاولة الرخام الأسود','مجلس مصري تقليدي','ساعة القاهرة العتيقة','عرش النمر الذهبي','شعار عائلة النمر','خزانة أدوات الورشة','آلة الورشة العتيقة','كاونتر عرض الهواتف','لافتة مرسي للمحمول','مرآة زينة حنان','طقم الشاي العائلي','مكتب قسم الشرطة','ساعة قسم الشرطة','نصب رفاعي الزومبي العملاق','عرش أسطورة الموتى',
]

def write(name, value):
 p=OUT/name;p.parent.mkdir(parents=True,exist_ok=True)
 p.write_bytes(value if isinstance(value,bytes) else (json.dumps(value,ensure_ascii=False,indent=2)+'\n').encode())

def find(root, name):
 candidates=list(root.rglob(name))
 if len(candidates)!=1:raise ValueError((name,candidates))
 return candidates[0]

def pack(name):return ROOT/'inputs'/name

def load_template(data):return nbtlib.File.parse(io.BytesIO(gzip.decompress(data)))

def save_template(name, n):
 stream=io.BytesIO();n.write(stream);write(name,gzip.compress(stream.getvalue(),mtime=0))

def pixels(name):return Image.open(name).convert('RGBA')

def model_bounds(d):
 """Conservative extents, including legal one-axis rotated cube corners."""
 points=[]
 for e in d['elements']:
  rotation=e.get('rotation');r=math.radians(rotation['angle']) if rotation else 0
  axis='xyz'.index(rotation['axis']) if rotation else 0
  for x in [e['from'][0],e['to'][0]]:
   for y in [e['from'][1],e['to'][1]]:
    for z in [e['from'][2],e['to'][2]]:
     v=[x,y,z]
     if rotation:
      o=rotation['origin'];a,b=[i for i in range(3) if i!=axis];u,w=v[a]-o[a],v[b]-o[b]
      v[a]=o[a]+u*math.cos(r)-w*math.sin(r);v[b]=o[b]+u*math.sin(r)+w*math.cos(r)
     points.append(v)
 return [[min(v[i] for v in points) for i in range(3)],[max(v[i] for v in points) for i in range(3)]]

def creatures(jar,langs,spec):
 sites=json.loads(jar.read(f'data/{NS}/mythic_sites.json'))
 shared=json.loads(jar.read(f'data/{NS}/worldgen/structure_set/reviewed_mythic_habitats.json'))
 write(f'assets/{NS}/textures/item/content_eggs.png',(ROOT/'art/content-eggs.png').read_bytes())
 for egg_index,p in enumerate(PROFILES):
  root=pack(p['source']);stem=p['stem'];id=p['id']
  geo=json.loads(find(root,stem+'.geo.json').read_text())
  animations=json.loads(find(root,stem+'.animation.json').read_text())
  native=model(geo,animations)
  for clip in native['animations'].values():
   for track in clip['tracks']:
    if track['channel']=='scale':
     for key in track['keys']:key[1:]=[.001 if abs(x)<1e-6 else x for x in key[1:]]
  texture=find(root,stem+'_Texture.png')
  glow_files=list(root.rglob(stem+'_Glowmask.png'))+list(root.rglob(stem+'_Texture_glowmask.png'))
  if len(glow_files)!=1:raise ValueError('Missing or ambiguous glow mask '+id)
  if id=='yuta_okkotsu':
   # Lossless atlas packing, not repainted art: both source RGBA regions retain
   # every original pixel. Route ALL Rika/FX bones to the second half by UV.
   for basefile,otherfile,suffix in [(texture,find(root,'Yuta_Okkotsu_Rika_Texture.png'),'.png'),(glow_files[0],find(root,'Yuta_Okkotsu_Rika_Texture_glowmask.png'),'_glow.png')]:
    first=pixels(basefile);second=pixels(otherfile);assert first.size==second.size==(128,128)
    atlas=Image.new('RGBA',(256,128));atlas.paste(first,(0,0));atlas.paste(second,(128,0))
    assert atlas.crop((0,0,128,128)).tobytes()==first.tobytes()
    assert atlas.crop((128,0,256,128)).tobytes()==second.tobytes()
    buf=io.BytesIO();atlas.save(buf,format='PNG');write(f'assets/{NS}/textures/entity/{id}{suffix}',buf.getvalue())
   native['width']=256
   for bone in native['bones']:
    if bone['name'].startswith(('rika_','fx_')):
     for cube in bone['cubes']:
      for uv in cube['faces'].values():uv[0]+=128;uv[2]+=128
  else:
   write(f'assets/{NS}/textures/entity/{id}.png',texture.read_bytes())
   write(f'assets/{NS}/textures/entity/{id}_glow.png',glow_files[0].read_bytes())
  write(f'assets/{NS}/mythic/{id}.json',native)
  egg=json.loads(jar.read(f'assets/{NS}/models/item/hanan_spawn_egg.json'))
  egg['textures']={'egg':NS+':item/content_eggs','particle':NS+':item/content_eggs'}
  u0=egg_index*16/3;u1=(egg_index+1)*16/3
  egg['elements'][0]['faces']['north']['uv']=[u0,0,u1,16]
  egg['elements'][0]['faces']['south']['uv']=[u1,0,u0,16]
  write(f'assets/{NS}/models/item/{id}_spawn_egg.json',egg)
  for clip,*_ in p['moves']:assert clip in native['animations']
  n=nbtlib.load(find(root,p['template']));n['DataVersion']=nbtlib.Int(3465)
  # DATA markers are editor metadata, not physical obstacles below the guardian.
  for i,state in enumerate(n['palette']):
   if str(state['Name'])=='minecraft:structure_block':
    state['Name']=nbtlib.String('minecraft:crying_obsidian');state.pop('Properties',None)
    for block in n['blocks']:
     if int(block['state'])==i:block.pop('nbt',None)
   if str(state['Name'])=='minecraft:short_grass':state['Name']=nbtlib.String('minecraft:grass')
  # A sparse template leaves unspecified terrain untouched. Make the guardian's
  # actual body space explicitly air; never erase authored solid blocks to pass.
  palette=n['palette'];air=next((i for i,s in enumerate(palette) if str(s['Name'])=='minecraft:air'),None)
  if air is None:palette.append(nbtlib.Compound({'Name':nbtlib.String('minecraft:air')}));air=len(palette)-1
  bypos={tuple(map(int,b['pos'])):b for b in n['blocks']};x,y,z=p['altar']
  for X in range(math.floor(x+.5-p['width']/2),math.ceil(x+.5+p['width']/2)):
   for Z in range(math.floor(z+.5-p['width']/2),math.ceil(z+.5+p['width']/2)):
    for Y in range(y,math.ceil(y+p['height'])):
     pos=(X,Y,Z)
     if pos in bypos:
      assert str(palette[int(bypos[pos]['state'])]['Name']) in {'minecraft:air','minecraft:cave_air'},(id,pos,'authored spawn obstructed')
     else:
      entry=nbtlib.Compound({'pos':nbtlib.List[nbtlib.Int](pos),'state':nbtlib.Int(air)});n['blocks'].append(entry);bypos[pos]=entry
  name='expansion_'+p['site'];path=f'data/{NS}/structures/content/{p["site"]}.nbt'
  save_template(path,n)
  site={'kind':p['index'],'structure':name,'altar':p['altar'],'rootOffset':[0,0,0]}
  sites.append(site);shared['structures'].append({'structure':NS+':'+name,'weight':1})
  structure=json.loads(jar.read(f'data/{NS}/worldgen/structure/expansion_hanan_cairo_house.json'))
  structure.update(biomes=NS+':'+p['biome'],start_pool=NS+':'+name,start_height={'absolute':-p['floor']})
  pool=json.loads(jar.read(f'data/{NS}/worldgen/template_pool/expansion_hanan_cairo_house.json'))
  pool['name']=NS+':'+name;pool['elements'][0]['element']['location']=NS+':content/'+p['site']
  write(f'data/{NS}/worldgen/structure/{name}.json',structure)
  write(f'data/{NS}/worldgen/template_pool/{name}.json',pool)
  spec['templates'][p['index']]={'path':path,'altar':p['altar'],'source_dimensions':list(map(int,n['size']))}
  for loc,d in langs.items():
   label=p['ar'] if loc=='ar_sa' else p['en']
   d['entity.'+NS+'.'+id]=label
   d['item.'+NS+'.'+id+'_spawn_egg']=('بيضة استدعاء '+label) if loc=='ar_sa' else label+' Spawn Egg'
   d['grimoire.'+NS+'.lore.'+id]=('اهزم الحارس في موطنه لتضم روحه إلى جيشك. الهروب ينهي المواجهة ويعيد صحته.' if loc=='ar_sa' else 'Defeat the guardian in its habitat to earn its soul. Retreat ends the encounter and restores it for your return.')
   d['grimoire.'+NS+'.abilities.'+id]=('قدرات: ' if loc=='ar_sa' else 'Abilities: ')+', '.join(m[0].replace('_',' ') for m in p['moves'])
  spec['profiles'].append({k:p[k] for k in ['index','id','health','damage','armor','speed','width','height','scale','walk','run','flies','moves']})
 # Minimum axis separation between adjacent grid candidates is 33 chunks.
 # New templates <=49 blocks; existing maximum remains checked in verify_content.
 assert shared['placement']['spacing']==48 and shared['placement']['separation']==32
 write(f'data/{NS}/mythic_sites.json',sites)
 write(f'data/{NS}/worldgen/structure_set/reviewed_mythic_habitats.json',shared)

def decorations(langs,spec):
 clan=pack('Decor_Pack_04_Shadow_Clan_Legacy')/'Decor_Pack_04_Shadow_Clan_Legacy'
 # Preserve the authored sequence, used only for localization, not registration IDs.
 clan_names=['throne_of_the_crownless_king','crimson_raven_monument','six_armed_shadow_idol','blood_moon_shrine','sharingan_eye_monument','itachis_raven_lantern','rinnegan_pillar','six_paths_ceremonial_altar','origami_angel_sculpture','thousand_paper_flower_wall','kamui_spiral_portal_frame','orange_spiral_mask_relic','madaras_war_fan_monument','susanoo_guardian_statue','clay_dragon_sculpture','explosive_clay_art_display','crimson_puppet_display','scorpion_tail_monument','akatsuki_crimson_cloud_banner','shadow_council_table']
 # The tiny Veiled Whorl altar has no clear supported display alcove for the
 # spiral frame. Use the existing larger crow retreat of the same clan instead.
 clan_targets=[6,22,28,21,22,22,10,10,13,13,22,22,16,16,23,23,17,17,23,16]
 entries=[]
 for id,kind in zip(clan_names,clan_targets):entries.append(('clan_',id,clan/f'assets/decor_pack_04/models/block/{id}.json',clan/f'assets/decor_pack_04/textures/block/{id}.png',kind,id.replace('_',' ').title()))
 manifest=find(pack('Decor_Pack_06_Tormented_Animals_Java'),'asset_manifest.json');root=manifest.parent
 animal_targets=[31,31,42,42,2,2,41,41,38,38,42,42,32,34,44,44,35,35,32,34]
 for a,kind in zip(json.loads(manifest.read_text())['assets'],animal_targets):entries.append(('tormented_',a['key'],root/a['block_model'],root/a['texture'],kind,a['title']))
 manifest=find(pack('Decor_Pack_07_Al_Ostoura_Java'),'asset_manifest.json');root=manifest.parent.parent
 ostoura_targets=[36,36,36,36,37,37,37,37,39,39,46,46,51,51,49,49,36,36,45,45]
 for a,kind in zip(json.loads(manifest.read_text())['assets'],ostoura_targets):entries.append(('ostoura_',a['slug'],root/a['java_model'],root/a['texture'],kind,a['title']))
 centers='amethyst_shard feather bone red_dye ender_pearl lantern purple_dye quartz paper white_dye orange_dye carved_pumpkin black_dye lapis_lazuli clay_ball terracotta string iron_ingot red_wool gold_ingot'.split()
 assert len(entries)==len(AR_DECOR)==60
 for i,((prefix,slug,modelpath,texture,kind,title),ar) in enumerate(zip(entries,AR_DECOR)):
  id=prefix+slug;d=json.loads(modelpath.read_text());bounds=model_bounds(d)
  # Java 1.20.1 rejects element coordinates outside [-16,32]. Uniform scaling
  # preserves all authored cuboids, proportions and UVs for the 32.1px crown.
  extreme=max(max(e['to']) for e in d['elements']);scale=min(1,32/extreme)
  if scale<1:
   for e in d['elements']:
    for key in ['from','to']:e[key]=[v*scale for v in e[key]]
    if 'rotation' in e:e['rotation']['origin']=[v*scale for v in e['rotation']['origin']]
  bounds=model_bounds(d)
  d['textures']={key:NS+':block/'+id for key in d['textures']}
  d['render_type']='minecraft:cutout';d['ambientocclusion']=False
  mount='ceiling' if any(x in slug for x in ['raven_lantern','cloud_banner','chandelier']) else 'wall' if any(x in slug for x in ['flower_wall','war_fan_monument','portrait','family_emblem','shop_sign','wall_clock']) else 'floor'
  b={'id':id,'category':'decor','light':9 if any(x in slug for x in ['lantern','chandelier']) else 0,'hardness':1.5,'visual_bounds':bounds,'mount':mount,'kind':kind,'pack':prefix.rstrip('_'),'source_scale':scale}
  spec['blocks'].append(b)
  write(f'assets/{NS}/models/block/{id}.json',d)
  write(f'assets/{NS}/models/item/{id}.json',{'parent':NS+':block/'+id})
  write(f'assets/{NS}/textures/block/{id}.png',texture.read_bytes())
  write(f'assets/{NS}/blockstates/{id}.json',{'variants':{f'facing={direction}':{'model':NS+':block/'+id,'y':angle} for direction,angle in [('north',0),('east',90),('south',180),('west',270)]}})
  write(f'data/{NS}/loot_tables/blocks/{id}.json',{'type':'minecraft:block','pools':[{'rolls':1,'entries':[{'type':'minecraft:item','name':NS+':'+id}],'conditions':[{'condition':'minecraft:survives_explosion'}]}]})
  base=['minecraft:blackstone','minecraft:quartz_block','minecraft:stone_bricks'][i//20]
  write(f'data/{NS}/recipes/content/{id}.json',{'type':'minecraft:crafting_shaped','pattern':['SSS','SCS','SSS'],'key':{'S':{'item':base},'C':{'item':'minecraft:'+centers[i%20]}},'result':{'item':NS+':'+id,'count':1}})
  write(f'data/{NS}/advancements/recipes/content/{id}.json',{'parent':'minecraft:recipes/root','criteria':{'materials':{'trigger':'minecraft:inventory_changed','conditions':{'items':[{'items':[base]},{'items':['minecraft:'+centers[i%20]]}]}}},'rewards':{'recipes':[NS+':content/'+id]}})
  langs['en_us']['block.'+NS+'.'+id]=title;langs['ar_sa']['block.'+NS+'.'+id]=ar

def ambience(spec):
 root=pack('X2_The_Living_Dimension_Forge_1.20.1')
 for parent,sub in [('Textures','assets/x2_living'),('Audio','assets/x2_living')]:
  start=root/parent/sub
  for p in start.rglob('*'):
   if p.is_file():write('assets/x2_living/'+p.relative_to(start).as_posix(),p.read_bytes())
 effects=[]
 for p in sorted((root/'Animated_Effects').glob('*/*.effect.json')):
  d=json.loads(p.read_text());effects.append(d)
  definition=root/'Textures/assets/x2_living/particles'/(d['particle']['registryName'].split(':')[1]+'.json')
  write(f'assets/{NS}/particles/{d["id"]}.json',json.loads(definition.read_text()))
 actual={'crimson_shadow_forest':'crimson_shadow','moonveil_hollow':'moonveil_hollow','shattered_sky':'shattered_sky','eternal_abyss':'eternal_abyss','sunken_kingdom':'sunken_kingdom'}
 profiles=[];unbound=[]
 for p in sorted((root/'Biome_Profiles').glob('*.json')):
  d=json.loads(p.read_text())
  if d['biomeKey'] not in actual:unbound.append(d['biomeKey']);continue
  d['biomeRegistryName']=NS+':'+actual[d['biomeKey']];profiles.append(d)
 sounds=json.loads((root/'Audio/assets/x2_living/sounds.json').read_text())
 with ZipFile(BASE) as jar:merged=json.loads(jar.read(f'assets/{NS}/sounds.json'))
 assert not(set(sounds)&set(merged));merged.update(sounds);write(f'assets/{NS}/sounds.json',merged)
 write(f'assets/{NS}/content/x2.json',{'effects':effects,'biomes':profiles,'performance':json.loads((root/'Integration/performance_settings.json').read_text()),'structures':[json.loads(p.read_text()) for p in sorted((root/'Structure_Atmospheres').glob('*.json'))]})
 spec['x2']={'registered_effect_ids':[d['id'] for d in effects],'biomes':actual,'unbound_biomes':unbound,'sound_ids':list(sounds)}
 x4=pack('X4_Shadow_Army_Autopilot_Forge_1.20.1')/'X4_Shadow_Army_Autopilot_Forge_1.20.1'
 for p in (x4/'Assets').rglob('*.png'):
  write(f'assets/{NS}/textures/gui/x4/'+p.relative_to(x4/'Assets').as_posix().lower(),p.read_bytes())

def localize(langs):
 entries={
  'title':('Army Autopilot','مساعد الجيش'),
  'enabled':('Autopilot','مساعد الجيش'),
  'building':('Building space','مساحة للبناء'),
  'combat':('Combat assistance','المساندة القتالية'),
  'performance':('Performance mode','وضع الأداء'),
  'follow':('Smart following','المتابعة الذكية'),
  'regroup':('Regroup safely','إعادة تجميع آمنة'),
  'quality':('Atmosphere quality','جودة أجواء البعد'),
  'fog':('Atmosphere fog','ضباب البعد'),
  'drag':('Scroll or drag empty space. U opens controls.','مرّر أو اسحب المساحة الخالية. زر U يفتح التحكم.'),
  'hud':('Army %s | Recovering %s | Failed %s','الجيش %s | يستعيد المسار %s | تعذّر %s'),
  'regroup_started':('Regroup requested. Explicit orders stay in control.','بدأ طلب إعادة التجميع مع احترام الأوامر الحالية.'),
  'recovery_failed':('A companion could not find a safe route.','تعذّر على رفيق العثور على مسار آمن.'),
  'quality.low':('Low','منخفضة'),'quality.medium':('Medium','متوسطة'),'quality.high':('High','مرتفعة'),
 }
 for key,(en,ar) in entries.items():
  langs['en_us']['content.shadowsovereign.'+key]=en;langs['ar_sa']['content.shadowsovereign.'+key]=ar
 langs['en_us']['key.shadowsovereign.autopilot']='Army Autopilot';langs['ar_sa']['key.shadowsovereign.autopilot']='مساعد الجيش'
 langs['en_us']['key.categories.shadowsovereign']='Soulbound Army';langs['ar_sa']['key.categories.shadowsovereign']='جيش الأرواح'
 english=['Crimson forest ambience','Moonveil ambience','Shattered sky ambience','Abyss ambience','Sunken kingdom ambience','Glass desert ambience','Thunderfall ambience','Dreamroot ambience','Mirrorfall ambience','Titanbone ambience']
 arabic=['أصوات الغابة القرمزية','أصوات وادي القمر','أصوات السماء المتشظية','أصوات الهاوية','أصوات المملكة الغارقة','أصوات صحراء الزجاج','أصوات مرتفعات الرعد','أصوات جذور الأحلام','أصوات مستنقع المرايا','أصوات كهوف العظام']
 keys=['crimson_shadow_forest','moonveil_hollow','shattered_sky','eternal_abyss','sunken_kingdom','singing_glass_desert','thunderfall_highlands','dreamroot_canopy','mirrorfall_marsh','titanbone_caverns']
 for key,en,ar in zip(keys,english,arabic):langs['en_us']['x2_living.ambient.'+key]=en;langs['ar_sa']['x2_living.ambient.'+key]=ar

def main():
 assert hashlib.sha256(BASE.read_bytes()).hexdigest()==BASE_SHA
 OUT.mkdir(exist_ok=True)
 spec={'base_sha256':BASE_SHA,'profiles':[],'blocks':[],'templates':{},'decor_placements':[]}
 with ZipFile(BASE) as jar:
  langs={loc:json.loads(jar.read(f'assets/{NS}/lang/{loc}.json')) for loc in ['en_us','ar_sa']}
  creatures(jar,langs,spec);decorations(langs,spec);ambience(spec);localize(langs)
  for loc,d in langs.items():write(f'assets/{NS}/lang/{loc}.json',d)
 (ROOT/'content-spec.json').write_text(json.dumps(spec,ensure_ascii=False,indent=2)+'\n')
 write(f'assets/{NS}/content/blocks.json',{'blocks':spec['blocks']})
 print('Prepared three appended creature resources, sixty decor resource sets and X2/X4 source assets; Java integration still required.')

if __name__=='__main__':main()
