# Release helpers

Scripts for uploading build artifacts to CurseForge. **No tokens are stored here** —
the token is passed in through an environment variable.

[中文备份](README.zh_cn.md)

## Why the project must be created manually

The [CurseForge Upload API](https://support.curseforge.com/support/solutions/articles/9000197321-curseforge-upload-api)
only offers the following:

| Capability | Endpoint |
|---|---|
| Upload a file to an **existing** project | `POST /api/projects/{projectId}/upload-file` |
| Update an existing file | `POST /api/projects/{projectId}/update-file` |
| Query game versions / dependencies | `GET /api/game/versions`, `/api/game/dependencies` |

There is **no endpoint for creating a project**. The project page has to be created in the web UI at
[authors.curseforge.com](https://authors.curseforge.com/) first.

## Usage

```bash
# 1) Build
./gradlew build

# 2) Provide the token and project id (current shell only — never written to the repo)
export CURSEFORGE_TOKEN='your api token'
export PROJECT_ID='123456'

# 3) Upload
./release/curseforge-upload.sh
```

The script will:

1. Resolve the game version ids for `1.20.1` and `Forge` via `/api/game/versions`
   and print them for a manual sanity check
2. Build the metadata from `release/release-notes-1.0.0-forge.md`
3. Declare `kaleidoscope-cookery` as a required dependency
4. `POST` the jar and print the returned file id

## Security notes

- **Never** put `CURSEFORGE_TOKEN` into a script, an `.env` file, or a commit
- Run `unset CURSEFORGE_TOKEN` afterwards
- If the token leaks, revoke it immediately at
  [API Tokens](https://authors.curseforge.com/#/settings/api-tokens)

## Files

| File | Purpose |
|---|---|
| `project-page.md` | Summary and description to paste into the CurseForge project page |
| `release-notes-1.0.0-forge.md` | Changelog submitted with the file |
| `resolve_versions.py` | Game version id resolver used by the main script |

Chinese backups are kept alongside as `*.zh_cn.md`.
