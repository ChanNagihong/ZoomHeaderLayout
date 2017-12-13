package com.nagihong.zoomheader;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nagihong.zoomheaderlayout.recycler.ZoomHeaderRecyclerViewHolder;

/**
 * Created by channagihong on 12/12/2017.
 */

public class RecyclerHolder extends ZoomHeaderRecyclerViewHolder<LinearLayout> {

    public RecyclerHolder(View itemView) {
        super(itemView);
    }

    public RecyclerHolder(int itemType, LinearLayout view) {
        super(itemType, view);
    }

    @Override
    public void bind(int position) {
        ((TextView) view.findViewById(R.id.item)).setText(String.format("position: %d", position));
    }
}
