# frameworks_base

- Source path in the build tree: `frameworks/base`
- Upstream: `LineageOS/android_frameworks_base`
- Upstream ref: `m/lineage-22.2`
- Base commit: `afac20ebf4b8dd7b36c517cf6cf7790d6a28d9a6`
- Local commits: 6
- Working-tree patch: yes
- Untracked text files included: 4
- Untracked binary files excluded: 0

## Apply

```bash
git clone <upstream-url> frameworks_base
cd frameworks_base
git checkout afac20ebf4b8dd7b36c517cf6cf7790d6a28d9a6
while IFS= read -r p; do git apply "$PATCH_DIR/frameworks_base/committed/$p"; done < "$PATCH_DIR/frameworks_base/ORDER"
[ -s "$PATCH_DIR/frameworks_base/working-tree.patch" ] && git apply "$PATCH_DIR/frameworks_base/working-tree.patch"
[ -d "$PATCH_DIR/frameworks_base/untracked" ] && cp -a "$PATCH_DIR/frameworks_base/untracked/." .
```

`untracked/` is an overlay, not a patch. Files listed in
`untracked-binaries.txt` are intentionally not published; see the top-level
README.
