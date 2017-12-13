package com.nagihong.zoomheaderlayout;

import android.content.res.TypedArray;
import android.view.View;

/**
 * Created by channagihong on 24/09/2017.
 */

public interface IZoomHeaderLayout<T extends View> {

    void handleHeaderStyleAttributes(TypedArray a);

    //============================ 基础get view ================================================
    View getHeaderContainer();

    View getZoomHeaderView();

    View getFixedHeaderView();

    T getContentView();

    //============================ 参数化 ================================================

    /**
     * 整个Header
     */
    boolean isHeaderEnabled();

    boolean isHeaderScrollParallaxEnabled();

    /**
     * 可放大部分Header view
     */
    boolean isZoomable();

    boolean isZoomingHeader();

    /**
     * refresh icon部分
     */
    boolean isDraggingRefreshIcon();

    boolean isRefreshable();

    boolean isRefreshing();

}
