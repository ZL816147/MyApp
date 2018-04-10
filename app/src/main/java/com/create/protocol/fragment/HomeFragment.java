package com.create.protocol.fragment;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.create.interfaces.LocationService;
import com.create.interfaces.OnLocationResultListener;
import com.create.protocol.EnterActivity;
import com.create.protocol.MainActivity;
import com.create.protocol.MyApplication;
import com.create.protocol.R;
import com.create.protocol.adapter.MainAdapter;
import com.create.protocol.adapter.WeatherAdapter;
import com.create.protocol.base.BaseFragment;
import com.create.protocol.constact.Constant;
import com.create.protocol.model.Weather;
import com.create.protocol.utils.BitmapUtils;
import com.create.protocol.utils.LogUtils;
import com.create.protocol.widget.CustomListView;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by jsntnjzb on 2018/1/30.
 */

public class HomeFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, OnLocationResultListener {
    @BindView(R.id.tv_enter)
    Button tvEnter;
    Unbinder unbinder;
    @BindView(R.id.tv_error_information)
    TextView mTvErrorInformation;
    @BindView(R.id.llayout_error)
    LinearLayout mLlayoutError;
    @BindView(R.id.recycler_view)
    EasyRecyclerView recyclerView;
    private WeatherAdapter weatherAdapter;
    private TextView mTvAddress;
    private TextView mTvTemperature;
    private TextView mTvTemperatureDetail;
    private TextView mTvWind;
    private TextView mTvWeather;
    private TextView mTvTemperaturePeople;
    private ImageView mImgWeatherBg;
    private View mHeaderView;
    private View mFootView;
    private MainAdapter mMainAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void init() {
        setOnLocationResultListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        weatherAdapter = new WeatherAdapter(getContext());
        dealWithAdapter(weatherAdapter);
        recyclerView.setRefreshListener(this);
    }

    private void initHeaderView(View view) {
        mTvAddress = view.findViewById(R.id.tv_address);
        mTvTemperature = view.findViewById(R.id.tv_temperature);
        mTvTemperatureDetail = view.findViewById(R.id.tv_temperature_detail);
        mTvWind = view.findViewById(R.id.tv_wind);
        mTvWeather = view.findViewById(R.id.tv_weather);
        mTvTemperaturePeople = view.findViewById(R.id.tv_temperature_people);
        mImgWeatherBg = view.findViewById(R.id.img_weather_bg);
        mImgWeatherBg.setImageBitmap(BitmapUtils.fastblur(getContext(), BitmapFactory.decodeResource(getContext().getResources(), R.drawable.main_bg), 1f, 15f));
    }

    private void dealWithAdapter(final RecyclerArrayAdapter<Weather.DataBean.ResultWeatherDataList> adapter) {
        recyclerView.setAdapterWithProgress(adapter);
        adapter.setError(R.layout.error_layout);
        mHeaderView = LayoutInflater.from(getContext()).inflate(R.layout.view_header, null);
        mHeaderView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        initHeaderView(mHeaderView);
        adapter.addHeader(new RecyclerArrayAdapter.ItemView() {
            @Override
            public View onCreateView(ViewGroup parent) {
                return mHeaderView;
            }

            @Override
            public void onBindView(View headerView) {

            }
        });

        mFootView = LayoutInflater.from(getContext()).inflate(R.layout.view_foot, null);
        mFootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        CustomListView listView = mFootView.findViewById(R.id.list_index);
        listView.setFocusable(false);
        mMainAdapter = new MainAdapter(getContext());
        listView.setAdapter(mMainAdapter);
        adapter.addFooter(new RecyclerArrayAdapter.ItemView() {
            @Override
            public View onCreateView(ViewGroup parent) {
                return mFootView;
            }

            @Override
            public void onBindView(View headerView) {

            }
        });
    }

    private void doWeather(final String mLocation) {
        MyApplication.getRetrofit(Constant.WEATHER_BASE_URL)
                .create(LocationService.class)
                .getWeather(mLocation, "json", Constant.BAIDU_WEATHER_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Weather>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        recyclerView.setVisibility(View.GONE);
                        mLlayoutError.setVisibility(View.VISIBLE);
                        mTvErrorInformation.setText(R.string.error);
                    }

                    @Override
                    public void onNext(Weather weather) {
                        if (weather.getError().equals("0") && weather.getStatus().equals("success")) {
                            recyclerView.setVisibility(View.VISIBLE);
                            mLlayoutError.setVisibility(View.GONE);
                            List<Weather.DataBean.ResultIndexList> listindex = weather.getResults().get(0).getIndex();
                            List<Weather.DataBean.ResultWeatherDataList> listweather = weather.getResults().get(0).getWeather_data();
                            String date = listweather.get(0).getDate().replaceAll(" ", "");// "周六",
                            String mWeather = listweather.get(0).getWeather();//  "晴转阴",
                            String wind = listweather.get(0).getWind();// ": "微风",
                            String[] aa = date.split("：");
                            mTvAddress.setText(mLocation);
                            mTvTemperature.setText(aa[1].replace("℃)", "") + "°");
                            mTvTemperatureDetail.setText(weather.getDate() + " " + aa[0].replace("(实时", "").substring(0, 2)
                                    + "  pm2.5 " + weather.getResults().get(0).getPm25());
                            mTvWind.setText(wind);
                            mTvWeather.setText(mWeather);
                            mTvTemperaturePeople.setText(aa[1].replace("℃)", "") + "°");
                            weatherAdapter.addAll(listweather);
                            mMainAdapter.replace(listindex);
                        } else {
                            recyclerView.setVisibility(View.GONE);
                            mLlayoutError.setVisibility(View.VISIBLE);
                            mTvErrorInformation.setText(R.string.error);
                        }
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.tv_enter, R.id.llayout_error})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_enter:
                startActivity(new Intent(getActivity(), EnterActivity.class));
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.inTransitionAnimation();
                break;
            case R.id.llayout_error:
                recyclerView.setVisibility(View.VISIBLE);
                mLlayoutError.setVisibility(View.GONE);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        location();
                    }
                }, 1000);
                break;
            default:
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                location();
            }
        }, 1000);
    }

    @Override
    public void onRefresh() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                weatherAdapter.clear();
                doWeather(mLocation);
            }
        }, 1000);
    }

    private String mLocation = "";

    @Override
    public void onLocationSuccess(String address) {
        weatherAdapter.clear();
        mLocation = address;
        doWeather(mLocation);
    }

    @Override
    public void onError() {
        recyclerView.setVisibility(View.GONE);
        mLlayoutError.setVisibility(View.VISIBLE);
        mTvErrorInformation.setText(R.string.location_error);
    }
}