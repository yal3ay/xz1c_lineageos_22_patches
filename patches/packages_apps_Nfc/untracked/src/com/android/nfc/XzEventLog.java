/*
 * Copyright (C) 2026 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package com.android.nfc;

import android.content.Context;
import android.os.Process;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.Date;
import java.util.Locale;

/**
 * 【本项目】门禁卡事件日志：写到文件，NFC 进程重启、logcat 滚动后都还在。
 *
 * 2026-10-08：门禁刷不开、重新选卡又好了，但 logcat 只剩最近半小时、NFC snoop 随
 * 进程重启清空，查不到当时发生了什么。这里只记关键事件（卡号下发、芯片初始化、
 * NFC 开关、亮灭屏、读卡器场强、卡模拟激活、硬件错误），用 dumpsys nfc 查看。
 * 文件超过 256 KB 轮换一次，最多占 512 KB。
 */
public final class XzEventLog {
    private static final String TAG = "XZ1C-NfcEvents";
    private static final String NAME = "xz1c_nfc_events.log";
    private static final long MAX_BYTES = 256 * 1024;
    private static final int DUMP_LINES = 400;

    private static File sFile;
    private static long sLastFieldOn, sLastFieldOff;
    private static int sFieldOnSkipped, sFieldOffSkipped;

    private XzEventLog() { }

    public static synchronized void init(Context context) {
        // 2026-10-13: the access card issue is fixed and the user asked to stop recording.
        // No file any more (events still go to logcat); remove what the old builds wrote.
        File dir = context.createDeviceProtectedStorageContext().getFilesDir();
        new File(dir, NAME).delete();
        new File(dir, NAME + ".1").delete();
    }

    public static synchronized void log(String msg) {
        Log.i(TAG, msg);
        if (sFile == null) return;
        final String line = new SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.US)
                .format(new Date()) + " [" + Process.myPid() + "] " + msg + "\n";
        try {
            if (sFile.length() > MAX_BYTES) {
                final File old = new File(sFile.getPath() + ".1");
                old.delete();
                sFile.renameTo(old);
            }
            try (FileWriter w = new FileWriter(sFile, true)) {
                w.write(line);
            }
        } catch (IOException e) {
            Log.w(TAG, "write failed", e);
        }
    }

    /** A reader polls many times a second; log at most one field event per second. */
    public static synchronized void fieldOn() {
        final long now = System.currentTimeMillis();
        if (now - sLastFieldOn < 1000) { sFieldOnSkipped++; return; }
        sLastFieldOn = now;
        log("reader field ON" + (sFieldOnSkipped > 0 ? " (+" + sFieldOnSkipped + " merged)" : ""));
        sFieldOnSkipped = 0;
    }

    public static synchronized void fieldOff() {
        final long now = System.currentTimeMillis();
        if (now - sLastFieldOff < 1000) { sFieldOffSkipped++; return; }
        sLastFieldOff = now;
        log("reader field OFF" + (sFieldOffSkipped > 0 ? " (+" + sFieldOffSkipped + " merged)" : ""));
        sFieldOffSkipped = 0;
    }

    public static synchronized void dump(PrintWriter pw) {
        pw.println("---- XZ1C access card events (last " + DUMP_LINES + ") ----");
        if (sFile == null) {
            pw.println("(not initialized)");
            return;
        }
        final ArrayDeque<String> tail = new ArrayDeque<>();
        for (File f : new File[] { new File(sFile.getPath() + ".1"), sFile }) {
            if (!f.exists()) continue;
            try (BufferedReader r = new BufferedReader(new FileReader(f))) {
                String l;
                while ((l = r.readLine()) != null) {
                    tail.addLast(l);
                    if (tail.size() > DUMP_LINES) tail.removeFirst();
                }
            } catch (IOException e) {
                pw.println("(read failed: " + e + ")");
            }
        }
        for (String l : tail) pw.println(l);
        pw.println("---- end XZ1C access card events ----");
    }
}
