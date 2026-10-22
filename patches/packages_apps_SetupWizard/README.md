# packages_apps_SetupWizard

- Source path in the build tree: `packages/apps/SetupWizard`
- Upstream: `LineageOS/android_packages_apps_SetupWizard`
- Upstream ref: `github/lineage-22.2`
- Base commit: `a064c564fb6c7e7f82d32fb06bf34c2ca2e24baa`
- Local commits: 1
- Working-tree patch: no
- Untracked text files included: 0
- Untracked binary files excluded: 0

## Apply

```bash
git clone <upstream-url> packages_apps_SetupWizard
cd packages_apps_SetupWizard
git checkout a064c564fb6c7e7f82d32fb06bf34c2ca2e24baa
while IFS= read -r p; do git apply "$PATCH_DIR/packages_apps_SetupWizard/committed/$p"; done < "$PATCH_DIR/packages_apps_SetupWizard/ORDER"
[ -s "$PATCH_DIR/packages_apps_SetupWizard/working-tree.patch" ] && git apply "$PATCH_DIR/packages_apps_SetupWizard/working-tree.patch"
[ -d "$PATCH_DIR/packages_apps_SetupWizard/untracked" ] && cp -a "$PATCH_DIR/packages_apps_SetupWizard/untracked/." .
```

`untracked/` is an overlay, not a patch. Files listed in
`untracked-binaries.txt` are intentionally not published; see the top-level
README.
