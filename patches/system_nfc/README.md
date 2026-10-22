# system_nfc

- Source path in the build tree: `system/nfc`
- Upstream: `LineageOS/android_system_nfc`
- Upstream ref: `github/lineage-22.2`
- Base commit: `b158cd56ae13bbed9cdbfdfe40e9fe5133892b83`
- Local commits: 1
- Working-tree patch: yes
- Untracked text files included: 0
- Untracked binary files excluded: 0

## Apply

```bash
git clone <upstream-url> system_nfc
cd system_nfc
git checkout b158cd56ae13bbed9cdbfdfe40e9fe5133892b83
while IFS= read -r p; do git apply "$PATCH_DIR/system_nfc/committed/$p"; done < "$PATCH_DIR/system_nfc/ORDER"
[ -s "$PATCH_DIR/system_nfc/working-tree.patch" ] && git apply "$PATCH_DIR/system_nfc/working-tree.patch"
[ -d "$PATCH_DIR/system_nfc/untracked" ] && cp -a "$PATCH_DIR/system_nfc/untracked/." .
```

`untracked/` is an overlay, not a patch. Files listed in
`untracked-binaries.txt` are intentionally not published; see the top-level
README.
