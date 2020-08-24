package com.rockbb.sms.commons;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Time utility methods
 *
 * Created by Milton on 2015/8/21 at 18:53.
 */
public class TimeUtil
{
    public static final int TIMEZONE_OFFSET = 1000 * 3600 * 8;

    public static final String FORMAT_YMD = "yyyy-MM-dd";
    public static final String FORMAT_YM = "yyyy-MM";
    public static final String FORMAT_YMD_HM = "yyyy-MM-dd HH:mm";
    public static final String FORMAT_YMD_HMS = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_HM = "HH:mm";

    public static final String FORMAT_SHORT_YMD = "yyyyMMdd";
    public static final String FORMAT_SHORT_YMDH = "yyyyMMddHH";
    public static final String FORMAT_SHORT_YMDHMS = "yyyyMMddHHmmss";
    public static final String FORMAT_SHORT_YMDHMSS = "yyyyMMddHHmmssSSS";
    public static final String FORMAT_SHORT_YMD_HM = "yyyyMMdd HHmm";
    public static final String FORMAT_SHORT_YYMMDHM = "yyMMddHHmm";
    public static final String FORMAT_SHORT_HHMMSS = "HHmmss";

    public static final String FORMAT_ZH_YMD = "yyyy年MM月dd日";
    public static final String FORMAT_ZH_YMD_HM = "yyyy年MM月dd日 HH:mm";

    public static final String REGEX_YMD = "(19|20)\\d\\d-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])";
    public static final String REGEX_YMD_HM = "(19|20)\\d\\d-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01]) ([0-1][0-9]|[2][0-3]):([0-5][0-9])";
    public static final String REGEX_YMD_HMS = "(19|20)\\d\\d-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01]) ([0-1][0-9]|[2][0-3]):([0-5][0-9]):([0-5][0-9])";

