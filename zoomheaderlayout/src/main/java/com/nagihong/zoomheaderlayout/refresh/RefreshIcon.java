package com.nagihong.zoomheaderlayout.refresh;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Point;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.nagihong.zoomheaderlayout.R;
import com.nagihong.zoomheaderlayout.utils.ZoomHeaderUtils;


/**
 * Created by channagihong on 9/25/17
 */

public class RefreshIcon extends FrameLayout implements IRefreshIcon {

    private final boolean LOG = true;

    protected final float START_REFRESH_BOUNCE_BACK = 0.5f;
    public final static int DURATION_FINISH_REFRESH_ANIMATION = 350;

    private ImageView outside;
    private ImageView inside;
    /**
     * 控制imageview内容旋转的matrix
     */
    private Matrix outsideMatrix;
    private Matrix insideMatrix;
    /**
     * 由于在ZoomHeaderLayout scroll过程中，会手动设置rotation，而不是使用animation
     * 所以这里会记录当前imageview中旋转的角度
     */
    private int outsideRotation = 0;
    private int insideRotation = 0;
    /**
     * 获取imageview的center x\y
     */
    private Point outsideCenterPoint;
    private Point insideCenterPoint;
    /**
     * refresh icon内容旋转animation
     */
    private RotateAnimation spinOuterAnimation, spinInnerAnimation;
    /**
     * refresh icon bounce animation
     * 通过runnable实现,
     * 主要是因为如果使用animation系统实现，一次动画后，再次下拉刷新，refresh icon不能再通过改变其topMargins实现下拉
     * 简单来说就是一次animation后，再次下拉refresh icon拉不出来
     */
    private RefreshBounceRunnable startToRefreshBounceBackRunnable;
    private RefreshBounceRunnable finishRefreshBounceBackRunnable;

    //============================ constructors and init ================================================
    public RefreshIcon(@NonNull Context context) {
        super(context);
        init();
    }

