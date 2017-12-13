package com.nagihong.zoomheaderlayout;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.nagihong.zoomheaderlayout.R;
import com.nagihong.zoomheaderlayout.listeners.OnRefreshListener;
import com.nagihong.zoomheaderlayout.refresh.RefreshIcon;
import com.nagihong.zoomheaderlayout.utils.ZoomHeaderUtils;


/**
 * Created by channagihong on 24/09/2017.
 */

public abstract class ZoomHeaderLayoutBase<T extends View> extends LinearLayout implements IZoomHeaderLayout<T> {

    protected boolean LOG = true;

    //============================ UI ================================================
    protected ZoomHeaderContainer headerContainer;
    protected View zoomHeaderView;
    protected View fixedHeaderView;
    protected T contentView;
    protected int headerContainerHeight;
    protected RefreshIcon refreshIcon;
    public static int ID_REFRESHICON = 34670;

    //============================ 参数化 ================================================
    /**
     * ZoomHeaderLayout总开关
     * 如果set false，则所有下拉动效全部没有
     */
    protected boolean enabled = true;
    /**
     * scroll过程中，header出现与消失的动效
     * 动效已看不出效果
     */
    protected boolean scrollParallaxEnabled = false;
    /**
     * 是否支持header view下拉放大
     */
    protected boolean zoomable = true;
    /**
     * 是否支持header view refresh功能
     */
    protected boolean refreshable = true;
    /**
     * scroll friction
     */
    protected final float DRAG_FRICTION = 2.0f;
    /**
     * header view下拉放大，最大时高度 = headerview正常高度 * MAX_ZOOM_FACTOR
     */
    protected final float MAX_ZOOM_FACTOR = 1.7f;
    /**
     * refresh icon下拉后，触发刷新的最小高度 = headerview正常高度 * TRIGGER_REFRESH_FACTOR
     */
    protected final float TRIGGER_REFRESH_FACTOR = 0.3f;
    /**
     * refresh icon下拉后，最大高度 = headerview正常高度 * MAX_REFRESH_ICON_FACTOR
     */
    protected final float MAX_REFRESH_ICON_FACTOR = 0.45f;
    /**
     * refresh icon left margin
     */
    protected final int REFRESH_ICON_X = 30;
    /**
     * header container default size
     */
    protected final int HEADER_DEFAULT_SIZE = 180;

    //============================ motion related ================================================
    /**
     * 触发ACTION_MOVE动作的最小距离，主要为了避免一些视觉上不会察觉的回调触发无谓的UI刷新
     */
    protected int touchSlop;
    /**
     * header view 是否正在被拉动
     */
    protected boolean isBeingDragged;
    /**
     * header view 是否正在放大
     */
    protected boolean isZoomingHeader = false;
    /**
     * refresh icon 是否正在被拉动
     */
    protected boolean isDraggingRefreshIcon = false;
    /**
     * 上一次 ACTION_MOVE 时数据
     */
    protected float lastMotionX;
    protected float lastMotionY;
    /**
     * 上一次 ACTION_DOWN 时数据
     */
    protected float lastDownMotionX;
    protected float lastDownMotionY;

    //============================ refresh related ================================================
    /**
     * ZoomHeaderLayout是否处于 正在刷新 状态
     */
    protected boolean isRefreshing = false;
    /**
     * header view 下来放大后下一个 ACTION_UP 时，将header view以动效缩小至正常高度的Runnable
     */
    protected ZoomBounceRunnable bounceRunnable;

    //============================ callback ================================================
    protected OnRefreshListener onRefreshListener;

    //============================ constructor ================================================
    public ZoomHeaderLayoutBase(Context context) {
        super(context);
        init(null);
    }

    public ZoomHeaderLayoutBase(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ZoomHeaderLayoutBase(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ZoomHeaderLayoutBase(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    //============================ init ================================================
    public void init(AttributeSet attrs) {
        setOrientation(VERTICAL);

        initVars();

        if (null == attrs) return;
        initHeaderContainer();

        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        @SuppressLint("CustomViewStyleable")
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ZoomHeaderLayout);

        initZoomHeader(typedArray, inflater, localDisplayMetrics);
        initFixedHeader(typedArray, inflater, localDisplayMetrics);
        initRefreshIcon();
        initContentView();

        scrollParallaxEnabled = typedArray.getBoolean(R.styleable.ZoomHeaderLayout_zhl_parallax, false);
        handleHeaderStyleAttributes(typedArray);
        typedArray.recycle();
    }

    private void initVars() {
        ViewConfiguration config = ViewConfiguration.get(getContext());
        touchSlop = config.getScaledTouchSlop();

        bounceRunnable = new ZoomBounceRunnable();
    }

    private void initHeaderContainer() {
        headerContainer = new ZoomHeaderContainer(getContext());
        ViewGroup.LayoutParams headerParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) ZoomHeaderUtils.dp2px(getContext(), HEADER_DEFAULT_SIZE));
        headerContainer.setMinHeight((int) ZoomHeaderUtils.dp2px(getContext(), HEADER_DEFAULT_SIZE));
        headerContainer.setLayoutParams(headerParams);
        headerContainer.setLayout(this);
    }

