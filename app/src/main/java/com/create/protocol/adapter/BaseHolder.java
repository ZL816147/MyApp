package com.create.protocol.adapter;


import android.view.View;

/**
 * 基类Holder.
 */

public abstract class BaseHolder<T> {
    protected View holderView;

    protected BaseHolder() {
        holderView = initView();
        holderView.setTag(this);
    }

    protected abstract View initView();

    public abstract void bindData(T data);

    public View getHolderView(int position) {
        return holderView;
    }
}

