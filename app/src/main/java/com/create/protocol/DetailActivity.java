package com.create.protocol;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.create.protocol.adapter.DetailAdapter;
import com.create.protocol.base.BaseActivity;
import com.create.protocol.model.Info;
import com.create.protocol.utils.LogUtils;
import com.create.protocol.utils.SaveToExcel;
import com.create.protocol.widget.CustomListView;
import com.itextpdf.text.DocumentException;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
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
    @BindView(R.id.btn_share)
    ImageView btnShare;
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
    private DetailAdapter adapter;
    private String date;
    private SaveToExcel saveToExcel;
    private List<Info> list;
    private double total;
    private String excelFileName;

    @Override
    protected int getLayout() {
        return R.layout.activity_detail;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        Date curDate = new Date(System.currentTimeMillis());
        date = formatter.format(curDate);
//        String excelFileName = date + "_" + "政策处理明细单" + ".xls";
        excelFileName = date + "_detail.xls";
        int id = getIntent().getIntExtra("id", 0);
        String projectCode = getIntent().getStringExtra("projectCode");
        Info info = DataSupport.find(Info.class, id);

        if (info != null) {
            tvId.setText(info.getId() + "");
            tvPowerName.setText(info.getPower());
            tvProjectName.setText(info.getProjectName());
            tvInvolvedSign.setText(info.getInvolvedPeople());
            tvMarketingNo.setText(info.getMarketingNo());
            tvMoneyTotal.setText(info.getDescribe());
            tvBankNumber.setText(info.getBankCard());
            tvOpenBank.setText(info.getOpenBank());

//            if (info.getInvolvedPeople().length() > 10) {
//                SpannableString spannable = getSpannableString(info.getInvolvedPeople());
//                tvInvolvedSign.setText(spannable);
//            } else {
            tvInvolvedSign.setText(info.getStatus());
//            }
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
        list = DataSupport.where("projectCode = ?", projectCode).find(Info.class);
//        Collections.reverse(list);
        total = 0;
        for (int i = 0; i < list.size(); i++) {
            total += Double.parseDouble(list.get(i).getDescribe());
        }
        tvTotal.setText("汇总金额(元)：" + String.format("%.2f", total));
        tvTotal.setFocusable(false);
        adapter = new DetailAdapter(list);
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

    @OnClick({R.id.iv_back, R.id.btn_share})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                outTransitionAnimation();
                break;
            case R.id.btn_share:
                // 保存成图片
                Bitmap bitmap = loadBitmapFromView(llView);
                String fileName = date + "_" + "detail.jpg";
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
                String pdfFile = imagePath + date +"_detail.pdf";
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
//                openAssignFolder(new File(pdfFile));

                saveToExcel = new SaveToExcel(this, imagePath + excelFileName);
                if (list != null) {
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i) != null) {
                            String power = list.get(i).getPower();
                            String projectName = list.get(i).getProjectName();
//                            String marketingNo = list.get(i).getMarketingNo();
                            String openBank = list.get(i).getOpenBank();
                            String bankCard = list.get(i).getBankCard();
                            String status = list.get(i).getStatus();
                            String describe = list.get(i).getDescribe();
                            if (i == list.size() - 1) {
                                saveToExcel.writeToExcel(power, projectName, openBank, bankCard, status, /*marketingNo,*/ describe, total);
                            } else {
                                saveToExcel.writeToExcel(power, projectName, openBank, bankCard, status, /*marketingNo,*/ describe, "");
                            }
                        }
                    }
                }


//                sharePDF(xlsFile);
                Toast.makeText(this, "保存成功", Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }
    }
}