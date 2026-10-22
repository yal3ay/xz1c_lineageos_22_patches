/*
 * Copyright (C) 2026 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package com.android.server.am;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.AudioPlaybackConfiguration;
import android.media.AudioRecordingConfiguration;
import android.os.Process;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.Slog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Unified controller for Smart App Freezing (Cgroup v1 Freezer at /dev/freezer)
 * and Cross-App Chain-Wakeup Interception.
 */
public final class SmartFreezeController {
    private static final String TAG = "SmartFreezeController";

    public static final String SETTING_AUTOSTART_BLOCKED_PACKAGES = "autostart_blocked_packages";
    public static final String SETTING_BG_TIMEOUT_SECONDS = "bg_timeout_seconds";
    public static final String SETTING_BG_CONTROL_MODE = "bg_control_mode";
    public static final String SETTING_BG_SCREEN_OFF_FREEZE = "bg_screen_off_freeze";
    public static final String SETTING_BG_FREEZE_CMD = "bg_freeze_cmd";

    public static final int MODE_SMART_FREEZE = 0;
    public static final int MODE_FORCE_STOP = 1;
    public static final int MODE_KILL_PROCESS = 2;

    private static final String FREEZER_ROOT_PROCS = "/dev/freezer/cgroup.procs";
    private static final String FREEZER_FROZEN_DIR = "/dev/freezer/frozen";
    private static final String FREEZER_FROZEN_STATE = "/dev/freezer/frozen/freezer.state";
    private static final String FREEZER_FROZEN_PROCS = "/dev/freezer/frozen/cgroup.procs";

    /** pid -> uid for currently frozen processes. */
    private static final ConcurrentHashMap<Integer, Integer> sFrozenPidToUid =
            new ConcurrentHashMap<>();
    /** pid -> packageName for currently frozen processes. */
    private static final ConcurrentHashMap<Integer, String> sFrozenPidToPkg =
            new ConcurrentHashMap<>();

    /** Cached autostart_blocked_packages set. */
    private static volatile Set<String> sBlockedPackages = new ArraySet<>();
    private static volatile long sLastBlockedReadMs = 0L;

    /** Total number of cross-app chain wakeups blocked since boot. */
    private static final AtomicInteger sBlockedWakeupCount = new AtomicInteger(0);

    private static volatile boolean sFreezerReady = false;

    private SmartFreezeController() {}

    /** Ensures /dev/freezer/frozen exists and is in FROZEN state. */
    public static synchronized boolean ensureFreezerReady() {
        if (sFreezerReady && new File(FREEZER_FROZEN_PROCS).canWrite()) {
            return true;
        }
        try {
            File frozenDir = new File(FREEZER_FROZEN_DIR);
            if (!frozenDir.exists()) {
                frozenDir.mkdirs();
            }
            File stateFile = new File(FREEZER_FROZEN_STATE);
            if (stateFile.exists()) {
                writeString(FREEZER_FROZEN_STATE, "FROZEN");
                sFreezerReady = new File(FREEZER_FROZEN_PROCS).canWrite();
                return sFreezerReady;
            }
        } catch (Exception e) {
            Slog.w(TAG, "ensureFreezerReady failed", e);
        }
        return false;
    }

    /** Freezes a single process PID into /dev/freezer/frozen/cgroup.procs. */
    public static boolean freezePid(int pid, int uid, String pkg) {
        if (pid <= 0 || uid < Process.FIRST_APPLICATION_UID) {
            return false;
        }
        if (!ensureFreezerReady()) {
            return false;
        }
        if (writeString(FREEZER_FROZEN_PROCS, Integer.toString(pid))) {
            sFrozenPidToUid.put(pid, uid);
            if (pkg != null) {
                sFrozenPidToPkg.put(pid, pkg);
            }
            Slog.i(TAG, "Frozen pid=" + pid + " uid=" + uid + " pkg=" + pkg);
            return true;
        }
        return false;
    }

    /** Thaws a single process PID back to /dev/freezer/cgroup.procs. */
    public static boolean thawPid(int pid) {
        if (pid <= 0) {
            return false;
        }
        Integer uid = sFrozenPidToUid.remove(pid);
        String pkg = sFrozenPidToPkg.remove(pid);
        if (uid != null || new File(FREEZER_ROOT_PROCS).canWrite()) {
            boolean ok = writeString(FREEZER_ROOT_PROCS, Integer.toString(pid));
            if (uid != null) {
                Slog.i(TAG, "Thawed pid=" + pid + " uid=" + uid + " pkg=" + pkg);
            }
            return ok;
        }
        return false;
    }

    /** Fast check whether a PID is currently frozen by SmartFreezeController. */
    public static boolean isPidFrozen(int pid) {
        return pid > 0 && sFrozenPidToUid.containsKey(pid);
    }

