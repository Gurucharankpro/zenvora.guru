#!/usr/bin/env bash
set -euo pipefail
# The original replay starts at 1.2.0 and compiled gameplay against ABI fixtures.
# It is retained in Git history, not as a release path that can erase reviewed fixes.
echo 'Historical binary replay is retired. Build native-review/qa repairJar reobfRepairJar against the hash-verified current JAR and overlay only intended changes.' >&2
exit 2
