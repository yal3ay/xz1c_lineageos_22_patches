/*
 * Copyright (C) 2026 The XZ1C Project
 * SPDX-License-Identifier: Apache-2.0
 */

package com.android.systemui.media.controls.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.PlaybackState;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.PathParser;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * The Apple TV remote's media card, laid out like the iPhone's: the TV's name, a progress bar
 * with elapsed and remaining time, remote · ⟲10 · play/pause · ⟳10 · output, and a volume
 * slider. It drives the AirCast "AppleTvRemote" session through its {@link MediaController}:
 * the position, duration and volume all come from the Apple TV, nothing is made up — without a
 * duration the progress row is hidden, without an absolute volume the slider is.
 * Shown in place of the regular player contents by {@code MediaViewController} (see
 * {@code xz1cAppleTvConstraints}); short (collapsed) cards keep only the name and the buttons.
 */
public class Xz1cAppleTvCard extends LinearLayout {
    private static final int FG = Color.WHITE;
    private static final int FG2 = 0x9effffff;
    private static final int TRACK = 0x38ffffff;
    private static final int FILL = 0xdbffffff;
    private static final float COMPACT_BELOW_DP = 168;

    private final Handler mMain = new Handler(Looper.getMainLooper());
    private final TextView mTitle, mSubtitle, mElapsed, mRemaining;
    private final Bar mSeek, mVolume;
    private final Icon mPlayPause;
    private final LinearLayout mProgressRow, mVolumeRow;
    private MediaController mController;
    private Runnable mOpenRemote, mOpenOutput;
    private boolean mCompact;

    private final MediaController.Callback mCallback = new MediaController.Callback() {
        @Override public void onPlaybackStateChanged(PlaybackState state) {
            update();
        }

        @Override public void onMetadataChanged(MediaMetadata metadata) {
            update();
        }

        @Override public void onAudioInfoChanged(MediaController.PlaybackInfo info) {
            update();
        }
    };

    private final Runnable mTick = new Runnable() {
        @Override public void run() {
            update();
            if (isAttachedToWindow() && isPlaying()) mMain.postDelayed(this, 500);
        }
    };

    public Xz1cAppleTvCard(Context context) {
        super(context);
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER_VERTICAL);
        setPadding(dp(16), dp(12), dp(16), dp(10));
        // translucent dark, the wallpaper shows through; the player's own album background is hidden
        setBackground(round(0xb8161719, 28));
        setClipToOutline(true);

        // the TV: a placeholder square (Apple TV sends no artwork) and its name
        LinearLayout head = new LinearLayout(context);
        head.setGravity(Gravity.CENTER_VERTICAL);
        // Apple TV sends no artwork: a grey square with the remote in its centre
        FrameLayout art = new FrameLayout(context);
        art.setBackground(round(0x24ffffff, 10));
        ImageView artIcon = new ImageView(context);
        artIcon.setImageDrawable(new Icon(Icon.REMOTE, FG2));
        art.addView(artIcon, new FrameLayout.LayoutParams(dp(26), dp(26), Gravity.CENTER));
        head.addView(art, new LayoutParams(dp(44), dp(44)));
        LinearLayout names = new LinearLayout(context);
        names.setOrientation(VERTICAL);
        names.setPadding(dp(12), 0, 0, 0);
        mTitle = text(15, FG);
        mTitle.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        mSubtitle = text(12, FG2);
        mSubtitle.setText("Apple TV");
        names.addView(mTitle);
        names.addView(mSubtitle);
        head.addView(names, new LayoutParams(0, -2, 1));
        addView(head);

        // elapsed · bar · −remaining
        mProgressRow = new LinearLayout(context);
        mProgressRow.setGravity(Gravity.CENTER_VERTICAL);
        mElapsed = text(11.5f, FG2);
        mElapsed.setFontFeatureSettings("tnum");
        mRemaining = text(11.5f, FG2);
        mRemaining.setFontFeatureSettings("tnum");
        mRemaining.setGravity(Gravity.END);
        mSeek = new Bar(context, false);
        mProgressRow.addView(mElapsed, new LayoutParams(dp(42), -2));
        mProgressRow.addView(mSeek, new LayoutParams(0, dp(22), 1));
        mProgressRow.addView(mRemaining, new LayoutParams(dp(48), -2));
        LayoutParams plp = new LayoutParams(-1, -2);
        plp.topMargin = dp(10);
        addView(mProgressRow, plp);