    private void initZoomHeader(TypedArray typedArray, LayoutInflater inflater, DisplayMetrics localDisplayMetrics) {
        int zoomHeaderViewResId = typedArray.getResourceId(R.styleable.ZoomHeaderLayout_zhl_zoomHeaderView, 0);
        if (zoomHeaderViewResId > 0) {
            zoomHeaderView = inflater.inflate(zoomHeaderViewResId, null, false);
        }
        if (null != zoomHeaderView) {
            headerContainer.addView(zoomHeaderView);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(localDisplayMetrics.widthPixels, ViewGroup.LayoutParams.MATCH_PARENT);
            zoomHeaderView.setLayoutParams(params);
        }
    }

    private void initFixedHeader(TypedArray typedArray, LayoutInflater inflater, DisplayMetrics localDisplayMetrics) {
        int fixedHeaderViewResId = typedArray.getResourceId(R.styleable.ZoomHeaderLayout_zhl_fixedHeaderView, 0);
        if (fixedHeaderViewResId > 0) {
            fixedHeaderView = inflater.inflate(fixedHeaderViewResId, null, false);
        }
        if (null != fixedHeaderView) {
            headerContainer.addView(fixedHeaderView);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(localDisplayMetrics.widthPixels, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.BOTTOM;
            fixedHeaderView.setLayoutParams(params);
        }
    }

    private void initRefreshIcon() {
        refreshIcon = new RefreshIcon(getContext());
        //设置id方便findViewById
        ID_REFRESHICON = View.generateViewId();
        refreshIcon.setId(ID_REFRESHICON);
        headerContainer.addView(refreshIcon);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(refreshIcon.getSize(), refreshIcon.getSize());
        params.leftMargin = REFRESH_ICON_X;
        params.topMargin = -1 * 200;
        refreshIcon.setLayoutParams(params);
    }

    private void initContentView() {
//        int contentViewResId = typedArray.getResourceId(R.styleable.ZoomHeaderLayout_zhl_contentView, 0);
//        if (contentViewResId > 0) {
//            //noinspection unchecked
//            contentView = (T) inflater.inflate(contentViewResId, null, false);
//        }
        contentView = onCreateContentView();
        if (null != contentView) {
            addView(contentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        } else {
            Log.d(getClass().getName(), "contentView is null");
        }
    }

    //========================= view ========================================================
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        try {
            super.onLayout(changed, l, t, r, b);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            //#2684 java.lang.IndexOutOfBoundsException
        }
        if (null != headerContainer && 0 == headerContainerHeight) {
            headerContainerHeight = headerContainer.getHeight();
        }
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        return super.onGenericMotionEvent(event);
    }

    //============================ 参数化 ================================================
    public void setHeaderEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    //============================ IZoomHeaderLayout ================================================
    @Override
    public void handleHeaderStyleAttributes(TypedArray a) {

    }

    @Override
    public View getHeaderContainer() {
        return headerContainer;
    }

    @Override
    public View getZoomHeaderView() {
        return zoomHeaderView;
    }

    @Override
    public View getFixedHeaderView() {
        return fixedHeaderView;
    }

    @Override
    public T getContentView() {
        return contentView;
    }

    @Override
    public boolean isHeaderEnabled() {
        return enabled;
    }

    @Override
    public boolean isHeaderScrollParallaxEnabled() {
        return scrollParallaxEnabled;
    }

    @Override
    public boolean isZoomingHeader() {
        return isZoomingHeader;
    }

    @Override
    public boolean isZoomable() {
        return zoomable;
    }

    @Override
    public boolean isDraggingRefreshIcon() {
        return isDraggingRefreshIcon;
    }

    @Override
    public boolean isRefreshable() {
        return refreshable;
    }

    @Override
    public boolean isRefreshing() {
        return isRefreshing;
    }

    //========================= UI业务方法 ========================================================
    protected abstract T onCreateContentView();

    //============================ callback ================================================
    public OnRefreshListener getOnRefreshListener() {
        return onRefreshListener;
    }

    public ZoomHeaderLayoutBase setOnRefreshListener(OnRefreshListener onRefreshListener) {
        this.onRefreshListener = onRefreshListener;
        return this;
    }

    //============================ 业务基础方法 ================================================
    //检查下拉header view过程中，header view的高度是否超过限制的最大高度
    protected int limitHeaderHeight(int height) {
        float maxHeight = headerContainerHeight * MAX_ZOOM_FACTOR;
        if (height > maxHeight) {
            height = (int) maxHeight;
        }
        return height;
    }

    //检查下拉refresh icon过程中，refresh icon的位置是否炒股限制的位置
    protected int limitRefreshIconY(int y) {
        float maxY = headerContainerHeight * MAX_REFRESH_ICON_FACTOR;
        if (y > maxY) {
            y = (int) maxY;
        }
        return y;
    }

    //开始刷新
    protected void startRefresh() {
        if (isRefreshing) return;
        if (null == refreshIcon) return;
        isRefreshing = true;

        refreshIcon.startToRefreshAnimation();
        if (null != onRefreshListener) {
            onRefreshListener.onRefreshStart(this);
        }
    }

    //完成刷新
    public void finishRefresh() {
        //收回动画完成后，才能开始下一次下拉
        refreshIcon.postDelayed(() -> isRefreshing = false, RefreshIcon.DURATION_FINISH_REFRESH_ANIMATION);
        if (null == refreshIcon) return;
        refreshIcon.finishRefreshAnimation();
    }

}
