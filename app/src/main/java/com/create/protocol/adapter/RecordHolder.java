package com.create.protocol.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.create.protocol.MyApplication;
import com.create.protocol.R;

/**
 * Created by jsntnjzb on 2017/2/17.
 */
public class RecordHolder extends BaseHolder<String> {

    private TextView tvProjectCode;

    public RecordHolder() {
    }

    @Override
    protected View initView() {
        View view = LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.item_fragment, null);
        tvProjectCode = view.findViewById(R.id.tv_project_code);
        return view;
    }

    @Override
    public void bindData(String s) {
        tvProjectCode.setText("项目文号：" + s);
    }
}
