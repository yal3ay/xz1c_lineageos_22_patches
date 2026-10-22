# packages_services_Telecomm

- Source path in the build tree: `packages/services/Telecomm`
- Upstream: `LineageOS/android_packages_services_Telecomm`
- Upstream ref: `github/lineage-22.2`
- Base commit: `e5ac6f8b03ba6ba5c78d359b2da76ca1ffb57d1a`
- Local commits: 1
- Working-tree patch: no
- Untracked text files included: 0
- Untracked binary files excluded: 0

## Apply

```bash
git clone <upstream-url> packages_services_Telecomm
cd packages_services_Telecomm
git checkout e5ac6f8b03ba6ba5c78d359b2da76ca1ffb57d1a
while IFS= read -r p; do git apply "$PATCH_DIR/packages_services_Telecomm/committed/$p"; done < "$PATCH_DIR/packages_services_Telecomm/ORDER"
[ -s "$PATCH_DIR/packages_services_Telecomm/working-tree.patch" ] && git apply "$PATCH_DIR/packages_services_Telecomm/working-tree.patch"
[ -d "$PATCH_DIR/packages_services_Telecomm/untracked" ] && cp -a "$PATCH_DIR/packages_services_Telecomm/untracked/." .
```

`untracked/` is an overlay, not a patch. Files listed in
`untracked-binaries.txt` are intentionally not published; see the top-level
README.
