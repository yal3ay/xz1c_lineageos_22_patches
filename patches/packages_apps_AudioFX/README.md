# packages_apps_AudioFX

- Source path in the build tree: `packages/apps/AudioFX`
- Upstream: `LineageOS/android_packages_apps_AudioFX`
- Upstream ref: `github/lineage-22.2`
- Base commit: `94275d69b285153ca58b1da1794a4fc7d67081b7`
- Local commits: 0
- Working-tree patch: yes
- Untracked text files included: 0
- Untracked binary files excluded: 0

## Apply

```bash
git clone <upstream-url> packages_apps_AudioFX
cd packages_apps_AudioFX
git checkout 94275d69b285153ca58b1da1794a4fc7d67081b7
while IFS= read -r p; do git apply "$PATCH_DIR/packages_apps_AudioFX/committed/$p"; done < "$PATCH_DIR/packages_apps_AudioFX/ORDER"
[ -s "$PATCH_DIR/packages_apps_AudioFX/working-tree.patch" ] && git apply "$PATCH_DIR/packages_apps_AudioFX/working-tree.patch"
[ -d "$PATCH_DIR/packages_apps_AudioFX/untracked" ] && cp -a "$PATCH_DIR/packages_apps_AudioFX/untracked/." .
```

`untracked/` is an overlay, not a patch. Files listed in
`untracked-binaries.txt` are intentionally not published; see the top-level
README.
