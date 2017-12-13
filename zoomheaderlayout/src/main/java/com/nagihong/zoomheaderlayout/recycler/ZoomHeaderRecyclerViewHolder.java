package com.nagihong.zoomheaderlayout.recycler;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by channagihong on 9/25/17
 */

public abstract class ZoomHeaderRecyclerViewHolder<V extends View> extends RecyclerView.ViewHolder {

    protected int itemType;
    protected V view;

    public ZoomHeaderRecyclerViewHolder(View itemView) {
        super(itemView);
    }

    public ZoomHeaderRecyclerViewHolder(int itemType, V view) {
        super(view);
        this.itemType = itemType;
        this.view = view;
    }

    public abstract void bind(int position);

}
