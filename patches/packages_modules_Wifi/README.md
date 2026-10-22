# packages_modules_Wifi

- Source path in the build tree: `packages/modules/Wifi`
- Upstream: `LineageOS/android_packages_modules_Wifi`
- Upstream ref: `github/lineage-22.2`
- Base commit: `d6417247ef863889b336afe5efb603c29883ad1e`
- Local commits: 1
- Working-tree patch: no
- Untracked text files included: 0
- Untracked binary files excluded: 0

## Apply

```bash
git clone <upstream-url> packages_modules_Wifi
cd packages_modules_Wifi
git checkout d6417247ef863889b336afe5efb603c29883ad1e
while IFS= read -r p; do git apply "$PATCH_DIR/packages_modules_Wifi/committed/$p"; done < "$PATCH_DIR/packages_modules_Wifi/ORDER"
[ -s "$PATCH_DIR/packages_modules_Wifi/working-tree.patch" ] && git apply "$PATCH_DIR/packages_modules_Wifi/working-tree.patch"
[ -d "$PATCH_DIR/packages_modules_Wifi/untracked" ] && cp -a "$PATCH_DIR/packages_modules_Wifi/untracked/." .
```

`untracked/` is an overlay, not a patch. Files listed in
`untracked-binaries.txt` are intentionally not published; see the top-level
README.
