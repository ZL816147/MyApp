package com.create.protocol;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.BankCardParams;
import com.baidu.ocr.sdk.model.BankCardResult;
import com.baidu.ocr.ui.camera.CameraActivity;
import com.create.protocol.base.BaseActivity;
import com.create.protocol.model.Info;
import com.create.protocol.utils.AudioRecoderUtils;
import com.create.protocol.utils.FileUtil;
import com.create.protocol.utils.LogUtils;
import com.create.protocol.utils.NumberToCN;
import com.create.protocol.utils.RateUtil;
import com.create.protocol.widget.AudioRecoderDialog;
import com.create.protocol.widget.TakePhotoPopupWindow;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.litepal.LitePal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by jsntnjzb on 2018/1/30.
 */

public class EnterActivity extends BaseActivity implements View.OnLongClickListener, View.OnFocusChangeListener, TextWatcher, AudioRecoderUtils.OnAudioStatusUpdateListener {
    private static float mX;
    private static float mY;
    @BindView(R.id.tv_create_address)
    TextView tvCreateAddress;
    @BindView(R.id.tv_create_date)
    TextView tvCreateDate;
    @BindView(R.id.tv_compensate_state)
    EditText tvCompensateState;
    @BindView(R.id.et_power_name)
    EditText etPowerName;
    @BindView(R.id.et_bank_number)
    EditText etBankNumber;
    Unbinder unbinder;
    @BindView(R.id.et_involved_sign)
    EditText etInvolvedSign;
    @BindView(R.id.btn_save)
    Button btnSave;
    @BindView(R.id.et_construction)
    EditText etConstruction;
    @BindView(R.id.et_project_file)
    EditText etProjectCode;
    @BindView(R.id.et_project_name)
    EditText etProjectName;
    @BindView(R.id.et_destroy_stuff)
    EditText etDestroyStuff;
    @BindView(R.id.et_amount)
    EditText etAmount;
    @BindView(R.id.et_unit_price)
    EditText etUnitPrice;
    @BindView(R.id.et_subtotal)
    EditText etSubtotal;
    @BindView(R.id.iv_identity_card)
    ImageView ivIdentityCard;
    @BindView(R.id.iv_bank_card)
    ImageView ivBankCard;
    @BindView(R.id.iv_scene1)
    ImageView ivScene1;
    @BindView(R.id.iv_scene2)
    ImageView ivScene2;
    @BindView(R.id.iv_scene3)
    ImageView ivScene3;
    @BindView(R.id.iv_scene4)
    ImageView ivScene4;
    @BindView(R.id.et_responsible_sign)
    EditText etResponsibleSign;
    @BindView(R.id.et_open_bank)
    EditText etOpenBank;
    @BindView(R.id.et_contact_number)
    EditText etContactNumber;
    @BindView(R.id.et_marketing_no)
    EditText etMarketingNo;
    @BindView(R.id.et_power_people)
    EditText etPowerPeople;
    @BindView(R.id.tv_total)
    TextView tvTotal;
    @BindView(R.id.ll_view)
    LinearLayout llView;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_add)
    TextView tvAdd;
    @BindView(R.id.tr_add_view)
    TableRow trAddView;
    @BindView(R.id.tl_protocol)
    TableLayout tlProtocol;
    @BindView(R.id.tv_identity_card)
    TextView tvIdentityCard;
    @BindView(R.id.tv_bank_card)
    TextView tvBankCard;
    @BindView(R.id.tv_scene1)
    TextView tvScene1;
    @BindView(R.id.tv_scene2)
    TextView tvScene2;
    @BindView(R.id.tv_scene3)
    TextView tvScene3;
    @BindView(R.id.tv_scene4)
    TextView tvScene4;
    @BindView(R.id.spinner_power_name)
    Spinner spinnerPowerName;
    @BindView(R.id.et_unit)
    EditText etUnit;
    @BindView(R.id.spinner_destroy_stuff)
    Spinner spinnerDestroyStuff;
    @BindView(R.id.spinner_unit)
    Spinner spinnerUnit;
    @BindView(R.id.tv_record)
    TextView tvRecord;
    @BindView(R.id.tv_play)
    TextView tvPlay;
    @BindView(R.id.iv_delete_voice)
    ImageView ivDeleteVoice;
    @BindView(R.id.ll_find)
    LinearLayout llFind;
    @BindView(R.id.iv_power_name)
    ImageView ivPowerName;
    @BindView(R.id.rl_power_name)
    RelativeLayout rlPowerName;
    @BindView(R.id.iv_destroy_stuff)
    ImageView ivDestroyStuff;
    @BindView(R.id.ll_destroy_stuff)
    LinearLayout llDestroyStuff;
    @BindView(R.id.iv_unit)
    ImageView ivUnit;
    @BindView(R.id.ll_unit)
    LinearLayout llUnit;
    private AMapLocationClient mLocationClient;
    private InputMethodManager mInputManager;
    private WindowManager.LayoutParams params;
    private Uri wordUri;
    private File newFile;
    private String site;
    private String date;
    private String status;
    private String powerName;
    private String construction;
    private String projectCode;
    private String projectName;
    private String involvedPeople;
    private String responsiblePeople;
    private String openBank;
    private String bankCard;
    private String contactNumber;
    private String marketingNo;
    private String powerPeople;
    private Window window;
    private Uri imageUri;
    public static final int REQUEST_CODE_CAMERA = 0;
    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;
    private TakePhotoPopupWindow takePhotoPopupWindow;
    private String time;

    public static final int SIGN = 1;
    public static final int IMAGE = 2;
    // 模板地址
    private static final String templatePath = Environment.getExternalStorageDirectory() + "/protocol/template.doc";
    // 创建生成的文件地址
    private static final String newPath = Environment.getExternalStorageDirectory() + "/protocol/";
    // 系统图库
    private static String cameraPath = Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_DCIM + File.separator + "Camera" + File.separator;
    //    private static final String imagePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
    // 签名
    public String signPath = "";
    //    public static String signPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
    //    // 模板地址
