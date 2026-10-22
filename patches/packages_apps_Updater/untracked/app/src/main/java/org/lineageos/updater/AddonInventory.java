/* SPDX-License-Identifier: Apache-2.0 */
package org.lineageos.updater;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/** Inventory generated from the real signed add-ons, never from a display-name prefix. */
final class AddonInventory {
    static final class Entry {
        final String packageName, label;
        final PackageInfo installed;
        Entry(String pkg, String fallback, PackageInfo info, PackageManager pm) {
            packageName = pkg; installed = info;
            label = info != null && info.applicationInfo != null
                    ? String.valueOf(info.applicationInfo.loadLabel(pm)) : fallback;
        }
        boolean installed() {
            return installed != null && installed.applicationInfo != null
                    && (installed.applicationInfo.flags & ApplicationInfo.FLAG_INSTALLED) != 0;
        }
    }

    static List<Entry> read(Context context, String id) throws Exception {
        if (!"sony".equals(id) && !"gapps".equals(id)) throw new IllegalArgumentException("id");
        JSONObject root;
        try (InputStream in = context.getAssets().open("xz1c-addons.json");
             ByteArrayOutputStream bytes = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[8192]; int count;
            while ((count = in.read(buffer)) != -1) bytes.write(buffer, 0, count);
            root = new JSONObject(bytes.toString("UTF-8"));
        }
        PackageManager pm = context.getPackageManager();
        List<Entry> entries = new ArrayList<>();
        JSONArray apps = root.getJSONObject(id).getJSONArray("apps");
        for (int i = 0; i < apps.length(); i++) {
            JSONObject app = apps.getJSONObject(i);
            String pkg = app.getString("package");
            PackageInfo installed = null;
            try { installed = pm.getPackageInfo(pkg, 0); }
            catch (PackageManager.NameNotFoundException ignored) { }
            entries.add(new Entry(pkg, app.optString("label", pkg), installed, pm));
        }
        return entries;
    }
}
