# packages_apps_DocumentsUI

- Source path in the build tree: `packages/apps/DocumentsUI`
- Upstream: `LineageOS/android_packages_apps_DocumentsUI`
- Upstream ref: `github/lineage-22.2`
- Base commit: `514aa82fc1d7786dce5900c339b210e42581462d`
- Local commits: 1
- Working-tree patch: no
- Untracked text files included: 0
- Untracked binary files excluded: 0

## Apply

```bash
git clone <upstream-url> packages_apps_DocumentsUI
cd packages_apps_DocumentsUI
git checkout 514aa82fc1d7786dce5900c339b210e42581462d
while IFS= read -r p; do git apply "$PATCH_DIR/packages_apps_DocumentsUI/committed/$p"; done < "$PATCH_DIR/packages_apps_DocumentsUI/ORDER"
[ -s "$PATCH_DIR/packages_apps_DocumentsUI/working-tree.patch" ] && git apply "$PATCH_DIR/packages_apps_DocumentsUI/working-tree.patch"
[ -d "$PATCH_DIR/packages_apps_DocumentsUI/untracked" ] && cp -a "$PATCH_DIR/packages_apps_DocumentsUI/untracked/." .
```

`untracked/` is an overlay, not a patch. Files listed in
`untracked-binaries.txt` are intentionally not published; see the top-level
README.