    /** Fast check whether any process of the given UID is currently frozen. */
    public static boolean isUidFrozen(int uid) {
        if (uid < Process.FIRST_APPLICATION_UID || sFrozenPidToUid.isEmpty()) {
            return false;
        }
        return sFrozenPidToUid.containsValue(uid);
    }

    /** Thaws all frozen processes belonging to the given UID immediately. */
    public static void thawByUid(int uid, String reason) {
        if (uid < Process.FIRST_APPLICATION_UID || sFrozenPidToUid.isEmpty()) {
            return;
        }
        boolean any = false;
        for (Map.Entry<Integer, Integer> entry : sFrozenPidToUid.entrySet()) {
            if (entry.getValue() != null && entry.getValue() == uid) {
                int pid = entry.getKey();
                sFrozenPidToUid.remove(pid);
                String pkg = sFrozenPidToPkg.remove(pid);
                writeString(FREEZER_ROOT_PROCS, Integer.toString(pid));
                Slog.i(TAG, "Thawed pid=" + pid + " pkg=" + pkg + " uid=" + uid
                        + " reason=" + reason);
                any = true;
            }
        }
    }

    /** Thaws all frozen processes belonging to the given package name. */
    public static void thawByPackage(String pkg, String reason) {
        if (pkg == null || sFrozenPidToPkg.isEmpty()) {
            return;
        }
        for (Map.Entry<Integer, String> entry : sFrozenPidToPkg.entrySet()) {
            if (pkg.equals(entry.getValue())) {
                int pid = entry.getKey();
                sFrozenPidToPkg.remove(pid);
                sFrozenPidToUid.remove(pid);
                writeString(FREEZER_ROOT_PROCS, Integer.toString(pid));
                Slog.i(TAG, "Thawed pid=" + pid + " pkg=" + pkg + " reason=" + reason);
            }
        }
    }

