package com.create.protocol.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.create.protocol.DetailActivity;
import com.create.protocol.MainActivity;
import com.create.protocol.R;
import com.create.protocol.adapter.MyAdapter;
import com.create.protocol.base.BaseFragment;
import com.create.protocol.model.Info;
import com.create.protocol.widget.ClearEditText;

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

public class RecordFragment extends BaseFragment implements MyAdapter.OnShowItemClickListener {
    Unbinder unbinder;
    @BindView(R.id.main_listview)
    ListView mainListview;
    @BindView(R.id.operate_back)
    TextView operateBack;
    @BindView(R.id.operate_select)
    TextView operateSelect;
    @BindView(R.id.invert_select)
    TextView invertSelect;
    @BindView(R.id.operate_delete)
    TextView operateDelete;
    @BindView(R.id.lay)
    LinearLayout lay;
    @BindView(R.id.filter_edit)
    ClearEditText filterEdit;
    private ArrayList<Info> dataList;
    private ArrayList<Info> selectList;
    private MyAdapter adapter;
    private boolean isShow;
    private List<Info> infoList;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_record;
    }

    @Override
    protected void init() {
        infoList = DataSupport.findAll(Info.class);
        Collections.reverse(infoList);
        dataList = new ArrayList<>();
        selectList = new ArrayList<>();
        dataList.addAll(infoList);
        filterEdit.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterData(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        adapter = new MyAdapter(dataList, getActivity());
        mainListview.setAdapter(adapter);
        adapter.setOnShowItemClickListener(this);
        mainListview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (isShow) {
                    return false;
                } else {
                    isShow = true;
                    for (Info bean : dataList) {
                        bean.setShow(true);
                    }
                    adapter.notifyDataSetChanged();
                    showOpervate();
                    mainListview.setLongClickable(false);
                }
                return true;
            }
        });
        mainListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Info info = dataList.get(position);
                if (isShow) {
                    boolean isChecked = info.isChecked();
                    if (isChecked) {
                        info.setChecked(false);
                    } else {
                        info.setChecked(true);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                    intent.putExtra("id", info.getId());
                    intent.putExtra("projectCode", info.getProjectCode());
                    startActivity(intent);
                    MainActivity mainActivity = (MainActivity) getActivity();
                    mainActivity.inTransitionAnimation();
                }
            }
        });
    }

    /**
     * @param filterStr
     */
    private void filterData(String filterStr) {
        List<Info> filterDateList = new ArrayList<>();

        if (TextUtils.isEmpty(filterStr)) {
            filterDateList = dataList;
        } else {
            filterDateList.clear();
            for (Info sortModel : dataList) {
                String name = sortModel.getStatus();
                if (name.indexOf(filterStr.toString()) != -1) {
                    filterDateList.add(sortModel);
                }
            }
        }
        adapter.updateListView(filterDateList);
    }

    /**
     * 显示操作界面
     */
    private void showOpervate() {
        lay.setVisibility(View.VISIBLE);
        Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.operate_in);
        lay.setAnimation(anim);
        operateBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isShow) {
                    selectList.clear();
                    for (Info bean : dataList) {
                        bean.setChecked(false);
                        bean.setShow(false);
                    }
                    adapter.notifyDataSetChanged();
                    isShow = false;
                    mainListview.setLongClickable(true);
                    dismissOperate();
                }
            }
        });
        operateSelect.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                for (Info bean : dataList) {
                    if (!bean.isChecked()) {
                        bean.setChecked(true);
                        if (!selectList.contains(bean)) {
                            selectList.add(bean);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
        invertSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Info bean : dataList) {
                    if (!bean.isChecked()) {
                        bean.setChecked(true);
                        if (!selectList.contains(bean)) {
                            selectList.add(bean);
                        }
                    } else {
                        bean.setChecked(false);
                        if (!selectList.contains(bean)) {
                            selectList.remove(bean);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
        operateDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (selectList != null && selectList.size() > 0) {
                    mPopupWindow = new TipsPopupWindow(getActivity());
                    showPopupWindow();
                } else {
                    Toast.makeText(getActivity(), "请选择条目", Toast.LENGTH_SHORT).show();
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    private TipsPopupWindow mPopupWindow;
    private Window window;
    private WindowManager.LayoutParams params;

    public class TipsPopupWindow extends PopupWindow {
        private final TextView tvTitle, tvContent, tvConfirm, tvCancel;
        private View view;

        @SuppressLint("WrongConstant")
        public TipsPopupWindow(final Context mContext) {
            view = LayoutInflater.from(mContext).inflate(R.layout.gesture_dialog_layout, null);
            tvConfirm = view.findViewById(R.id.tv_gesture_confirm);
            tvCancel = view.findViewById(R.id.tv_gesture_ignore);

            tvTitle = view.findViewById(R.id.tv_title);
            tvContent = view.findViewById(R.id.tv_content);
            tvCancel.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    dismiss();
                }
            });

            tvConfirm.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    for (int i = 0; i < selectList.size(); i++) {
                        DataSupport.delete(Info.class, selectList.get(i).getId());
                    }
                    dataList.removeAll(selectList);
                    adapter.notifyDataSetChanged();
                    selectList.clear();
                    adapter.notifyDataSetChanged();
                    dismiss();
                }
            });
            setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
            setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            setOutsideTouchable(false);
            setContentView(this.view);
            setHeight(RelativeLayout.LayoutParams.MATCH_PARENT);
            setWidth(RelativeLayout.LayoutParams.MATCH_PARENT);
            setFocusable(true);
            setBackgroundDrawable(new ColorDrawable(0x10000000));
        }
    }

    private void showPopupWindow() {
        // 显示窗口,设置layout在PopupWindow中显示的位置
        mPopupWindow.showAtLocation(mainListview, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        window = getActivity().getWindow();
        params = window.getAttributes();
        // 当弹出Popupwindow时，背景变半透明
        params.alpha = 0.7f;
        // 此行代码主要是解决在华为手机上半透明效果无效的bug
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setAttributes(params);
        // 设置Popupwindow关闭监听，当Popupwindow关闭，背景恢复1f
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                params = window.getAttributes();
                params.alpha = 1f;
                // 不移除该Flag的话,在有视频的页面上的视频或者页面切换时会出现黑屏的bug
                window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                window.setAttributes(params);
            }
        });
    }

    /**
     * 隐藏操作界面
     */
    private void dismissOperate() {
        Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.operate_out);
        lay.setVisibility(View.GONE);
        lay.setAnimation(anim);
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
    public void onShowItemClick(Info bean) {
        if (bean.isChecked() && !selectList.contains(bean)) {
            selectList.add(bean);
        } else if (!bean.isChecked() && selectList.contains(bean)) {
            selectList.remove(bean);
        }
    }

}
