package com.ugoodtech.umi.core.utils;


import com.ugoodtech.umi.core.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
    public static Date getDate(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_TIME_FORMAT);
        try {
            return dateFormat.parse(date);
        } catch (ParseException e) {
            logger.error("error parse date [" + date + "] with format" + Constants.DATE_TIME_FORMAT);
            return null;
        }
    }

    public static Date getNextDayCalender(int dayNumber) {
        Date day;
        Calendar cal = Calendar.getInstance();
        //n为推迟的周数，1本周，-1向前推迟一周，2下周，依次类推
        int n = 1;
        cal.add(Calendar.DATE, n * 7);
        cal.set(Calendar.DAY_OF_WEEK, (dayNumber + 2) % 7);
        day = cal.getTime();
//        System.out.println(new SimpleDateFormat("yyyy-MM-dd").format(day));
        return day;
    }

    public static Date getNextWeekCalender(int weekNumber, int dayNumber) {
        Date day;
        Calendar cal = Calendar.getInstance();

        cal.add(Calendar.MONTH, 1);
        cal.set(Calendar.WEEK_OF_MONTH, weekNumber);
        cal.set(Calendar.DAY_OF_WEEK, (dayNumber + 2) % 7);
        day = cal.getTime();
//        System.out.println(new SimpleDateFormat("yyyy-MM-dd").format(day));
        return day;
    }


    public static Date getNextMonthCalender(int monthNumber, int weekNumber, int dayNumber) {
        Date day;
        Calendar cal = Calendar.getInstance();
        int monthDay = cal.get(Calendar.MONTH);
        if (0 == monthDay % 3) {
            cal.add(Calendar.MONTH, 3 + monthNumber);
        } else if (1 == monthDay % 3) {
            cal.add(Calendar.MONTH, 2 + monthNumber);
        } else if (2 == monthDay % 3) {
            cal.add(Calendar.MONTH, 1 + monthNumber);
        }
        cal.set(Calendar.WEEK_OF_MONTH, weekNumber + 1);
        cal.set(Calendar.DAY_OF_WEEK, (dayNumber + 2) % 7);
        day = cal.getTime();
//        System.out.println(new SimpleDateFormat("yyyy-MM-dd").format(day));
        return day;
    }

    /**
     * 明天
     * @return
     */
    public static String getNextDayCalender() {
        Format f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date today = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(today);
        c.add(Calendar.DAY_OF_MONTH, 1);// 今天+1天
        Date tomorrow = c.getTime();
        return f.format(tomorrow);
    }
    /**
     * 时间差
     */
    public static  String differentDaysByMillisecond(Date date1,Date date2)
    {
        String days = ((date2.getTime() - date1.getTime()) / (1000))+"";
//        if("1h".equals(days)||"0h".equals(days)){
//            int d = (int) ((date2.getTime() - date1.getTime()) / (1000*60));
//            if(d>60){
//                days="1h";
//                return days;
//            }
//            days = (int) ((date2.getTime() - date1.getTime()) / (1000*60))+"m";
//            if("0m".equals(days)){
//                days="1m";
//            }
//        }
        return days;
    }
    static Logger logger = LoggerFactory.getLogger(DateUtil.class);
}
