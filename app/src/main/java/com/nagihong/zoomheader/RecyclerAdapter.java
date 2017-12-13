package com.nagihong.zoomheader;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.nagihong.zoomheaderlayout.recycler.ZoomHeaderRecyclerAdapter;
import com.nagihong.zoomheaderlayout.recycler.ZoomHeaderRecyclerViewHolder;

/**
 * Created by channagihong on 12/12/2017.
 */

public class RecyclerAdapter extends ZoomHeaderRecyclerAdapter {

    @Override
    public ZoomHeaderRecyclerViewHolder onCreateContentViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new RecyclerHolder(viewType, (LinearLayout) view);
    }

    @Override
    public void onBind(ZoomHeaderRecyclerViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int onGetItemViewType(int position) {
        return TYPE_CONTENT;
    }

    @Override
    public int onGetItemCount() {
        return 100;
    }
}
