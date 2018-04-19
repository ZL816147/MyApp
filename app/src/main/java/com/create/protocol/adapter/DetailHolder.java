package com.create.protocol.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.create.protocol.MyApplication;
import com.create.protocol.R;
import com.create.protocol.model.Info;

/**
 * Created by jsntnjzb on 2017/2/17.
 */
public class DetailHolder extends BaseHolder<Info> {

    private TextView tvId;
    private TextView tvPowerName;
    private TextView tvProjectName;
    private EditText etInvolvedSign;
    private TextView tvMarketingNo;
    private TextView tvMoneyTotal;
    private TextView tvBankNumber;
    private TextView tvOpenBank;

    public DetailHolder() {
    }

    @Override
    protected View initView() {
        View view = LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.item_record, null);
        tvId = view.findViewById(R.id.tv_id);
        tvPowerName = view.findViewById(R.id.tv_power_name);
        tvProjectName = view.findViewById(R.id.tv_project_name);
        etInvolvedSign = view.findViewById(R.id.tv_involved_sign);
        tvMarketingNo = view.findViewById(R.id.tv_marketing_no);
        tvMoneyTotal = view.findViewById(R.id.tv_money_total);
        tvBankNumber = view.findViewById(R.id.tv_bank_number);
        tvOpenBank = view.findViewById(R.id.tv_open_bank);
        return view;
    }

    @Override
    public void bindData(Info data) {
        tvId.setText(data.getId() + "");
        tvPowerName.setText(data.getPower());
        tvProjectName.setText(data.getProjectName());
        etInvolvedSign.setText(data.getInvolvedPeople());
        tvMarketingNo.setText(data.getMarketingNo());
        tvMoneyTotal.setText(data.getDescribe());
        tvBankNumber.setText(data.getBankCard());
        tvOpenBank.setText(data.getOpenBank());

        if (data.getInvolvedPeople().length() > 10) {
            Bitmap bitmap = stringtoBitmap(data.getInvolvedPeople());
            SpannableString spannable = new SpannableString(1 + "");
            Drawable drawable = new BitmapDrawable(bitmap);//加载应用程序中图片
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight()); //设置宽'高
            // 若该TextView 上既有文本又有图片设置图片与文本底部对齐
            ImageSpan tv_span = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
            spannable.setSpan(tv_span, 0, spannable.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            etInvolvedSign.setText(spannable);
        } else {
            etInvolvedSign.setText(data.getInvolvedPeople());
        }
        etInvolvedSign.setFocusable(false);
    }

    public Bitmap stringtoBitmap(String string) {
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


}
