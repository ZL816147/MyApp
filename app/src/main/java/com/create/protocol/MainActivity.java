package com.create.protocol;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.create.protocol.base.BaseActivity;
import com.create.protocol.fragment.HomeFragment;
import com.create.protocol.fragment.RecordFragment;
import com.create.protocol.utils.BottomNavigationViewHelper;
import com.create.protocol.widget.FragmentTabHost;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements TabHost.OnTabChangeListener {

    @BindView(android.R.id.tabcontent)
    FrameLayout tabcontent;
    @BindView(android.R.id.tabhost)
    FragmentTabHost mTabHost;
    @BindView(R.id.content)
    FrameLayout mContent;
    public static final String TAB_RECOMMENT = "recomment", TAB_FINANACING = "financing";
    // 定义数组来存放Fragment界面
    private Class<?> fragmentArray[] = {HomeFragment.class, RecordFragment.class};
    // 定义数组来存放按钮图片
    private int mImageViewArray[] = {R.drawable.selector_tab_discover, R.drawable.selector_tab_list};
    // 定义Tab选项卡标记
    private String mTabTags[] = {TAB_RECOMMENT, TAB_FINANACING};
    // Tab选项卡的文字
    private int mTextviewArray[] = {R.string.home_tab_recomment, R.string.home_tab_notice};
    private double lasttime;
    private LayoutInflater layoutInflater;

    @Override
    protected int getLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        layoutInflater = LayoutInflater.from(getApplicationContext());
        initBottomNavigation();
        getPermission();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getPermission();
    }

    /**
     * 初始化底部导航
     */
    private void initBottomNavigation() {
        mTabHost.setup(getApplicationContext(), getSupportFragmentManager(), R.id.content);
        mTabHost.getTabWidget().setDividerDrawable(android.R.color.white);
        mTabHost.setOnTabChangedListener(this);
        // 得到fragment的个数
        int count = fragmentArray.length;
        for (int i = 0; i < count; i++) {
            // 为每一个Tab按钮设置图标、文字和内容
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(mTabTags[i]).setIndicator(getTabItemView(i));
            // 将Tab按钮添加进Tab选项卡中
            mTabHost.addTab(tabSpec, fragmentArray[i], null);
            // 设置Tab按钮的背景
            // mTabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.selector_tab_background);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                long nowtime = System.currentTimeMillis();
                if ((nowtime - lasttime) < 2000) {
                    finish();
                } else {
                    lasttime = nowtime;
                    Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                    return true;
                }
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 获取权限
     */
    private void getPermission() {
        AndPermission
                .with(this)
                .permission(
                        Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
                        Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.WRITE_SETTINGS,
                        Manifest.permission.BLUETOOTH_ADMIN,
                        Manifest.permission.BLUETOOTH,
                        Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
//                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.ACCESS_WIFI_STATE,
                        Manifest.permission.CHANGE_WIFI_STATE)
                .requestCode(200)
                .callback(mPermissionListener)
                .start();
    }

    private PermissionListener mPermissionListener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, List<String> grantedPermissions) {
            if (requestCode == 200) {
            }
        }

        @Override
        public void onFailed(int requestCode, List<String> deniedPermissions) {
            if (requestCode == 200) {

            }
        }
    };


    @Override
    public void onTabChanged(String tabId) {

    }

    /**
     * 给Tab按钮设置图标和文字
     */
    public View getTabItemView(int index) {
        View view = layoutInflater.inflate(R.layout.tab_item_view, null);
        ImageView imageView = view.findViewById(R.id.imageview);
        imageView.setImageResource(mImageViewArray[index]);
        TextView textView = view.findViewById(R.id.textview);
        textView.setText(mTextviewArray[index]);
        return view;
    }
}
