# packages_apps_Nfc

- Source path in the build tree: `packages/apps/Nfc`
- Upstream: `LineageOS/android_packages_apps_Nfc`
- Upstream ref: `github/lineage-22.2`
- Base commit: `0a1d1f83635eedecc50ccaca4952f005cb31719b`
- Local commits: 1
- Working-tree patch: yes
- Untracked text files included: 1
- Untracked binary files excluded: 0

## Apply

```bash
git clone <upstream-url> packages_apps_Nfc
cd packages_apps_Nfc
git checkout 0a1d1f83635eedecc50ccaca4952f005cb31719b
while IFS= read -r p; do git apply "$PATCH_DIR/packages_apps_Nfc/committed/$p"; done < "$PATCH_DIR/packages_apps_Nfc/ORDER"
[ -s "$PATCH_DIR/packages_apps_Nfc/working-tree.patch" ] && git apply "$PATCH_DIR/packages_apps_Nfc/working-tree.patch"
[ -d "$PATCH_DIR/packages_apps_Nfc/untracked" ] && cp -a "$PATCH_DIR/packages_apps_Nfc/untracked/." .
```

`untracked/` is an overlay, not a patch. Files listed in
`untracked-binaries.txt` are intentionally not published; see the top-level
README.