        // remote · ⟲10 · ▶/❚❚ · ⟳10 · tv
        LinearLayout controls = new LinearLayout(context);
        controls.setGravity(Gravity.CENTER_VERTICAL);
        controls.addView(cell(new Icon(Icon.REMOTE, FG2), 22, () -> run(mOpenRemote)), new LayoutParams(0, dp(46), 1));
        controls.addView(cell(new Icon(Icon.BACK10, FG), 32, () -> custom("back10")), new LayoutParams(0, dp(46), 1));
        mPlayPause = new Icon(Icon.PLAY, FG);
        controls.addView(cell(mPlayPause, 38, this::playPause), new LayoutParams(0, dp(46), 1.2f));
        controls.addView(cell(new Icon(Icon.FWD10, FG), 32, () -> custom("fwd10")), new LayoutParams(0, dp(46), 1));
        TextView tv = text(10, FG);
        tv.setText("tv");
        tv.setTypeface(Typeface.DEFAULT_BOLD);
        tv.setPadding(dp(5), dp(3), dp(5), dp(3));
        tv.setBackground(round(0x38ffffff, 5));
        FrameLayout tvCell = new FrameLayout(context);
        tvCell.addView(tv, new FrameLayout.LayoutParams(-2, -2, Gravity.CENTER));
        tvCell.setOnClickListener(v -> run(mOpenOutput));
        controls.addView(tvCell, new LayoutParams(0, dp(46), 1));
        LayoutParams clp = new LayoutParams(-1, -2);
        clp.topMargin = dp(2);
        addView(controls, clp);

        // 🔈 ─── 🔊
        mVolumeRow = new LinearLayout(context);
        mVolumeRow.setGravity(Gravity.CENTER_VERTICAL);
        ImageView low = new ImageView(context);
        low.setImageDrawable(new Icon(Icon.VOLUME_LOW, FG2));
        ImageView high = new ImageView(context);
        high.setImageDrawable(new Icon(Icon.VOLUME_HIGH, FG2));
        mVolume = new Bar(context, true);
        mVolumeRow.addView(low, new LayoutParams(dp(16), dp(16)));
        LayoutParams vlp = new LayoutParams(0, dp(26), 1);
        vlp.leftMargin = vlp.rightMargin = dp(10);
        mVolumeRow.addView(mVolume, vlp);
        mVolumeRow.addView(high, new LayoutParams(dp(20), dp(20)));
        addView(mVolumeRow, new LayoutParams(-1, -2));

