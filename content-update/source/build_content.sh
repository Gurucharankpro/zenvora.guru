#!/usr/bin/env bash
set -euo pipefail
source_dir=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)
task_dir=$(dirname "$source_dir")
repo=/workspace/zenvora.guru
base="$task_dir/../soulbound-army-1.20.1-1.8.1-reviewed.jar"
export JAVA_HOME="$task_dir/toolchain/jdk-17.0.17+10"
export GRADLE_USER_HOME="$task_dir/gradle-home"
export PYTHONPATH="/workspace/mod-integration/tools/python${PYTHONPATH:+:$PYTHONPATH}"
printf '%s  %s\n' 2b9f00f36e7d1228fe31838daf6c147b5f5c719211d3f8467fedac88eba4da26 "$base" | sha256sum --check --status
cd "$task_dir"
python3 "$source_dir/prepare_content.py"
python3 "$source_dir/place_decor.py"
python3 "$source_dir/verify_content.py"
python3 "$source_dir/test_generator_repairs.py"
# This is a native ForgeGradle source set. Never replace it with an ABI fixture
# compiler, skip a failed task, or copy an old helper-classes output into release.
mapfile -t proxy_args < <(python3 "$source_dir/gradle_proxy.py")
"$task_dir/toolchain/gradle-8.8/bin/gradle" "${proxy_args[@]}" --no-daemon --console=plain --max-workers=2 -p "$repo/native-review/qa" repairJar reobfRepairJar
overlay="$repo/native-review/qa/build/libs/soulbound-181-qa-1.0-repair.jar"
test -s "$overlay"
mkdir -p "$task_dir/build/patcher" "$task_dir/dist"
"$JAVA_HOME/bin/javac" -cp '/workspace/mod-integration/tools/*' -d "$task_dir/build/patcher" "$source_dir/PatchReviewed.java" "$source_dir/CatalogueTest.java" "$source_dir/FinalCatalogueTest.java"
"$JAVA_HOME/bin/java" -cp "/workspace/mod-integration/tools/*:$task_dir/build/patcher" CatalogueTest "$base" "$task_dir/content-spec.json" "$task_dir/resources" /workspace/mod-integration/tools/gson.jar
jar="$task_dir/dist/soulbound-army-1.20.1-1.8.1-content.jar"
"$JAVA_HOME/bin/java" -cp "/workspace/mod-integration/tools/*:$task_dir/build/patcher" PatchReviewed "$base" "$task_dir/resources" "$task_dir/content-spec.json" "$overlay" "$jar"
"$JAVA_HOME/bin/java" -cp '/workspace/mod-integration/tools/*' "$repo/integration/OwnLinkageTest.java" "$jar"
python3 "$source_dir/verify_release.py" "$jar"
"$JAVA_HOME/bin/java" -cp "/workspace/mod-integration/tools/*:$task_dir/build/patcher" FinalCatalogueTest "$base" "$jar" /workspace/mod-integration/tools/gson.jar
sha256sum "$jar"
python3 "$source_dir/package_phone.py" "$jar" "$task_dir/phone-delivery" --version 1.8.1
