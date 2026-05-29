package com.jycloud.tool.common.util;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.Locale;

/**
 * 时间工具类
 */
public class DateUtil {
    /**
     * 时间格式(yyyy-MM-dd)
     */
    public final static String DATE_PATTERN = "yyyy-MM-dd";
    /**
     * 时间格式(yyyy-MM-dd HH:mm:ss)
     */
    public final static String FULL_TIME_SPLIT_PATTERN = "yyyy-MM-dd HH:mm:ss";
    /**
     * 时间格式(yyyyMMddHHmmss)
     */
    public static final String FULL_TIME_PATTERN = "yyyyMMddHHmmss";



    public static String formatFullTime(LocalDateTime localDateTime) {
        return formatFullTime(localDateTime, FULL_TIME_PATTERN);
    }

    public static String formatFullTime(LocalDateTime localDateTime, String pattern) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        return localDateTime.format(dateTimeFormatter);
    }

    private static String getDateFormat(Date date, String dateFormatType) {
        SimpleDateFormat simformat = new SimpleDateFormat(dateFormatType);
        return simformat.format(date);
    }

    public static String formatCSTTime(String date, String format) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
        Date d = sdf.parse(date);
        return DateUtil.getDateFormat(d, format);
    }

    /**
     * 获取当前时间戳（精确到秒）
     *
     * @return
     */
    public static Integer nowTimeStamp() {
        return (int) (System.currentTimeMillis() / 1000);
    }

    /**
     * 获取指定时间戳（精确到秒）
     *
     * @return
     */
    public static Integer specTimeStamp(Date date) {
        return (int) (date.getTime() / 1000);
    }

    /**
     * 字符串时间格式,转时间戳
     * @param str "2019-09-16 09:40:19"
     * @param format "yyyy-MM-dd HH:mm:ss"
     * @return
     */
    public static Integer stringTimeToTimestamp(String str,String format){
        if(StringUtils.isNotBlank(str)){
            if (format == null || format.isEmpty()) {
                format = "yyyy-MM-dd HH:mm:ss";
            }
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            //字符串转日期
            try {
                Date date = sdf.parse(str);
                return specTimeStamp(date);
            }catch (Exception e){
                return null;
            }
        }
        return null;
    }

    /**
     * * 时间戳转换成日期格式字符串
     * * @param seconds 精确到秒
     * * @param formatStr
     * * @return
     */
    public static String timeStamp2Date(Integer seconds, String format) {
        if (format == null || format.isEmpty()) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(Long.valueOf(seconds + "000")));
    }

    /**
     * 验证日期格式是否满足要求
     *
     * @param str          需要验证的日期格式
     * @param formatString 验证的标准格式，如：（yyyy/MM/dd HH:mm:ss）
     * @return 返回验证结果
     */
    public static boolean isValidDate(String str, String formatString) {
        // 指定日期格式，注意yyyy/MM/dd区分大小写；
        SimpleDateFormat format = new SimpleDateFormat(formatString);
        try {
            // 设置lenient为false.
            // 否则SimpleDateFormat会比较宽松地验证日期，比如2007/02/29会被接受，并转换成2007/03/01
            format.setLenient(false);
            format.parse(str);
        } catch (ParseException e) {
            // e.printStackTrace();
            // 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
            return false;
        }
        return true;
    }

    /**
     * 日期格式化 日期格式为：yyyy-MM-dd
     *
     * @param date    日期
     * @param pattern 格式，如：DateUtil.DATE_TIME_PATTERN
     * @return 返回yyyy-MM-dd格式日期
     */
    public static String format(Date date, String pattern) {
        if (date != null) {
            SimpleDateFormat df = new SimpleDateFormat(pattern);
            return df.format(date);
        }
        return null;
    }

    /**
     * 获取今天的日期
     *
     * @return 时间
     */
    public static String today() {
        return format(new Date(), "yyyyMMdd");
    }

    /**
     * 获取指定间隔时间后的时间
     *
     * @param date
     * @param minute 间隔时间 分钟
     * @return
     */
    public static Date getIntervalTimeAfterDate(Date date, Integer minute){
        long currentTimeMillis = date.getTime();
        long newDateTimeMillis = currentTimeMillis + minute * 60 * 1000;
        Date newDateTime = new Date(newDateTimeMillis);
        return newDateTime;
    }

    // 本月/上月判断

    /**
     * 判断是否为本月数据
     */
    public static boolean isCurrentMonth(LocalDateTime dateTime) {
        if (dateTime == null) {
            return false;
        }
        YearMonth current = YearMonth.now();
        YearMonth target = YearMonth.from(dateTime);
        return current.equals(target);
    }

    /**
     * 判断是否为上月数据
     */
    public static boolean isLastMonth(LocalDateTime dateTime) {
        if (dateTime == null) {
            return false;
        }
        YearMonth last = YearMonth.now().minusMonths(1);
        YearMonth target = YearMonth.from(dateTime);
        return last.equals(target);
    }

    /**
     * 获取本月起始时间（YYYY-MM-01 00:00:00）
     */
    public static LocalDateTime getCurrentMonthStart() {
        return LocalDateTime.now()
                .with(TemporalAdjusters.firstDayOfMonth())
                .withHour(0).withMinute(0).withSecond(0).withNano(0);
    }

    /**
     * 获取本月结束时间（YYYY-MM-31 23:59:59）
     */
    public static LocalDateTime getCurrentMonthEnd() {
        return LocalDateTime.now()
                .with(TemporalAdjusters.lastDayOfMonth())
                .withHour(23).withMinute(59).withSecond(59).withNano(999999999);
    }

    /**
     * 获取上月起始时间
     */
    public static LocalDateTime getLastMonthStart() {
        return LocalDateTime.now()
                .minusMonths(1)
                .with(TemporalAdjusters.firstDayOfMonth())
                .withHour(0).withMinute(0).withSecond(0).withNano(0);
    }

    /**
     * 获取上月结束时间
     */
    public static LocalDateTime getLastMonthEnd() {
        return LocalDateTime.now()
                .minusMonths(1)
                .with(TemporalAdjusters.lastDayOfMonth())
                .withHour(23).withMinute(59).withSecond(59).withNano(999999999);
    }

    /**
     * 获取指定月份起始时间
     */
    public static LocalDateTime getMonthStart(YearMonth yearMonth) {
        return yearMonth.atDay(1).atStartOfDay();
    }

    /**
     * 获取指定月份结束时间
     */
    public static LocalDateTime getMonthEnd(YearMonth yearMonth) {
        return yearMonth.atEndOfMonth().atTime(23, 59, 59, 999999999);
    }

}
