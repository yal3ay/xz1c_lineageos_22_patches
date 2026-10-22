# packages_modules_IntentResolver

- Source path in the build tree: `packages/modules/IntentResolver`
- Upstream: `LineageOS/android_packages_modules_IntentResolver`
- Upstream ref: `github/lineage-22.2`
- Base commit: `f0a3fa3c8c5219a9bd6eafd5847dbfdfc1da752a`
- Local commits: 0
- Working-tree patch: yes
- Untracked text files included: 0
- Untracked binary files excluded: 0

## Apply

```bash
git clone <upstream-url> packages_modules_IntentResolver
cd packages_modules_IntentResolver
git checkout f0a3fa3c8c5219a9bd6eafd5847dbfdfc1da752a
while IFS= read -r p; do git apply "$PATCH_DIR/packages_modules_IntentResolver/committed/$p"; done < "$PATCH_DIR/packages_modules_IntentResolver/ORDER"
[ -s "$PATCH_DIR/packages_modules_IntentResolver/working-tree.patch" ] && git apply "$PATCH_DIR/packages_modules_IntentResolver/working-tree.patch"
[ -d "$PATCH_DIR/packages_modules_IntentResolver/untracked" ] && cp -a "$PATCH_DIR/packages_modules_IntentResolver/untracked/." .
```

`untracked/` is an overlay, not a patch. Files listed in
`untracked-binaries.txt` are intentionally not published; see the top-level
README.
