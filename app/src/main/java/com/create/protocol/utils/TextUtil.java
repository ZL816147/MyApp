package com.create.protocol.utils;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtil {

    private final static String SERVER_TIME_FORMAT = "yyyyMMddHHmmss";
    private final static String CLIENT_TIME_FORMAT = "yyyy-MM-dd HH:mm";

    /**
     * 文件大小格式化
     *
     * @param fileSize
     * @return
     */
    public static String formatFileSize(long fileSize) {
        DecimalFormat df = new DecimalFormat("0");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileSize == 0) {
            return wrongSize;
        }
        if (fileSize < 1024) {
            fileSizeString = df.format((double) fileSize) + "B";
        } else if (fileSize < 1048576) {
            fileSizeString = df.format((double) fileSize / 1024) + "KB";
        } else if (fileSize < 1073741824) {
            fileSizeString = df.format((double) fileSize / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileSize / 1073741824) + "GB";
        }
        return fileSizeString;
    }

    /**
     * 根据用户名的不同长度，来进行替换 ，达到保密效果
     *
     * @param userName 用户名
     * @return 替换后的用户名
     */
    public static String userNameReplaceWithStar(String userName) {
        if (userName == null) {
            userName = "";
        }
        if (!TextUtils.isEmpty(userName) && userName.length() >= 2) {
            StringBuilder sb = new StringBuilder();
            if (userName.length() == 2) {
                for (int i = 0; i < userName.length(); i++) {
                    char c = userName.charAt(i);
                    if (i == 0) {
                        sb.append('*');
                    } else {
                        sb.append(c);
                    }
                }
                return sb.toString();
            } else if (userName.length() > 2) {
                for (int i = 0; i < userName.length(); i++) {
                    char c = userName.charAt(i);
                    if (i >= 1 && i <= userName.length() - 2) {
                        sb.append('*');
                    } else {
                        sb.append(c);
                    }
                }
                return sb.toString();
            }
        }
        return "*";
    }

    /**
     * 手机号码加密
     *
     * @param pNumber
     * @return
     */
    public static String phoneNumberReplaceWithStar(String pNumber) {
        if (!TextUtils.isEmpty(pNumber) && pNumber.length() > 6) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < pNumber.length(); i++) {
                char c = pNumber.charAt(i);
                if (i >= 3 && i <= pNumber.length() - 4) {
                    sb.append('*');
                } else {
                    sb.append(c);
                }
            }
            return sb.toString();
        }
        return "";
    }

    /**
     * 紧急联系人加密
     *
     * @param
     * @return
     */
    public static String emergencyNameReplaceWithStar(String userName) {
        if (!TextUtils.isEmpty(userName) && userName.length() > 2) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < userName.length(); i++) {
                char c = userName.charAt(i);
                if (i >= 1 && i <= userName.length()) {
                    sb.append('*');
                } else {
                    sb.append(c);
                }
            }
            return sb.toString();
        }
        return "";
    }

    /**
     * 身份证号码加密
     *
     * @param cardNumber
     * @return
     */
    public static String cardNumberReplaceWithStar(String cardNumber) {
        if (!TextUtils.isEmpty(cardNumber) && cardNumber.length() > 14) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < cardNumber.length(); i++) {
                char c = cardNumber.charAt(i);
                if (i >= 3 && i <= cardNumber.length() - 5) {
                    sb.append('*');
                } else {
                    sb.append(c);
                }
            }
            return sb.toString();
        }
        return "";
    }

    /**
     * 截取字段
     *
     * @param
     * @return
     */
    public static String cutTitleStr(String str) {
        if (str != null) {
            if (str.length() > 10) {
                return str.substring(0, 10) + "...";
            } else {
                return str;
            }
        }
        return "";
    }

    /**
     * 版本号比较
     *
     * @param version1
     * @param version2
     * @return
     */
    public static int compareVersionName(String version1, String version2) {
        try {
            if (version1.equals(version2)) {
                return 0;
            }
            String[] version1Array = version1.split("\\.");
            String[] version2Array = version2.split("\\.");
            int index = 0;
            int minLen = Math.min(version1Array.length, version2Array.length);
            int diff = 0;
            while (index < minLen
                    && (diff = Integer.parseInt(version1Array[index]) - Integer.parseInt(version2Array[index])) == 0) {
                index++;
            }
            if (diff == 0) {
                for (int i = index; i < version1Array.length; i++) {
                    if (Integer.parseInt(version1Array[i]) > 0) {
                        return 1;
                    }
                }
                for (int i = index; i < version2Array.length; i++) {
                    if (Integer.parseInt(version2Array[i]) > 0) {
                        return -1;
                    }
                }
                return 0;
            } else {
                return diff > 0 ? 1 : -1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 版本号比较
     *
     * @param version1
     * @param version2
     * @return
     */
    public static int compareVersionCode(int version1, int version2) {
        if (version1 == version2) {
            return 0;
        }
        if (version1 != version2) {
            return -1;
        }
        return 0;
    }

    /**
     * 价格格式化
     *
     * @param price
     * @return
     */
    public static String formatPrice(int price) {
        DecimalFormat df = new DecimalFormat("0.00");
        String fileSizeString = df.format((double) (price / 100.0));
        return fileSizeString;
    }

    /**
     * 下载进度百分比
     *
     * @param total
     * @param loaded
     * @return
     */
    public static String formatLoadPercent(int total, int loaded) {
        int progress = 0;
        if (total > 0 && loaded > 0) {
            progress = (int) ((((float) loaded) / ((float) total)) * 100);
            return progress + "%";
        }
        return 0 + "%";
    }

    /**
     * 提取数字
     */
    public static String getNumberFromString(String content) {
        String regEx = "[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(content);
        return m.replaceAll("").trim();
    }

    /**
     * 日期格式转换 20160101转换成2016-01-01
     */
    public static String getFormatDateFromString(String dateString) {
        StringBuffer dateBuffer = new StringBuffer(dateString);
        dateBuffer.insert(4, "-");
        dateBuffer.insert(7, "-");
        return dateBuffer.toString();
    }

    /**
     * 判断是否是正确密码
     *
     * @param mobiles
     * @return
     */
    public static boolean isFormatPassword(String mobiles) {
        Pattern p = Pattern.compile("(?!^[0-9]*$)(?!^[_@#]*$)(?!^[a-zA-Z]*$)^([a-zA-Z0-9_@#]{6,20})$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    /**
     * 时间的格式化
     *
     * @param time
     * @return
     */
    public static String tryFormatDate(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat();
        String timeResult = null;
        try {
            sdf.applyPattern(SERVER_TIME_FORMAT);
            Date date = sdf.parse(time);
            sdf.applyPattern(CLIENT_TIME_FORMAT);
            timeResult = sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (timeResult == null) {
            sdf.applyPattern(CLIENT_TIME_FORMAT);
            timeResult = sdf.format(new Date(System.currentTimeMillis()));
        }
        return timeResult;
    }

    /**
     * @param msg json內容
     * @param def
     * @return
     */
    public static int parseJsonResultCode(String msg, int def) {
        if (msg == null || msg.length() <= 0) {
            return def;
        }
        int start = msg.indexOf("\"code\"");
        if (start < 0) {
            return def;
        }
        StringBuilder sb = new StringBuilder();
        boolean contentStated = false;
        for (int i = start; i < msg.length(); i++) {
            char c = msg.charAt(i);
            if (contentStated) {
                if (c == '\"') {
                    continue;
                } else if (c == ',' || c == '}') {
                    break;
                }
                sb.append(c);
            } else if (c == ':') {
                contentStated = true;
            }
        }
        try {
            return Integer.valueOf(sb.toString());
        } catch (NumberFormatException e) {
            return def;
        }
    }

    /**
     * 多个关键字高亮变色
     *
     * @param color   变化的色值
     * @param text    文字
     * @param keyword 文字中的关键字数组
     * @return
     */
    public static SpannableString matcherSearchTitle(int color, String text, String keyword) {
        SpannableString s = new SpannableString(text);
        Pattern p = Pattern.compile(keyword);
        Matcher m = p.matcher(s);
        while (m.find()) {
            int start = m.start();
            int end = m.end();
            s.setSpan(new ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return s;
    }

    /**
     * 项目中红包提取数字
     *
     * @param text
     * @return
     */
    public static String replaceAndFilterNumber(String text) {
        String regEx = "[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(text);
        return m.replaceAll("").trim();
    }

    /**
     * 去空格
     * @param str
     * @return
     */
    public static String removeAllSpace(String str) {
        return str.replace(" ", "");
    }
}