    /**
     * 使用已知的日期格式, 还原日期字符串至日期对象, 出错则返回空
     */
    public static Date getDate(String strDate, String format) {
        Date date = null;
        if (strDate != null && strDate.length() > 0) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                date = sdf.parse(strDate);
            } catch (Exception ex) {
            }
        }
        return date;
    }

    /**
     * Format current time with specified format, using default locale
     */
    public static String getStr(String format) {
        return getStr(new Date(), format, null);
    }

    /**
     * Format a date with specified format, using default locale
     */
    public static String getStr(Date date, String format) {
        return getStr(date, format, null);
    }

    /**
     * Format a date with specified format and locale
     */
    public static String getStr(Date date, String format, Locale locale) {
        if (date != null) {
            SimpleDateFormat dFormat;
            if (locale != null)
                dFormat = new SimpleDateFormat(format, locale);
            else
                dFormat = new SimpleDateFormat(format);

            return dFormat.format(date);
        } else {
            return "";
        }
    }

    public static int getDay() {
        return getDay(null);
    }

    public static int getDay(Date date) {
        Calendar cal = Calendar.getInstance();
        if (date != null)
            cal.setTime(date);
        return cal.get(Calendar.YEAR) * 10000 + (cal.get(Calendar.MONTH) + 1) * 100 + cal.get(Calendar.DATE);
    }

    /**
     * 获取日期分解得到的整数数组, 分别代表日, 月, 年
     *
     * @return int[]{day, month, year}
     */
    public static int[] getDayInts(Date date) {
        Calendar cal = Calendar.getInstance();
        if (date != null)
            cal.setTime(date);
        return new int[]{cal.get(Calendar.DATE), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR)};
    }

    /**
     * 获取两个时间点之间的天数
     *
     * @param timeZoneOffset 标准时间与当地时间的时差, 毫秒数
     */
    public static int getDaysDiff(int timeZoneOffset, Date from, Date to) {
        if (from == null || to == null) return 0;
        long daysFrom = (from.getTime() + timeZoneOffset) / (1000L * 3600 * 24);
        long daysTo = (to.getTime() + timeZoneOffset) / (1000L * 3600 * 24);
        return (int)(daysTo - daysFrom);
    }

    /**
     * 获取两个时间点之间的天数, 使用默认+8时区
     *
     */
    public static int getDaysDiff(Date from, Date to) {
        return getDaysDiff(TIMEZONE_OFFSET, from, to);
    }

    /**
     * 生成指定日期的准确开始和结束时间, 从0:00.000到23:59:999
     *
     * @param date 如果为空, 则自动获取当前时间
     * @return 包含两个时间值的数组
     */
    public static Date[] getDayRangeOfDate(Date date) {
        Date[] range = new Date[2];

        if (date == null)
            date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        floor(cal, Calendar.HOUR_OF_DAY);
        range[0] = cal.getTime();
        ceiling(cal, Calendar.HOUR_OF_DAY);
        range[1] = cal.getTime();
        return range;
    }

    /**
     * 获取指定时间日期的开始时间
     *
     * @param date 如果为空, 则自动获取当前时间
     * @return 日期时间的左边界
     */
    public static Date getDayStart(Date date) {
        if (date == null)
            date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        floor(cal, Calendar.HOUR_OF_DAY);
        return cal.getTime();
    }

    /**
     * 获取指定时间日期的结束时间
     *
     * @param date 如果为空, 则自动获取当前时间
     * @return 日期时间的右边界
     */
    public static Date getDayEnd(Date date) {
        if (date == null)
            date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        ceiling(cal, Calendar.HOUR_OF_DAY);
        return cal.getTime();
    }

    /**
     * 将连续时间按每隔4秒定位, 用于缓存利用
     * @param date 时间
     * @param hours 小时偏移量, 负数表示时间提前
     * @return 修剪后的时间
     */
    public static Date clipIn4Seconds(Date date, int hours) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        if (hours != 0) {
            cal.add(Calendar.HOUR_OF_DAY, hours);
        }
        int second = cal.get(Calendar.SECOND);
        second = second >> 2 << 2;
        cal.set(Calendar.SECOND, second);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * 生成一系列的月份开始和结束日期
     *
     * @param offset 开始距现在的月份数
     * @param months 输入多少个月, 负数表示输出的是过去的月份
     * @return 按时间顺序排列的列表, 每个节点是一个时间数组, 包含两个时间值, 代表月份的起始和结束时间
     */
    public static List<Date[]> getDateRangeByMonth(int offset, int months) {
        List<Date[]> ranges = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, offset);
        if (months < 0) {
            cal.add(Calendar.MONTH, months + 1);
            months = 0 - months;
        }

        for (int i = 0; i < months; i++) {
            if (i > 0)
                cal.add(Calendar.MONTH, 1);
            Date[] range = new Date[2];
            floor(cal, Calendar.DATE);
            range[0] = cal.getTime();
            ceiling(cal, Calendar.DATE);
            range[1] = cal.getTime();
            ranges.add(range);
        }

        return ranges;
    }

    private static void floor(Calendar cal, int indicator) {
        switch (indicator) {
            case Calendar.DATE:
                cal.set(Calendar.DATE, cal.getMinimum(Calendar.DATE));
            case Calendar.HOUR_OF_DAY:
                cal.set(Calendar.HOUR_OF_DAY, 0);
            case Calendar.MINUTE:
                cal.set(Calendar.MINUTE, 0);
            case Calendar.SECOND:
                cal.set(Calendar.SECOND, 0);
            case Calendar.MILLISECOND:
                cal.set(Calendar.MILLISECOND, 0);
        }
    }

    private static void ceiling(Calendar cal, int indicator) {
        switch (indicator) {
            case Calendar.DATE:
                cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
            case Calendar.HOUR_OF_DAY:
                cal.set(Calendar.HOUR_OF_DAY, cal.getActualMaximum(Calendar.HOUR_OF_DAY));
            case Calendar.MINUTE:
                cal.set(Calendar.MINUTE, cal.getActualMaximum(Calendar.MINUTE));
            case Calendar.SECOND:
                cal.set(Calendar.SECOND, cal.getActualMaximum(Calendar.SECOND));
            case Calendar.MILLISECOND:
                cal.set(Calendar.MILLISECOND, 0);// MySQL不能存储毫秒, 设为0以确保MySQL存储为当天
        }
    }

    /**
     * 得到某月的开始时间
     */
    public static Date getLeftBoundaryOfMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        floor(cal, Calendar.DATE);
        return cal.getTime();
    }

    /**
     * 得到某年某月的开始时间
     */
    public static Date getLeftBoundaryOfMonth(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.DAY_OF_MONTH, cal.getMinimum(Calendar.DATE));
        return getDayRangeOfDate(cal.getTime())[0];
    }

    /**
     * 得到某月的结束时间
     */
    public static Date getRightBoundaryOfMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        ceiling(cal, Calendar.DATE);
        return cal.getTime();
    }

    /**
     * 得到某年某月的结束时间
     */
    public static Date getRightBoundaryOfMonth(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        return getDayRangeOfDate(cal.getTime())[1];
    }

    /**
     * 获取指定日期偏移add天后的日期
     * @param add 增加的天数, 如果为负则往前移
     * @param boundary -1:到日期左边界, 0:不变, 1:到日期右边界
     */
    public static Date shiftByDay(Date date, int add, int boundary) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, add);
        switch (boundary) {
            case -1:
                floor(cal, Calendar.HOUR_OF_DAY);
                break;
            case 1:
                ceiling(cal, Calendar.HOUR_OF_DAY);
                break;
        }
        return cal.getTime();
    }

    /**
     * 获取指定时间偏移add分钟后的时间
     * @param add 增加的分钟数, 如果为负则往前移
     * @param boundary -1:到分钟左边界, 0:不变, 1:到分钟右边界
     */
    public static Date shiftByMinute(Date date, int add, int boundary) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MINUTE, add);
        switch (boundary) {
            case -1:
                floor(cal, Calendar.SECOND);
                break;
            case 1:
                ceiling(cal, Calendar.SECOND);
                break;
        }
        return cal.getTime();
    }


    /**
     * 获取指定时间偏移add秒后的时间
     * @param add 增加的秒数, 如果为负则往前移
     */
    public static Date shiftBySecond(Date date, int add) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.SECOND, add);
        return cal.getTime();
    }

    /**
     * 获取指定时间偏移add小时后的时间
     * @param add 增加的小时数, 如果为负则往前移
     * @param boundary -1:到小时左边界, 0:不变, 1:到小时右边界
     */
    public static Date shiftByHour(Date date, int add, int boundary) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.HOUR_OF_DAY, add);
        switch (boundary) {
            case -1:
                floor(cal, Calendar.MINUTE);
                break;
            case 1:
                ceiling(cal, Calendar.MINUTE);
                break;
        }
        return cal.getTime();
    }

    /**
     * 获取指定日期偏移add个月后的天数
     *
     * @param add 增加的月数, 如果为负则往前移
     * @param day 指定日期, 如果&lt;=0则不变
     * @param boundary -1:到日期左边界, 0:不变, 1:到日期右边界
     */
    public static Date shiftByMonth(Date date, int add, int day, int boundary){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, add);
        if (day > 0) {
            cal.set(Calendar.DATE, day);
        }
        switch (boundary) {
            case -1:
                floor(cal, Calendar.HOUR_OF_DAY);
                break;
            case 1:
                ceiling(cal, Calendar.HOUR_OF_DAY);
                break;
        }
        return cal.getTime();
    }

    /**
     * 查询指定时间段内的季度
     *
     * @param sort  true 倒序， false 正序
     */
    public static List<Date[]> getQuarter (Date startDate, Date endDate, boolean sort) {
        List<Date[]> quarterResult = new ArrayList<>();
        Calendar sc = Calendar.getInstance();
        sc.setTime(startDate);
        int sy = sc.get(Calendar.YEAR);
        Calendar ec = Calendar.getInstance();
        ec.setTime(endDate);
        int ey = ec.get(Calendar.YEAR);
        for(int y = sy; y <= ey; y ++){
            List<Date[]> quarterList = getQuarterByYear(y);
            for(Date[] quarter : quarterList) {
                if(quarter[0].getTime()  <= endDate.getTime() && quarter[1].getTime() >= startDate.getTime()){
                    quarterResult.add(quarter);
                }
            }
        }
        if(sort) {
            Collections.reverse(quarterResult);
        }
        return quarterResult;
    }

    public static List<Date[]> getQuarterByYear (int year) {
        List<Date[]> quarterList = new ArrayList<>();
        Date[] quarter = new Date[2];
        quarter[0] = getLeftBoundaryOfMonth(year, 1);
        quarter[1] = getRightBoundaryOfMonth(year, 3);
        quarterList.add(quarter);
        quarter = new Date[2];
        quarter[0] = getLeftBoundaryOfMonth(year, 4);
        quarter[1] = getRightBoundaryOfMonth(year, 6);
        quarterList.add(quarter);
        quarter = new Date[2];
        quarter[0] = getLeftBoundaryOfMonth(year, 7);
        quarter[1] = getRightBoundaryOfMonth(year, 9);
        quarterList.add(quarter);
        quarter = new Date[2];
        quarter[0] = getLeftBoundaryOfMonth(year, 10);
        quarter[1] = getRightBoundaryOfMonth(year, 12);
        quarterList.add(quarter);
        return quarterList;
    }

    public static int getMonthsDiff(Date from, Date to){
        Calendar dateFrom = Calendar.getInstance();
        dateFrom.setTime(from);
        Calendar dateTo = Calendar.getInstance();
        dateTo.setTime(to);
        int periods = dateTo.get(Calendar.YEAR) * 12 + dateTo.get(Calendar.MONTH) - dateFrom.get(Calendar.YEAR) * 12 - dateFrom.get(Calendar.MONTH);
        return periods == 0 ? 1 : Math.abs(periods);
    }

    public static void main(String[] args) {
        Date date = new Date();
        System.out.println(shiftByDay(date, -3, 0));
        System.out.println(shiftByDay(date, -3, -1));
        System.out.println(shiftByDay(date, -3, 1));
        System.out.println(shiftByDay(date, 3, -1));
        System.out.println(shiftByMonth(date, -3, 0, -1));
        System.out.println(shiftByMonth(date, -3, 0, 1));
        System.out.println(shiftByMinute(date, -3, -1));
        System.out.println(shiftByMinute(date, -3, 1));
        System.out.println(getDayStart(date));
        System.out.println(getDayEnd(date));
        Date start = getLeftBoundaryOfMonth(date);
        Date end = getRightBoundaryOfMonth(date);
        System.out.println(start);
        System.out.println(end);
        List<Date[]> pairs = getDateRangeByMonth(-5, 12);
        for (Date[] pair : pairs) {
            System.out.println(pair[0]);
            System.out.println(pair[1]);
        }
        Date[] pair = getDayRangeOfDate(new Date());
        System.out.println(pair[0]);
        System.out.println(pair[1]);
        
        System.out.println(TimeUtil.getDaysDiff(new Date(), TimeUtil.getDate("2016-05-19", TimeUtil.FORMAT_YMD)));
        System.out.println(TimeUtil.shiftByDay(new Date(), 30, 1));
    }
}
