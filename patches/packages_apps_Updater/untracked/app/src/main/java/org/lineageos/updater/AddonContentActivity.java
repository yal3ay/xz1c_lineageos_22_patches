/* SPDX-License-Identifier: Apache-2.0 */
package org.lineageos.updater;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

/** Internal details page. All text follows Android's current resource locale. */
public final class AddonContentActivity extends AppCompatActivity {
    private AddonUi mUi;
    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        String id = getIntent().getStringExtra("addon_id");
        if (!"sony".equals(id) && !"gapps".equals(id)) { finish(); return; }
        mUi = new AddonUi(this, new AddonsManager(this));
        mUi.showDetails(id);
    }
    @Override public void onResume() { super.onResume(); if (mUi != null) mUi.refresh(); }
}
