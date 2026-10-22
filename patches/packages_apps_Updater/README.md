# packages_apps_Updater

- Source path in the build tree: `packages/apps/Updater`
- Upstream: `LineageOS/android_packages_apps_Updater`
- Upstream ref: `github/lineage-22.2`
- Base commit: `bb7a985ab0f705bc8458022a5e2f71986856722d`
- Local commits: 1
- Working-tree patch: yes
- Untracked text files included: 24
- Untracked binary files excluded: 0

## Apply

```bash
git clone <upstream-url> packages_apps_Updater
cd packages_apps_Updater
git checkout bb7a985ab0f705bc8458022a5e2f71986856722d
while IFS= read -r p; do git apply "$PATCH_DIR/packages_apps_Updater/committed/$p"; done < "$PATCH_DIR/packages_apps_Updater/ORDER"
[ -s "$PATCH_DIR/packages_apps_Updater/working-tree.patch" ] && git apply "$PATCH_DIR/packages_apps_Updater/working-tree.patch"
[ -d "$PATCH_DIR/packages_apps_Updater/untracked" ] && cp -a "$PATCH_DIR/packages_apps_Updater/untracked/." .
```

`untracked/` is an overlay, not a patch. Files listed in
`untracked-binaries.txt` are intentionally not published; see the top-level
README.
