# ZoomHeaderLayout  

![](http://orqt8ndmu.bkt.clouddn.com/device-2017-12-16-211315.gif)  
## Support ListView and RecyclerView  

```Gradle  
compile 'com.nagihong:zoomheaderlayout:1.0.1'
```  

### Usage for ListView  
#### ListActivity  
```Java
public class ListActivity extends Activity {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_list);

    ZoomHeaderListView listView = findViewById(R.id.activity_list);
    listView.setAdapter(new ListAdapter());
    listView.setOnRefreshListener(view -> view.postDelayed(() -> listView.finishRefresh(), 3000));
}
```  
#### R.layout.activity_list  
```xml
<?xml version="1.0" encoding="utf-8"?>
<com.nagihong.zoomheaderlayout.listview.ZoomHeaderListView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/activity_list"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:zhl_fixedHeaderView="@layout/zoomheader_fixedview"
    app:zhl_zoomHeaderView="@layout/zoomheader_zoomview" />
```  

### Usage for RecyclerView  
#### RecyclerActivity  
```Java  
public class RecyclerActivity extends Activity {

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
```  
#### R.layout.activity_recycler  
```xml  
<?xml version="1.0" encoding="utf-8"?>
<com.nagihong.zoomheaderlayout.recycler.ZoomHeaderRecyclerView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/activity_recycler"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:zhl_fixedHeaderView="@layout/zoomheader_fixedview"
    app:zhl_zoomHeaderView="@layout/zoomheader_zoomview" />
```  
### #RecyclerAdapter  
```Java  
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
```




