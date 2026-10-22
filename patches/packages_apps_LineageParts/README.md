# packages_apps_LineageParts

- Source path in the build tree: `packages/apps/LineageParts`
- Upstream: `LineageOS/android_packages_apps_LineageParts`
- Upstream ref: `github/lineage-22.2`
- Base commit: `45c744432efe62704a636a8acd71a3724412cf49`
- Local commits: 2
- Working-tree patch: yes
- Untracked text files included: 0
- Untracked binary files excluded: 0

## Apply

```bash
git clone <upstream-url> packages_apps_LineageParts
cd packages_apps_LineageParts
git checkout 45c744432efe62704a636a8acd71a3724412cf49
while IFS= read -r p; do git apply "$PATCH_DIR/packages_apps_LineageParts/committed/$p"; done < "$PATCH_DIR/packages_apps_LineageParts/ORDER"
[ -s "$PATCH_DIR/packages_apps_LineageParts/working-tree.patch" ] && git apply "$PATCH_DIR/packages_apps_LineageParts/working-tree.patch"
[ -d "$PATCH_DIR/packages_apps_LineageParts/untracked" ] && cp -a "$PATCH_DIR/packages_apps_LineageParts/untracked/." .
```

`untracked/` is an overlay, not a patch. Files listed in
`untracked-binaries.txt` are intentionally not published; see the top-level
README.
