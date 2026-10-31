"""Exactly 500 additive native advancement lessons; no existing IDs are replaced."""
import argparse,json
from pathlib import Path
NS='shadowsovereign'
VANILLA='crafting_table furnace chest barrel torch lantern campfire ladder oak_planks stick oak_slab oak_stairs oak_fence oak_fence_gate oak_door oak_trapdoor stone_pickaxe stone_axe stone_shovel stone_sword iron_pickaxe iron_axe iron_shovel iron_sword iron_helmet iron_chestplate iron_leggings iron_boots shield bucket shears flint_and_steel compass clock map fishing_rod bow crossbow arrow bread white_bed oak_boat glass_pane stone_bricks polished_andesite polished_diorite polished_granite bookshelf paper book'.split()
assert len(VANILLA)==50

def build(out):
 spec=json.loads((out/'integration-spec.json').read_text())
 langs={loc:json.loads((out/f'assets/{NS}/lang/{loc}.json').read_text()) for loc in ['en_us','ar_sa']}
 quests=[]
 def write(path,d):
  p=out/path;assert not p.exists(),path;p.parent.mkdir(parents=True,exist_ok=True);p.write_text(json.dumps(d,ensure_ascii=False,indent=2)+'\n')
 def lesson(id,title,ar,help,arhelp,criterion,icon,xp=10,parent=None):
  key='lesson.shadowsovereign.'+id
  for loc,t,h in [('en_us',title,help),('ar_sa',ar,arhelp)]:langs[loc][key+'.title']=t;langs[loc][key+'.help']=h
  d={'display':{'icon':{'item':icon},'title':{'translate':key+'.title'},'description':{'translate':key+'.help'},'frame':'task','show_toast':False,'announce_to_chat':False,'hidden':False},'criteria':{'practice':criterion},'rewards':{'experience':xp}}
  if parent:d['parent']=NS+':learning/'+parent
  else:d['display']['background']='minecraft:textures/block/stone_bricks.png'
  if id.startswith('stock_') and (len(quests)+1)%30==0:d['rewards']['loot']=[NS+':chests/waystation']
  write(f'data/{NS}/advancements/learning/{id}.json',d);quests.append(id)
 biomes=[r['biome'] for r in spec['regional_settlements']]
 descriptions={
  'decor':('Static visual prop: recover its anchor block to reuse it. Leave room around oversized models.','زخرفة ثابتة: اكسر كتلة الارتكاز لاسترجاعها. اترك مساحة حول النماذج الكبيرة.'),
  'aquatic':('Aquatic decoration belongs in water; explore Sunken Kingdom with breathing supplies.','ضع الزينة المائية في الماء، واستعد بالتنفس تحت الماء قبل استكشاف المملكة الغارقة.'),
  'plant':('Use compatible biome soil and light; leave paths clear for companions.','استخدم تربة البايوم المناسبة والإضاءة، واترك الممرات مفتوحة للرفاق.'),
  'sapling':('Leave space above seedlings for tree growth and gather wood before long expeditions.','اترك فراغًا فوق الشتلات لنمو الأشجار واجمع الخشب قبل الرحلات الطويلة.'),
  'leaves':('Foliage makes paths recognizable. Preserve routes back to your base.','استخدم الأوراق لتمييز الطرق وحافظ على مسار واضح للعودة إلى القاعدة.'),
  'log':('Wood supports building and crafting; keep a reserve for your next expedition.','الخشب أساس البناء والصناعة؛ احتفظ بمخزون لرحلتك المقبلة.'),
  'glass':('Transparent blocks help watch nearby guardians from a prepared shelter.','استخدم الكتل الشفافة لمراقبة الحراس القريبين من مأوى مجهز.'),
  'stone':('Build a well-lit shelter and keep entrances wide enough for your army.','ابنِ مأوى مضاءً واترك مداخله واسعة بما يكفي لجيشك.')}
 # 300 specific block lessons: discover, place, stock. IDs are append-only.
 for i,b in enumerate(spec['blocks']):
  id=b['id'];item=NS+':'+id;help,arhelp=descriptions[b['category']]
  en=langs['en_us'].get('block.'+NS+'.'+id,id.replace('_',' '));ar=langs['ar_sa'].get('block.'+NS+'.'+id,en)
  if b['category']=='decor':
   help+=f" Light: {b['light']}/15. Suggested mounting: {b.get('mount','floor')}. Find relics in regional waystations."
   arhelp+=f" الإضاءة: {b['light']} من 15. ابحث عن الزخرفة في الاستراحات الإقليمية وراعِ مساحة النموذج."
  elif id.startswith('sunken_'):
   help+=' Origin: Sunken Kingdom; carry underwater supplies.';arhelp+=' مصدرها المملكة الغارقة؛ جهّز مؤن التنفس تحت الماء.'
  elif id.startswith('abyss_'):
   help+=' Origin: Eternal Abyss; mark your return route.';arhelp+=' مصدرها الهاوية الأبدية؛ علّم طريق العودة.'
  reserve=2 if b['category']=='decor' else 8
  # Ten modest tabs avoid a single giant horizontal advancement tree.
  previous=None if i%10==0 else f"stock_{spec['blocks'][i-1]['id']}"
  lesson('find_'+id,'Discover '+en,'تعرّف على '+ar,'Obtain one. '+help,'احصل على واحدة. '+arhelp,{'trigger':'minecraft:inventory_changed','conditions':{'items':[{'items':[item]}]}},item,5,previous)
  lesson('place_'+id,'Build with '+en,'ابنِ باستخدام '+ar,'Place this block in Echo Realms. '+help,'ضع هذه الكتلة في بُعد الأرواح. '+arhelp,{'trigger':'minecraft:placed_block','conditions':{'block':item,'location':{'dimension':NS+':echo_realms'}}},item,10,'find_'+id)
  lesson('stock_'+id,'Prepare a reserve: '+en,'جهّز مخزونًا: '+ar,f'Keep {reserve} at once for repairs and marking both ends of a route. '+help,f'احتفظ بـ{reserve} معًا للإصلاح وتمييز طرفي الطريق. '+arhelp,{'trigger':'minecraft:inventory_changed','conditions':{'items':[{'items':[item],'count':{'min':reserve}}]}},item,15,'place_'+id)
 # 60 contextual decoration tasks, each with a named target biome.
 for i,id in enumerate(spec['decor_ids']):
  b=next(b for b in spec['blocks'] if b['id']==id);biome=biomes[i%10];item=NS+':'+id
  en=langs['en_us'].get('block.'+NS+'.'+id,id);ar=langs['ar_sa'].get('block.'+NS+'.'+id,en)
  label=langs['ar_sa'].get('biome.'+NS+'.'+biome,biome)
  lesson('landmark_'+id,'Mark '+biome.replace('_',' ')+': '+en,'علّم طريقك في '+label+': '+ar,'Place this relic in the named biome as a recognizable landmark. Leave space around it; it does not lock guardian fights.','ضع الزخرفة في هذا البايوم كعلامة طريق. اترك مساحة حولها؛ الزينة لا تحبس اللاعب في قتال الحراس.',{'trigger':'minecraft:placed_block','conditions':{'block':item,'location':{'dimension':NS+':echo_realms','biome':NS+':'+biome}}},item,15,'find_'+id)
 # 40 region-specific readiness goals teach supplies before combat, not forced wins.
 for biome in biomes:
  label=langs['ar_sa'].get('biome.'+NS+'.'+biome,biome)
  for item,count,en,ar in [('bread',8,'Food','الطعام'),('torch',16,'Light','الإضاءة'),('shield',1,'Defense','الدفاع'),('crafting_table',1,'Field crafting','صناعة ميدانية')]:
   lesson('prepare_'+biome+'_'+item,en+' for '+biome.replace('_',' '),ar+' في '+label,f'Obtain or move {count} {item} in your inventory while in this biome. Scout, prepare an escape route and approach guardians when ready.',f'اجمع أو انقل {count} من الغرض داخل حقيبتك أثناء وجودك في البايوم. استكشف وجهّز طريق هروب قبل مواجهة الحراس.',{'trigger':'minecraft:inventory_changed','conditions':{'player':{'location':{'dimension':NS+':echo_realms','biome':NS+':'+biome}},'items':[{'items':['minecraft:'+item],'count':{'min':count}}]}},'minecraft:'+item,15,'find_'+spec['blocks'][0]['id'])
 # 100 preparation/crafting lessons: learn a real vanilla recipe, then obtain its product.
 for i,item in enumerate(VANILLA):
  previous='find_'+spec['blocks'][0]['id'] if i==0 else 'kit_'+VANILLA[i-1]
  label=item.replace('_',' ').title()
  lesson('recipe_'+item,'Learn the recipe: '+label,'تعلّم وصفة '+label,'Unlock this recipe in your recipe book. Learn the ingredients before gathering expedition equipment.','افتح هذه الوصفة في كتاب الوصفات وتعرّف على مكوناتها قبل تجهيز معدات الرحلة.',{'trigger':'minecraft:recipe_unlocked','conditions':{'recipe':'minecraft:'+item}},'minecraft:'+item,5,previous)
  lesson('kit_'+item,'Pack '+label,'جهّز '+label,'Obtain the finished item. Use the recipe book to replenish your kit; new companions still need safe spawn space.','احصل على الغرض النهائي. استخدم كتاب الوصفات لتجديد معداتك، واترك مساحة آمنة لظهور رفاقك.',{'trigger':'minecraft:inventory_changed','conditions':{'items':[{'items':['minecraft:'+item]}]}},'minecraft:'+item,10,'recipe_'+item)
 assert len(quests)==500 and len(set(quests))==500
 for loc,d in langs.items():(out/f'assets/{NS}/lang/{loc}.json').write_text(json.dumps(d,ensure_ascii=False,indent=2)+'\n')
 spec['learning_quests']=quests;(out/'integration-spec.json').write_text(json.dumps(spec,ensure_ascii=False,indent=2)+'\n')
 print('Added exactly 500 additive, localized, one-time learning advancement quests (300 block, 60 landmark, 40 readiness, 100 crafting).')
if __name__=='__main__':
 p=argparse.ArgumentParser();p.add_argument('--resources',type=Path,required=True);build(p.parse_args().resources)
