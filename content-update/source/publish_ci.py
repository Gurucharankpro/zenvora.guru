"""Write truthful delivery status and reviewer handoff after the CI build step."""
import hashlib
import json
import os
from pathlib import Path

root=Path(__file__).resolve().parents[2]
status=os.environ['BUILD_OUTCOME'];ok=status=='success'
report=root/'content-update/reports';report.mkdir(parents=True,exist_ok=True)
data={'status':status,'source_commit':os.environ['GITHUB_SHA'],'run_id':os.environ['GITHUB_RUN_ID'],'production_built':ok,'game_launched':False}
if ok:
 jar=root/'downloads/current/soulbound-army-1.20.1-1.8.1-content.jar'
 data.update(jar_sha256=hashlib.sha256(jar.read_bytes()).hexdigest(),jar_bytes=jar.stat().st_size)
(report/'CI-RESULT.json').write_text(json.dumps(data,indent=2)+'\n')
sha=data.get('jar_sha256','NOT BUILT')
readme=f'''# Soulbound Army — content update on reviewed 1.8.1

**Build status: {status.upper()}. Final updated JAR built: {str(ok).lower()}.**

Source commit: `{data['source_commit']}`.
Base: `1.8.1-reviewed`, SHA256
`2b9f00f36e7d1228fe31838daf6c147b5f5c719211d3f8467fedac88eba4da26`.
Output SHA256: `{sha}`.

New content: Morsi and mobile shop (Crimson Shadow); Porcelain Duchess and palace
(Moonveil Hollow); Yuta/Rika and academy (Crimson Shadow). Cursed Bell Ringer is
already present and its supplied native model/animations/textures match; no second
registry entry was created. All 51 old profiles retain their order/definitions.
60 new decor designs are registered/craftable and placed in matching habitats.
X2 provides 20 registered effects/182 sprites, five verified existing-biome
bindings, ten registered ambient sound assets and bounded client ambience.
Its five absent biome profiles do not create invented biomes. X4 adds server-owned
controls, follow/recovery/building/combat assistance, config and a scrolling GUI.

Read [the reviewer prompt](NEXT-AGENT-PROMPT-AR.md) and [the actual build log](../content-update/reports/CI-BUILD.log).
Game/render/multiplayer tests have NOT been run for this new update.

'''
if ok:
 readme+='''## Downloads

- [Complete updated JAR](../downloads/current/soulbound-army-1.20.1-1.8.1-content.jar?raw=true)
- [Phone split volumes, all <=8 MiB](../downloads/current)
- [Output fingerprint and changed entries](../downloads/current/CONTENT-MANIFEST.json)

Download every matching .z01/.z02/.../.zip into one folder and open the LAST .zip
with ZArchiver. The extracted JAR is verified against the built artifact by CRC
and SHA-256. Replace the old JAR; install this same new file on both sides.
Mod version remains 1.8.1-reviewed as requested, content protocol is38-content-x2-x4.
The file in content-update/base is only the input, not the updated mod.
'''
else:
 readme+='''## Not a finished distribution

No final updated JAR is claimed. Read CI-BUILD.log, fix the reported build issue,
and rerun the real Forge build. Do not install the input JAR as the new release.
'''
readme+='''
## Navigation

- [Native production/QA project](../native-review)
- [Overlay/resource build source](../content-update/source)
- [Supplied assets](../content-update/inputs)
- [Staged resources](../content-update/resources)

This is a binary overlay and focused native project, not the original complete
Forge source tree. Historical fixture compilation is retired and never used here.
Reviewed OpenMythics/X1Hooks bytecode, the X1 atlas, 500 old lessons, save identities
and the mod version are explicitly protected by final-distribution comparison.
'''
review=root/'review';review.mkdir(exist_ok=True)
(review/'README.md').write_text(readme)
prompt=f'''# تعليمات اختبار Soulbound Army بعد دمج الحزم

حالة البناء: {status}. بصمة الملف الجديد: `{sha}`.
لا تعتبر الملف الموجود داخل content-update/base نسخة محدثة.

ابدأ من review/README.md وcontent-update/reports/CI-RESULT.json وCI-BUILD.log.
النسخة الكاملة وأجزاء الهاتف في downloads/current عند نجاح البناء فقط.
Forge 1.20.1 /47.3+ وJava17، modid shadowsovereign. ملفات native-review مشروع
إصلاح/إضافة حقيقي محدود، وليس السورس الأصلي الكامل. لا تشغّل historical
integration/build.sh ولا أي مولد قديم على النسخة الأحدث.

1. تأكد من SHA وCRC ثم اختبر التحميل بسيرفر وعميل منفصلين وعالم اختبار.
2. تحقق من المخلوقات الجديدة الثلاثة: مرسي/متجره، الدوقة/قصرها، يوتا/أكاديميته.
   افحص ارتفاع ودوران المباني وموضع الحارس، خصوصًا حيز يوتا المضاف كهواء صريح.
3. اختبر الحصول على الروح والكتاب والبوصلة والمجموعة والبيض ثم استدعاء الجيش
   والاسترجاع والموت والحفظ وإعادة الفتح. لا توجد إضافة ثانية لقارع الجرس.
4. اختبر Rika والخامات والشفافية والتوهج والحركات وتوقيت التأثير/الضرر.
5. اختبر الهروب: حذف الجسم والـUUID واللقطة والهجمات والشريط، العودة بصحة
   كاملة، منع جسم قديم متأخر من الظهور، واستدعاء مخلوق آخر والتكسير طبيعيًا.
6. افحص الديكور الستين والوصفات وفتحها والتبويب والاتجاه والحدود والقطع الطويلة.
   أُضيف الديكور في هواء الأبنية، ولم تُمسح كتلها الصلبة أو غنائمها.
7. اختبر X2 بأنواعه المسجلة وصوره الـ182 وأصواته. الربط التلقائي بخمسة بايومات
   موجودة فقط؛ لا توجد البايومات الخمسة الأخرى في الأساس. افحص حدود الأعداد
   والضباب أثناء القتال وتغير العالم وجودة LOW/MEDIUM/HIGH والأداء على الهاتف.
8. افتح X4 بزر U أو /soulboundautopilot. اختبر جميع الأزرار والتمرير بالسحب،
   FOLLOW مقابل STAY/GUARD/PATROL والأهداف الصريحة، البناء، الممرات الضيقة،
   التعثر، التجميع، منع الأماكن الخطرة والقطع غير المحملة، والانتقال المعطل
   افتراضيًا. اختبر ملكية لاعبين والرسائل المتكررة والحفظ وتغيير البعد.
9. راجع ميزانيات العمل والبحث وعدد المهاجمين واستقرار الأهداف والرؤية وعدم
   تعطيل AI الأصلي. الأداء والتوازن ودقة كل إطار لم تُثبت باختبارات الموارد.

البناء والفحوص الساكنة لا تثبت الرسم أو codecs أو اللعب الجماعي. سجّل ما
شغّلته فعلًا، مع نسخ اللعبة/Forge والسجلات وخطوات إعادة كل مشكلة. لا تعطّل
حواجز منع التكرار لإجبار سيناريو متعجل على النجاح، ولا تستخدم حفظ المستخدم.
'''
(review/'NEXT-AGENT-PROMPT-AR.md').write_text(prompt)
(root/'README.md').write_text('# Soulbound Army content update\n\n[Start here / ابدأ من هنا](review/README.md).\n\nBuild status: **'+status+'**.\n')
