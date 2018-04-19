package com.create.protocol.adapter;

import java.util.List;

/**
 * Created by jsntnjzb on 2017/2/17.
 */

public class RecordAdapter extends BasicAdapter {


    public RecordAdapter(List<String> mList) {
        super(mList);
    }

    @Override
    protected BaseHolder getHolder(int position) {
        return new RecordHolder();
    }
}
