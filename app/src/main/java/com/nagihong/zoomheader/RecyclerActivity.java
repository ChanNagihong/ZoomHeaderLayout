package com.nagihong.zoomheader;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;

import com.nagihong.zoomheaderlayout.recycler.ZoomHeaderRecyclerView;

public class RecyclerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler);

        ZoomHeaderRecyclerView recyclerView = findViewById(R.id.activity_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new RecyclerAdapter());
        recyclerView.setOnRefreshListener(view -> view.postDelayed(() -> recyclerView.finishRefresh(), 3000));

    }
}
