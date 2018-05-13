package com.create.protocol.base;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.create.protocol.MyApplication;
import com.create.protocol.R;
import com.create.protocol.utils.PdfBackground;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.umeng.analytics.MobclickAgent;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import butterknife.ButterKnife;

import static com.create.protocol.utils.PdfBackground.BORDER_WIDTH;

/**
 * Activity基类
 * Created by xiarh on 2017/5/8.
 */

public abstract class BaseActivity extends AppCompatActivity {

    private Uri imageUri;
    protected File myDir;

    protected abstract int getLayout();

    protected abstract void init(Bundle savedInstanceState);

    // 图片存储
    private static final String imagePath = Environment.getExternalStorageDirectory().toString();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        setContentView(getLayout());
        ButterKnife.bind(this);
        myDir = new File(imagePath + "/protocol/");
        myDir.mkdirs();

        init(savedInstanceState);
        if (MyApplication.getInstance() != null) {
            MyApplication.getInstance().addActivity(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (MyApplication.getInstance() != null) {
            MyApplication.getInstance().removeActivity(this);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // 回退
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void inTransitionAnimation() {
        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
    }

    protected void outTransitionAnimation() {
        overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
    }

    public Bitmap decodeBitmap(String string) {
        //将字符串转换成Bitmap类型
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(string, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public Bitmap decode(byte[] bitmapArray) {
        //将字符串转换成Bitmap类型
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public String encodeBitmap(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String s = new String(Base64.encodeToString(byteArray, Base64.DEFAULT));
//        bitmap.recycle();
        return s;
    }

    public byte[] encode(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return byteArray;
    }

    /**
     * view 转化为Bitmap
     *
     * @param v
     * @return
     */
    public Bitmap loadBitmapFromView(View v) {
        int w = v.getWidth();
        int h = v.getHeight();
        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);
        c.drawColor(Color.WHITE);
        /** 如果不设置canvas画布为白色，则生成透明 */
        v.layout(0, 0, w, h);
        v.draw(c);
        return bmp;
    }

    /**
     * 根据图片生成PDF
     *
     * @param pdfPath       生成的PDF文件的路径
     * @param imagePathList 待生成PDF文件的图片集合
     * @throws IOException       可能出现的IO操作异常
     * @throws DocumentException PDF生成异常
     */
    public void createPdf(String pdfPath, List<String> imagePathList) throws IOException, DocumentException {
        Document document = new Document();
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfPath));

        //设置pdf背景
        PdfBackground event = new PdfBackground();
        writer.setPageEvent(event);

        document.open();
        for (int i = 0; i < imagePathList.size(); i++) {
            document.newPage();
            Image img = Image.getInstance(imagePathList.get(i));
            //设置图片缩放到A4纸的大小
            img.scaleToFit(PageSize.A4.getWidth() - BORDER_WIDTH * 2, PageSize.A4.getHeight() - BORDER_WIDTH * 2);
            //设置图片的显示位置（居中）
            img.setAbsolutePosition((PageSize.A4.getWidth() - img.getScaledWidth()) / 2, (PageSize.A4.getHeight() - img.getScaledHeight()) / 2);
            document.add(img);
        }
        document.close();
    }

    public void openAssignFolder(File file) {
        if (null == file || !file.exists()) {
            return;
        }
        // 获取父目录
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri;
        if (Build.VERSION.SDK_INT >= 24) {
            uri = FileProvider.getUriForFile(this, "com.create.protocol", file);
        } else {
            uri = Uri.fromFile(file);
        }
        intent.setDataAndType(uri, "application/pdf");
        startActivity(intent);

//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.addCategory(Intent.CATEGORY_DEFAULT);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        if (Build.VERSION.SDK_INT >= 24) {
//            imageUri = FileProvider.getUriForFile(this, "com.create.protocol", file);
//        } else {
//            imageUri = Uri.fromFile(file);
//        }
//        intent.setDataAndType(imageUri, "file/*");
//        try {
//            startActivity(intent);
//            startActivity(Intent.createChooser(intent, "选择浏览工具"));
//        } catch (ActivityNotFoundException e) {
//            e.printStackTrace();
//        }
    }

    public void sharePDF(File file) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.addCategory("android.intent.category.DEFAULT");
        Uri pdfUri;
        if (Build.VERSION.SDK_INT >= 24) {
            pdfUri = FileProvider.getUriForFile(this, "com.create.protocol", file);
        } else {
            pdfUri = Uri.fromFile(file);
        }
        intent.putExtra(Intent.EXTRA_STREAM, pdfUri);
        intent.setType("application/pdf");

        try {
            startActivity(Intent.createChooser(intent, file.getName()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}