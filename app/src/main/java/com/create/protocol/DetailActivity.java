package com.create.protocol;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.create.protocol.adapter.RecordAdapter;
import com.create.protocol.base.BaseActivity;
import com.create.protocol.model.Info;
import com.create.protocol.utils.LogUtils;
import com.create.protocol.widget.CustomListView;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

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

public class DetailActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.btn_save)
    ImageView btnSave;
    @BindView(R.id.tv_id)
    TextView tvId;
    @BindView(R.id.tv_power_name)
    TextView tvPowerName;
    @BindView(R.id.tv_project_name)
    TextView tvProjectName;
    @BindView(R.id.tv_involved_sign)
    TextView tvInvolvedSign;
    @BindView(R.id.tv_marketing_no)
    TextView tvMarketingNo;
    @BindView(R.id.tv_money_total)
    TextView tvMoneyTotal;
    @BindView(R.id.tv_total)
    TextView tvTotal;
    @BindView(R.id.tv_bank_number)
    TextView tvBankNumber;
    @BindView(R.id.tv_open_bank)
    TextView tvOpenBank;
    @BindView(R.id.lv_records)
    CustomListView lvRecords;
    @BindView(R.id.tv_responsible_sign)
    EditText tvResponsibleSign;
    @BindView(R.id.tv_power_people)
    TextView tvPowerPeople;
    @BindView(R.id.tv_electrofarming_company)
    TextView tvElectrofarmingCompany;
    @BindView(R.id.ll_view)
    LinearLayout llView;

    // 图片存储
    private static final String imagePath = Environment.getExternalStorageDirectory() + "/protocol/";
    private Uri imageUri;
    private RecordAdapter adapter;

    @Override
    protected int getLayout() {
        return R.layout.activity_detail;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        int id = getIntent().getIntExtra("id", 0);
        String projectCode = getIntent().getStringExtra("projectCode");
        Info info = DataSupport.find(Info.class, id);
        LogUtils.e(projectCode + "++++++++++++++++++++++++++++++++");
        List<Info> list = DataSupport.where("projectCode = ?", projectCode).find(Info.class);

        if (info != null) {
            tvId.setText(info.getId() + "");
            tvPowerName.setText(info.getPower());
            tvProjectName.setText(info.getProjectName());
            tvInvolvedSign.setText(info.getInvolvedPeople());
            tvMarketingNo.setText(info.getMarketingNo());
            tvMoneyTotal.setText(info.getDescribe());
            tvBankNumber.setText(info.getBankCard());
            tvOpenBank.setText(info.getOpenBank());

            if (info.getInvolvedPeople().length() > 10) {
                SpannableString spannable = getSpannableString(info.getInvolvedPeople());
                tvInvolvedSign.setText(spannable);
            } else {
                tvInvolvedSign.setText(info.getInvolvedPeople());
            }
            tvInvolvedSign.setFocusable(false);
            if (info.getResponsiblePeople().length() > 10) {
                SpannableString spannable = getSpannableString(info.getResponsiblePeople());
                tvResponsibleSign.setText(spannable);
            } else {
                tvResponsibleSign.setText(info.getResponsiblePeople());
            }
            tvResponsibleSign.setFocusable(false);


//            tvPowerPeople.setText("供电所负责人：" + info.getPowerPeople());
        }
        double total = 0;
        for (int i = 0; i < list.size(); i++) {
            total += Double.parseDouble(list.get(i).getDescribe());
        }
        tvTotal.setText("汇总金额(元)：" + String.format("%.2f", total));
        tvTotal.setFocusable(false);
        adapter = new RecordAdapter(list);
        lvRecords.setAdapter(adapter);
    }

    private SpannableString getSpannableString(String s) {
        Bitmap bitmap = decodeBitmap(s);
        SpannableString spannable = new SpannableString(1 + "");
        Drawable drawable = new BitmapDrawable(bitmap);//加载应用程序中图片
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight()); //设置宽'高
        // 若该TextView 上既有文本又有图片设置图片与文本底部对齐
        ImageSpan tv_span = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
        spannable.setSpan(tv_span, 0, spannable.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        return spannable;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

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
                Bitmap bitmap = loadBitmapFromView(llView);
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
                String pdfFile = imagePath + str + "_" + "政策处理明细单" + ".pdf";
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

                Toast.makeText(this, "保存成功", Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }
    }
}