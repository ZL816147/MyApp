package com.create.protocol.adapter;

import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * 基类Adapter
 *
 * @param
 * @author kaige.shen
 */
public abstract class BasicAdapter<T> extends BaseAdapter {
    protected List<T> mData;
    protected String TAG = getClass().getSimpleName();

    public BasicAdapter(List<T> list) {
        this.mData = list;
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        if (observer != null) {
            super.unregisterDataSetObserver(observer);
        }
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        if (mData == null) {
            return 0;
        }
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return getData(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BaseHolder holder;
        if (convertView == null) {
            holder = getHolder(position);
        } else {
            holder = (BaseHolder) convertView.getTag();
        }
        holder.bindData(mData.get(position));
        return holder.getHolderView(position);
    }

    public void setData(List<T> data, boolean notifyDataSetChanged) {
        mData = data;
        if (notifyDataSetChanged) {
            notifyDataSetChanged();
        }
    }

    public Object getData(int position) {
        if (mData != null && position >= 0 && position < mData.size()) {
            return mData.get(position);
        }
        return null;
    }

    protected abstract BaseHolder<T> getHolder(int position);

}
