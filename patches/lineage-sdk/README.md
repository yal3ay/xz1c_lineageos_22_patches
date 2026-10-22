# lineage-sdk

- Source path in the build tree: `lineage-sdk`
- Upstream: `LineageOS/android_lineage-sdk`
- Upstream ref: `github/lineage-22.2`
- Base commit: `8c4f0c6fda7f85de486b30ce0a727bcce80a9ae1`
- Local commits: 2
- Working-tree patch: no
- Untracked text files included: 0
- Untracked binary files excluded: 0

## Apply

```bash
git clone <upstream-url> lineage-sdk
cd lineage-sdk
git checkout 8c4f0c6fda7f85de486b30ce0a727bcce80a9ae1
while IFS= read -r p; do git apply "$PATCH_DIR/lineage-sdk/committed/$p"; done < "$PATCH_DIR/lineage-sdk/ORDER"
[ -s "$PATCH_DIR/lineage-sdk/working-tree.patch" ] && git apply "$PATCH_DIR/lineage-sdk/working-tree.patch"
[ -d "$PATCH_DIR/lineage-sdk/untracked" ] && cp -a "$PATCH_DIR/lineage-sdk/untracked/." .
```

`untracked/` is an overlay, not a patch. Files listed in
`untracked-binaries.txt` are intentionally not published; see the top-level
README.
