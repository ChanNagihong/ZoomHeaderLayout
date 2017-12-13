package com.nagihong.zoomheader;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * Created by channagihong on 12/12/2017.
 */

public class ListAdapter extends BaseAdapter {

    @Override
    public int getCount() {
        return 100;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ListHolder holder;
        if (null != convertView) {
            holder = (ListHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
            holder = new ListHolder(convertView);

            convertView.setTag(holder);
        }
        holder.bind(position);
        return convertView;
    }
}
