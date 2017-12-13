package com.nagihong.zoomheaderlayout.utils;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by channagihong on 25/09/2017.
 */

public class ZoomHeaderUtils {

    public static void updateLayoutParamsHeight(View view, int height) {
        if (null == view) return;
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (null == params) return;
        if(params.height == height) return;

        params.height = height;
        view.setLayoutParams(params);
    }

    public static void updateLayoutParamsTopMargin(View view, int top) {
        if (null == view) return;
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
        if (null == params) return;
        if(params.topMargin == top) return;

        params.topMargin = top;
        view.setLayoutParams(params);
    }

    public static int getTopMargin(View view) {
        if (null == view) return 0;
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
        if (null == params) return 0;

        return params.topMargin;
    }

    public static float dp2px(Context context, float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dp, context.getResources().getDisplayMetrics());
    }
}
