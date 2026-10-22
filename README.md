# XZ1C LineageOS 22.2 — patches for modified AOSP/LineageOS projects

This repository contains the patches and working-tree overlays for the
AOSP/LineageOS projects modified for the unofficial LineageOS 22.2 build for
the Sony Xperia XZ1 Compact SO-02K (`lilac_dcm`).

It is not a full source tree. The device trees, kernel, and local manifests are
published separately:

- `yal3ay/android_device_sony_lilac`
- `yal3ay/android_device_sony_yoshino-common`
- `yal3ay/android_kernel_sony_msm8998`
- `yal3ay/xz1c_local_manifests`

Upstream AOSP/LineageOS already hosts the unchanged code. This repository
publishes only the delta, with the base commit for each project so the result
is reproducible.

## Baseline

Snapshot date: 2026-10-22. The patches were generated from the maintainer's
build tree. The tree was **not** rebuilt as part of this publication task.

| Project | Upstream | Ref | Base | Local commits | Patch files | Working tree | Untracked |
|---|---|---|---|---|---:|---:|---|---:|
| `frameworks_av` | `LineageOS/android_frameworks_av` | `github/lineage-22.2` | `1754b106f9d4` | 1 | 1 | yes | 0 |
| `frameworks_base` | `LineageOS/android_frameworks_base` | `m/lineage-22.2` | `afac20ebf4b8` | 6 | 6 | yes | 4 |
| `hardware_interfaces` | `LineageOS/android_hardware_interfaces` | `github/lineage-22.2` | `d9590e019b78` | 1 | 1 | yes | 0 |
| `hardware_qcom_media` | `LineageOS/android_hardware_qcom_media` | `github/lineage-22.2-caf-msm8998` | `f5b08c7c82e8` | 1 | 1 | no | 0 |
| `lineage-sdk` | `LineageOS/android_lineage-sdk` | `github/lineage-22.2` | `8c4f0c6fda7f` | 2 | 2 | no | 0 |
| `packages_apps_AudioFX` | `LineageOS/android_packages_apps_AudioFX` | `github/lineage-22.2` | `94275d69b285` | 0 | 0 | yes | 0 |
| `packages_apps_DocumentsUI` | `LineageOS/android_packages_apps_DocumentsUI` | `github/lineage-22.2` | `514aa82fc1d7` | 1 | 1 | no | 0 |
| `packages_apps_LineageParts` | `LineageOS/android_packages_apps_LineageParts` | `github/lineage-22.2` | `45c744432efe` | 2 | 2 | yes | 0 |
| `packages_apps_Messaging` | `LineageOS/android_packages_apps_Messaging` | `github/lineage-22.2` | `2068344589cb` | 1 | 1 | no | 0 |
| `packages_apps_Nfc` | `LineageOS/android_packages_apps_Nfc` | `github/lineage-22.2` | `0a1d1f83635e` | 1 | 1 | yes | 1 |
| `packages_apps_Settings` | `LineageOS/android_packages_apps_Settings` | `github/lineage-22.2` | `2bee33660548` | 3 | 3 | yes | 0 |
| `packages_apps_SetupWizard` | `LineageOS/android_packages_apps_SetupWizard` | `github/lineage-22.2` | `a064c564fb6c` | 1 | 1 | no | 0 |
| `packages_apps_Trebuchet` | `LineageOS/android_packages_apps_Trebuchet` | `github/lineage-22.2` | `2bee8237d1bb` | 1 | 1 | no | 0 |
| `packages_apps_Updater` | `LineageOS/android_packages_apps_Updater` | `github/lineage-22.2` | `bb7a985ab0f7` | 1 | 1 | yes | 24 |
| `packages_modules_Connectivity` | `LineageOS/android_packages_modules_Connectivity` | `github/lineage-22.2` | `42f77549fc9d` | 2 | 2 | no | 0 |
| `packages_modules_DnsResolver` | `LineageOS/android_packages_modules_DnsResolver` | `m/lineage-22.2` | `1f9421eb0f17` | 4 | 4 | no | 0 |
| `packages_modules_IntentResolver` | `LineageOS/android_packages_modules_IntentResolver` | `github/lineage-22.2` | `f0a3fa3c8c52` | 0 | 0 | yes | 0 |
| `packages_modules_Wifi` | `LineageOS/android_packages_modules_Wifi` | `github/lineage-22.2` | `d6417247ef86` | 1 | 1 | no | 0 |
| `packages_services_Telecomm` | `LineageOS/android_packages_services_Telecomm` | `github/lineage-22.2` | `e5ac6f8b03ba` | 1 | 1 | no | 0 |
| `system_core` | `LineageOS/android_system_core` | `github/lineage-22.2` | `bcb8623207cf` | 1 | 1 | yes | 0 |
| `system_nfc` | `LineageOS/android_system_nfc` | `github/lineage-22.2` | `b158cd56ae13` | 1 | 1 | yes | 0 |
| `vendor_lineage` | `LineageOS/android_vendor_lineage` | `github/lineage-22.2` | `87f03bdeafbf` | 1 | 1 | no | 0 |

