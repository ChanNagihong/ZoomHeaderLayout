package com.nagihong.zoomheader;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.nagihong.zoomheaderlayout.listview.ZoomHeaderListView;

/**
 * Created by channagihong on 12/12/2017.
 */

public class ListActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        ZoomHeaderListView listView = findViewById(R.id.activity_list);
        listView.setAdapter(new ListAdapter());
        listView.setOnRefreshListener(view -> view.postDelayed(() -> listView.finishRefresh(), 3000));
    }
}
