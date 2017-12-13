package com.nagihong.zoomheaderlayout.recycler;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by channagihong on 9/25/17
 * 预设了header和footer的adapter，所以必然地，继承此adapter的class，
 * 尽量不要重写RecyclerView.Adapter的getItemCount等标准方法
 */
public abstract class ZoomHeaderRecyclerAdapter<T> extends RecyclerView.Adapter<ZoomHeaderRecyclerViewHolder> {

    public final static int TYPE_HEADER = 999;
    public final static int TYPE_CONTENT = 1;

    protected Context context;
    protected List<T> data;
    protected ZoomHeaderRecyclerViewHolder headerHolder;
    private ZoomHeaderRecyclerView zoomHeaderRecyclerView;

    //========================= RecyclerView.Adapter ========================================================
    @Override
    public int getItemCount() {
        int dataCount = onGetItemCount();
        int headerCount = null == headerHolder ? 0 : 1;
        return dataCount + headerCount;
    }

    @Override
    public int getItemViewType(int position) {
        int fixedPosition = position;
        if (null != headerHolder) {
            if (position == 0) {
                return TYPE_HEADER;
            } else {
                //调整position
                fixedPosition--;
            }
        }
        /*
            在调用onGetItemViewType的时候，传入的position最好是对调用方是无感的
            所以传入到onGetItemViewType的position最好控制范围是[0, onGetItemCount() - 1]
         */
        return onGetItemViewType(fixedPosition);
    }

    @Override
    public ZoomHeaderRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ZoomHeaderRecyclerViewHolder holder = null;
        if (viewType == TYPE_HEADER) {
            holder = getHeaderHolder();
        } else {
            holder = onCreateContentViewHolder(parent, viewType);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(ZoomHeaderRecyclerViewHolder holder, int position) {
        int fixedPosition = position;
        if (null != headerHolder) {
            if (0 == position) {
                return;
            } else {
                fixedPosition--;
            }
        }
        /*
            onBind，传入的position最好是对调用方是无感的
            所以传入到onBind的position最好控制范围是[0, onGetItemCount() - 1]
         */
        onBind(holder, fixedPosition);
    }

    //========================= 子类该继承的封装接口 ========================================================
    public abstract ZoomHeaderRecyclerViewHolder onCreateContentViewHolder(ViewGroup parent, int viewType);

    public abstract void onBind(ZoomHeaderRecyclerViewHolder holder, int position);

    public abstract int onGetItemViewType(int position);

    public abstract int onGetItemCount();

    //========================= header related ========================================================
    public ZoomHeaderRecyclerAdapter setHeaderHolder(ZoomHeaderRecyclerViewHolder headerHolder) {
        this.headerHolder = headerHolder;
        return this;
    }

    protected ZoomHeaderRecyclerViewHolder getHeaderHolder() {
        return headerHolder;
    }

    //========================= data related ========================================================
    public List<T> getData() {
        if (null == data) data = new LinkedList<>();
        return data;
    }

    public ZoomHeaderRecyclerAdapter setData(List<T> data) {
        this.data = data;
        return this;
    }

    public void removeData(T data) {
        getData().remove(data);
    }

    public void addData(List<T> data) {
        getData().addAll(data);
    }

    public T getLastData() {
        if (null == data || 0 == data.size()) return null;
        return data.get(data.size() - 1);
    }

    public void clear() {
        getData().clear();
    }

    public boolean replace(T data) {
        int index = getData().indexOf(data);
        if (index < 0) return false;
        getData().set(index, data);
        return true;
    }

    //============================ getters and setters ================================================
    public ZoomHeaderRecyclerView getZoomHeaderRecyclerView() {
        return zoomHeaderRecyclerView;
    }

    public ZoomHeaderRecyclerAdapter setZoomHeaderRecyclerView(ZoomHeaderRecyclerView zoomHeaderRecyclerView) {
        this.zoomHeaderRecyclerView = zoomHeaderRecyclerView;
        return this;
    }

    public Context getContext() {
        return context;
    }

    public ZoomHeaderRecyclerAdapter setContext(Context context) {
        this.context = context;
        return this;
    }
}
