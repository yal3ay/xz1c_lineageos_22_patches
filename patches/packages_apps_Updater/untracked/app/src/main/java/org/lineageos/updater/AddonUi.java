/* SPDX-License-Identifier: Apache-2.0 */
package org.lineageos.updater;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

/**
 * The add-on cards on the updates page and the "Add-on contents" page: soft green surfaces,
 * a white logo tile, title and state, then text actions (green "View contents", red
 * "Uninstall"); the contents page has a Sony / Google switch, the apps with their state, the
 * update policy and an outlined "Uninstall this package". All text comes from resources.
 */
final class AddonUi {
    private final AppCompatActivity a;
    private final AddonsManager manager;
    private LinearLayout host;
    private String detailId;

    AddonUi(AppCompatActivity activity, AddonsManager owner) {
        a = activity;
        manager = owner;
    }

    // ---------------------------------------------------------------- helpers

    private int dp(float value) {
        return Math.round(value * a.getResources().getDisplayMetrics().density);
    }

    private int color(int id) {
        return a.getColor(id);
    }

    private String name(String id) {
        return a.getString("sony".equals(id) ? R.string.addon_sony_apps : R.string.addon_google_apps);
    }

    private LinearLayout column() {
        LinearLayout v = new LinearLayout(a);
        v.setOrientation(LinearLayout.VERTICAL);
        return v;
    }

    private LinearLayout row() {
        LinearLayout v = new LinearLayout(a);
        v.setGravity(Gravity.CENTER_VERTICAL);
        return v;
    }

    private TextView text(CharSequence label, float size, int textColor) {
        TextView t = new TextView(a);
        t.setText(label);
        t.setTextSize(size);
        t.setTextColor(color(textColor));
        t.setTextDirection(View.TEXT_DIRECTION_LOCALE);
        return t;
    }

    private TextView medium(CharSequence label, float size, int textColor) {
        TextView t = text(label, size, textColor);
        t.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        return t;
    }

