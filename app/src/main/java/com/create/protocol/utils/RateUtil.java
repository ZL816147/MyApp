package com.create.protocol.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * 汇率计算的工具类
 *
 * @author qian.liu
 */
public class RateUtil {

    // 默认除法运算精度
    private static final int DEFAULT_DIV_SCALE = 1;

    /**
     * 按月还息到期还本 金额×年利率×月数 等额本息计算方式
     *
     * @param rate
     * @param bifee
     * @param time
     * @param type
     * @return
     */
    public static double getEqualityInterest(int rate, double bifee, int time, int type, int repaytype) {
        double rest = 0;
        double money = 0;
        double interest = 0;
        if (repaytype == 0) {
            if (0 == type) {
                return formatDoublePrice(bifee * (double) rate / 10000 * time / 365);
            } else if (1 == type) {
                return formatDoublePrice(bifee * (double) rate / 10000 * time / 12);
            }
        } else if (repaytype == 1) {
            for (int i = 1; i < time; i++) {
                double moneythis = formatDoublePrice(
                        (bifee * ((double) rate / 120000) * Math.pow((1f + ((double) rate / 120000)), (double) time))
                                / (Math.pow(1 + ((double) rate / 120000), (double) time) - 1));
                money += moneythis;
                double restthis = formatDoublePrice(
                        (bifee * ((double) rate / 120000) * Math.pow((1f + ((double) rate / 120000)), (double) (i - 1)))
                                / (Math.pow((1 + ((double) rate / 120000)), (double) (time)) - 1));
                rest += restthis;
                interest += moneythis - restthis;
            }
            return interest += formatDoublePrice((bifee - rest) * (double) rate / 10000 / 12);
        }
        return formatDoublePrice(bifee * (double) rate / 10000 * (double) time / 12);
    }

    /**
     * 提供精确的乘法运算。
     *
     * @param v1
     * @param v2
     * @return 两个参数的积
     */

    public static double multiply(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.multiply(b2).floatValue();
    }

    /**
     * 提供精确的减法运算
     *
     * @param v1
     * @param v2
     * @return 两个参数数学差，以字符串格式返回
     */

    public static String subtract(String v1, String v2) {
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return b1.subtract(b2).toString();
    }

    /**
     * 提供精确的加法运算
     *
     * @param d1
     * @param d2
     * @return
     */
    public static float add(float d1, float d2) { // 进行加法运算
        BigDecimal b1 = new BigDecimal(d1);
        BigDecimal b2 = new BigDecimal(d2);
        return b1.add(b2).floatValue();
    }

    /**
     * 等额本息计算方式
     *
     * @param rate
     * @param bifee
     * @param time
     * @return
     */
    public static double getEqualityInterest(int rate, long bifee, int time) {
        double rest = 0;
        double money = 0;
        double interest = 0;
        for (int i = 0; i < time; i++) {
            long moneythis = Math.round((bifee * (rate / 120000) * Math.pow((1 + (rate / 120000)), time))
                    / (Math.pow(1 + (rate / 120000), time) - 1));
            money += moneythis;
            long restthis = Math.round((bifee * (rate / 120000) * Math.pow((1 + (rate / 120000)), (i - 1)))
                    / (Math.pow((1 + (rate / 120000)), (time)) - 1));
            rest += restthis;
            interest += moneythis - restthis;
        }
        return interest += Math.round((bifee - rest) * rate / 10000 / 12);
    }

    /**
     * <计算等额本息月利息>
     *
     * @param rate       年利率
     * @param bifee      投资总额
     * @param totalmonth 总月数
     * @return 返回等额本息某月数的本息 本项计算公式由范世荣提供可见确认邮件 .先按BX=a*i(1+i)^N/[(1+i)^N-1] 公式算出
     * [1~（N-1）]个月帐期的待还本息； （注：BX=等额本息还贷每月所还本金和利息总额， B=等额本息还贷每月所还本金，
     * a=贷款总金额 i=贷款月利率， N=还贷总月数， n=第n期还贷数 X=等额本息还贷每月所还的利息）
     */
    public static long getEqualityInterestAndCorpus(Integer rate, Long bifee, Integer totalmonth) {
        double rateDouble = ((double) rate) / 10000 / 12;
        Long interestandCorpus = Math
                .round(((double) bifee * (rateDouble) * Math.pow((1d + rateDouble), (double) totalmonth))
                        / (Math.pow(1d + rateDouble, (double) totalmonth) - 1d));
        return interestandCorpus;
    }

