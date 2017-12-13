package com.nagihong.zoomheader;

import android.view.View;
import android.widget.TextView;

/**
 * Created by channagihong on 12/12/2017.
 */

public class ListHolder {

    View view;

    public ListHolder(View view) {
        this.view = view;
    }

    public void bind(int position) {
        ((TextView) view.findViewById(R.id.item)).setText(String.format("position: %d", position));
    }

}
