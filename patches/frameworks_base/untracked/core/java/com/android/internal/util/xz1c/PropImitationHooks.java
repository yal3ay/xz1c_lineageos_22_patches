/*
 * Copyright (C) 2026 The LineageOS Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.internal.util.xz1c;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Built-in replacement for the Play Integrity Fork module: inside Google Play services'
 * DroidGuard process ("com.google.android.gms.unstable") the device presents the build
 * fields of a Pixel Canary build, and key attestation is reported as unavailable, so the
 * integrity check falls back to the basic/device verdicts an unlocked phone can pass.
 *
 * Values come from persist.sys.xz1c.pif.* (kept current by System Manager from the
 * project's pif.json) and fall back to the ones built in below.
 * persist.sys.xz1c.pif.enabled=0 turns it off.
 */
public final class PropImitationHooks {

    private static final String TAG = "PropImitationHooks";

    private static final String PACKAGE_GMS = "com.google.android.gms";
    private static final String PROCESS_GMS_UNSTABLE = PACKAGE_GMS + ".unstable";

    private static final String PROP_ENABLED = "persist.sys.xz1c.pif.enabled";
    private static final String PROP_PREFIX = "persist.sys.xz1c.pif.";

    /** Build / Build.VERSION fields that may be spoofed, in the order they are applied. */
    private static final String[] FIELDS = {
        "MANUFACTURER", "BRAND", "MODEL", "PRODUCT", "DEVICE", "FINGERPRINT",
        "ID", "INCREMENTAL", "TYPE", "TAGS", "RELEASE", "SECURITY_PATCH",
        "DEVICE_INITIAL_SDK_INT",
    };

    // Fallback until System Manager has fetched pif.json (generated 2026-10-10).
    private static final String[][] DEFAULTS = {
        { "MANUFACTURER", "Google" },
        { "MODEL", "Pixel 11 Pro" },
        { "FINGERPRINT", "google/grizzly_beta/grizzly:CANARY/ZP11.260918.007/16484274:user/release-keys" },
        { "PRODUCT", "grizzly_beta" },
        { "DEVICE", "grizzly" },
        { "SECURITY_PATCH", "2026-10-05" },
        { "DEVICE_INITIAL_SDK_INT", "32" },
    };

    private static volatile boolean sIsGmsUnstable;

    private PropImitationHooks() {
    }

    /** Called from Instrumentation before the application is attached. */
    public static void setProps(Context context) {
        if (context == null || !PACKAGE_GMS.equals(context.getPackageName())) {
            return;
        }
        if (!PROCESS_GMS_UNSTABLE.equals(Application.getProcessName())) {
            return;
        }
        if (!SystemProperties.getBoolean(PROP_ENABLED, true)) {
            return;
        }
        sIsGmsUnstable = true;
        for (Map.Entry<String, String> e : values().entrySet()) {
            setField(e.getKey(), e.getValue());
        }
        // This ROM is a userdebug build; present a release build like the spoofed one.
        setField("TYPE", "user");
        setField("TAGS", "release-keys");
        setField("IS_DEBUGGABLE", "false");
    }

    /** Called from AndroidKeyStoreSpi: no key attestation for DroidGuard. */
    public static void onEngineGetCertificateChain() {
        if (sIsGmsUnstable) {
            throw new UnsupportedOperationException();
        }
    }

    private static Map<String, String> values() {
        Map<String, String> values = new LinkedHashMap<>();
        // Use the fetched set only as a whole, never mixed with the built-in one.
        if (!TextUtils.isEmpty(SystemProperties.get(PROP_PREFIX + "FINGERPRINT"))) {
            for (String field : FIELDS) {
                String value = SystemProperties.get(PROP_PREFIX + field);
                if (!TextUtils.isEmpty(value)) {
                    values.put(field, value);
                }
            }
        } else {
            for (String[] pair : DEFAULTS) {
                values.put(pair[0], pair[1]);
            }
        }
        return values;
    }

    private static void setField(String name, String value) {
        Field field;
        try {
            field = Build.class.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            try {
                field = Build.VERSION.class.getDeclaredField(name);
            } catch (NoSuchFieldException e2) {
                Log.w(TAG, "No Build field " + name);
                return;
            }
        }
        try {
            field.setAccessible(true);
            Class<?> type = field.getType();
            if (type == String.class) {
                field.set(null, value);
            } else if (type == int.class) {
                field.setInt(null, Integer.parseInt(value));
            } else if (type == long.class) {
                field.setLong(null, Long.parseLong(value));
            } else if (type == boolean.class) {
                field.setBoolean(null, Boolean.parseBoolean(value));
            } else {
                Log.w(TAG, "Unsupported type for " + name);
                return;
            }
            field.setAccessible(false);
        } catch (IllegalAccessException | IllegalArgumentException e) {
            Log.e(TAG, "Failed to set " + name, e);
        }
    }
}
