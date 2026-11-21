#!/usr/bin/env bash
#
# CurseForge 文件上传脚本
#
# 用法：
#   export CURSEFORGE_TOKEN='你的 API Token'      # 不要写进仓库、不要提交
#   export PROJECT_ID='123456'                    # 项目页 URL 里的数字
#   ./release/curseforge-upload.sh                # 默认取 build/libs 下的 jar
#
# Token 获取：https://authors.curseforge.com/#/settings/api-tokens
# 官方文档：https://support.curseforge.com/support/solutions/articles/9000197321-curseforge-upload-api
#
set -euo pipefail

: "${CURSEFORGE_TOKEN:?请先 export CURSEFORGE_TOKEN}"
: "${PROJECT_ID:?请先 export PROJECT_ID}"

HERE="$(cd "$(dirname "$0")" && pwd)"
ROOT="$(dirname "$HERE")"
JAR="${1:-$ROOT/build/libs/boiling-cauldron-1.0.0-forge.jar}"
CHANGELOG="$HERE/release-notes-1.0.0-forge.md"
API="https://minecraft.curseforge.com/api"

[ -f "$JAR" ] || { echo "找不到 jar：$JAR" >&2; exit 1; }
[ -f "$CHANGELOG" ] || { echo "找不到更新日志：$CHANGELOG" >&2; exit 1; }

echo "→ 查询 CurseForge 游戏版本 ID ..."
VERSIONS_JSON=$(curl -sS --max-time 30 \
  -H "X-Api-Token: ${CURSEFORGE_TOKEN}" \
  "${API}/game/versions")

echo "  匹配到的版本条目："
GAME_IDS=$(printf '%s' "$VERSIONS_JSON" | python3 "$HERE/resolve_versions.py")
echo "  使用 ID：$GAME_IDS"

echo "→ 生成 metadata ..."
META_FILE=$(mktemp)
python3 - "$CHANGELOG" "$GAME_IDS" "$(basename "$JAR")" > "$META_FILE" <<'PY'
import json
import sys

changelog_path, ids_csv, display_name = sys.argv[1], sys.argv[2], sys.argv[3]
with open(changelog_path, encoding="utf-8") as fh:
    changelog = fh.read()

metadata = {
    "changelog": changelog,
    "changelogType": "markdown",
    "displayName": display_name,
    "releaseType": "release",
    "gameVersions": [int(x) for x in ids_csv.split(",") if x],
    "relations": {
        "projects": [
            {"slug": "kaleidoscope-cookery", "type": "requiredDependency"}
        ]
    },
}
print(json.dumps(metadata, ensure_ascii=False))
PY

echo "→ 上传 $JAR ..."
RESP=$(curl -sS --max-time 300 -X POST \
  -H "X-Api-Token: ${CURSEFORGE_TOKEN}" \
  -F "metadata=<${META_FILE};type=application/json" \
  -F "file=@${JAR}" \
  "${API}/projects/${PROJECT_ID}/upload-file")

rm -f "$META_FILE"
echo "← 响应：$RESP"
echo
echo "若返回 {\"id\": <文件ID>} 表示上传成功；文件可能仍需审核后才会公开。"
