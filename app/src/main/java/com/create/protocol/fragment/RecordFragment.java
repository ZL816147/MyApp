package com.create.protocol.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.create.protocol.DetailActivity;
import com.create.protocol.ItemActivity;
import com.create.protocol.MainActivity;
import com.create.protocol.R;
import com.create.protocol.adapter.RecordAdapter;
import com.create.protocol.base.BaseFragment;
import com.create.protocol.model.Info;
import com.create.protocol.utils.LogUtils;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by jsntnjzb on 2018/1/30.
 */

public class RecordFragment extends BaseFragment implements AdapterView.OnItemClickListener {
    Unbinder unbinder;
    @BindView(R.id.listview)
    ListView listview;
    private List<Info> infoList;
    private List<String> projectCodes;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_record;
    }

    @Override
    protected void init() {
        infoList = DataSupport.findAll(Info.class);
        Collections.reverse(infoList);
        projectCodes = new ArrayList<>();
        for (int i = 0; i < infoList.size(); i++) {
            String projectCode = infoList.get(i).getProjectCode();
            if (!projectCodes.contains(projectCode)) {
                projectCodes.add(projectCode);
            }
        }
        LogUtils.e(projectCodes.size() + "++++++++++++++++++++");
        RecordAdapter recordAdapter = new RecordAdapter(projectCodes);
        listview.setAdapter(recordAdapter);
        listview.setOnItemClickListener(this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(), ItemActivity.class);
        intent.putExtra("projectCode", projectCodes.get(position));
        startActivity(intent);
        MainActivity activity = (MainActivity) getActivity();
        activity.inTransitionAnimation();
    }
}
