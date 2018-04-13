package com.create.protocol;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.create.protocol.base.BaseActivity;
import com.create.protocol.model.Info;
import com.itextpdf.text.DocumentException;
import com.joanzapata.pdfview.PDFView;
import com.joanzapata.pdfview.listener.OnDrawListener;
import com.joanzapata.pdfview.listener.OnLoadCompleteListener;
import com.joanzapata.pdfview.listener.OnPageChangeListener;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by jsntnjzb on 2018/1/30.
 */

public class ProtocolActivity extends BaseActivity implements OnLoadCompleteListener, OnDrawListener, OnPageChangeListener {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.btn_save)
    ImageView btnSave;
    @BindView(R.id.pdf_view)
    PDFView pdfView;
    @BindView(R.id.im_view)
    ImageView imView;

    @Override
    protected int getLayout() {
        return R.layout.activity_protocol;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        // 从assets目录读取pdf
//        displayFromAssets("pdfxieyi.pdf");
        // 从文件中读取pdf
        // displayFromFile( new file( "filename"));
        int id = getIntent().getIntExtra("id", 0);
        Info info = DataSupport.find(Info.class, id);
        if (info != null) {
            if (info != null && info.getProtocolBitmap() != null && !TextUtils.isEmpty(info.getProtocolBitmap())) {
                imView.setImageBitmap(decodeBitmap(info.getProtocolBitmap()));
            }
        }
    }


    private void displayFromAssets(String assetFileName) {
        pdfView.fromAsset(assetFileName) //设置pdf文件地址
                .defaultPage(1)  //设置默认显示第1页
                .onPageChange(this) //设置翻页监听
                .onLoad(this)  //设置加载监听
                .onDraw(this)  //绘图监听
                .showMinimap(true) //pdf放大的时候，是否在屏幕的右上角生成小地图
                .swipeVertical(true) //pdf文档翻页是否是垂直翻页，默认是左右滑动翻页
                .enableSwipe(true) //是否允许翻页，默认是允许翻页
//   .pages() //把 5 过滤掉
                .load();

    }


    private void displayFromFile(File file) {
        pdfView.fromFile(file) //设置pdf文件地址
                .defaultPage(1)  //设置默认显示第1页
                .onPageChange(this) //设置翻页监听
                .onLoad(this)  //设置加载监听
                .onDraw(this)  //绘图监听
                .showMinimap(false) //pdf放大的时候，是否在屏幕的右上角生成小地图
                .swipeVertical(true) //pdf文档翻页是否是垂直翻页，默认是左右滑动翻页
                .enableSwipe(true) //是否允许翻页，默认是允许翻
                // .pages( 2 ，5 ) //把2 5 过滤掉
                .load();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    // 图片存储
    private static final String imagePath = Environment.getExternalStorageDirectory() + "/protocol/";

    @OnClick({R.id.iv_back, R.id.btn_save})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                outTransitionAnimation();
                break;
            case R.id.btn_save:
                SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
                Date curDate = new Date(System.currentTimeMillis());
                String str = formatter.format(curDate);
                // 保存成图片
                Bitmap bitmap = loadBitmapFromView(imView);
                String fileName = str + "_" + "政策处理明细单" + ".jpg";
                File file = new File(imagePath, fileName);
                try {
                    if (file.exists()) {
                        file.delete();
                    }
                    file.createNewFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    FileOutputStream fos = new FileOutputStream(file);
                    if (bitmap != null) {
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    }
                    fos.flush();
                    fos.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String pdfFile = imagePath + str + "_" + "政策处理协议" + ".pdf";
                List imgList = new ArrayList<String>();
                imgList.add(imagePath + fileName);
                try {
                    createPdf(pdfFile, imgList);
//                    file.delete();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
                openAssignFolder(file);
//                sharePDF(file);
                Toast.makeText(this, "保存成功", Toast.LENGTH_LONG).show();
//                ShareBoardConfig config = new ShareBoardConfig();
//                config.setMenuItemBackgroundShape(ShareBoardConfig.BG_SHAPE_CIRCULAR);// 圆角背景
//                config.setIndicatorVisibility(false); // 取消指示器
//                config.setCancelButtonVisibility(false);
//                new ShareAction(this)
//                        .setDisplayList(SHARE_MEDIA.QQ, SHARE_MEDIA.WEIXIN)
//                        .withText("")
//                        .withMedia(new UMImage(this, bitmap))
//                        .setCallback(umShareListener).open(config);
                break;
        }
    }


//    private UMShareListener umShareListener = new UMShareListener() {
//        @Override
//        public void onStart(SHARE_MEDIA share_media) {
//
//        }
//
//        @Override
//        public void onResult(SHARE_MEDIA platform) {
//            if (platform.name().equals("WEIXIN_FAVORITE")) {
////                toast(getString(R.string.toast_collection_success));
//            } else {
////                toast(getString(R.string.toast_share_success));
//            }
//        }
//
//        @Override
//        public void onError(SHARE_MEDIA platform, Throwable t) {
////            toast(getString(R.string.toast_share_fail));
//            if (t != null) {
//                LogUtils.d("throw:" + t.getMessage());
//            }
//        }
//
//        @Override
//        public void onCancel(SHARE_MEDIA platform) {
////            toast(getString(R.string.toast_share_cancel));
//        }
//    };

    @Override
    public void onLayerDrawn(Canvas canvas, float pageWidth, float pageHeight, int displayedPage) {

    }

    @Override
    public void loadComplete(int nbPages) {
        Toast.makeText(this, "加载完成" + nbPages, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPageChanged(int page, int pageCount) {
    }
}