# packages_apps_Messaging

- Source path in the build tree: `packages/apps/Messaging`
- Upstream: `LineageOS/android_packages_apps_Messaging`
- Upstream ref: `github/lineage-22.2`
- Base commit: `2068344589cb805e44e60b185c44c54d546ffb04`
- Local commits: 1
- Working-tree patch: no
- Untracked text files included: 0
- Untracked binary files excluded: 0

## Apply

```bash
git clone <upstream-url> packages_apps_Messaging
cd packages_apps_Messaging
git checkout 2068344589cb805e44e60b185c44c54d546ffb04
while IFS= read -r p; do git apply "$PATCH_DIR/packages_apps_Messaging/committed/$p"; done < "$PATCH_DIR/packages_apps_Messaging/ORDER"
[ -s "$PATCH_DIR/packages_apps_Messaging/working-tree.patch" ] && git apply "$PATCH_DIR/packages_apps_Messaging/working-tree.patch"
[ -d "$PATCH_DIR/packages_apps_Messaging/untracked" ] && cp -a "$PATCH_DIR/packages_apps_Messaging/untracked/." .
```

`untracked/` is an overlay, not a patch. Files listed in
`untracked-binaries.txt` are intentionally not published; see the top-level
README.
