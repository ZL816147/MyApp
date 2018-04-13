package com.create.protocol.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.create.protocol.EnterActivity;
import com.create.protocol.MainActivity;
import com.create.protocol.MyApplication;
import com.create.protocol.R;
import com.venusic.handwrite.view.HandWriteView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by jsntnjzb on 2017/7/14.
 */

public class TakePhotoPopupWindow extends PopupWindow {
    private final TextView tvTakePhoto;
    private final TextView tvSelectPick;
    private final TextView tvCancel;
    private final HandWriteView sfvSign;
    private final View confirmTv;
    private final View cancelTv;
    private final LinearLayout llImage;
    private final LinearLayout llSign;
    private final String signPath;
    private Bitmap mBitmap;

    private View view;
    private Canvas mCanvas;
    private Path mPath = new Path();
    private float mX;
    private float mY;
    private Paint mPaint = new Paint();


    public TakePhotoPopupWindow(Context mContext, View.OnClickListener itemsOnClick, int type, final OnSignListener signListener, final String signPath) {
        this.signListener = signListener;
        this.signPath = signPath;
        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        view = LayoutInflater.from(mContext).inflate(R.layout.take_photo_pop, null);

        llImage = view.findViewById(R.id.ll_image);
        tvTakePhoto = view.findViewById(R.id.tv_take_photo);
        tvSelectPick = view.findViewById(R.id.tv_select_photo);
        tvCancel = view.findViewById(R.id.tv_cancel);

        llSign = view.findViewById(R.id.ll_sign);
        confirmTv = view.findViewById(R.id.confirm_tv);
        sfvSign = view.findViewById(R.id.sfv_sign);
        cancelTv = view.findViewById(R.id.cancel_tv);

        if (type == 1) {
            llSign.setVisibility(View.VISIBLE);
            llImage.setVisibility(View.GONE);
        } else if (type == 2) {
            llSign.setVisibility(View.GONE);
            llImage.setVisibility(View.VISIBLE);
        }
        // 取消按钮
        tvCancel.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // 销毁弹出框
                dismiss();
            }
        });    // 取消按钮
        cancelTv.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // 销毁弹出框
                dismiss();
            }
        });
        confirmTv.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (sfvSign.isSign()) {
                    try {
                        /**
                         * path     路径
                         * clearBlank 是否清除边缘空白
                         * blank边缘  保留多少像素空白
                         * isEncrypt 是否加密存储 如果加密存储会自动在路径后面追加后缀.sign
                         */
                        sfvSign.save(signPath, true, 10, false);
                        signListener.onSignSuccess(true);
                        dismiss();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(MyApplication.getContext(), "您没有签名~", Toast.LENGTH_SHORT).show();
                }
            }
        });
        // 设置按钮监听
        tvTakePhoto.setOnClickListener(itemsOnClick);
        tvSelectPick.setOnClickListener(itemsOnClick);
        // 设置外部可点击
        this.setOutsideTouchable(true);
        // mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        view.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                int height = view.findViewById(R.id.pop_layout).getTop();

                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });


    /* 设置弹出窗口特征 */
        // 设置视图
        setContentView(this.view);
        // 设置弹出窗体的宽和高
        setHeight(RelativeLayout.LayoutParams.WRAP_CONTENT);
        setWidth(RelativeLayout.LayoutParams.MATCH_PARENT);

        // 设置弹出窗体可点击
        setFocusable(true);

        // 设置弹出窗体显示时的动画，从底部向上弹出
        setAnimationStyle(R.style.pop_anim);
        // 实例化一个ColorDrawable颜色为半透明
        setBackgroundDrawable(new ColorDrawable(0x00000000));

    }


    private OnSignListener signListener;

    public void setOnSignListener(OnSignListener signListener) {
        this.signListener = signListener;
    }

    public interface OnSignListener {
        /**
         * 绘制完成
         */
        void onSignSuccess(boolean signed);
    }

}
