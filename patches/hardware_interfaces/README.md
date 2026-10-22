# hardware_interfaces

- Source path in the build tree: `hardware/interfaces`
- Upstream: `LineageOS/android_hardware_interfaces`
- Upstream ref: `github/lineage-22.2`
- Base commit: `d9590e019b78db193c3cec3e6272e7341a892bd7`
- Local commits: 1
- Working-tree patch: yes
- Untracked text files included: 0
- Untracked binary files excluded: 0

## Apply

```bash
git clone <upstream-url> hardware_interfaces
cd hardware_interfaces
git checkout d9590e019b78db193c3cec3e6272e7341a892bd7
while IFS= read -r p; do git apply "$PATCH_DIR/hardware_interfaces/committed/$p"; done < "$PATCH_DIR/hardware_interfaces/ORDER"
[ -s "$PATCH_DIR/hardware_interfaces/working-tree.patch" ] && git apply "$PATCH_DIR/hardware_interfaces/working-tree.patch"
[ -d "$PATCH_DIR/hardware_interfaces/untracked" ] && cp -a "$PATCH_DIR/hardware_interfaces/untracked/." .
```

`untracked/` is an overlay, not a patch. Files listed in
`untracked-binaries.txt` are intentionally not published; see the top-level
README.
