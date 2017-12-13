package com.nagihong.zoomheaderlayout;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.nagihong.zoomheaderlayout.recycler.ZoomHeaderRecyclerView;
import com.nagihong.zoomheaderlayout.refresh.IRefreshIcon;

/**
 * Created by channagihong on 9/26/17
 */

public class ZoomHeaderContainer extends FrameLayout {

    private final boolean LOG = false;
    private ZoomHeaderLayoutBase layout;
    private int minHeight;

    public ZoomHeaderContainer(@NonNull Context context) {
        super(context);
    }

    public ZoomHeaderContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ZoomHeaderContainer(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ZoomHeaderContainer(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public ZoomHeaderContainer setLayout(ZoomHeaderLayoutBase layout) {
        this.layout = layout;
        return this;
    }

    /**
     * 当ZoomHeaderContainer用在RecyclerView上时，headerContainer有可能高度会被设置成0
     * 所以这里强制改动一下onMeasure方法的heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (height < minHeight) {
            int mode = MeasureSpec.getMode(heightMeasureSpec);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(minHeight, mode);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 主要是有些情况下，refresh icon处于正在刷新状态时，
     * 用户向上滑动ZoomHeaderLayout，refresh icon及header view都移出到界面外部(甚至有可能被回收了)
     * 然后用户在刷新结束之前滑动回到ZoomHeaderLayout顶部
     * 此时refresh icon的动效会消失了
     * 这里需要检查一下，如果还是处于正在刷新状态，则refresh icon继续执行刷新(自转)动效
     */
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (null == layout) return;
        IRefreshIcon refreshIcon = findViewById(ZoomHeaderRecyclerView.ID_REFRESHICON);
        if (null == refreshIcon) return;

        if (layout.isRefreshing()) {
            //restore animation if needed
            refreshIcon.refreshingAnimation();
        } else {
            refreshIcon.reset();
        }
    }

    public ZoomHeaderContainer setMinHeight(int minHeight) {
        this.minHeight = minHeight;
        return this;
    }
}