    /** Thaws all frozen processes in /dev/freezer/frozen/cgroup.procs. */
    public static void thawAll(String reason) {
        for (Integer pid : sFrozenPidToUid.keySet()) {
            writeString(FREEZER_ROOT_PROCS, Integer.toString(pid));
        }
        sFrozenPidToUid.clear();
        sFrozenPidToPkg.clear();
        // Also drain any PIDs present in /dev/freezer/frozen/cgroup.procs directly
        File frozenProcs = new File(FREEZER_FROZEN_PROCS);
        if (frozenProcs.canRead()) {
            try (BufferedReader br = new BufferedReader(new FileReader(frozenProcs))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String trimmed = line.trim();
                    if (!trimmed.isEmpty()) {
                        writeString(FREEZER_ROOT_PROCS, trimmed);
                    }
                }
            } catch (Exception ignored) {
            }
        }
        Slog.i(TAG, "Thawed all processes, reason=" + reason);
    }

    /** Syncs in-memory map with live /dev/freezer/frozen/cgroup.procs (prunes dead PIDs). */
    public static void pruneDeadFrozenPids() {
        if (sFrozenPidToUid.isEmpty()) {
            return;
        }
        Set<Integer> liveFrozen = readFrozenPidsFromCgroup();
        for (Integer pid : sFrozenPidToUid.keySet()) {
            if (!liveFrozen.contains(pid)) {
                sFrozenPidToUid.remove(pid);
                sFrozenPidToPkg.remove(pid);
            }
        }
    }

    /** Reads actual PIDs currently inside /dev/freezer/frozen/cgroup.procs. */
    public static Set<Integer> readFrozenPidsFromCgroup() {
        ArraySet<Integer> set = new ArraySet<>();
        File f = new File(FREEZER_FROZEN_PROCS);
        if (!f.canRead()) {
            return set;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                String t = line.trim();
                if (!t.isEmpty()) {
                    try {
                        set.add(Integer.parseInt(t));
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return set;
    }

    /** Returns UIDs that currently have active audio playback or active recording. */
    public static Set<Integer> getActiveAudioUids(Context context) {
        ArraySet<Integer> activeUids = new ArraySet<>();
        if (context == null) {
            return activeUids;
        }
        try {
            AudioManager am = context.getSystemService(AudioManager.class);
            if (am == null) {
                return activeUids;
            }
            List<AudioPlaybackConfiguration> playbacks = am.getActivePlaybackConfigurations();
            if (playbacks != null) {
                for (AudioPlaybackConfiguration apc : playbacks) {
                    if (apc != null && apc.isActive()) {
                        activeUids.add(apc.getClientUid());
                    }
                }
            }
            List<AudioRecordingConfiguration> recordings = am.getActiveRecordingConfigurations();
            if (recordings != null) {
                for (AudioRecordingConfiguration arc : recordings) {
                    if (arc != null) {
                        activeUids.add(arc.getClientUid());
                    }
                }
            }
        } catch (Exception e) {
            Slog.w(TAG, "getActiveAudioUids failed", e);
        }
        return activeUids;
    }

    /** Refreshes the cached autostart_blocked_packages list from Settings.Global. */
    public static void refreshBlockedPackages(Context context) {
        if (context == null) {
            return;
        }
        try {
            String raw = Settings.Global.getString(
                    context.getContentResolver(), SETTING_AUTOSTART_BLOCKED_PACKAGES);
            ArraySet<String> next = new ArraySet<>();
            if (!TextUtils.isEmpty(raw)) {
                for (String s : raw.split(",")) {
                    String pkg = s.trim();
                    if (!pkg.isEmpty()) {
                        next.add(pkg);
                    }
                }
            }
            sBlockedPackages = next;
            sLastBlockedReadMs = android.os.SystemClock.elapsedRealtime();
        } catch (Exception e) {
            Slog.w(TAG, "refreshBlockedPackages failed", e);
        }
    }

    public static boolean isAutoStartBlocked(Context context, String pkg) {
        if (TextUtils.isEmpty(pkg)) {
            return false;
        }
        long now = android.os.SystemClock.elapsedRealtime();
        if (sLastBlockedReadMs == 0L || (now - sLastBlockedReadMs) > 30_000L) {
            refreshBlockedPackages(context);
        }
        return sBlockedPackages.contains(pkg);
    }

    /**
     * Checks whether a cross-app Service or ContentProvider wakeup should be blocked.
     *
     * Rules:
     * 1. Caller is a third-party app (appId >= FIRST_APPLICATION_UID and not system).
     * 2. Target is a different app (callingUid != targetUid).
     * 3. Target package is in autostart_blocked_packages.
     * 4. Target app is not currently TOP or running a FOREGROUND_SERVICE.
     */
    public static boolean shouldBlockChainWakeup(Context context,
            int callingUid, @ android.annotation.Nullable String callingPkg, boolean isCallerSystem,
            int targetUid, @ android.annotation.Nullable String targetPkg, int targetProcState,
            String type, String detail) {
        if (targetPkg == null || callingUid == targetUid) {
            return false;
        }
        if (isCallerSystem || UserHandle.getAppId(callingUid) < Process.FIRST_APPLICATION_UID) {
            return false;
        }
        if (callingPkg != null && callingPkg.equals(targetPkg)) {
            return false;
        }
        if (targetProcState <= ActivityManager.PROCESS_STATE_FOREGROUND_SERVICE) {
            return false;
        }
        if (!isAutoStartBlocked(context, targetPkg)) {
            return false;
        }
        int count = sBlockedWakeupCount.incrementAndGet();
        Slog.i(TAG, "Blocked chain wakeup [" + type + "] #" + count
                + ": caller=" + callingPkg + "(" + callingUid + ") -> target="
                + targetPkg + "(" + targetUid + ") " + detail);
        return true;
    }

    /**
     * Checks whether a broadcast about to be delivered to a warm frozen process should be skipped
     * so the frozen process is not disturbed or congested.
     */
    public static boolean shouldSkipBroadcastForFrozenProcess(Context context,
            ProcessRecord app, BroadcastRecord r) {
        if (app == null || r == null || !isPidFrozen(app.getPid())) {
            return false;
        }
        final String action = r.intent != null ? r.intent.getAction() : null;
        // Cross-app broadcast from a 3rd-party app to an autostart-blocked frozen app: skip
        if (UserHandle.getAppId(r.callingUid) >= Process.FIRST_APPLICATION_UID
                && r.callingUid != app.uid
                && isAutoStartBlocked(context, app.info.packageName)) {
            sBlockedWakeupCount.incrementAndGet();
            return true;
        }
        // Routine high-frequency system/background broadcasts should not wake or flood frozen apps
        if (!r.ordered && !r.isForeground()) {
            return true;
        }
        if (Intent.ACTION_TIME_TICK.equals(action)
                || Intent.ACTION_BATTERY_CHANGED.equals(action)
                || Intent.ACTION_SCREEN_ON.equals(action)
                || Intent.ACTION_SCREEN_OFF.equals(action)
                || Intent.ACTION_USER_PRESENT.equals(action)
                || Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(action)
                || "android.net.conn.CONNECTIVITY_CHANGE".equals(action)
                || "android.net.wifi.WIFI_STATE_CHANGED".equals(action)
                || "android.net.wifi.RSSI_CHANGED".equals(action)) {
            return true;
        }
        return false;
    }

    public static int getBlockedWakeupCount() {
        return sBlockedWakeupCount.get();
    }

    private static boolean writeString(String path, String value) {
        try (FileOutputStream fos = new FileOutputStream(path)) {
            fos.write(value.getBytes(StandardCharsets.UTF_8));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