    private GradientDrawable rounded(int fill, float radius) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(fill);
        d.setCornerRadius(dp(radius));
        return d;
    }

    private Drawable ripple(Drawable content, float radius) {
        GradientDrawable mask = new GradientDrawable();
        mask.setColor(Color.WHITE);
        mask.setCornerRadius(dp(radius));
        return new RippleDrawable(ColorStateList.valueOf(0x1f000000), content, mask);
    }

    private Drawable icon(int res, int tint) {
        Drawable d = a.getDrawable(res).mutate();
        d.setTint(color(tint));
        return d;
    }

    private void spaced(LinearLayout parent, View child, int top) {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, -2);
        p.topMargin = dp(top);
        parent.addView(child, p);
    }

    /** A flat text action: green, or red with a trash glyph. */
    private TextView action(int string, Runnable run, boolean destructive) {
        TextView b = medium(a.getString(string), 15, destructive ? R.color.addon_error : R.color.addon_accent);
        b.setGravity(Gravity.CENTER);
        b.setPadding(dp(14), dp(10), dp(14), dp(10));
        if (destructive) {
            Drawable trash = icon(R.drawable.xz1c_ic_trash, R.color.addon_error);
            trash.setBounds(0, 0, dp(20), dp(20));
            b.setCompoundDrawablesRelative(trash, null, null, null);
            b.setCompoundDrawablePadding(dp(8));
        }
        b.setBackground(ripple(null, 20));
        b.setOnClickListener(v -> run.run());
        return b;
    }

    /** The brand tile: SONY in a serif face, or a blue G. */
    private TextView logo(String id, int size) {
        boolean sony = "sony".equals(id);
        TextView t = new TextView(a);
        t.setText(sony ? "SONY" : "G");
        t.setGravity(Gravity.CENTER);
        t.setSingleLine(true);
        t.setTextColor(sony ? 0xff111111 : 0xff4285f4);
        t.setTypeface(sony ? Typeface.create(Typeface.SERIF, Typeface.BOLD) : Typeface.create("sans-serif", Typeface.BOLD));
        t.setTextSize(sony ? size * 0.25f : size * 0.6f);
        if (sony) t.setLetterSpacing(0.02f);
        t.setBackground(rounded(Color.WHITE, size * 0.28f));
        t.setElevation(dp(size > 60 ? 3 : 0));
        return t;
    }

    private String stateText(String id) {
        int state = manager.removalState(id);
        return a.getString(state != 0 ? R.string.addon_remove_pending
                : manager.installed(id) ? R.string.addon_state_installed : R.string.addon_state_missing);
    }

    // ---------------------------------------------------------------- updates page cards

    void bind(LinearLayout target) {
        host = target;
        host.setOrientation(LinearLayout.VERTICAL);
        refresh();
    }

    void refresh() {
        if (host == null) return;
        host.removeAllViews();
        if (detailId != null) {
            populateDetails(detailId);
            return;
        }
        TextView title = medium(a.getString(R.string.addon_section_title), 15, R.color.addon_accent);
        title.setPadding(dp(4), 0, 0, 0);
        spaced(host, title, 8);
        for (String id : new String[] { "sony", "gapps" }) addCard(id);
        pendingFooter(host);
    }

    private void addCard(String id) {
        LinearLayout card = column();
        card.setPadding(dp(20), dp(20), dp(12), dp(8));
        card.setBackground(rounded(color(R.color.addon_surface), 28));

        LinearLayout heading = row();
        heading.addView(logo(id, 52), new LinearLayout.LayoutParams(dp(52), dp(52)));
        LinearLayout titles = column();
        titles.setPadding(dp(14), 0, dp(8), 0);
        titles.addView(text(name(id), 18, R.color.addon_foreground));
        TextView state = text(stateText(id), 13, R.color.addon_secondary);
        state.setPadding(0, dp(2), 0, 0);
        titles.addView(state);
        heading.addView(titles, new LinearLayout.LayoutParams(0, -2, 1));
        if (manager.installed(id) && manager.removalState(id) == 0) {
            ImageView check = new ImageView(a);
            check.setImageDrawable(icon(R.drawable.xz1c_ic_check, R.color.addon_accent));
            LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(dp(26), dp(26));
            cp.rightMargin = dp(10);
            heading.addView(check, cp);
        }
        card.addView(heading);

        TextView summary = text(a.getString("sony".equals(id) ? R.string.addon_sony_summary
                : R.string.addon_google_summary), 13, R.color.addon_secondary);
        summary.setPadding(0, 0, dp(8), 0);
        spaced(card, summary, 16);

        LinearLayout actions = row();
        actions.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
        actions.addView(action(R.string.addon_view_contents, () -> openDetails(id), false));
        if (manager.installed(id)) {
            boolean pending = manager.removalState(id) != 0;
            actions.addView(action(pending ? R.string.addon_cancel_remove : R.string.addon_uninstall,
                    () -> confirmRemoval(id), !pending));
        } else {
            actions.addView(action(R.string.addon_install_action, () -> manager.installAddon(id), false));
        }
        spaced(card, actions, 10);
        spaced(host, card, 14);
    }

    private void openDetails(String id) {
        a.startActivity(new Intent(a, AddonContentActivity.class).putExtra("addon_id", id));
    }

    // ---------------------------------------------------------------- contents page

    void showDetails(String id) {
        detailId = id;
        LinearLayout page = column();
        page.setFitsSystemWindows(true);
        page.setBackgroundColor(color(R.color.addon_background));

        // ← Add-on contents
        LinearLayout bar = row();
        bar.setPadding(dp(8), dp(8), dp(16), dp(8));
        ImageView back = new ImageView(a);
        back.setImageDrawable(icon(R.drawable.xz1c_ic_back, R.color.addon_foreground));
        back.setPadding(dp(12), dp(12), dp(12), dp(12));
        back.setBackground(ripple(null, 24));
        back.setContentDescription(a.getString(R.string.addon_back));
        back.setOnClickListener(v -> a.finish());
        bar.addView(back, new LinearLayout.LayoutParams(dp(48), dp(48)));
        TextView title = text(a.getString(R.string.xz1c_addon_contents_title), 21, R.color.addon_foreground);
        title.setPadding(dp(14), 0, 0, 0);
        bar.addView(title, new LinearLayout.LayoutParams(0, -2, 1));
        page.addView(bar);

        ScrollView scroll = new ScrollView(a);
        scroll.setFillViewport(true);
        host = column();
        host.setPadding(dp(20), dp(8), dp(20), dp(28));
        scroll.addView(host);
        page.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));
        a.getWindow().setStatusBarColor(color(R.color.addon_background));
        a.getWindow().setNavigationBarColor(color(R.color.addon_background));
        a.setContentView(page);
        refresh();
    }

    private void populateDetails(String id) {
        FrameLayout logoBox = new FrameLayout(a);
        logoBox.addView(logo(id, 72), new FrameLayout.LayoutParams(dp(72), dp(72), Gravity.CENTER));
        spaced(host, logoBox, 16);

        TextView title = text(name(id), 25, R.color.addon_foreground);
        title.setGravity(Gravity.CENTER);
        spaced(host, title, 18);

        // state chip
        FrameLayout chipBox = new FrameLayout(a);
        TextView chip = medium(stateText(id), 13, R.color.addon_accent);
        chip.setGravity(Gravity.CENTER_VERTICAL);
        chip.setPadding(dp(14), dp(7), dp(16), dp(7));
        chip.setBackground(rounded(color(R.color.addon_surface), 20));
        if (manager.installed(id) && manager.removalState(id) == 0) {
            Drawable ok = icon(R.drawable.xz1c_ic_check_circle, R.color.addon_accent);
            ok.setBounds(0, 0, dp(18), dp(18));
            chip.setCompoundDrawablesRelative(ok, null, null, null);
            chip.setCompoundDrawablePadding(dp(6));
        }
        chipBox.addView(chip, new FrameLayout.LayoutParams(-2, -2, Gravity.CENTER));
        spaced(host, chipBox, 12);

        TextView summary = text(a.getString("sony".equals(id) ? R.string.xz1c_addon_sony_detail
                : R.string.xz1c_addon_google_detail), 13, R.color.addon_secondary);
        summary.setGravity(Gravity.CENTER);
        spaced(host, summary, 14);

        // Sony | Google
        LinearLayout seg = row();
        seg.setPadding(dp(4), dp(4), dp(4), dp(4));
        seg.setBackground(rounded(color(R.color.addon_surface), 28));
        for (String other : new String[] { "sony", "gapps" }) {
            boolean on = other.equals(id);
            TextView t = text("sony".equals(other) ? "Sony" : "Google", 15,
                    on ? android.R.color.white : R.color.addon_secondary);
            if (on) t.setTextColor(Color.WHITE);
            t.setGravity(Gravity.CENTER);
            t.setPadding(0, dp(12), 0, dp(12));
            if (on) t.setBackground(rounded(color(R.color.addon_accent), 24));
            t.setOnClickListener(v -> {
                if (!other.equals(detailId)) {
                    detailId = other;
                    refresh();
                }
            });
            seg.addView(t, new LinearLayout.LayoutParams(0, -2, 1));
        }
        spaced(host, seg, 24);

        try {
            List<AddonInventory.Entry> entries = AddonInventory.read(a, id);
            LinearLayout head = row();
            head.setPadding(dp(4), 0, dp(4), 0);
            head.addView(medium(a.getString(R.string.xz1c_addon_included), 15, R.color.addon_accent),
                    new LinearLayout.LayoutParams(0, -2, 1));
            head.addView(text(a.getResources().getQuantityString(R.plurals.xz1c_addon_app_count,
                    entries.size(), entries.size()), 14, R.color.addon_secondary));
            spaced(host, head, 26);

            LinearLayout list = column();
            list.setPadding(dp(16), dp(4), dp(16), dp(4));
            list.setBackground(rounded(color(R.color.addon_surface), 28));
            for (int i = 0; i < entries.size(); i++) {
                AddonInventory.Entry entry = entries.get(i);
                if (i > 0) {
                    View line = new View(a);
                    line.setBackgroundColor(0x14000000);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, dp(1));
                    list.addView(line, lp);
                }
                LinearLayout item = row();
                item.setPadding(0, dp(14), 0, dp(14));
                ImageView tile = new ImageView(a);
                Drawable appIcon = null;
                if (entry.installed != null && entry.installed.applicationInfo != null
                        && entry.installed.applicationInfo.icon != 0) {
                    appIcon = entry.installed.applicationInfo.loadIcon(a.getPackageManager());
                    // some components point at the system's default robot: draw the glyph instead
                    Drawable robot = a.getPackageManager().getDefaultActivityIcon();
                    if (robot.getConstantState() != null && appIcon != null
                            && robot.getConstantState().equals(appIcon.getConstantState())) {
                        appIcon = null;
                    }
                }
                if (appIcon != null) {
                    tile.setImageDrawable(appIcon);
                    tile.setPadding(dp(8), dp(8), dp(8), dp(8));
                } else {
                    tile.setImageDrawable(icon(R.drawable.xz1c_ic_package, R.color.addon_accent));
                    tile.setPadding(dp(13), dp(13), dp(13), dp(13));
                }
                tile.setBackground(rounded(Color.WHITE, 14));
                item.addView(tile, new LinearLayout.LayoutParams(dp(48), dp(48)));
                LinearLayout labels = column();
                labels.setPadding(dp(14), 0, dp(8), 0);
                TextView label = text(entry.label, 15, R.color.addon_foreground);
                label.setSingleLine(true);
                label.setEllipsize(TextUtils.TruncateAt.END);
                labels.addView(label);
                String version = entry.installed != null ? entry.installed.versionName : null;
                TextView sub = text(version != null && !version.isEmpty()
                        ? a.getString(R.string.addon_app_version, version) : entry.packageName, 12, R.color.addon_secondary);
                sub.setSingleLine(true);
                sub.setEllipsize(TextUtils.TruncateAt.END);
                sub.setTextDirection(View.TEXT_DIRECTION_LTR);
                labels.addView(sub);
                item.addView(labels, new LinearLayout.LayoutParams(0, -2, 1));
                if (entry.installed()) {
                    ImageView check = new ImageView(a);
                    check.setImageDrawable(icon(R.drawable.xz1c_ic_check, R.color.addon_accent));
                    item.addView(check, new LinearLayout.LayoutParams(dp(24), dp(24)));
                } else {
                    item.addView(text(a.getString(R.string.addon_state_missing), 12, R.color.addon_secondary));
                }
                list.addView(item);
            }
            spaced(host, list, 10);
        } catch (Exception error) {
            spaced(host, text(a.getString(R.string.addon_inventory_failed), 14, R.color.addon_error), 20);
        }

        // kept during system updates
        LinearLayout policy = row();
        policy.setGravity(Gravity.TOP);
        policy.setPadding(dp(8), 0, dp(4), 0);
        ImageView sync = new ImageView(a);
        sync.setImageDrawable(icon(R.drawable.ic_menu_refresh, R.color.addon_accent));
        LinearLayout.LayoutParams syncLp = new LinearLayout.LayoutParams(dp(24), dp(24));
        syncLp.topMargin = dp(2);
        policy.addView(sync, syncLp);
        LinearLayout ptext = column();
        ptext.setPadding(dp(16), 0, 0, 0);
        ptext.addView(medium(a.getString(R.string.addon_update_policy), 15, R.color.addon_foreground));
        TextView pnote = text(a.getString(R.string.xz1c_addon_policy_short), 13, R.color.addon_secondary);
        pnote.setPadding(0, dp(4), 0, 0);
        ptext.addView(pnote);
        policy.addView(ptext, new LinearLayout.LayoutParams(0, -2, 1));
        spaced(host, policy, 26);

        // uninstall / install
        TextView big;
        if (manager.installed(id)) {
            boolean pending = manager.removalState(id) != 0;
            big = action(pending ? R.string.addon_cancel_remove : R.string.addon_uninstall_package,
                    () -> confirmRemoval(id), !pending);
            GradientDrawable outline = rounded(Color.TRANSPARENT, 28);
            outline.setStroke(dp(1), pending ? color(R.color.addon_accent) : 0x33000000);
            big.setBackground(ripple(outline, 28));
        } else {
            big = action(R.string.addon_install_action, () -> manager.installAddon(id), false);
            big.setTextColor(Color.WHITE);
            big.setBackground(ripple(rounded(color(R.color.addon_accent), 28), 28));
        }
        big.setTextSize(15);
        big.setPadding(dp(16), dp(16), dp(16), dp(16));
        spaced(host, big, 22);
        if (manager.installed(id)) {
            TextView foot = text(a.getString(R.string.xz1c_addon_remove_footnote), 13, R.color.addon_secondary);
            foot.setGravity(Gravity.CENTER);
            spaced(host, foot, 14);
        }
        pendingFooter(host);
    }

    // ---------------------------------------------------------------- removal (unchanged flow)

    private void confirmRemoval(String id) {
        if (manager.removalState(id) != 0) {
            manager.queueRemoval(id, 0);
            refresh();
            return;
        }
        LinearLayout box = column();
        box.setPadding(dp(24), dp(4), dp(24), 0);
        box.addView(text(a.getString("sony".equals(id) ? R.string.addon_remove_sony_note
                : R.string.addon_remove_google_note), 14, R.color.addon_secondary));
        CheckBox delete = new CheckBox(a);
        delete.setText(R.string.addon_remove_data);
        delete.setChecked(false);
        spaced(box, delete, 16);
        spaced(box, text(a.getString(R.string.addon_remove_data_note), 12, R.color.addon_secondary), 4);
        new AlertDialog.Builder(a).setTitle(a.getString(R.string.addon_remove_title, name(id))).setView(box)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(R.string.addon_uninstall, (d, w) -> {
                    manager.queueRemoval(id, delete.isChecked() ? 2 : 1);
                    refresh();
                }).show();
    }

    private void pendingFooter(LinearLayout target) {
        if (manager.removalState("sony") == 0 && manager.removalState("gapps") == 0) return;
        spaced(target, text(a.getString(R.string.addon_remove_restart_note), 13, R.color.addon_secondary), 20);
        TextView apply = action(R.string.addon_remove_apply, () -> manager.applyRemovals(), false);
        apply.setTextColor(Color.WHITE);
        apply.setBackground(ripple(rounded(color(R.color.addon_accent), 26), 26));
        apply.setPadding(dp(16), dp(14), dp(16), dp(14));
        spaced(target, apply, 10);
    }
}