//    private static final String templatePath = "/mnt/sdcard/protocol/template.doc";
//    // 创建生成的文件地址
//    private static final String newPath = "/mnt/sdcard/protocol/";
//    // 图片存储
//    private static final String imagePath = "/mnt/sdcard/protocol/";
    private String flag;
    private double total;
    private String ivIdentityCardCode;
    private String ivBankCardCode;
    private String ivScene1Code;
    private String ivScene2Code;
    private String ivScene3Code;
    private String ivScene4Code;
    private String t;
    private ArrayAdapter<CharSequence> adapter1;
    private ArrayAdapter<CharSequence> adapter3;
    private ArrayAdapter<CharSequence> adapter2;
    private MediaRecorder mRecorder;
    private String mFileName;
    private AudioRecoderDialog recoderDialog;
    private AudioRecoderUtils recoderUtils;
    private long downT;
    private String pathName = "";
    private boolean emptyPowerName;
    private File audioFile;

    @Override
    protected int getLayout() {
        return R.layout.activity_enter;
    }

    @Override
    protected void init(Bundle savedInstanceState) {

        setListener();
        mInputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        startLocation();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        String str = formatter.format(curDate);
        tvCreateDate.setText(Html.fromHtml(getString(R.string.create_date_format, str)));
        time = tvCreateDate.getText().toString().trim().replace("制单日：", "").replace("年", "").replace("月", "").replace("日", "").replace(" ", "").replace(":", "");

        recoderDialog = new AudioRecoderDialog(this);
        recoderDialog.setShowAlpha(0.98f);
        if (!TextUtils.isEmpty(tvCompensateState.getText().toString())) {
            pathName = tvCompensateState.getText().toString() + "_" + time + ".amr";
        } else {
            pathName = time + ".amr";
        }
        audioFile = new File(myDir, pathName);
        try {
            if (audioFile.exists()) {
                audioFile.delete();
            }
            audioFile.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        recoderUtils = new AudioRecoderUtils(audioFile);
        recoderUtils.setOnAudioStatusUpdateListener(EnterActivity.this);
        discern();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setListener() {
        etPowerName.setOnLongClickListener(this);
        etConstruction.setOnLongClickListener(this);
        etProjectCode.setOnLongClickListener(this);
        etProjectName.setOnLongClickListener(this);
        etInvolvedSign.setOnLongClickListener(this);
        etResponsibleSign.setOnLongClickListener(this);
        etOpenBank.setOnLongClickListener(this);
        etBankNumber.setOnLongClickListener(this);
        etContactNumber.setOnLongClickListener(this);
        etMarketingNo.setOnLongClickListener(this);
        etPowerPeople.setOnLongClickListener(this);

        etSubtotal.setOnFocusChangeListener(this);
        etSubtotal.addTextChangedListener(this);
        tvCreateAddress.addTextChangedListener(this);
        tvCreateDate.addTextChangedListener(this);
        tvCompensateState.addTextChangedListener(this);
        etPowerName.addTextChangedListener(this);
        etConstruction.addTextChangedListener(this);
        etProjectCode.addTextChangedListener(this);
        etProjectName.addTextChangedListener(this);
        etResponsibleSign.addTextChangedListener(this);
        etInvolvedSign.addTextChangedListener(this);
        etOpenBank.addTextChangedListener(this);
        etBankNumber.addTextChangedListener(this);
        etContactNumber.addTextChangedListener(this);
        etMarketingNo.addTextChangedListener(this);
        etPowerPeople.addTextChangedListener(this);

//        btnSave.setEnabled(false);
//        btnSave.setBackgroundResource(R.drawable.shape_next_button);

        adapter1 = ArrayAdapter.createFromResource(this, R.array.supply_power, R.layout.spinner_item);
        adapter1.setDropDownViewResource(R.layout.dropdown_stytle);
        spinnerPowerName.setAdapter(adapter1);

        adapter2 = ArrayAdapter.createFromResource(this, R.array.destroy_goods, R.layout.spinner_item);
        adapter2.setDropDownViewResource(R.layout.dropdown_stytle);
        spinnerDestroyStuff.setAdapter(adapter2);

        adapter3 = ArrayAdapter.createFromResource(this, R.array.goods_unit, R.layout.spinner_item);
        adapter3.setDropDownViewResource(R.layout.dropdown_stytle);
        spinnerUnit.setAdapter(adapter3);
        spinnerPowerName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == adapter1.getCount() - 1) {
//                    initSelectViewStatus(view);
                    rlPowerName.setVisibility(View.VISIBLE);
                    spinnerPowerName.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerDestroyStuff.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == adapter2.getCount() - 1) {
                    llDestroyStuff.setVisibility(View.VISIBLE);
                    spinnerDestroyStuff.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == adapter3.getCount() - 1) {
                    llUnit.setVisibility(View.VISIBLE);
                    spinnerUnit.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        tvRecord.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        recoderUtils.startRecord();
                        downT = System.currentTimeMillis();
                        recoderDialog.showAtLocation(tvRecord, Gravity.CENTER, 0, 0);
                        tvRecord.setBackgroundResource(R.drawable.shape_recoder_recoding);
                        return true;
                    case MotionEvent.ACTION_UP:
                        recoderUtils.stopRecord();
                        recoderDialog.dismiss();
                        tvRecord.setBackgroundResource(R.drawable.shape_recoder_normal);
                        tvRecord.setVisibility(View.GONE);
                        llFind.setVisibility(View.VISIBLE);
                        return true;
                }
                return false;
            }
        });
    }

    private void initSelectViewStatus(View view) {
        EditText etSelect = view.findViewById(R.id.et_select);
        etSelect.setFocusable(true);
        etSelect.setFocusableInTouchMode(true);
    }

    @Override
    public void onUpdate(double db) {
        if (null != recoderDialog) {
            int level = (int) db;
            recoderDialog.setLevel((int) db);
            recoderDialog.setTime(System.currentTimeMillis() - downT);
        }
    }

    @Override
    public void onStop(String filePath) {
        Toast.makeText(this, "录音保存在：" + filePath, Toast.LENGTH_SHORT).show();
    }

    private void discern() {
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "= 5a90c10c");
    }

    /**
     * 语音转化为文字
     *
     * @param flag
     */
    //TODO 开始说话：
    private void btnVoice(final String flag) {
        RecognizerDialog dialog = new RecognizerDialog(this, null);
        dialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        dialog.setParameter(SpeechConstant.ACCENT, "mandarin");
        dialog.setListener(new RecognizerDialogListener() {
            @Override
            public void onResult(RecognizerResult recognizerResult, boolean b) {
                printResult(recognizerResult, flag);
            }

            @Override
            public void onError(SpeechError speechError) {
            }
        });
        dialog.show();
        Toast.makeText(this, "请开始说话", Toast.LENGTH_SHORT).show();
    }

    //回调结果：

    private void printResult(RecognizerResult results, String flag) {
        String text = parseIatResult(results.getResultString());
        // 自动填写地址
        if ("".equals(flag)) {
        } else if ("powername".equals(flag)) {
            etPowerName.append(text);
        } else if ("construction".equals(flag)) {
            etConstruction.append(text);
        } else if ("projectfile".equals(flag)) {
            etProjectCode.append(text);
        } else if ("projectname".equals(flag)) {
            etProjectName.append(text);
        } else if ("openbank".equals(flag)) {
            etOpenBank.append(text);
        } else if ("contactnumber".equals(flag)) {
            etContactNumber.append(text);
        } else if ("marketingno".equals(flag)) {
            etMarketingNo.append(text);
        } else if ("powerpeople".equals(flag)) {
            etPowerPeople.append(text);
        }
    }

    public static String parseIatResult(String json) {
        StringBuffer ret = new StringBuffer();
        try {
            JSONTokener tokener = new JSONTokener(json);
            JSONObject joResult = new JSONObject(tokener);
            JSONArray words = joResult.getJSONArray("ws");
            for (int i = 0; i < words.length(); i++) {
                // 转写结果词，默认使用第一个结果
                JSONArray items = words.getJSONObject(i).getJSONArray("cw");
                JSONObject obj = items.getJSONObject(0);
//                ret.append(obj.getString("w"));
                // 标点符号不做处理
                if (obj.getString("w").equals("。") || obj.getString("w").equals("！") || obj.getString("w").equals("？")) {
                    ret = ret.append("");
                } else {
                    ret.append(obj.getString("w"));  //找到需要的字符串，拼出一句话
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret.toString();
    }

    /**
     * 定位
     */
    public void startLocation() {
        //初始化定位
        mLocationClient = new AMapLocationClient(this);
        //初始化AMapLocationClientOption对象
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
        /**
         * 设置定位场景，目前支持三种场景（签到、出行、运动，默认无场景）
         */
        mLocationOption.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.SignIn);
        if (null != mLocationClient) {
            mLocationClient.setLocationOption(mLocationOption);
            //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
            mLocationClient.stopLocation();
            mLocationClient.startLocation();
        }
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
        //声明定位回调监听器
        AMapLocationListener mLocationListener = new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation != null) {
                    if (aMapLocation.getErrorCode() == 0) {
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date date = new Date(aMapLocation.getTime());
                        //可在其中解析amapLocation获取相应内容。
                        LogUtils.e(aMapLocation.getLocationType()//获取当前定位结果来源，如网络定位结果，详见定位类型表
                                + aMapLocation.getLatitude()//获取纬度
                                + aMapLocation.getLongitude()//获取经度
                                + aMapLocation.getAccuracy()//获取精度信息
                                + aMapLocation.getAddress()//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                                + aMapLocation.getCountry()//国家信息
                                + aMapLocation.getProvince()//省信息
                                + aMapLocation.getCity()//城市信息
                                + aMapLocation.getDistrict()//城区信息
                                + aMapLocation.getStreet()//街道信息
                                + aMapLocation.getStreetNum()//街道门牌号信息
                                + aMapLocation.getCityCode()//城市编码
                                + aMapLocation.getAdCode()//地区编码
                                + aMapLocation.getAoiName()//获取当前定位点的AOI信息
                                + aMapLocation.getBuildingId()//获取当前室内定位的建筑物Id
                                + aMapLocation.getFloor()//获取当前室内定位的楼层
                                + aMapLocation.getGpsAccuracyStatus()//获取GPS的当前状态
                                + df.format(date));//获取定位时间
                        tvCreateAddress.setText(Html.fromHtml(getString(R.string.create_address_format, aMapLocation.getProvince()//省信息
                                + aMapLocation.getCity()//城市信息
                                + aMapLocation.getDistrict()//城区信息
                                + aMapLocation.getStreet()//街道信息
                                + aMapLocation.getStreetNum())));
                    } else {
                        //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                        LogUtils.e("location Error, ErrCode:" + aMapLocation.getErrorCode() + ", errInfo:" + aMapLocation.getErrorInfo());
                        Toast.makeText(EnterActivity.this, aMapLocation.getErrorInfo(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        };
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
    }

    private void saveData() {
        SQLiteDatabase db = LitePal.getDatabase();
        site = tvCreateAddress.getText().toString().substring(3);
        date = tvCreateDate.getText().toString().substring(4);
        status = tvCompensateState.getText().toString();
        if (rlPowerName.getVisibility() == View.GONE) {
            powerName = spinnerPowerName.getSelectedItem().toString();
        } else if (rlPowerName.getVisibility() == View.VISIBLE) {
            powerName = etPowerName.getText().toString();
        } else {
            powerName = "";
        }
        construction = etConstruction.getText().toString();
        projectCode = etProjectCode.getText().toString();
        projectName = etProjectName.getText().toString();

        involvedPeople = etInvolvedSign.getText().toString();
        if ("1".equals(involvedPeople)) {
            involvedPeople = etInvolvedSignBitmap;
        }
        responsiblePeople = etResponsibleSign.getText().toString();
        if ("1".equals(responsiblePeople)) {
            responsiblePeople = etResponsibleSignBitmap;
        }
        openBank = etOpenBank.getText().toString();
        bankCard = etBankNumber.getText().toString();
        contactNumber = etContactNumber.getText().toString();
        marketingNo = etMarketingNo.getText().toString();
        powerPeople = etPowerPeople.getText().toString();
        // 保存成图片
        Bitmap protocolBitmap = loadBitmapFromView(llView);
        String protocolFile = time + "_" + "protocol" + ".jpg";
        File file = new File(cameraPath, protocolFile);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            if (protocolBitmap != null) {
                protocolBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            }
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        Info info = new Info();
        info.setSite(site);
        info.setDate(date);
        info.setStatus(status);
        info.setPower(powerName);
        info.setConstruction(construction);
        info.setProjectCode(projectCode);
        info.setProjectName(projectName);
        info.setDescribe(t);
//        info.setIdentityCardImage(ivIdentityCardCode);
//        info.setBankCardImage(ivBankCardCode);
//        info.setSceneImage1(ivScene1Code);
//        info.setSceneImage2(ivScene2Code);
//        info.setSceneImage3(ivScene3Code);
//        info.setSceneImage4(ivScene4Code);
        info.setInvolvedPeople(involvedPeople);
        info.setResponsiblePeople(responsiblePeople);
        info.setOpenBank(openBank);
        info.setBankCard(bankCard);
        info.setContactNumber(contactNumber);
        info.setMarketingNo(marketingNo);
        info.setPowerPeople(powerPeople);
        info.setProtocolBitmap(encodeBitmap(protocolBitmap));
        if (info.save()) {
            Toast.makeText(this, "存储成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "存储失败", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 底部弹出窗
     *
     * @param type
     */
    private void showSelectPopupWindow(int type) {
        if (!TextUtils.isEmpty(tvCompensateState.getText().toString())) {
            signPath = tvCompensateState.getText().toString() + "_" + "sign" + "_" + time + ".png";
        } else {
            signPath = "sign" + "_" + time + ".png";
        }
        File file = new File(cameraPath, signPath);
        try {
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        takePhotoPopupWindow = new TakePhotoPopupWindow(this, popItemListener, type, signListener, cameraPath + signPath);
        // 显示窗口,设置layout在PopupWindow中显示的位置
        takePhotoPopupWindow.showAtLocation(btnSave, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        window = this.getWindow();
        params = window.getAttributes();
        // 当弹出Popupwindow时，背景变半透明
        params.alpha = 0.7f;
        // 此行代码主要是解决在华为手机上半透明效果无效的bug
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setAttributes(params);
        // 设置Popupwindow关闭监听，当Popupwindow关闭，背景恢复1f
        takePhotoPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
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

    private String etInvolvedSignBitmap;
    private String etResponsibleSignBitmap;
    private TakePhotoPopupWindow.OnSignListener signListener = new TakePhotoPopupWindow.OnSignListener() {
        @Override
        public void onSignSuccess(boolean signed) {
            if (signed) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 3;
                Bitmap bitmap = BitmapFactory.decodeFile(cameraPath + signPath, options);
                SpannableString spannable = new SpannableString(1 + "");
                Drawable drawable = new BitmapDrawable(bitmap);//加载应用程序中图片
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight()); //设置宽'高
                // 若该TextView 上既有文本又有图片设置图片与文本底部对齐
                ImageSpan tv_span = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
                spannable.setSpan(tv_span, 0, spannable.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                if ("involved".equals(flag)) {
                    etInvolvedSign.setText(spannable);
                    etInvolvedSignBitmap = encodeBitmap(bitmap);
                } else if ("responsible".equals(flag)) {
                    etResponsibleSign.setText(spannable);
                    etResponsibleSignBitmap = encodeBitmap(bitmap);
                }
            }
        }
    };
    /**
     * 底部弹出窗，拍照/相册选择
     */
    private View.OnClickListener popItemListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_take_photo:
                    takePhoto();
                    break;
                case R.id.tv_select_photo:
                    selectPhoto();
                    break;
            }
        }
    };

    /**
     * 拍照
     */
    private void takePhoto() {
        openCamera();
        takePhotoPopupWindow.dismiss();
    }

    /**
     * 选择图片
     */
    private void selectPhoto() {
        openAlbum();
        takePhotoPopupWindow.dismiss();
    }

    /**
     * 打开相机
     */
    private void openCamera() {
        // 创建file对象，用于存储拍照后的图片；
        File outputImage = new File(cameraPath, time + "_" + flag + ".jpg");
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (Build.VERSION.SDK_INT >= 24) {
            imageUri = FileProvider.getUriForFile(this, "com.create.protocol", outputImage);
        } else {
            imageUri = Uri.fromFile(outputImage);
        }
        // 启动相机程序
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, TAKE_PHOTO);
    }

    /**
     * 打开相册
     */
    private void openAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);
    }

    /**
     * 识别银行卡号
     */
    private void discernBank() {
        // 生成intent对象
        Intent intent = new Intent(this, CameraActivity.class);
        // 设置临时存储
        intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH, FileUtil.getSaveFile(this).getAbsolutePath());
        // 调用拍摄银行卡的activity
        intent.putExtra(CameraActivity.KEY_CONTENT_TYPE, CameraActivity.CONTENT_TYPE_BANK_CARD);
        startActivityForResult(intent, REQUEST_CODE_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 获取调用参数
        // 通过临时文件获取拍摄的图片
        String filePath = FileUtil.getSaveFile(this).getAbsolutePath();
        switch (requestCode) {
            case REQUEST_CODE_CAMERA:
                // 判断是否是身份证正面
                if (data != null) {
                    String contentType = data.getStringExtra(CameraActivity.KEY_CONTENT_TYPE);
                    if (CameraActivity.CONTENT_TYPE_BANK_CARD.equals(contentType)) {
                        // 银行卡识别参数设置
                        BankCardParams param = new BankCardParams();
                        param.setImageFile(new File(filePath));
                        // 调用银行卡识别服务
                        OCR.getInstance().recognizeBankCard(param, new OnResultListener<BankCardResult>() {
                            @Override
                            public void onResult(BankCardResult result) {
                                // 调用成功，返回BankCardResult对象
                                LogUtils.e(result.getBankCardNumber());
                                etBankNumber.setText(result.getBankCardNumber().replace(" ", ""));
                            }

                            @Override
                            public void onError(OCRError error) {
                                // 调用失败，返回OCRError对象
                            }
                        });
                    }
                }
                break;
            case TAKE_PHOTO:
                startUcrop(imageUri.toString());
                break;
            case CHOOSE_PHOTO:
                if (data != null) {
                    if (Build.VERSION.SDK_INT >= 19) {
                        // 4.4及以上的系统使用这个方法处理图片；
                        startUcrop(handleImageOnKitKat(data));
                    } else {
                        startUcrop(handleImageBeforeKitKat(data));
                    }
                }
                break;
            case UCrop.REQUEST_CROP:
                if (data != null) {
                    final Uri resultUri = UCrop.getOutput(data);
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                        if ("identity_card".equals(flag)) {
                            ivIdentityCard.setImageBitmap(bitmap);
                            ivIdentityCardCode = encodeBitmap(bitmap);
                            tvIdentityCard.setVisibility(View.GONE);
                        } else if ("bank_card".equals(flag)) {
                            ivBankCard.setImageBitmap(bitmap);
                            ivBankCardCode = encodeBitmap(bitmap);
                            tvBankCard.setVisibility(View.GONE);
                        } else if ("scene1".equals(flag)) {
                            ivScene1.setImageBitmap(bitmap);
                            ivScene1Code = encodeBitmap(bitmap);
                            tvScene1.setVisibility(View.GONE);
                        } else if ("scene2".equals(flag)) {
                            ivScene2.setImageBitmap(bitmap);
                            ivScene2Code = encodeBitmap(bitmap);
                            tvScene2.setVisibility(View.GONE);
                        } else if ("scene3".equals(flag)) {
                            ivScene3.setImageBitmap(bitmap);
                            ivScene3Code = encodeBitmap(bitmap);
                            tvScene3.setVisibility(View.GONE);
                        } else if ("scene4".equals(flag)) {
                            ivScene4.setImageBitmap(bitmap);
                            ivScene4Code = encodeBitmap(bitmap);
                            tvScene4.setVisibility(View.GONE);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case UCrop.RESULT_ERROR:
                if (data != null) {
                    Throwable cropError = UCrop.getError(data);
                }
                break;
            default:
                break;
        }
    }

    private void startUcrop(String path) {
        Uri uri_crop = Uri.parse(path);
        // 裁剪后保存到文件中
        File outDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if (!outDir.exists()) {
            outDir.mkdirs();
        }
        Uri destinationUri = Uri.fromFile(new File(outDir, System.currentTimeMillis() + ".jpg"));
        UCrop uCrop = UCrop.of(uri_crop, destinationUri);
        UCrop.Options options = new UCrop.Options();
        // 设置裁剪图片可操作的手势
        options.setAllowedGestures(UCropActivity.SCALE, UCropActivity.ROTATE, UCropActivity.ALL);
        // 设置隐藏底部容器，默认显示
        // options.setHideBottomControls(true);
        // 设置toolbar颜色
        options.setToolbarColor(ActivityCompat.getColor(this, R.color.colorPrimary));
        //设置状态栏颜色
        options.setStatusBarColor(ActivityCompat.getColor(this, R.color.colorPrimary));
        // 是否能调整裁剪框
//        options.setFreeStyleCropEnabled(true);
        uCrop.withOptions(options);
        uCrop.start(this);
        LogUtils.e("++++++++++++++++++++++++++++++++++++++");
    }

    /**
     * 4.4以前的系统使用这个方法处理图片
     *
     * @param data
     */
    private String handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        return getImagePath(uri, null);
    }

    /**
     * 4.4及以上的系统使用这个方法处理图片
     *
     * @param data
     */
    @TargetApi(19)
    private String handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            // 如果document类型的Uri,则通过document来处理
            String docID = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docID.split(":")[1];     //解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docID));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是content类型的uri，则使用普通方式使用
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 如果是file类型的uri，直接获取路径即可
            imagePath = uri.getPath();
        }
        LogUtils.e(imagePath);
        return imagePath;
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = this.getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
            path = "file://" + path;
        }
        return path;
    }

    @OnClick({R.id.iv_power_name, R.id.iv_destroy_stuff, R.id.iv_unit, R.id.tv_play, R.id.iv_delete_voice, R.id.btn_save, R.id.iv_identity_card, R.id.iv_bank_card, R.id.iv_scene1, R.id.iv_scene2, R.id.iv_scene3, R.id.iv_scene4, R.id.iv_back, R.id.tv_total, R.id.tv_add})
    public void onClick(View v) {
        File file = new File(myDir, pathName);
        switch (v.getId()) {
            case R.id.iv_power_name:
                etPowerName.setText("");
                powerName = "";
                rlPowerName.setVisibility(View.GONE);
                spinnerPowerName.setVisibility(View.VISIBLE);
                spinnerPowerName.setSelection(0);
                spinnerPowerName.performClick();
                break;
            case R.id.iv_destroy_stuff:
                etDestroyStuff.setText("");
                llDestroyStuff.setVisibility(View.GONE);
                spinnerDestroyStuff.setVisibility(View.VISIBLE);
                spinnerDestroyStuff.setSelection(0);
                spinnerDestroyStuff.performClick();
                break;
            case R.id.iv_unit:
                etUnit.setText("");
                llUnit.setVisibility(View.GONE);
                spinnerUnit.setVisibility(View.VISIBLE);
                spinnerUnit.setSelection(0);
                spinnerUnit.performClick();
                break;
            case R.id.tv_play:
                if (null == audioFile || !audioFile.exists()) {
                    return;
                }
//                // 获取父目录
////                File parentFlie = new File(audioFile.getParent());
//                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                intent.addCategory(Intent.CATEGORY_OPENABLE);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                Uri uri;
//                if (Build.VERSION.SDK_INT >= 24) {
//                    uri = FileProvider.getUriForFile(this, "com.create.protocol", audioFile);
//                } else {
//                    uri = Uri.fromFile(audioFile);
//                }
//                intent.setDataAndType(uri, "*/*");
//                startActivity(intent);

//                 获取父目录
//                File parentFlie = new File(audioFile.getParent());
                Uri uri;
                if (Build.VERSION.SDK_INT >= 24) {
                    uri = FileProvider.getUriForFile(this, "com.create.protocol", audioFile);
                } else {
                    uri = Uri.fromFile(audioFile);
                }
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setDataAndType(uri, "*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivity(intent);
                break;
            case R.id.iv_delete_voice:
                if (audioFile.exists()) {
                    audioFile.delete();
                    llFind.setVisibility(View.GONE);
                    tvRecord.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.btn_save:
//                try {
//                    InputStream inputStream = this.getAssets().open("template.doc");
//                    FileUtil.writeFile(new File(templatePath), inputStream);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                doScan();
                tvTotal.performClick();
                // 保存到数据库
//                setAllFocusable();
                saveData();
                finish();
                break;
            case R.id.iv_identity_card:
                flag = "identity_card";
                showSelectPopupWindow(IMAGE);
                break;
            case R.id.iv_bank_card:
                flag = "bank_card";
                showSelectPopupWindow(IMAGE);
                break;
            case R.id.iv_scene1:
                flag = "scene1";
                showSelectPopupWindow(IMAGE);
                break;
            case R.id.iv_scene2:
                flag = "scene2";
                showSelectPopupWindow(IMAGE);
                break;
            case R.id.iv_scene3:
                flag = "scene3";
                showSelectPopupWindow(IMAGE);
                break;
            case R.id.iv_scene4:
                flag = "scene4";
                showSelectPopupWindow(IMAGE);
                break;
            case R.id.iv_back:
                finish();
                outTransitionAnimation();
                break;
            case R.id.tv_total:
                double price = 0.00;
                if (!TextUtils.isEmpty(etSubtotal.getText().toString())) {
                    price = Double.parseDouble(etSubtotal.getText().toString());
                }
                if (tlProtocol.getChildCount() > 12) {
                    for (int i = 4; i < index; i++) {
                        EditText etSubtotal = tlProtocol.getChildAt(i).findViewById(R.id.et_subtotal);
                        if (!TextUtils.isEmpty(etSubtotal.getText().toString())) {
                            double subtotal = Double.parseDouble(etSubtotal.getText().toString());
                            price = price + subtotal;
                        } else {
                            Toast.makeText(this, "请计算单价", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                }
                t = String.format("%.2f", price);
//                Toast.makeText(this, t, Toast.LENGTH_LONG).show();
                String d = NumberToCN.number2CNMontrayUnit(new BigDecimal(Double.parseDouble(t)));
                tvTotal.setText("其他：\n经项目管理单位、施工方单位、涉赔人员协商确定一次性补偿人民币合计：(" + t + ")元， 金额(" + d + ")");
                break;
            case R.id.tv_add:
                LogUtils.e(tlProtocol.getChildCount() + "");
                final View view = addView();
                tlProtocol.addView(view, index++);

                view.findViewById(R.id.tv_delete).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tlProtocol.removeView(view);
                        index--;
                    }
                });
                view.findViewById(R.id.iv_destroy_stuff_add).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        view.findViewById(R.id.ll_destroy_stuff_add).setVisibility(View.GONE);
                        Spinner spinnerDestroyStuff = view.findViewById(R.id.spinner_destroy_stuff);
                        spinnerDestroyStuff.setVisibility(View.VISIBLE);
                        spinnerDestroyStuff.setSelection(0);
                        spinnerDestroyStuff.performClick();
                    }
                });
                view.findViewById(R.id.iv_unit_add).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        view.findViewById(R.id.ll_unit_add).setVisibility(View.GONE);
                        Spinner spinnerUnit = view.findViewById(R.id.spinner_unit);
                        spinnerUnit.setVisibility(View.VISIBLE);
                        spinnerUnit.setSelection(0);
                        spinnerUnit.performClick();
                    }
                });
                final EditText etAmount = view.findViewById(R.id.et_amount);
                final EditText etUnitPrice = view.findViewById(R.id.et_unit_price);
                final EditText etSubtotal = view.findViewById(R.id.et_subtotal);
                etSubtotal.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        calculateUnitPrice((EditText) v, hasFocus, etAmount, etUnitPrice);
                    }
                });
                etSubtotal.addTextChangedListener(this);
                break;
            default:
                break;
        }
    }

    private void calculateUnitPrice(EditText v, boolean hasFocus, EditText etAmount, EditText etUnitPrice) {
        if (!TextUtils.isEmpty(etAmount.getText().toString()) && !TextUtils.isEmpty(etUnitPrice.getText().toString())) {
            EditText edit = v;
            double amount = Double.parseDouble(etAmount.getText().toString());
            double unitPrice = Double.parseDouble(etUnitPrice.getText().toString());
            double subtotal = RateUtil.multiply(amount, unitPrice);
            total = total + subtotal;
            edit.setText(String.format("%.2f", subtotal));
        } else if (hasFocus && TextUtils.isEmpty(etAmount.getText().toString()) || hasFocus && TextUtils.isEmpty(etUnitPrice.getText().toString())) {
            Toast.makeText(EnterActivity.this, "请输入数量和单价", Toast.LENGTH_SHORT).show();
        }
    }

    private int index = 4;

    private View addView() {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LayoutInflater inflater = LayoutInflater.from(this);
        final View view = inflater.inflate(R.layout.one, null);
        final Spinner spinnerDestroy = view.findViewById(R.id.spinner_destroy_stuff);
        final Spinner spinnerUnit = view.findViewById(R.id.spinner_unit);
        final LinearLayout llStuffAdd = view.findViewById(R.id.ll_destroy_stuff_add);
        final LinearLayout llUnitAdd = view.findViewById(R.id.ll_unit_add);

        adapter2 = ArrayAdapter.createFromResource(this, R.array.destroy_goods, R.layout.spinner_item);
        adapter2.setDropDownViewResource(R.layout.dropdown_stytle);
        spinnerDestroy.setAdapter(adapter2);

        adapter3 = ArrayAdapter.createFromResource(this, R.array.goods_unit, R.layout.spinner_item);
        adapter3.setDropDownViewResource(R.layout.dropdown_stytle);
        spinnerUnit.setAdapter(adapter3);

        spinnerDestroy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == adapter2.getCount() - 1) {
//                    initSelectViewStatus(view);
                    llStuffAdd.setVisibility(View.VISIBLE);
                    spinnerDestroy.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == adapter3.getCount() - 1) {
//                    initSelectViewStatus(view);
                    llUnitAdd.setVisibility(View.VISIBLE);
                    spinnerUnit.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        view.setLayoutParams(lp);
        return view;
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.et_power_name:
                flag = "powername";
                btnVoice(flag);
                break;
            case R.id.et_construction:
                flag = "construction";
                btnVoice(flag);
                break;
            case R.id.et_project_file:
                flag = "projectfile";
                btnVoice(flag);
                break;
            case R.id.et_project_name:
                flag = "projectname";
                btnVoice(flag);
                break;
            case R.id.et_open_bank:
                flag = "openbank";
                btnVoice(flag);
                break;
            case R.id.et_contact_number:
                flag = "contactnumber";
                btnVoice(flag);
                break;
            case R.id.et_marketing_no:
                flag = "marketingno";
                btnVoice(flag);
                break;
            case R.id.et_power_people:
                flag = "powerpeople";
                btnVoice(flag);
                break;
            case R.id.et_bank_number:
                discernBank();
                break;
            case R.id.et_involved_sign:
                flag = "involved";
                showSelectPopupWindow(SIGN);
                break;
            case R.id.et_responsible_sign:
                flag = "responsible";
                showSelectPopupWindow(SIGN);
                break;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        calculateUnitPrice((EditText) v, hasFocus, etAmount, etUnitPrice);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void afterTextChanged(Editable s) {
        tvTotal.performClick();
//        String t = String.format("%.2f", total);
//        String d = NumberToCN.number2CNMontrayUnit(new BigDecimal(Double.parseDouble(t)));
//        tvTotal.setText("其他：\n经项目管理单位、施工方单位、涉赔人员协商确定一次性补偿人民币合计：(" + t + ")元， 金额(" + d + ")");
        if (rlPowerName.getVisibility() == View.GONE) {
            emptyPowerName = TextUtils.isEmpty(spinnerPowerName.getSelectedItem().toString());
        } else if (rlPowerName.getVisibility() == View.VISIBLE) {
            emptyPowerName = TextUtils.isEmpty(etPowerName.getText().toString());
        }
        if (!TextUtils.isEmpty(etSubtotal.getText()) &&
                !TextUtils.isEmpty(tvCreateAddress.getText()) &&
                !TextUtils.isEmpty(tvCreateDate.getText()) &&
                !TextUtils.isEmpty(tvCompensateState.getText()) &&
                !emptyPowerName &&
                !TextUtils.isEmpty(etConstruction.getText()) &&
                !TextUtils.isEmpty(etProjectCode.getText()) &&
                !TextUtils.isEmpty(etProjectName.getText()) &&
                !TextUtils.isEmpty(etResponsibleSign.getText()) &&
                !TextUtils.isEmpty(etInvolvedSign.getText()) &&
                !TextUtils.isEmpty(etOpenBank.getText()) &&
                !TextUtils.isEmpty(etBankNumber.getText()) &&
                !TextUtils.isEmpty(etContactNumber.getText()) &&
                !TextUtils.isEmpty(etMarketingNo.getText()) &&
                !TextUtils.isEmpty(etPowerPeople.getText())) {
            btnSave.setEnabled(true);
            btnSave.setBackgroundResource(R.drawable.selector_confirm_button);
        } else {
            btnSave.setEnabled(false);
            btnSave.setBackgroundResource(R.drawable.shape_next_button);
            btnSave.setText("请填写信息完整信息");
        }
    }

    private void setAllFocusable() {
        etSubtotal.setFocusable(false);
        etPowerName.setFocusable(false);
        tvCompensateState.setFocusable(false);
        spinnerPowerName.setFocusable(false);
        etConstruction.setFocusable(false);
        etProjectCode.setFocusable(false);
        etProjectName.setFocusable(false);
        etResponsibleSign.setFocusable(false);
        etInvolvedSign.setFocusable(false);
        etOpenBank.setFocusable(false);
        etBankNumber.setFocusable(false);
        etContactNumber.setFocusable(false);
        etMarketingNo.setFocusable(false);
        etPowerPeople.setFocusable(false);
    }

//    private void doScan() {
//        //获取模板文件
//        File templateFile = new File(templatePath);
//        //创建生成的文件
//
//        newFile = new File(newPath + time + ".doc");
//        Map<String, Object> map = new HashMap<>();
//        map.put("$DD$", "南京市");
//        map.put("$ZDR$", "2018-03-05 17:39:42");
//        map.put("$PFQK$", "未赔付");
//        map.put("$GDS$", "雨花台区供电所");
//        map.put("$SGF$", "南京第二市政有限公司");
//        map.put("$XMWJ$", "政策处理补偿协议");
//        map.put("$XMMC$", "城南大道快速化工程");
//        map.put("$SHWP$", "+损坏物品：脚手架   数量：2  单价：5000    小计：10000  \n+损坏物品：挖掘机   数量：1  单价：500000    小计：500000");
//        map.put("$QT$", "其他他他");
//        map.put("$BC$", "510000");
//        map.put("$BCDX$", "伍拾壹万圆整");
//        map.put("$PSRQZ$", "李世民");
//        map.put("$JBRQZ$", "赵匡胤");
//        map.put("$KHH$", "中国建设银行");
//        map.put("$YHKH$", "6227001251210791123");
//        map.put("$LXDH$", "15112345678");
//        map.put("$YXHH$", "41-BGH21515");
//        map.put("$GDSFZR$", "嬴政");
//        Map<String, Object> img = new HashMap<>();
//        img.put("width", 500);
//        img.put("height", 400);
//        img.put("type", "png");
//        try {
//            img.put("content", inputStream2ByteArray(new FileInputStream("tab_discover_h.png"), true));
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        // 图片信息放在map中
//        map.put("${GAISHU}", img);
//        writeDoc(templateFile, newFile, map);
//        // 查看
//        doOpenWord();
//    }
//
//    /**
//     * 调用手机中安装的可打开word的软件
//     */
//    private void doOpenWord() {
//        try {
//            if (Build.VERSION.SDK_INT >= 24) {
//                wordUri = FileProvider.getUriForFile(this, "com.create.protocol", newFile);
//            } else {
//                wordUri = Uri.fromFile(newFile);
//            }
//            // 启动相机程序
//            Intent intent = new Intent();
//            intent.setAction("android.intent.action.VIEW");
//            intent.addCategory("android.intent.category.DEFAULT");
//            String fileMimeType = "application/msword";
//            intent.setDataAndType(wordUri, fileMimeType);
////            intent.putExtra(MediaStore.EXTRA_OUTPUT, wordUri);
//            startActivity(intent);
//        } catch (ActivityNotFoundException e) {
//            //检测到系统尚未安装OliveOffice的apk程序
//            Toast.makeText(this, "未找到软件", Toast.LENGTH_LONG).show();
//            //请先到www.olivephone.com/e.apk下载并安装
//        }
//    }


//    public byte[] inputStream2ByteArray(InputStream in, boolean isClose) {
//        byte[] byteArray = null;
//        try {
//            int total = in.available();
//            byteArray = new byte[total];
//            in.read(byteArray);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (isClose) {
//                try {
//                    in.close();
//                } catch (Exception e2) {
//                    System.out.println("关闭流失败");
//                }
//            }
//        }
//        return byteArray;
//    }


//    /**
//     * demoFile 模板文件
//     * newFile 生成文件
//     * map 要填充的数据
//     */
//    public void writeDoc(File demoFile, File newFile, Map<String, Object> map) {
//        try {
//            FileInputStream in = new FileInputStream(demoFile);
//            HWPFDocument hdt = new HWPFDocument(in);
//            // Fields fields = hdt.getFields();
//            // 读取word文本内容
//            Range range = hdt.getRange();
//            // System.out.println(range.text());
//            // 替换文本内容
//            for (Map.Entry<String, Object> entry : map.entrySet()) {
//                range.replaceText(entry.getKey(), entry.getValue().toString());
//            }
//            ByteArrayOutputStream ostream = new ByteArrayOutputStream();
//            FileOutputStream out = new FileOutputStream(newFile, true);
//            hdt.write(ostream);
//            // 输出字节流
//            out.write(ostream.toByteArray());
//            out.close();
//            ostream.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}