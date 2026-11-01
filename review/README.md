# Soulbound Army 1.8.1 — ابدأ المراجعة هنا

هذه صفحة تسليم للذكاء الاصطناعي لمراجعة آخر نسخة مصححة من المود من GitHub،
بدون رفع ملفات كبيرة إلى المحادثة. **كود Java وملفات البيانات متاحة كملفات
منفصلة للقراءة مباشرة**، ولا تحتاج فتح ZIP لقراءة هذه الملفات.

هذه نسخة 1.8.1 نفسها، وليست تعديلًا جديدًا على أسلوب اللعب. بصمة JAR:
`beeedcd097ebbfc8dc212f34b52d6fd3f6d09504aacced44bfd249aa7a16cf3d`.

## ابعت الرسالة دي للذكاء الاصطناعي

> راجع Soulbound Army 1.8.1 من هذا الفرع تحديدًا:
> `ai-review-soulbound-army-1.8.1`. اقرأ صفحة review/README.md، ثم تقرير
> الاختبارات وملفات integration. افحص خطأ تحميل GloomContent، والاستدعاء
> والهروب من المخلوقات الأسطورية، والأبعاد والمهام والديكور. كود Java المفكوك
> في review/decompiled وملفات البيانات في review/resources متاحان مباشرة.
> استخدم GitHub أو git clone حسب أدواتك. ميّز الاختبار الفعلي من الفحص الساكن،
> واذكر مسار الملف والدليل وخطوات إعادة كل خطأ تجده. لا تفترض أن كود Java
> المفكوك مشروع Forge أصلي قابل للبناء. لو لا تستطيع فتح الرابط فقل ذلك.

## الملفات المهمة

| المحتوى | الرابط |
| --- | --- |
| تقرير ما اختُبر وما لم يُختبر | [TEST-REPORT.md](../downloads/TEST-REPORT.md) |
| خطوات المراجعة بالتفصيل | [AI-REVIEW.md](../downloads/AI-REVIEW.md) |
| كود التعديلات والاختبارات | [integration](../integration) |
| فهرس 287 ملف Java مفكوكًا من النسخة الحالية | [JAVA-INDEX.md](JAVA-INDEX.md) |
| ملفات البيانات النصية، 3458 ملفًا مطابقًا للمود | [resources](resources) |
| بصمات ملفات المراجعة وبصمة المود | [MANIFEST.json](MANIFEST.json) |
| نتائج البناء والاختبارات | [BUILD-TEST.log](../downloads/BUILD-TEST.log) |
| نتيجة الفحص على النسخة القديمة التي بها الخطأ | [known-bad-linkage.log](../downloads/known-bad-linkage.log) |
| المود الكامل وأجزاؤه وطريقة تجميعه | [downloads](../downloads) |

## نقاط بداية مباشرة في الكود

- [PatchMod.java](../integration/PatchMod.java): تعديل البايت كود وإصلاح دالة التسجيل.
- [OwnLinkageTest.java](../integration/OwnLinkageTest.java): الفحص الذي يكشف الخطأ القديم.
- [OpenMythics.java](../integration/OpenMythics.java): التعامل المفتوح مع المواجهات الأسطورية.
- [X1Hooks.java](../integration/X1Hooks.java): ربط المؤثرات بالأحداث.
- [GloomContent.java](decompiled/dev/shadowsovereign/gloom/GloomContent.java): التسجيلات الأصلية والدالة الصحيحة food.
- [GloomRealm.java](decompiled/dev/shadowsovereign/gloom/GloomRealm.java): البعد وتكوينه.
- [MythicExpedition.java](decompiled/dev/shadowsovereign/mythic/MythicExpedition.java): الاستدعاء والمواجهة.
- [MythicBeast.java](decompiled/dev/shadowsovereign/mythic/MythicBeast.java): سلوك المخلوقات الأسطورية.
- [ShadowService.java](decompiled/dev/shadowsovereign/army/ShadowService.java): إدارة الجيش والاستدعاء والاسترجاع.

لو GitHub لا يعرض ملف JSON كبيرًا، استخدم زر **Raw** أو اقرأه من نسخة git.
الفهرس والملفات المفكوكة مرجع للمراجعة. الجرافيك والصوت وملفات NBT الثنائية
وملفات class تظل متاحة في المود الكامل داخل downloads؛ ليست ضمن resources
النصية هنا. لا تحتاج تحميلها لمجرد قراءة الكود والبيانات النصية.

## لو عندك طرفية / أداة برمجة

```bash
git clone --depth 1 --single-branch --branch ai-review-soulbound-army-1.8.1 https://github.com/Gurucharankpro/zenvora.guru.git soulbound-review
cd soulbound-review
```

هذا يتجاوز الحاجة لإرفاق المود في المحادثة؛ حد ملفات المحادثة لا يحدد حجم
git clone، لكن الأداة الأخرى قد تكون لها حدود تنزيل أو قراءة خاصة بها.
إذا لم تكن لديك صلاحية تصفح أو الوصول إلى GitHub، فالرابط وحده لا يمنحك تلك
القدرة. الوصول إلى مستودع خاص يحتاج توصيل GitHub وصلاحية للمستودع من صاحبه.

## حدود المراجعة

Java هنا ناتج CFR 0.152 من JAR المصحح نفسه، وليس المشروع الأصلي الكامل القابل
للبناء. الدالة SoulPlay.tick لم تُفك بنيويًا بالكامل؛ اقرأ [CFR-NOTES.txt](CFR-NOTES.txt)
وافحص البايت كود عند الحاجة. ملفات البيانات نُسخت بايتًا ببايت من JAR.

نجحت اختبارات الموارد والمحاكاة وفحص الربط الموثقة. **لم يُشغَّل Forge فعليًا
هنا** بسبب سياسة الشبكة؛ لا تعتبر ذلك ضمانًا بعدم وجود أخطاء أخرى. لم تُعد
اختبارات اللعب من أجل إضافة صفحة المراجعة؛ ملف المود لم يتغير.
