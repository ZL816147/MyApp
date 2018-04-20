package com.create.protocol;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.umeng.commonsdk.UMConfigure;

import org.litepal.LitePal;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by xiarh on 2017/5/9.
 */

public class MyApplication extends Application {

    private static Retrofit retrofit;
    private Set<Activity> allActivities;

    public static MyApplication instance;
    private static Context context;
    private static OkHttpClient client;

    public static MyApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        context = getApplicationContext();

        UMConfigure.init(this, UMConfigure.DEVICE_TYPE_PHONE, "");
//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            return;
//        }
//        LeakCanary.install(this);

        LitePal.initialize(this);
        SQLiteDatabase db = LitePal.getDatabase();
        OCR.getInstance().initAccessTokenWithAkSk(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken result) {
                // 调用成功，返回AccessToken对象
                String token = result.getAccessToken();
            }

            @Override
            public void onError(OCRError error) {
                // 调用失败，返回OCRError子类SDKError对象
            }
        }, context, "hX2dicBCZcoqXpB4BWlgC9eR", "5ccgl5eeKYl0W75OrCdAQlNKUsmg3Ki9");
        OCR.getInstance().initAccessToken(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken result) {
                // 调用成功，返回AccessToken对象
                String token = result.getAccessToken();
            }

            @Override
            public void onError(OCRError error) {
                // 调用失败，返回OCRError子类SDKError对象
            }
        }, context);
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                //打印retrofit日志
                Log.i("MyApplication", message);
            }
        });
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .connectTimeout(10000L, TimeUnit.SECONDS)
                .readTimeout(10000L, TimeUnit.SECONDS)
                .writeTimeout(10000L, TimeUnit.SECONDS)
                .build();
    }

    public static Context getContext() {
        return context;
    }

    public void addActivity(Activity act) {
        if (null == allActivities) {
            allActivities = new HashSet<>();
        }
        allActivities.add(act);
    }

    public void removeActivity(Activity act) {
        if (null != allActivities) {
            allActivities.remove(act);
        }
    }

    /**
     * 退出应用，kill进程
     */
    public void exitApp() {
        if (null != allActivities) {
            synchronized (allActivities) {
                for (Activity act : allActivities) {
                    act.finish();
                }
            }
        }
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    public static Retrofit getRetrofit(String baseUrl) {
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(MyApplication.client)
                .addConverterFactory(ScalarsConverterFactory.create())//增加返回值为String的支持
                .addConverterFactory(GsonConverterFactory.create())//默认支持Gson解析
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        return retrofit;
    }


}
