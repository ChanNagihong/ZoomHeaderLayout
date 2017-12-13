package com.nagihong.zoomheaderlayout;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.nagihong.zoomheaderlayout.utils.ZoomHeaderUtils;

/**
 * Created by channagihong on 24/09/2017.
 */

public abstract class ZoomHeaderLayout<T extends View> extends ZoomHeaderLayoutBase<T> {

    public ZoomHeaderLayout(Context context) {
        super(context);
    }

    public ZoomHeaderLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ZoomHeaderLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ZoomHeaderLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    //============================ gesture ================================================
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!isHeaderEnabled()) return false;
        if (ev.getAction() == MotionEvent.ACTION_DOWN && isBeingDragged) return true;

        switch (ev.getAction()) {

            case MotionEvent.ACTION_DOWN:
                if (isReadyForPulldownHeaderView()) {
                    lastMotionX = lastDownMotionX = ev.getX();
                    lastMotionY = lastDownMotionY = ev.getY();
                    isBeingDragged = false;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (isReadyForPulldownHeaderView()) {
                    float yDiff, xDiff, absYDiff, absXDiff;

                    yDiff = ev.getY() - lastMotionY;
                    xDiff = ev.getX() - lastMotionX;
                    absYDiff = Math.abs(yDiff);
                    absXDiff = Math.abs(xDiff);


                    if (absYDiff > touchSlop && absYDiff > absXDiff) {
                        if (yDiff > 1f && isReadyForPulldownHeaderView()) {
                            lastMotionX = ev.getX();
                            lastMotionY = ev.getY();
                            isBeingDragged = true;
                        }
                    }
                }
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                isBeingDragged = false;
                break;

        }
        return isBeingDragged;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isHeaderEnabled()) return false;
        if (event.getAction() == MotionEvent.ACTION_DOWN && event.getEdgeFlags() != 0) return false;

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                if (isReadyForPulldownHeaderView()) {
                    lastMotionY = lastDownMotionY = event.getY();
                    lastMotionX = lastDownMotionX = event.getX();
                    return true;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                lastMotionX = event.getX();
                lastMotionY = event.getY();
                //如果当前处于正在刷新状态，则不改动任何UI
                if (isBeingDragged && !isRefreshing) {

                    int scrolledValue = Math.round(Math.min(lastDownMotionY - lastMotionY, 0) / DRAG_FRICTION);
                    if (isZoomable()) {
                        onHeaderViewPulldowned(scrolledValue);
                        isZoomingHeader = true;
                    }
                    if (isRefreshable()) {
                        onRefreshIconDropdowned(scrolledValue);
                        isDraggingRefreshIcon = true;
                    }
                    return true;
                }
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (isBeingDragged) {
                    isBeingDragged = false;
                    if (isZoomingHeader()) {
                        bounceHeader();
                        isZoomingHeader = false;
                    }
                    if (isDraggingRefreshIcon()) {
                        bounceRefreshIcon();
                        isDraggingRefreshIcon = false;
                    }
                    return true;
                }
                break;

        }
        return false;
    }

    //============================ start to drop down ================================================
    protected void onHeaderViewPulldowned(int scrolledValue) {
        if (null != bounceRunnable && !bounceRunnable.isFinished()) {
            bounceRunnable.abortAnimation();
        }
        int height = Math.abs(scrolledValue) + headerContainerHeight;
        height = limitHeaderHeight(height);

        if (null != headerContainer) {
            ZoomHeaderUtils.updateLayoutParamsHeight(headerContainer, height);
        }
    }

    protected void onRefreshIconDropdowned(int scrolledValue) {
        if (null == refreshIcon) return;
        if (!refreshIcon.isAnimationsFinished()) refreshIcon.abortAnimations();

        //这里要求refresh icon与header view同时到达下拉最大高度
        float maxScrolledValue = headerContainerHeight * (MAX_ZOOM_FACTOR - 1.0f);
        //progress必须从0开始，最后到1
        float progress = Math.abs(scrolledValue) / maxScrolledValue;
        if (progress >= 1) progress = 1;
        refreshIcon.setRotation(progress);

        //distance表示refresh icon从停留位置到下拉到的最大位置的距离
        float distance = headerContainerHeight * MAX_REFRESH_ICON_FACTOR + refreshIcon.getHeight();
        //refreshIcon的开始拉动位置为y=-refreshIcon.getHeight()
        int refreshIconY = (int) (distance * progress - refreshIcon.getHeight());
        refreshIconY = limitRefreshIconY(refreshIconY);

        if (null != refreshIcon) {
            ZoomHeaderUtils.updateLayoutParamsTopMargin(refreshIcon, refreshIconY);
        }
    }

    //============================ bounce back ================================================
    protected void bounceHeader() {
        float scale = headerContainer.getBottom() / (float) headerContainerHeight;
        if (null == bounceRunnable) return;
        bounceRunnable.startAnimation(200, scale, headerContainerHeight, headerContainer);
    }

    protected void bounceRefreshIcon() {
        float scrolledValue = Math.round((lastMotionY - lastDownMotionY) / DRAG_FRICTION);
        float triggerRefreshY = headerContainerHeight * TRIGGER_REFRESH_FACTOR;
        float currentRefreshIconY = scrolledValue - refreshIcon.getHeight();
        if (currentRefreshIconY >= triggerRefreshY) {
            startRefresh();
        } else {
            finishRefresh();
        }
    }

    //============================ 基础业务 ================================================
    protected void onScrolled() {
        if (null != zoomHeaderView && isHeaderEnabled() && isZoomable()) {
            if (isHeaderScrollParallaxEnabled()) {
                //paralax scrolling,滑动时headerview的视差效果
                float factor = headerContainerHeight - headerContainer.getBottom();
                if (factor > 0 && factor < headerContainerHeight) {
                    float friction = 0.65f;
                    int frictionFactor = (int) (factor * friction * -1);
                    headerContainer.scrollTo(0, frictionFactor);
                } else if (headerContainer.getScrollY() != 0) {
                    headerContainer.scrollTo(0, 0);
                }
            }
        }
    }

    //========================= abstract methods(需要被ZoomHeaderLayout调用的部分) ========================================================
    protected abstract boolean isReadyForPulldownHeaderView();
}
