package com.nagihong.zoomheaderlayout;

import android.os.SystemClock;
import android.util.Log;
import android.view.View;

import com.nagihong.zoomheaderlayout.utils.ZoomHeaderUtils;

/**
 * Created by channagihong on 25/09/2017.
 */

public class ZoomBounceRunnable implements Runnable {

    private final boolean LOG = false;
    protected long duration;
    protected boolean finished;
    protected float fromScale;
    protected long startTime;
    protected View[] targets;
    protected int expectHeight;

    public void startAnimation(long duration, float fromScale, int expectHeight, View... targets) {
        if (null == targets || 0 == targets.length) return;
        this.duration = duration;
        this.fromScale = fromScale;
        this.targets = targets;
        this.expectHeight = expectHeight;

        startTime = SystemClock.currentThreadTimeMillis();
        finished = false;
        if (null != targets[0]) {
            targets[0].post(this);
        }
    }

    public void abortAnimation() {
        finished = true;
    }

    @Override
    public void run() {
        if (LOG) {
            Log.d(getClass().getName(), String.format("run(target, %b, %f, %d)", finished, fromScale, expectHeight));
        }
        if (null == targets || 0 == targets.length) return;
        if (finished) return;
        if (fromScale <= 1.0f) return;

        float progress = (SystemClock.currentThreadTimeMillis() - startTime) / (float) duration;
        float consumedScale = (fromScale - 1.0f) * frictionProgress(progress);
        float newScale = fromScale - consumedScale;
        if (LOG) {
            Log.d(getClass().getName(), String.format("run() currentScale: %f, consumedScale: %f, newScale: %f", fromScale, consumedScale, newScale));
        }
        if (newScale <= 1.0f) {
            finished = true;
            return;
        }

        for (View target : targets) {
            ZoomHeaderUtils.updateLayoutParamsHeight(target, (int) (newScale * expectHeight));
        }
        targets[0].post(this);
    }

    public boolean isFinished() {
        return finished;
    }

    private float frictionProgress(float progress) {
        progress -= 1.0f;
        return 1.0f + progress * progress * progress * progress * progress;
    }

}
