package com.nagihong.zoomheaderlayout.refresh;

import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.nagihong.zoomheaderlayout.listeners.OnAnimationEndListener;

/**
 * Created by channagihong on 25/09/2017.
 */

public class RefreshBounceRunnable implements Runnable {

    private final boolean LOG = false;
    private final String Tag = RefreshBounceRunnable.class.getSimpleName();

    protected long duration;
    protected long starTime;
    protected View target;
    protected int fromY;
    protected int toY;
    protected boolean finished;
    protected OnAnimationEndListener listener;

    public long getDuration() {
        return duration;
    }

    public RefreshBounceRunnable setDuration(long duration) {
        this.duration = duration;
        return this;
    }

    public View getTarget() {
        return target;
    }

    public RefreshBounceRunnable setTarget(View target) {
        this.target = target;
        return this;
    }

    public int getFromY() {
        return fromY;
    }

    public RefreshBounceRunnable setFromY(int fromY) {
        this.fromY = fromY;
        return this;
    }

    public int getToY() {
        return toY;
    }

    public RefreshBounceRunnable setToY(int toY) {
        this.toY = toY;
        return this;
    }

    public boolean isFinished() {
        return finished;
    }

    public RefreshBounceRunnable setListener(OnAnimationEndListener listener) {
        this.listener = listener;
        return this;
    }

    public void startAnimation() {
        if (null == target) return;
        if (duration < 0) duration = 1;
        if (fromY == toY) return;

        if (LOG) {
            Log.d(Tag, String.format("startAnimation() fromY: %d, toY: %d, duration: %d", fromY, toY, duration));
        }

        starTime = SystemClock.currentThreadTimeMillis();
        finished = false;
        target.post(this);
    }

    public void abortAnimation() {
        if (LOG) {
            Log.d(getClass().getName(), "abortAnimation()");
        }
        finished = true;
    }

    @Override
    public void run() {
        if (LOG) {
            Log.d(getClass().getName(), String.format("run() finished: %b", finished));
        }
        if (null == target) return;
        if (finished) {
            if (LOG) {
                Log.d(getClass().getName(), "canceled()");
            }
            return;
        }

        float progress = (SystemClock.currentThreadTimeMillis() - starTime) / (float) duration;
        progress = frictionProgress(progress);
        if (LOG) {
            Log.d(getClass().getName(), String.format("run() progress: %f, finished: %b", progress, finished));
        }
        if (progress >= 1) {
            if (LOG) {
                Log.d(getClass().getName(), "end()");
            }
            finished = true;
            if (null != listener) {
                listener.onAnimationEnd();
            }
            return;
        }
        float newDistance = (toY - fromY) * progress;
        float newY = fromY + newDistance;
        if (LOG) {
            Log.d(getClass().getName(), String.format("run() newY: %f, finished: %b", newY, finished));
        }
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) target.getLayoutParams();
        params.topMargin = (int) newY;
        target.setLayoutParams(params);
        target.post(this);
    }

    private float frictionProgress(float progress) {
        progress -= 1.0f;
        return 1.0f + progress * progress * progress * progress * progress;
    }

}
