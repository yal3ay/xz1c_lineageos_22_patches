# packages_modules_Connectivity

- Source path in the build tree: `packages/modules/Connectivity`
- Upstream: `LineageOS/android_packages_modules_Connectivity`
- Upstream ref: `github/lineage-22.2`
- Base commit: `42f77549fc9d96709966c04f31011a27bc2b26c5`
- Local commits: 2
- Working-tree patch: no
- Untracked text files included: 0
- Untracked binary files excluded: 0

## Apply

```bash
git clone <upstream-url> packages_modules_Connectivity
cd packages_modules_Connectivity
git checkout 42f77549fc9d96709966c04f31011a27bc2b26c5
while IFS= read -r p; do git apply "$PATCH_DIR/packages_modules_Connectivity/committed/$p"; done < "$PATCH_DIR/packages_modules_Connectivity/ORDER"
[ -s "$PATCH_DIR/packages_modules_Connectivity/working-tree.patch" ] && git apply "$PATCH_DIR/packages_modules_Connectivity/working-tree.patch"
[ -d "$PATCH_DIR/packages_modules_Connectivity/untracked" ] && cp -a "$PATCH_DIR/packages_modules_Connectivity/untracked/." .
```

`untracked/` is an overlay, not a patch. Files listed in
`untracked-binaries.txt` are intentionally not published; see the top-level
README.