        mSeek.listener = (fraction, done) -> {
            long duration = duration();
            if (duration <= 0) return;
            showTime((long) (fraction * duration), duration);
            if (done && mController != null) mController.getTransportControls().seekTo((long) (fraction * duration));
        };
        mVolume.listener = (fraction, done) -> {
            MediaController c = mController;
            MediaController.PlaybackInfo info = c == null ? null : c.getPlaybackInfo();
            if (info == null || info.getMaxVolume() <= 0) return;
            c.setVolumeTo(Math.round(fraction * info.getMaxVolume()), 0);
        };
        update();
    }

    /**
     * Binds the card to the remote's session, or unbinds it ({@code controller} null) when the
     * player shows something else. {@code openRemote} and {@code openOutput} are the player's
     * own tap and output-switcher actions.
     */
    public void bind(MediaController controller, CharSequence title, Runnable openRemote, Runnable openOutput) {
        if (mController != null && (controller == null
                || !mController.getSessionToken().equals(controller.getSessionToken()))) {
            mController.unregisterCallback(mCallback);
            mController = null;
        }
        if (controller != null && mController == null) {
            mController = controller;
            mController.registerCallback(mCallback, mMain);
        }
        mOpenRemote = openRemote;
        mOpenOutput = openOutput;
        mTitle.setText(title);
        update();
    }

    @Override protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        update();
    }

    @Override protected void onDetachedFromWindow() {
        mMain.removeCallbacks(mTick);
        super.onDetachedFromWindow();
    }

    @Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        boolean compact = h > 0 && h < dp(COMPACT_BELOW_DP);
        if (compact != mCompact) {
            mCompact = compact;
            post(this::update);
        }
    }

    // ---------------------------------------------------------------- state

    private PlaybackState state() {
        return mController == null ? null : mController.getPlaybackState();
    }

    private boolean isPlaying() {
        PlaybackState s = state();
        return s != null && s.getState() == PlaybackState.STATE_PLAYING;
    }

    private long duration() {
        MediaMetadata m = mController == null ? null : mController.getMetadata();
        return m == null ? 0 : m.getLong(MediaMetadata.METADATA_KEY_DURATION);
    }

    private long position() {
        PlaybackState s = state();
        if (s == null || s.getPosition() < 0) return -1;
        long pos = s.getPosition();
        if (s.getState() == PlaybackState.STATE_PLAYING) {
            pos += (long) ((SystemClock.elapsedRealtime() - s.getLastPositionUpdateTime()) * s.getPlaybackSpeed());
        }
        return pos;
    }

    private void update() {
        boolean playing = isPlaying();
        mPlayPause.setShape(playing ? Icon.PAUSE : Icon.PLAY);

        long duration = duration();
        long pos = position();
        boolean showProgress = !mCompact && duration > 0 && pos >= 0;
        mProgressRow.setVisibility(showProgress ? VISIBLE : GONE);
        if (showProgress && !mSeek.dragging) {
            pos = Math.min(pos, duration);
            mSeek.setFraction((float) pos / duration);
            showTime(pos, duration);
        }

        MediaController.PlaybackInfo info = mController == null ? null : mController.getPlaybackInfo();
        boolean absolute = info != null && info.getVolumeControl() == android.media.VolumeProvider.VOLUME_CONTROL_ABSOLUTE
                && info.getMaxVolume() > 0;
        mVolumeRow.setVisibility(!mCompact && absolute ? VISIBLE : GONE);
        if (absolute && !mVolume.dragging) mVolume.setFraction((float) info.getCurrentVolume() / info.getMaxVolume());

        mMain.removeCallbacks(mTick);
        if (playing && showProgress && isAttachedToWindow()) mMain.postDelayed(mTick, 500);
    }

    private void showTime(long pos, long duration) {
        mElapsed.setText(format(pos));
        mRemaining.setText("−" + format(Math.max(0, duration - pos)));
    }

    private static String format(long ms) {
        long s = Math.max(0, ms / 1000);
        long h = s / 3600, m = s % 3600 / 60, x = s % 60;
        return h > 0 ? String.format("%d:%02d:%02d", h, m, x) : String.format("%d:%02d", m, x);
    }

    // ---------------------------------------------------------------- actions

    private void playPause() {
        if (mController == null) return;
        if (isPlaying()) mController.getTransportControls().pause();
        else mController.getTransportControls().play();
    }

    private void custom(String action) {
        if (mController != null) mController.getTransportControls().sendCustomAction(action, null);
    }

    private static void run(Runnable r) {
        if (r != null) r.run();
    }

    // ---------------------------------------------------------------- views

    private FrameLayout cell(Icon icon, int sizeDp, Runnable click) {
        FrameLayout f = new FrameLayout(getContext());
        ImageView v = new ImageView(getContext());
        v.setImageDrawable(icon);
        f.addView(v, new FrameLayout.LayoutParams(dp(sizeDp), dp(sizeDp), Gravity.CENTER));
        f.setOnClickListener(x -> {
            x.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            click.run();
        });
        return f;
    }

    private TextView text(float sp, int color) {
        TextView t = new TextView(getContext());
        t.setTextSize(sp);
        t.setTextColor(color);
        t.setSingleLine(true);
        return t;
    }

    private GradientDrawable round(int color, int radiusDp) {
        GradientDrawable g = new GradientDrawable();
        g.setColor(color);
        g.setCornerRadius(dp(radiusDp));
        return g;
    }

    private int dp(float v) {
        return Math.round(v * getResources().getDisplayMetrics().density);
    }

    /** A thin rounded track the finger drags; thicker while dragging, like the iPhone's. */
    private interface BarListener {
        void onChange(float fraction, boolean done);
    }

    private final class Bar extends View {
        final boolean continuous;
        BarListener listener;
        boolean dragging;
        private float mFraction;
        private long mLastSent;
        private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final RectF mRect = new RectF();

        Bar(Context c, boolean continuous) {
            super(c);
            this.continuous = continuous;
        }

        void setFraction(float f) {
            mFraction = Math.max(0, Math.min(1, f));
            invalidate();
        }

        @Override protected void onDraw(Canvas canvas) {
            float h = dp(dragging ? 9 : 5);
            float top = (getHeight() - h) / 2f;
            mRect.set(0, top, getWidth(), top + h);
            mPaint.setColor(TRACK);
            canvas.drawRoundRect(mRect, h / 2, h / 2, mPaint);
            mRect.right = Math.max(h, getWidth() * mFraction);
            mPaint.setColor(FILL);
            canvas.drawRoundRect(mRect, h / 2, h / 2, mPaint);
        }

        @Override public boolean onTouchEvent(MotionEvent e) {
            float f = Math.max(0, Math.min(1, e.getX() / Math.max(1, getWidth())));
            switch (e.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    dragging = true;
                    getParent().requestDisallowInterceptTouchEvent(true);
                    // fall through
                case MotionEvent.ACTION_MOVE:
                    setFraction(f);
                    long now = SystemClock.uptimeMillis();
                    if (listener != null && (!continuous || now - mLastSent > 80)) {
                        mLastSent = now;
                        listener.onChange(f, false);
                    }
                    return true;
                case MotionEvent.ACTION_UP:
                    setFraction(f);
                    // fall through
                case MotionEvent.ACTION_CANCEL:
                    dragging = false;
                    invalidate();
                    if (listener != null && e.getActionMasked() == MotionEvent.ACTION_UP) listener.onChange(f, true);
                    update();
                    return true;
                default:
                    return false;
            }
        }
    }

    /** The card's glyphs on a 24×24 grid, matching the iPhone's: strokes, the ▶ filled. */
    private static final class Icon extends Drawable {
        static final int REMOTE = 0, BACK10 = 1, FWD10 = 2, PLAY = 3, PAUSE = 4, VOLUME_LOW = 5, VOLUME_HIGH = 6;
        private final Paint mStroke = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint mFill = new Paint(Paint.ANTI_ALIAS_FLAG);
        private int mShape;

        Icon(int shape, int color) {
            mShape = shape;
            mStroke.setStyle(Paint.Style.STROKE);
            mStroke.setStrokeCap(Paint.Cap.ROUND);
            mStroke.setStrokeJoin(Paint.Join.ROUND);
            mStroke.setColor(color);
            mFill.setColor(color);
            mFill.setTextAlign(Paint.Align.CENTER);
            mFill.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
        }

        void setShape(int shape) {
            if (shape != mShape) {
                mShape = shape;
                invalidateSelf();
            }
        }

        private static Path path(String d) {
            return PathParser.createPathFromPathData(d);
        }

        @Override public void draw(Canvas canvas) {
            float s = Math.min(getBounds().width(), getBounds().height()) / 24f;
            canvas.save();
            canvas.translate(getBounds().left, getBounds().top);
            canvas.scale(s, s);
            mStroke.setStrokeWidth(1.8f);
            switch (mShape) {
                case REMOTE:
                    canvas.drawRoundRect(8f, 2.5f, 16f, 21.5f, 3f, 3f, mStroke);
                    canvas.drawCircle(12f, 7f, 1.6f, mFill);
                    canvas.drawCircle(12f, 12f, 1f, mFill);
                    canvas.drawCircle(12f, 15.5f, 1f, mFill);
                    break;
                case BACK10:
                case FWD10: {
                    mStroke.setStrokeWidth(1.7f);
                    canvas.save();
                    if (mShape == FWD10) canvas.scale(-1f, 1f, 12f, 12f);
                    canvas.drawPath(path("M4.5,12a7.5,7.5 0 1,0 2.2,-5.3"), mStroke);
                    canvas.drawPath(path("M4,3.5v3.7h3.7"), mStroke);
                    canvas.restore();
                    mFill.setTextSize(7.2f);
                    canvas.drawText("10", 12f, 15.3f, mFill);
                    break;
                }
                case PLAY:
                    canvas.drawPath(path("M8,5.2v13.6a0.8,0.8 0 0,0 1.2,0.7l10.6,-6.8a0.8,0.8 0 0,0 0,-1.4L9.2,4.5A0.8,0.8 0 0,0 8,5.2z"), mFill);
                    break;
                case PAUSE:
                    canvas.drawRoundRect(6.5f, 5f, 10.3f, 19f, 1.2f, 1.2f, mFill);
                    canvas.drawRoundRect(13.7f, 5f, 17.5f, 19f, 1.2f, 1.2f, mFill);
                    break;
                case VOLUME_LOW:
                    canvas.drawPath(path("M4,9h4l5,-4v14l-5,-4H4z"), mFill);
                    break;
                case VOLUME_HIGH:
                    canvas.drawPath(path("M3,9h4l5,-4v14l-5,-4H3z"), mFill);
                    mStroke.setStrokeWidth(1.7f);
                    canvas.drawPath(path("M15.5,9a4,4 0 0,1 0,6"), mStroke);
                    canvas.drawPath(path("M18.5,6.5a7.5,7.5 0 0,1 0,11"), mStroke);
                    break;
                default:
                    break;
            }
            canvas.restore();
        }

        @Override public void setAlpha(int alpha) {
            mStroke.setAlpha(alpha);
            mFill.setAlpha(alpha);
        }

        @Override public void setColorFilter(ColorFilter cf) {
            mStroke.setColorFilter(cf);
            mFill.setColorFilter(cf);
        }

        @Override public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }
    }
}
