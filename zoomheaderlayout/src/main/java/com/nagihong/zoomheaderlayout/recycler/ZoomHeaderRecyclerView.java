package com.nagihong.zoomheaderlayout.recycler;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.nagihong.zoomheaderlayout.ZoomHeaderLayout;
import com.nagihong.zoomheaderlayout.utils.ZoomHeaderUtils;

/**
 * Created by channagihong on 25/09/2017.
 */

public class ZoomHeaderRecyclerView extends ZoomHeaderLayout<RecyclerView> {

    public ZoomHeaderRecyclerView(Context context) {
        super(context);
        init();
    }

    public ZoomHeaderRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ZoomHeaderRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public ZoomHeaderRecyclerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    @Override
    protected RecyclerView onCreateContentView() {
        return new RecyclerView(getContext());
    }

    private void init() {
        if (null != contentView) {
            contentView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    ZoomHeaderRecyclerView.this.onScrolled();
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
        return contentView.computeVerticalScrollOffset() <= 0;
    }

    //========================= recycler view ========================================================
    public void setAdapter(ZoomHeaderRecyclerAdapter adapter) {
        if (null == contentView) return;
        contentView.setAdapter(adapter);
        /**
         * 用于recycler view没有header，所以需要往adapter中植入代码进行处理
         * 因此也多了几个文件
         * {@link ZoomHeaderRecyclerAdapter}
         * {@link ZoomHeaderRecyclerViewHolder}
         * {@link ZoomHeaderRecyclerHeaderHolder}
         */
        adapter.setHeaderHolder(new ZoomHeaderRecyclerHeaderHolder(ZoomHeaderRecyclerAdapter.TYPE_HEADER, headerContainer));
        adapter.setZoomHeaderRecyclerView(this);
        adapter.setContext(getContext());
    }

    public void setLayoutManager(RecyclerView.LayoutManager manager) {
        if (null == contentView) return;
        contentView.setLayoutManager(manager);
    }

    public void addItemDecoration(RecyclerView.ItemDecoration decoration) {
        if (null == contentView) return;
        contentView.addItemDecoration(decoration);
    }

    public void addItemDecoration(RecyclerView.ItemDecoration decoration, int index) {
        if (null == contentView) return;
        contentView.addItemDecoration(decoration, index);
    }

    public RecyclerView getRecyclerView() {
        return contentView;
    }

    //========================= getters and setters ========================================================
    public int getHeaderDefaultHeight() {
        return (int) ZoomHeaderUtils.dp2px(getContext(), HEADER_DEFAULT_SIZE);
    }
}
