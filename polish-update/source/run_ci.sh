#!/usr/bin/env bash
set -euo pipefail
task_dir=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/.." && pwd)
repo=$(cd -- "$task_dir/.." && pwd)
export SOULBOUND_BASE_JAR="$task_dir/base/soulbound-army-1.20.1-1.8.1-repaired-content.jar"
cd "$task_dir"
printf '%s  %s\n' f88cac49bae0d10961a277ab2277738892cb5ba4aaaae596fc5b0c17d64edb04 "$SOULBOUND_BASE_JAR" | sha256sum --check
python3 source/prepare_content.py
python3 source/place_decor.py
python3 source/visual_polish.py
python3 source/verify_resources.py
mkdir -p build/tools build/patcher dist
fetch() {
 local url="$1" name="$2" expected="$3"
 curl --fail --location --silent --show-error "$url" -o "build/tools/$name"
 printf '%s  %s\n' "$expected" "build/tools/$name" | sha256sum --check --status
}
fetch https://repo.maven.apache.org/maven2/org/ow2/asm/asm/9.7.1/asm-9.7.1.jar asm.jar 8cadd43ac5eb6d09de05faecca38b917a040bb9139c7edeb4cc81c740b713281
fetch https://repo.maven.apache.org/maven2/org/ow2/asm/asm-tree/9.7.1/asm-tree-9.7.1.jar asm-tree.jar 9929881f59eb6b840e86d54570c77b59ce721d104e6dfd7a40978991c2d3b41f
fetch https://repo.maven.apache.org/maven2/org/ow2/asm/asm-analysis/9.7.1/asm-analysis-9.7.1.jar asm-analysis.jar 85b29371884ba31bb76edf22323c2c24e172c3267a67152eba3d1ccc2e041ef2
fetch https://repo.maven.apache.org/maven2/com/google/code/gson/gson/2.10.1/gson-2.10.1.jar gson.jar 4241c14a7727c34feea6507ec801318a3d4a90f070e4525681079fb94ee4c593
javac -cp 'build/tools/*' -d build/patcher source/PatchPolish.java source/CatalogueTest.java source/FinalCatalogueTest.java source/JdkBootstrapTest.java
python3 - <<'PY'
import os,shutil
from pathlib import Path
root=Path('../native-review/qa/localrepo/local/soulbound/army/1.8.1-polishbase');root.mkdir(parents=True,exist_ok=True)
shutil.copyfile(os.environ['SOULBOUND_BASE_JAR'],root/'army-1.8.1-polishbase.jar')
(root/'army-1.8.1-polishbase.pom').write_text('<project><modelVersion>4.0.0</modelVersion><groupId>local.soulbound</groupId><artifactId>army</artifactId><version>1.8.1-polishbase</version></project>\n')
Path('../native-review/qa/gradle.properties').write_text('org.gradle.jvmargs=-Xmx3G -Dfile.encoding=UTF-8\norg.gradle.daemon=false\norg.gradle.workers.max=2\norg.gradle.parallel=false\n')
PY
# The only production Java compiler is ForgeGradle using actual engine/mod APIs.
gradle --no-daemon --console=plain --max-workers=2 -p ../native-review/qa -PqaArtifact=1.8.1-polishbase repairJar reobfRepairJar jar reobfJar
overlay="$repo/native-review/qa/build/libs/soulbound-181-qa-1.0-repair.jar"
test -s "$overlay"
jar="$task_dir/dist/soulbound-army-1.20.1-1.8.1-polished.jar"
java -cp 'build/tools/*:build/patcher' PatchPolish "$SOULBOUND_BASE_JAR" resources content-spec.json "$overlay" "$jar"
java -cp 'build/tools/*' "$repo/integration/OwnLinkageTest.java" "$jar"
java -cp 'build/tools/*:build/patcher' JdkBootstrapTest "$jar"
python3 source/verify_release.py "$jar"
java -cp 'build/tools/*:build/patcher' FinalCatalogueTest "$SOULBOUND_BASE_JAR" "$jar" build/tools/gson.jar
python3 source/server_smoke.py
phone_dir=$(mktemp -d "$task_dir/build/phone.XXXXXX")
python3 source/package_phone.py "$jar" "$phone_dir" --version 1.8.1
cp "$jar" "$phone_dir/"
cp dist/CONTENT-MANIFEST.json "$phone_dir/"
# Stage a complete verified delivery before replacing the generated current
# directory; a subsequent CI run must never mix its volumes with old ones.
python3 - "$phone_dir" "$repo/downloads/current" <<'PY'
import sys,shutil
from pathlib import Path
source,target=map(Path,sys.argv[1:])
if target.exists():shutil.rmtree(target)
target.parent.mkdir(parents=True,exist_ok=True)
shutil.move(str(source),str(target))
PY
sha256sum "$jar"
echo 'CONTENT BUILD PASSED: real-API compile/reobfuscation, catalogue/resources, owned linkage, exact overlay and complete 8 MiB split delivery'
