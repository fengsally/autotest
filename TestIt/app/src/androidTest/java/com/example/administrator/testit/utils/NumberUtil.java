package com.example.administrator.testit.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Created by vivian on 16/1/2.
 */
public class NumberUtil {
    static final String DEFAULT_NUMBER_STR = "一一";
    static final int DEF_DECIMAL_SCALE = 2; //精确到小数点后两位

    static final DecimalFormat formatIntegerMoney = new DecimalFormat("#,###");
    static final DecimalFormat formatMoney = new DecimalFormat("#,###.00");
    static final DecimalFormat formatPercent = new DecimalFormat("#.00");


    public static double add(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.add(b2).doubleValue();
    }

    public static Double add(String v1, String v2) {
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return b1.add(b2).doubleValue();
    }

    public static Double sub(String v1, String v2) {
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return b1.subtract(b2).setScale(DEF_DECIMAL_SCALE, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static double mul(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.multiply(b2).setScale(DEF_DECIMAL_SCALE, BigDecimal.ROUND_HALF_DOWN).doubleValue();
    }

    public static Double mul(String v1, Float v2) {
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(Float.toString(v2));
        return b1.multiply(b2).setScale(DEF_DECIMAL_SCALE, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static BigDecimal mul(BigDecimal v1, double v2) {
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return v1.multiply(b2).setScale(DEF_DECIMAL_SCALE, BigDecimal.ROUND_HALF_DOWN);
    }

    public static double div(double v1, double v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static double div(double v1, int v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static BigDecimal add(BigDecimal bigDecimal, String s) {
        return bigDecimal.add(format2BigDecimal(s));
    }

    public static BigDecimal substract(BigDecimal bigDecimal, String s) {
        return bigDecimal.subtract(format2BigDecimal(s));
    }

    public static double div(double v1, double v2) {
        return div(v1, v2, DEF_DECIMAL_SCALE);
    }

    public static double div(BigDecimal v1, double v2) {
        return div(v1.doubleValue(), v2);
    }

    public static Float format2Float(String v) {
        return Float.valueOf(v);
    }

    public static String formatString(String v) {
        String s=v;
        if(v.contains("￥")){
            s=v.replace("￥","");
        }else if(v.contains(",")){
            s=v.replace(",","");
        }else if(v.contains("元")){
            s=v.replace("元","");
        }
        return s;
    }

    public static Double format2Double(String v){return Double.valueOf(v);}

    public static int format2Integer(String v) {
        return Integer.valueOf(v);
    }

    public static double round(double v, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        BigDecimal b = new BigDecimal(Double.toString(v));
        BigDecimal one = new BigDecimal("1");
        return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static BigDecimal format2BigDecimal(String val) {
        return format2BigDecimal(val, new BigDecimal("0.0"));
    }

    public static BigDecimal format2BigDecimal(String val, BigDecimal def) {
        if (val == null) {
            return def;
        }

        val = val.replaceAll(",", "");
        if (!isNumber(val)) {
            return def;
        }

        BigDecimal decimal = new BigDecimal(val);
        return decimal.setScale(DEF_DECIMAL_SCALE, RoundingMode.HALF_UP);
    }

    public static String format2IntegerMoney(BigDecimal decimal) {
        if (decimal == null) {
            return DEFAULT_NUMBER_STR;
        }
        String res = formatIntegerMoney.format(decimal);
        return "".equals(res) ? "0" : res;
    }

    public static String format2Money(BigDecimal decimal) {
        if (decimal == null) {
            return DEFAULT_NUMBER_STR;
        }
        String decimalStr = formatMoney.format(decimal).equals(".00") ? "0.00" : formatMoney.format(decimal);
        if (decimalStr.startsWith(".")) {
            decimalStr = "0" + decimalStr;
        }
        return decimalStr;
    }

    public static boolean isDouble(String value) {
        try {
            Double.parseDouble(value);
            if (value.contains(".")) {
                return true;
            }
            return false;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isNumber(String value) {
        return isInteger(value) || isDouble(value);
    }

    public static int strToInt(String str, int defValue) {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
            return defValue;
        }
    }


}
