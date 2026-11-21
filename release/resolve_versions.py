#!/usr/bin/env python3
"""从 CurseForge /api/game/versions 的响应中解析 1.20.1 与 Forge 的版本 ID。

标准输入：versions 接口返回的 JSON 数组
标准输出：逗号分隔的 ID 列表
标准错误：匹配到的条目明细（供人工确认）
"""
import json
import sys


def main() -> int:
    try:
        data = json.load(sys.stdin)
    except json.JSONDecodeError as exc:
        print(f"JSON 解析失败：{exc}", file=sys.stderr)
        return 1

    if not isinstance(data, list):
        print(f"意外的响应结构：{type(data).__name__}", file=sys.stderr)
        return 1

    picked = []
    for entry in data:
        name = str(entry.get("name", ""))
        if name == "1.20.1" or name.lower() == "forge":
            picked.append(entry)

    for entry in picked:
        print(
            "  - id={id:<7} name={name!r} gameVersionTypeID={t}".format(
                id=entry.get("id"),
                name=entry.get("name"),
                t=entry.get("gameVersionTypeID"),
            ),
            file=sys.stderr,
        )

    if not picked:
        print("未匹配到任何版本，请检查响应内容", file=sys.stderr)
        return 2

    print(",".join(str(entry["id"]) for entry in picked))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