    /**
     * <计算待还本金>
     *
     * @param rate       利率
     * @param bifee      投资总额
     * @param month      当前月数
     * @param totalmonth 总月数
     * @return 当前月还的本金 B=a*i(1+i)^(n-1)/[(1+i)^N-1]
     */
    public long getEqualityCorpus(Integer rate, Long bifee, Integer month, Integer totalmonth) {
        double rateDouble = ((double) rate) / 10000 / 12;
        Long corpus = Math.round(((double) bifee * rateDouble * Math.pow((1d + rateDouble), ((double) month - 1d)))
                / (Math.pow((1d + rateDouble), ((double) totalmonth)) - 1d));
        return corpus;
    }

    /**
     * <按月还息到期还本> 金额×年利率×月数
     *
     * @param rate      利息
     * @param lastbifee 本金
     * @return 当月应还利息 X=BX-B
     */
    public static long getEqualityInterest(Integer rate, Long lastbifee, int month) {
        double rateDouble = ((double) rate) / 10000 / 12;
        Long corpus = Math.round((double) lastbifee * rateDouble * month);
        return corpus;
    }

    /**
     * <获取最后一个月的利息>
     *
     * @param rate      利息
     * @param lastbifee 本金
     * @return 当月应还利息 X=BX-B
     */
    public long getEqualityInterest(Integer rate, Long lastbifee) {
        double rateDouble = ((double) rate) / 10000 / 12;
        Long corpus = Math.round((double) lastbifee * rateDouble);
        return corpus;
    }

    /**
     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指 定精度，以后的数字四舍五入。舍入模式采用ROUND_HALF_EVEN
     *
     * @param v1
     * @param v2
     * @param scale 表示需要精确到小数点以后几位
     * @return 两个参数的商，以字符串格式返回
     */
    public static String divide(String v1, String v2, int scale) {
        return divide(v1, v2, DEFAULT_DIV_SCALE, BigDecimal.ROUND_HALF_EVEN);
    }

    /**
     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指 定精度，以后的数字四舍五入。舍入模式采用用户指定舍入模式
     *
     * @param divider
     * @param devidered
     * @param scale      表示需要精确到小数点以后几位
     * @param round_mode 表示用户指定的舍入模式
     * @return 两个参数的商，以字符串格式返回
     */
    public static String divide(String divider, String devidered, int scale, int round_mode) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(divider);
        BigDecimal b2 = new BigDecimal(devidered);
        BigDecimal b3 = b1.add(b2).stripTrailingZeros();
        if (b3.compareTo(BigDecimal.ZERO) == 0) {
            return b1.divide(b2, 0, round_mode).toString();
        } else {
            return b1.divide(b2, scale, round_mode).toString();
        }
    }

    /**
     * 项目中投资列表等金额 整数转换 金额，1000代表10.00 格式为XX.00元
     *
     * @param value
     * @return
     */
    public static String formatPrice(long value) {
        NumberFormat currency = NumberFormat.getCurrencyInstance();
        currency.setMinimumFractionDigits(2);
        return currency.format(value);
    }

    /**
     * 项目中投资列表等金额 整数转换 金额，1000代表10.00 格式为XX.00元
     *
     * @param value
     * @return
     */
    public static String formatPrice(float value) {
        NumberFormat currency = NumberFormat.getInstance();
        currency.setMinimumFractionDigits(2);
        currency.setMaximumFractionDigits(2);
        return currency.format(value);
    }

    /**
     * 项目中投资列表等金额 整数转换 金额，1000代表10.00 格式为XX.00元
     *
     * @param value
     * @return
     */
    public static String formatPrice(double value) {
        NumberFormat currency = NumberFormat.getInstance();
        currency.setMinimumFractionDigits(2);
        currency.setMaximumFractionDigits(2);
        return currency.format(value);
    }

    /**
     * 投标 全投等情况下金额的转换，转换保留两位小数
     *
     * @param value
     * @return
     */
    public static String formatAllBidPrice(float value) {
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(value);
    }

    /**
     * 投标 全投等情况下金额的转换，转换保留两位小数
     *
     * @param value
     * @return
     */
    public static String formatAllBidPrice(double value) {
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(value);
    }

    /**
     * 根据小数转换成百分比
     *
     * @param decimal
     * @return
     */
    public static String formatPercentValue(double decimal) {
        BigDecimal bigdec = new BigDecimal(decimal).multiply(new BigDecimal(100)).setScale(1, BigDecimal.ROUND_HALF_UP);
        return bigdec + "%";
    }

    /**
     * 根据double值精确计算金额
     *
     * @param price
     * @return
     */
    public static double formatDoublePrice(double price) {
        BigDecimal b = new BigDecimal(price);
        double f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        return f1;
    }

    /**
     * 去掉后面无用的零 如小数点后面全是零则去掉小数点
     *
     * @param value
     * @return
     */
    public static String removeZeroAndParseDouble(String value) {
        if (value.indexOf(".") > 0) {
            value = value.replaceAll("0+?$", "");
            value = value.replaceAll("[.]$", "");
        }
        return value;
    }

}
