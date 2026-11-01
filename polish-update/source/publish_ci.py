"""Report actual build/runtime evidence; never present an input JAR as an update."""
import hashlib,json,os
from pathlib import Path
repo=Path(__file__).resolve().parents[2];root=repo/'polish-update';report=root/'reports';report.mkdir(parents=True,exist_ok=True)
status=os.environ['BUILD_OUTCOME'];ok=status=='success'
result={'status':status,'source_commit':os.environ['GITHUB_SHA'],'run_id':os.environ['GITHUB_RUN_ID'],'production_built':ok,'graphical_client_tested':False,'multiplayer_tested':False}
smoke=report/'PRODUCTION-SMOKE.json'
if smoke.exists():result['production_smoke']=json.loads(smoke.read_text())
if ok:
 jar=repo/'downloads/current/soulbound-army-1.20.1-1.8.1-polished.jar';result.update(jar_sha256=hashlib.sha256(jar.read_bytes()).hexdigest(),jar_bytes=jar.stat().st_size)
(report/'CI-RESULT.json').write_text(json.dumps(result,indent=2)+'\n');sha=result.get('jar_sha256','NOT BUILT')
review=repo/'review';review.mkdir(exist_ok=True)
text=f'''# Soulbound Army — reviewed 1.8.1 polish and Singing Glass Desert

Build: **{status.upper()}**. Source: `{result['source_commit']}`.
Final output SHA256: `{sha}`.
Minecraft 1.20.1, Forge 47.3.22, Java 17, modid `shadowsovereign`.
Mod version remains `1.8.1-reviewed`; matching client/server protocol is `39-polish-glass-desert`.

This update starts from the latest complete aa4e701d content distribution, repairs
its 63 incorrect LambdaMetafactory bootstrap handles and debug-log minimum, then
applies the new content. The reconstructed repair input SHA is
`f88cac49bae0d10961a277ab2277738892cb5ba4aaaae596fc5b0c17d64edb04`.
The supplied reports describe a b198b947 corrected file, but that binary was not
attached; we do not claim our reconstructed ZIP is byte-identical to it. The
repair changes exactly GloomContent.class and ContentConfig.class, preserving
every other uncompressed entry of the latest complete distribution.

New content:
- Dr. Salmawy and Secret Mansion: Crimson Shadow. Data marker removed from body
  space, dining entrance opened, last basement stair completed; all six supplied
  loot chests retained. No second copy of the optional template is registered.
- Raven Seamstress and Theater of Black Curtains: Moonveil Hollow. All four arms,
  three attached ravens, original UVs and numeric animation tracks are retained.
- Clockwork Widow and Palace of Broken Hours: Moonveil Hollow, underground arena.
  Full 788-cube supplied model and 10 authored clips retained. Empty chests in the
  two new Eclipse habitats receive existing vanilla loot tables.
- 20 Eclipse props: registered, craftable, in the decor tab and conservatively
  placed in matching future habitats. Valid supplied Java exports are used;
  illegal full-scale vanilla model coordinates are not shipped as block models.
  The pack's Pale Broker naming does not replace existing Yuta or invent a mob.
- Singing Glass Desert: appended to Echo Realms' multi-noise source, preserving
  the ten old biome entries. 20 supplied environmental blocks/plants, 4 buildings
  and 4 tree templates, passive Prism Jerboa, glowing crystals, vegetation and
  bounded oases. X2's existing desert sound and two effects are now biome-bound.
  All seven new buildings share the reviewed cross-type structure placement.
- Larger book previews use sampled visible idle-body bounds, excluding hidden FX.
  Combat scale/hitboxes and saved armies are unchanged. Four distinct new egg
  images and 16 bespoke journey badge images, including the new biome visit.
  Fifteen compass placeholders are replaced; the two compass lessons keep their
  meaningful compass icons. Existing 500 lessons and rewards remain byte-identical.

Verification: see [CI log](../polish-update/reports/CI-BUILD.log),
[runtime log](../polish-update/reports/PRODUCTION-SERVER.log),
[native results](../polish-update/reports/PRODUCTION-SMOKE.json),
[result summary](../polish-update/reports/CI-RESULT.json).
Source compilation uses actual Forge/Minecraft APIs. Final obfuscated JAR checks
include Java 17 bootstrap resolution and exact unchanged-entry comparison.
On successful builds, an official isolated Forge server loads the final JAR with
a separate QA mod, checks real block properties/template decodes/entity attributes
and save/reload, locates the new biome, requests generation, flushes and stops.
No graphical client, mobile rendering, long combat session or multiplayer test
is claimed. Visual quality, performance, structure entrances/rotation across
terrain and complete encounter/recruit/retreat gameplay need in-game review.
Worldgen frequency/terrain figures in the uploaded pack are guidance; the native
implementation uses conservative bounded generation, not a literal new noise
router for every dune or every described formation variant.

'''
if ok:
 text+='''## Downloads

- [Complete updated JAR](../downloads/current/soulbound-army-1.20.1-1.8.1-polished.jar?raw=true)
- [All phone volumes, each <=8 MiB](../downloads/current)
- [Exact changed-entry list and fingerprint](../downloads/current/CONTENT-MANIFEST.json)

Download every matching .z01/.z02/.../.zip into one folder, then extract the LAST
.zip with ZArchiver. CRC and extracted JAR SHA are verified against this build.
Replace the previous JAR with this same file on client/server. Do not install the
input under polish-update/base as the new mod. QA mod is never part of delivery.
'''
else:text+='\nNo final release is claimed. Read the failed build/runtime log; do not install the input as an update.\n'
(review/'README.md').write_text(text)
(review/'NEXT-AGENT-PROMPT-AR.md').write_text(f'''# مراجعة التحديث الجديد وSinging Glass Desert

الحالة: {status}. بصمة JAR النهائي: `{sha}`.
ابدأ من review/README.md ثم polish-update/reports/CI-RESULT.json والسجلات.
استخدم downloads/current فقط عند نجاح البناء؛ ملفات base مدخلات تاريخية.
المشروع إصلاحات وإضافات محدودة فوق آخر JAR كامل، وليس السورس الأصلي الكامل.

افحص الجديد والأجزاء المتأثرة في عالم منفصل:
1. بدء عميل Forge 1.20.1/47.3.22 مع Java17 بالنسخة النهائية، ومطابقة السيرفر.
2. معاينات الكتاب على شاشة صغيرة: التكبير، السحب، التمرير، العربية والإنجليزية،
   عدم قص الجسم أو Rika، وعدم تغير حجم الكائن في العالم.
3. الصور الست عشرة للإنجازات وبيض المخلوقات الأربعة، والخامات والتوهج والحركات.
   لم تتغير شروط أو مكافآت إنجازات قديمة، ولم تتغير دروس البوصلة الدالة عليها.
4. السلماوي/قصره في Crimson Shadow؛ الخياطة/مسرحها والأرملة/قصرها في Moonveil
   Hollow. انتظر تحميل القطع وجاهزية بيانات العميل قبل الاستدعاء؛ افحص منصة
   الحارس وارتفاع المبنى ودورانه ومداخله ومسار القبو والغنائم وتوقيت الضرر.
5. أسر الروح والجيش والملكية والكتاب والبوصلة والحفظ وإعادة الفتح. الهروب يجب
   أن ينهي المواجهة ويحذف الجسم وUUID واللقطة والهجمات والشريط، وتبدأ العودة
   بصحة كاملة، مع استدعاء الجيش والتكسير ومواجهة أسطوري آخر طبيعيًا.
6. الديكور العشرين في تبويب الديكور ووصفاته والأبنية، دون محو كتل أو غنائم قديمة.
7. بايوم صحراء الزجاج المغني داخل Echo Realms: الرمل والبلور والنباتات والأشجار
   والواحات واليربوع و4 مبانٍ، والإضاءة والضباب والصوت وX2 وحدود الأداء.
   التوليد الجديد للقطع الجديدة فقط؛ لا تعاد كتابة أبنية أو قطع اللاعب القديمة.
8. السيرفر الآلي اختبر codecs والتسجيلات والخصائص وحفظ الكيانات/تحميلها فقط؛
   لا تعتبر ذلك اختبار رسم أو إثباتًا لكل القتال أو اللعب الجماعي. لا تعطل حواجز
   منع التكرار لإخفاء استعجال سكربت الاختبار. سجل ما نفذته وما تعذر مع السجلات.
''')
(repo/'README.md').write_text('# Soulbound Army — current update\n\n[Downloads and actual test evidence / التحميل والفحوص](review/README.md).\n\nBuild status: **'+status+'**.\n')
