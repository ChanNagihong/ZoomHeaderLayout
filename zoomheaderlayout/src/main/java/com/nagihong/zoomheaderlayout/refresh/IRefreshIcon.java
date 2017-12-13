package com.nagihong.zoomheaderlayout.refresh;

/**
 * Created by channagihong on 9/25/17
 */

public interface IRefreshIcon {

    void reset();

    void setRotation(float progress);

    void startToRefreshAnimation();

    void refreshingAnimation();

    void finishRefreshAnimation();

    void abortAnimations();

    boolean isAnimationsFinished();

}
