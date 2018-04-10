package com.create.protocol.adapter;

import com.create.protocol.model.Info;

import java.util.List;

/**
 * Created by jsntnjzb on 2017/2/17.
 */

public class RecordAdapter extends BasicAdapter {


    public RecordAdapter(List<Info> mList) {
        super(mList);
    }

    @Override
    protected BaseHolder getHolder(int position) {
        return new RecordHolder();
    }
}
