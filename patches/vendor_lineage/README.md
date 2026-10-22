# vendor_lineage

- Source path in the build tree: `vendor/lineage`
- Upstream: `LineageOS/android_vendor_lineage`
- Upstream ref: `github/lineage-22.2`
- Base commit: `87f03bdeafbf22dc35130c61ae2ff97f2041b90f`
- Local commits: 1
- Working-tree patch: no
- Untracked text files included: 0
- Untracked binary files excluded: 0

## Apply

```bash
git clone <upstream-url> vendor_lineage
cd vendor_lineage
git checkout 87f03bdeafbf22dc35130c61ae2ff97f2041b90f
while IFS= read -r p; do git apply "$PATCH_DIR/vendor_lineage/committed/$p"; done < "$PATCH_DIR/vendor_lineage/ORDER"
[ -s "$PATCH_DIR/vendor_lineage/working-tree.patch" ] && git apply "$PATCH_DIR/vendor_lineage/working-tree.patch"
[ -d "$PATCH_DIR/vendor_lineage/untracked" ] && cp -a "$PATCH_DIR/vendor_lineage/untracked/." .
```

`untracked/` is an overlay, not a patch. Files listed in
`untracked-binaries.txt` are intentionally not published; see the top-level
README.
