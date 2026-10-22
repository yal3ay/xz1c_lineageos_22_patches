# packages_modules_DnsResolver

- Source path in the build tree: `packages/modules/DnsResolver`
- Upstream: `LineageOS/android_packages_modules_DnsResolver`
- Upstream ref: `m/lineage-22.2`
- Base commit: `1f9421eb0f173da2f359a19de142e4334ea99050`
- Local commits: 4
- Working-tree patch: no
- Untracked text files included: 0
- Untracked binary files excluded: 0

## Apply

```bash
git clone <upstream-url> packages_modules_DnsResolver
cd packages_modules_DnsResolver
git checkout 1f9421eb0f173da2f359a19de142e4334ea99050
while IFS= read -r p; do git apply "$PATCH_DIR/packages_modules_DnsResolver/committed/$p"; done < "$PATCH_DIR/packages_modules_DnsResolver/ORDER"
[ -s "$PATCH_DIR/packages_modules_DnsResolver/working-tree.patch" ] && git apply "$PATCH_DIR/packages_modules_DnsResolver/working-tree.patch"
[ -d "$PATCH_DIR/packages_modules_DnsResolver/untracked" ] && cp -a "$PATCH_DIR/packages_modules_DnsResolver/untracked/." .
```

`untracked/` is an overlay, not a patch. Files listed in
`untracked-binaries.txt` are intentionally not published; see the top-level
README.
