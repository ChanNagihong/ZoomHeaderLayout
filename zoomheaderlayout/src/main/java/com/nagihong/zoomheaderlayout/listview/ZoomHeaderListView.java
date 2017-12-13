package com.nagihong.zoomheaderlayout.listview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.nagihong.zoomheaderlayout.ZoomHeaderLayout;

/**
 * Created by channagihong on 10/12/2017.
 */

public class ZoomHeaderListView extends ZoomHeaderLayout<ListView> {

    public ZoomHeaderListView(Context context) {
        super(context);
        init();
    }

    public ZoomHeaderListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ZoomHeaderListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public ZoomHeaderListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    @Override
    protected ListView onCreateContentView() {
        return new ListView(getContext());
    }

    private void init() {
        if (null != contentView) {
            contentView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    ZoomHeaderListView.this.onScrolled();
                }
            });
        }
    }

    //========================= implements from parent ========================================================

    /**
     * 检测当前是否已经滑到recycler view的最顶部
     * 用于判断当前的下滑手势是否可以出发下拉header view和refresh icon
     */
    @Override
    protected boolean isReadyForPulldownHeaderView() {
        if (null == contentView) return false;
        if (contentView.getChildCount() == 0) return true;
        return contentView.getChildAt(0).getTop() == 0;
    }

    //============================ listview ================================================
    public void setAdapter(BaseAdapter adapter) {
        if (null == contentView) return;
        contentView.setAdapter(adapter);
        contentView.addHeaderView(headerContainer);
    }

}
