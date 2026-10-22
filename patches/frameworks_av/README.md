# frameworks_av

- Source path in the build tree: `frameworks/av`
- Upstream: `LineageOS/android_frameworks_av`
- Upstream ref: `github/lineage-22.2`
- Base commit: `1754b106f9d46d7345d30115294c8e90cf4717af`
- Local commits: 1
- Working-tree patch: yes
- Untracked text files included: 0
- Untracked binary files excluded: 0

## Apply

```bash
git clone <upstream-url> frameworks_av
cd frameworks_av
git checkout 1754b106f9d46d7345d30115294c8e90cf4717af
while IFS= read -r p; do git apply "$PATCH_DIR/frameworks_av/committed/$p"; done < "$PATCH_DIR/frameworks_av/ORDER"
[ -s "$PATCH_DIR/frameworks_av/working-tree.patch" ] && git apply "$PATCH_DIR/frameworks_av/working-tree.patch"
[ -d "$PATCH_DIR/frameworks_av/untracked" ] && cp -a "$PATCH_DIR/frameworks_av/untracked/." .
```

`untracked/` is an overlay, not a patch. Files listed in
`untracked-binaries.txt` are intentionally not published; see the top-level
README.
