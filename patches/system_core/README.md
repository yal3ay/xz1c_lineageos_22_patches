# system_core

- Source path in the build tree: `system/core`
- Upstream: `LineageOS/android_system_core`
- Upstream ref: `github/lineage-22.2`
- Base commit: `bcb8623207cf6e7a51876a891d8b7ad3d04fd8c2`
- Local commits: 1
- Working-tree patch: yes
- Untracked text files included: 0
- Untracked binary files excluded: 0

## Apply

```bash
git clone <upstream-url> system_core
cd system_core
git checkout bcb8623207cf6e7a51876a891d8b7ad3d04fd8c2
while IFS= read -r p; do git apply "$PATCH_DIR/system_core/committed/$p"; done < "$PATCH_DIR/system_core/ORDER"
[ -s "$PATCH_DIR/system_core/working-tree.patch" ] && git apply "$PATCH_DIR/system_core/working-tree.patch"
[ -d "$PATCH_DIR/system_core/untracked" ] && cp -a "$PATCH_DIR/system_core/untracked/." .
```

`untracked/` is an overlay, not a patch. Files listed in
`untracked-binaries.txt` are intentionally not published; see the top-level
README.