    public RefreshIcon(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RefreshIcon(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RefreshIcon(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {

        outside = new ImageView(getContext());
        outside.setImageResource(R.mipmap.refresh_icon_outside);
        outside.setScaleType(ImageView.ScaleType.MATRIX);
        outsideMatrix = new Matrix();
        outside.setImageMatrix(outsideMatrix);
        addView(outside);
        LayoutParams outsideParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        outsideParams.gravity = Gravity.CENTER;
        outside.setLayoutParams(outsideParams);

        inside = new ImageView(getContext());
        inside.setImageResource(R.mipmap.refresh_icon_inside);
        inside.setScaleType(ImageView.ScaleType.MATRIX);
        insideMatrix = new Matrix();
        inside.setImageMatrix(insideMatrix);
        addView(inside);
        LayoutParams insideParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        insideParams.gravity = Gravity.CENTER;
        inside.setLayoutParams(insideParams);

        setBackgroundResource(R.drawable.circle_white);
    }

    //============================ view ================================================
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(getSize(), widthMode);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(getSize(), heightMode);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    //============================ 基础业务方法 ================================================

    /**
     * 获取refresh icon大小
     */
    public int getSize() {
        return (int) ZoomHeaderUtils.dp2px(getContext(), 25);
    }

    /**
     * 通过progress计算出旋转角度
     *
     * @param progress rang from 0 to 1
     */
    private int calcRotation(float progress) {
        return (int) (progress * 360);
    }

    private Point getOutsideCenterPoint() {
        if (null == outsideCenterPoint) outsideCenterPoint = new Point();
        outsideCenterPoint.set(outside.getWidth() / 2, outside.getHeight() / 2);
        return outsideCenterPoint;
    }

    private Point getInsideCenterPoint() {
        if (null == insideCenterPoint) insideCenterPoint = new Point();
        insideCenterPoint.set(inside.getWidth() / 2, inside.getHeight() / 2);
        return insideCenterPoint;
    }

    //============================ animation related ================================================

    /**
     * 1500 milliseconds rotate 720 degree clockwise
     *
     * @return
     */
    private RotateAnimation spinOuterAnimation() {
        if (null == spinOuterAnimation) {
            getOutsideCenterPoint();
            spinOuterAnimation = new RotateAnimation(0, 720, outsideCenterPoint.x, outsideCenterPoint.y);
            spinOuterAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
//            spinOuterAnimation().setInterpolator(new LinearInterpolator());
            spinOuterAnimation.setRepeatCount(Animation.INFINITE);
            spinOuterAnimation.setDuration(1500);
        }
        return spinOuterAnimation;
    }

    /**
     * 1500 milliseconds rotate 720 degree anti-clockwise
     */
    private RotateAnimation spinInnerAnimation() {
        if (null == spinInnerAnimation) {
            getInsideCenterPoint();
            spinInnerAnimation = new RotateAnimation(720, 0, insideCenterPoint.x, insideCenterPoint.y);
            spinInnerAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
//            spinOuterAnimation().setInterpolator(new LinearInterpolator());
            spinInnerAnimation.setRepeatCount(Animation.INFINITE);
            spinInnerAnimation.setDuration(1500);
        }
        return spinInnerAnimation;
    }

    /**
     * 下拉refresh icon至最低点，放手，开始刷新
     * 此时refresh icon会先往上移动一点，然后停留进入刷新状态
     */
    private RefreshBounceRunnable startToRefreshBounceBack() {
        if (null == startToRefreshBounceBackRunnable) {
            startToRefreshBounceBackRunnable = new RefreshBounceRunnable().setTarget(this);
        }
        return startToRefreshBounceBackRunnable;
    }

    /**
     * 刷新完成后，停止refresh icon刷新动画
     * 然后refresh icon往上移动，直至退出界面
     */
    private RefreshBounceRunnable finishRefreshBounceBack() {
        if (null == finishRefreshBounceBackRunnable) {
            finishRefreshBounceBackRunnable = new RefreshBounceRunnable().setTarget(this);
        }
        return finishRefreshBounceBackRunnable;
    }

    //========================= IRefreshIcon ========================================================
    @Override
    public void reset() {
        ZoomHeaderUtils.updateLayoutParamsTopMargin(this, -getSize());
    }

    /**
     * 目前主要用于ZoomHeaderLayout在scroll下拉refresh icon的过程中，
     * refresh icon需要根据progress设置旋转角度
     * 从而达到一种下拉过程中，refresh icon有跟着手势滑动而旋转的视觉效果
     *
     * @param progress range from 0 to 1
     */
    @Override
    public void setRotation(float progress) {
        if (LOG) {
//            Log.d(getClass().getGroupName(), String.format("setRotation(%f)", progress));
        }
        int rotation = calcRotation(progress);
        int changedRotation = rotation - outsideRotation;
        if (changedRotation == 0) {
            return;
        }
        if (LOG) {
//            Log.d(getClass().getGroupName(), String.format("rotation: %d, changedRotation: %d", rotation, changedRotation));
        }
        outsideRotation = rotation;
        insideRotation = -rotation;

        getOutsideCenterPoint();
        outsideMatrix.postRotate(changedRotation, outsideCenterPoint.x, outsideCenterPoint.y);
        outside.setImageMatrix(outsideMatrix);

        getInsideCenterPoint();
        insideMatrix.postRotate(-changedRotation, insideCenterPoint.x, insideCenterPoint.y);
        inside.setImageMatrix(insideMatrix);
    }

    /**
     * 准备进入刷新状态动画
     * refresh icon先往上移动一点距离，然后到达目的位置后
     * refresh icon停留在该位置，icon内容开始旋转
     */
    @Override
    public void startToRefreshAnimation() {
        if (LOG) {
            Log.d(getClass().getName(), "startToRefreshAnimation()");
        }
        final int fromY = ZoomHeaderUtils.getTopMargin(this);
        final int toY = (int) (fromY * START_REFRESH_BOUNCE_BACK);
        startToRefreshBounceBack().setFromY(fromY).setToY(toY).setDuration(100)
                .setListener(this::refreshingAnimation)
                .startAnimation();
    }

    /**
     * refresh icon内容开始自动重复旋转
     */
    @Override
    public void refreshingAnimation() {
        if (LOG) {
            Log.d(getClass().getName(), "refreshingAnimation()");
        }
        outside.startAnimation(spinOuterAnimation());
        inside.startAnimation(spinInnerAnimation());
    }

    /**
     * ZoomHeaderLayout结束刷新时调用
     * refresh icon内容停止自转
     * 然后往上移动直至离开界面
     */
    @Override
    public void finishRefreshAnimation() {
        spinOuterAnimation().cancel();
        spinInnerAnimation().cancel();
        outside.clearAnimation();
        inside.clearAnimation();
        if (null != startToRefreshBounceBackRunnable) {
            startToRefreshBounceBackRunnable.abortAnimation();
        }

        final int fromY = ZoomHeaderUtils.getTopMargin(this);
        final int toY = getHeight() * -2;
        if (LOG) {
            Log.d(getClass().getName(), String.format("finishRefreshAnimation() fromY: %d, toY: %d", fromY, toY));
        }
        finishRefreshBounceBack().setFromY(fromY).setToY(toY).setDuration(DURATION_FINISH_REFRESH_ANIMATION).startAnimation();
    }

    /**
     * 中断所有动画
     * 主要是动画执行过程中，用户手动再对ZoomHeaderLayout进行滑动，
     * 此时需要停止之前的执行动画，并且refresh icon会依据用户滑动进行位移以及{@link #setRotation(float)}
     */
    @Override
    public void abortAnimations() {
        spinOuterAnimation().cancel();
        spinInnerAnimation().cancel();
        outside.clearAnimation();
        inside.clearAnimation();
        startToRefreshBounceBack().abortAnimation();
        finishRefreshBounceBack().abortAnimation();
    }

    /**
     * refresh icon位移动画是否已经执行完成
     */
    @Override
    public boolean isAnimationsFinished() {
        boolean startFinished = startToRefreshBounceBack().isFinished();
        boolean stopFinished = finishRefreshBounceBack().isFinished();
        if (LOG) {
            Log.d(getClass().getName(), String.format("isAnimationsFinished() return %b, %b", startFinished, stopFinished));
        }
        return startFinished && stopFinished;
    }
}
