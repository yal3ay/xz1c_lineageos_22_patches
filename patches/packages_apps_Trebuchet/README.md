# packages_apps_Trebuchet

- Source path in the build tree: `packages/apps/Trebuchet`
- Upstream: `LineageOS/android_packages_apps_Trebuchet`
- Upstream ref: `github/lineage-22.2`
- Base commit: `2bee8237d1bb4bc79c348cc7178a780e44a822a3`
- Local commits: 1
- Working-tree patch: no
- Untracked text files included: 0
- Untracked binary files excluded: 0

## Apply

```bash
git clone <upstream-url> packages_apps_Trebuchet
cd packages_apps_Trebuchet
git checkout 2bee8237d1bb4bc79c348cc7178a780e44a822a3
while IFS= read -r p; do git apply "$PATCH_DIR/packages_apps_Trebuchet/committed/$p"; done < "$PATCH_DIR/packages_apps_Trebuchet/ORDER"
[ -s "$PATCH_DIR/packages_apps_Trebuchet/working-tree.patch" ] && git apply "$PATCH_DIR/packages_apps_Trebuchet/working-tree.patch"
[ -d "$PATCH_DIR/packages_apps_Trebuchet/untracked" ] && cp -a "$PATCH_DIR/packages_apps_Trebuchet/untracked/." .
```

`untracked/` is an overlay, not a patch. Files listed in
`untracked-binaries.txt` are intentionally not published; see the top-level
README.