`LOCAL_COMMITS` counts commits from the upstream base to the local HEAD.
`Working tree` means a `git diff HEAD` patch is included. `Untracked` counts
new text files copied into the per-project `untracked/` overlay.

## How to apply

Each project directory contains:

- `BASE` — upstream base commit.
- `ORDER` — committed patch filenames, in order.
- `committed/` — `git format-patch` output.
- `working-tree.patch` — uncommitted tracked changes (may be empty).
- `untracked/` — new text files (overlay, not a patch).
- `untracked-files.txt` / `untracked-binaries.txt` — what was included/excluded.
- `README.md` — project-specific notes.

Example:

```bash
git clone https://github.com/LineageOS/android_frameworks_base
cd android_frameworks_base
git checkout afac20ebf4b8dd7b36c517cf6cf7790d6a28d9a6
PATCH_DIR=/path/to/xz1c_lineageos_22_patches
while IFS= read -r p; do
  git apply "$PATCH_DIR/patches/frameworks_base/committed/$p"
done < "$PATCH_DIR/patches/frameworks_base/ORDER"
git apply "$PATCH_DIR/patches/frameworks_base/working-tree.patch"
cp -a "$PATCH_DIR/patches/frameworks_base/untracked/." .
```

All patches were checked with `git apply --cached --check` against their base
commits.

## What is not included

- No `vendor/sony/**` or Sony proprietary files.
- No device-extracted blobs: `.so`, `.bin`, `.mbn`, firmware, `.apk`.
- No signing material: `.pk8`, `.p12`, `.jks`, `~/.android-certs`,
  `vendor/lineage-priv`.
- No binary patches. The only binary working-tree change found was
  `packages/apps/LineageParts/assets/contributors.db`; it is excluded.
- The DnsResolver prebuilt `libxz1c_dns_engine.so` is excluded. The source
  under `xz1c-engine/` and its build script are included; rebuild the library
  locally with the documented script.
- Backup files (`*.bak`, `*.prehidl`, etc.) from the working tree are excluded.

## Licensing

The patch repository itself is Apache-2.0 (`LICENSE`). Each patch modifies
upstream files and retains the copyright/license headers of those files. The
DnsResolver patch includes vendored `quic-go` (MIT) and `golang.org/x/*` (BSD)
source; their license files are part of the patched tree.

KernelSU-Next is not in this repository. See the kernel repository.

## 中文說明

這個 repo 只放 22 個被修改的 AOSP/LineageOS 專案的 patch 與 working-tree
overlay，不是完整源碼樹。裝置樹、kernel、local manifests 分別放在其他 repo。
每個專案都有 `BASE`（上游 baseline）、`ORDER`（套用順序）、`committed/`
（已提交 patch）、`working-tree.patch`（未提交修改）與 `untracked/`（新增文字
檔）。所有 patch 都用 `git apply --cached --check` 對 baseline 驗證過。

不含 `vendor/sony/**`、Sony 閉源檔、裝置提取 blob（`.so`、`.bin`、`.mbn`、
firmware、`.apk`）、簽名材料或 binary patch。DnsResolver 的預編譯
`libxz1c_dns_engine.so` 不公開，請用 `xz1c-engine/` 源碼重建。本次沒有重新
編譯，只公開源碼與 patch。
