#!/usr/bin/env bash
set -euo pipefail
if [ "$#" -ne 3 ]; then
  echo 'Usage: bash build.sh BASE.jar EXTRACTED_INPUTS_DIR BUILD_DIR' >&2
  exit 2
fi
base_jar=$(realpath "$1")
input_dir=$(realpath "$2")
mkdir -p "$3"
build_dir=$(realpath "$3")
source_dir=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)
mkdir -p "$build_dir/tools"
cd "$build_dir/tools"
fetch() {
  local url="$1" name="$2" expected="$3"
  if [ ! -f "$name" ]; then curl --fail --location --silent --show-error "$url" -o "$name"; fi
  printf '%s  %s\n' "$expected" "$name" | sha256sum --check --status
}
fetch https://repo.maven.apache.org/maven2/org/ow2/asm/asm/9.7.1/asm-9.7.1.jar asm.jar 8cadd43ac5eb6d09de05faecca38b917a040bb9139c7edeb4cc81c740b713281
fetch https://repo.maven.apache.org/maven2/org/ow2/asm/asm-tree/9.7.1/asm-tree-9.7.1.jar asm-tree.jar 9929881f59eb6b840e86d54570c77b59ce721d104e6dfd7a40978991c2d3b41f
fetch https://repo.maven.apache.org/maven2/org/ow2/asm/asm-analysis/9.7.1/asm-analysis-9.7.1.jar asm-analysis.jar 85b29371884ba31bb76edf22323c2c24e172c3267a67152eba3d1ccc2e041ef2
fetch https://repo.maven.apache.org/maven2/org/ow2/asm/asm-commons/9.7.1/asm-commons-9.7.1.jar asm-commons.jar 9a579b54d292ad9be171d4313fd4739c635592c2b5ac3a459bbd1049cddec6a0
fetch https://repo.maven.apache.org/maven2/com/google/code/gson/gson/2.10.1/gson-2.10.1.jar gson.jar 4241c14a7727c34feea6507ec801318a3d4a90f070e4525681079fb94ee4c593
export PYTHONPATH="$build_dir/tools/python${PYTHONPATH:+:$PYTHONPATH}"
if ! python3 -c 'import nbtlib, numpy; assert nbtlib.__version__ == "2.0.4" and numpy.__version__ == "2.5.4"'; then
  python3 -m pip install --target "$build_dir/tools/python" --upgrade nbtlib==2.0.4 numpy==2.5.4
fi
resources=$(mktemp -d "$build_dir/resources.XXXXXX")
python3 "$source_dir/prepare.py" --inputs "$input_dir" --base "$base_jar" --out "$resources"
python3 "$source_dir/decor_packs.py" --inputs "$input_dir" --base "$base_jar" --out "$resources"
python3 "$source_dir/living_realms.py" --resources "$resources"
python3 "$source_dir/learning_quests.py" --resources "$resources"
python3 "$source_dir/latest_packs.py" --inputs "$input_dir" --resources "$resources"
python3 "$source_dir/x1_assets.py" --inputs "$input_dir" --resources "$resources"
python3 "$source_dir/polish_assets.py" --base "$base_jar" --out "$resources"
python3 "$source_dir/compile_helper.py" "$build_dir" "$resources"
jar="$build_dir/dist/soulbound-army-1.20.1-1.8.1-shadow-awakening-fix.jar"
java --class-path "$build_dir/tools/*" "$source_dir/PatchMod.java" "$base_jar" "$resources" "$jar"
previous_jar="$build_dir/dist/soulbound-army-1.20.1-1.8.0-shadow-awakening-preview.jar"
previous_args=()
if [ -f "$previous_jar" ]; then previous_args=(--previous "$previous_jar"); fi
java --class-path "$build_dir/tools/*" "$source_dir/OwnLinkageTest.java" "$jar"
java --class-path "$build_dir/tools/*" "$source_dir/RegistryTest.java" "$base_jar" "$jar" "$build_dir/tools/gson.jar" "${previous_args[@]}"
python3 "$source_dir/verify.py" --resources "$resources" --jar "$jar" --base "$base_jar" "${previous_args[@]}"
sha256sum "$jar"
printf 'Resource output: %s\n' "$resources"
