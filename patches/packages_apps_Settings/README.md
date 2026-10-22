# packages_apps_Settings

- Source path in the build tree: `packages/apps/Settings`
- Upstream: `LineageOS/android_packages_apps_Settings`
- Upstream ref: `github/lineage-22.2`
- Base commit: `2bee3366054852e5851efc65577eebd8e1dd68eb`
- Local commits: 3
- Working-tree patch: yes
- Untracked text files included: 0
- Untracked binary files excluded: 0

## Apply

```bash
git clone <upstream-url> packages_apps_Settings
cd packages_apps_Settings
git checkout 2bee3366054852e5851efc65577eebd8e1dd68eb
while IFS= read -r p; do git apply "$PATCH_DIR/packages_apps_Settings/committed/$p"; done < "$PATCH_DIR/packages_apps_Settings/ORDER"
[ -s "$PATCH_DIR/packages_apps_Settings/working-tree.patch" ] && git apply "$PATCH_DIR/packages_apps_Settings/working-tree.patch"
[ -d "$PATCH_DIR/packages_apps_Settings/untracked" ] && cp -a "$PATCH_DIR/packages_apps_Settings/untracked/." .
```

`untracked/` is an overlay, not a patch. Files listed in
`untracked-binaries.txt` are intentionally not published; see the top-level
README.
