# hardware_qcom_media

- Source path in the build tree: `hardware/qcom-caf/msm8998/media`
- Upstream: `LineageOS/android_hardware_qcom_media`
- Upstream ref: `github/lineage-22.2-caf-msm8998`
- Base commit: `f5b08c7c82e8c3445712df2f48c0d14eaa74e313`
- Local commits: 1
- Working-tree patch: no
- Untracked text files included: 0
- Untracked binary files excluded: 0

## Apply

```bash
git clone <upstream-url> hardware_qcom_media
cd hardware_qcom_media
git checkout f5b08c7c82e8c3445712df2f48c0d14eaa74e313
while IFS= read -r p; do git apply "$PATCH_DIR/hardware_qcom_media/committed/$p"; done < "$PATCH_DIR/hardware_qcom_media/ORDER"
[ -s "$PATCH_DIR/hardware_qcom_media/working-tree.patch" ] && git apply "$PATCH_DIR/hardware_qcom_media/working-tree.patch"
[ -d "$PATCH_DIR/hardware_qcom_media/untracked" ] && cp -a "$PATCH_DIR/hardware_qcom_media/untracked/." .
```

`untracked/` is an overlay, not a patch. Files listed in
`untracked-binaries.txt` are intentionally not published; see the top-level
README.
