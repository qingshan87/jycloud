package com.jycloud.tool.common.util;


import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidatorUtil {

    /**
     * 账号验证;
     * 账号必须以字母开头，后面可以是字母、数字和下划线，总长度在6到20个字符之间
     *
     * @param account
     * @return
     */
    public static boolean isValidAccount(String account) {
        String pattern = "^[a-zA-Z][a-zA-Z0-9_]{5,19}$";
        return account.matches(pattern);
    }

    /**
     * 判断字符串是否符合手机号码格式
     * 移动号段: 134,135,136,137,138,139,147,150,151,152,157,158,159,170,178,182,183,184,187,188,198
     * 联通号段: 130,131,132,145,155,156,170,171,175,176,185,186,196,166
     * 电信号段: 133,149,153,170,173,177,180,181,189,199
     *
     * @param mobile
     * @return
     */
    public static boolean isMobile(String mobile) {
        String telRegex = "^((13[0-9])|(14[5,7,9])|(15[^4])|(16[^4])|(18[0-9])|(19[0-9])|(17[0,1,3,5,6,7,8]))\\d{8}$";
        if (StringUtils.isEmpty(mobile))
            return false;
        else
            return mobile.matches(telRegex);
    }

    public static boolean isEmail(String email) {
        if (null == email || "".equals(email)) {
            return false;
        }
        String regEx1 = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern p = Pattern.compile(regEx1);
        Matcher m = p.matcher(email);
        if (m.matches()) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 判断网络图片是否存在
     * posturl 图片地址链接
     */
    public static boolean isImagesTrue(String imgurl) {
        if (StringUtils.isNotBlank(imgurl)) {
            try {
                URL url = new URL(imgurl);
                HttpURLConnection urlcon = (HttpURLConnection) url.openConnection();
                if (urlcon.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    System.out.println(HttpURLConnection.HTTP_OK + imgurl
                            + ":posted ok!");
                    return true;
                } else {
                    System.out.println(urlcon.getResponseCode() + imgurl
                            + ":Bad post...");
                    return false;
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        return false;
    }

    /**
     * 获取URL链接的文件类型
     *
     * @param fileUrl
     * @return
     */
    public static String getFileType(String fileUrl) {

        if (StringUtils.isNotBlank(fileUrl)) {
            try {
                URL url = new URL(fileUrl);
                HttpURLConnection urlcon = (HttpURLConnection) url.openConnection();
                if (urlcon.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    return urlcon.getContentType();
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        return null;
    }

    /**
     * 判断value是否在interval中
     *
     * @param value    1
     * @param interval 1,2,3
     * @return
     */
    public static boolean isInTheInterval(String value, String interval) {
        if (StringUtils.isNotBlank(value) && StringUtils.isNotBlank(interval)) {
            List<String> list = Arrays.asList(interval.split(","));
            if (list != null && list.size() > 0 && list.contains(value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断数字在一定范围内
     *
     * @param value
     * @param intervals
     * @return
     */
    public static boolean isInTheInterval(String value, String[] intervals) {
        if (StringUtils.isNotBlank(value) && intervals != null && intervals.length == 2) {
            if (StringUtils.isBlank(intervals[0]) && StringUtils.isBlank(intervals[1])) {
                return false;
            }
            if (!isNumericInt(value) || (StringUtils.isNotBlank(intervals[0]) && !isNumericInt(intervals[0]))
                    || (StringUtils.isNotBlank(intervals[1]) && !isNumericInt(intervals[1]))) {
                return false;
            }

            int valueInt = Integer.parseInt(value);
            if (StringUtils.isBlank(intervals[0])) {
                int max = Integer.parseInt(intervals[1]);
                System.out.println("valueInt < max :" + max);
                return valueInt < max ? true : false;
            } else if (StringUtils.isBlank(intervals[1])) {
                int min = Integer.parseInt(intervals[0]);
                System.out.println("valueInt > min :" + min);
                return valueInt > min ? true : false;
            } else {
                int max = Integer.parseInt(intervals[1]);
                int min = Integer.parseInt(intervals[0]);
                System.out.println("valueInt > min && valueInt < max :" + min + "/" + max);
                return valueInt > min && valueInt < max ? true : false;
            }
        }
        return false;
    }

    /**
     * 判断是否是整数
     *
     * @param str
     * @return
     */
    public static boolean isNumericInt(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    /**
     * 判断字符串是否为整数或者小数
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {

        Pattern pattern = Pattern.compile("[-+]?\\d+(\\.\\d+)?");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    /**
     * 判断字符串是否等于零
     * 示例: 0 | 0.00 | -0.00
     *
     * @param str
     * @return
     */
    public static boolean isZero(String str) {
        if (isNumeric(str)) {
            BigDecimal zero = BigDecimal.ZERO;
            BigDecimal bigDecimal = new BigDecimal(str);
            if (bigDecimal.compareTo(zero) == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 字符串是否是base64
     *
     * @param str
     * @return
     */
    public static boolean isBase64(String str) {
        String base64Pattern = "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)$";
        return Pattern.matches(base64Pattern, str);
    }

    /**
     * 判断字符串中是否全是中文
     *
     * @param str
     * @return
     */
    public static boolean isAllChinese(String str) {
        if (str == null) {
            return false;
        }
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]+");
        Matcher m = p.matcher(str);
        return m.matches();
    }


    /**
     * 判断字符串中是否包含中文
     *
     * @param str 待校验字符串
     * @return 是否为中文
     * @warn 不能校验是否为中文标点符号
     */
    public static boolean isContainsChinese(String str) {
        if (str == null) {
            return false;
        }
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        return m.find();
    }

    /**
     * 判断字符串长度
     *
     * @param str
     * @param min
     * @param max
     * @return
     */
    public static boolean isStringLength(String str, Integer min, Integer max) {
        if (str == null || (min == null && max == null)) {
            return false;
        }

        int strLength = str.length();
        if (min == null && max != null) {
            // 判断最大值
            return strLength <= max;
        } else if (min != null && max == null) {
            return strLength >= min;
        } else {
            return strLength >= min && strLength <= max;
        }

    }

    /**
     * 判断字符串只能用中文、英文、数字组成
     *
     * @param str
     * @return
     */
    public static boolean isChineseEnglishDigitOnly(String str) {
        return str.matches("^[a-zA-Z\u4e00-\u9fa5\\d]+$");
    }

    /**
     * 判断字符串只能用中文、英文、数字组成,并且不能以数字开头
     *
     * @param str
     * @return
     */
    public static boolean isChineseEnglishDigitString(String str) {
        // 正则表达式：以非数字开头，后面跟中文、英文字母和数字的组合
        return !str.isEmpty() && !str.matches("^[0-9].*") && str.matches("[a-zA-Z0-9\u4e00-\u9fa5]+");
    }


    public static void main(String[] args) {


    }

}
