package com.raspisaniyevuzov.app.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by SAPOZHKOV on 23.09.2015.
 */
public class TimeUtil {

    public static final long SECOND = 1000;
    public static final long MINUTE = 60 * SECOND;
    public static final long HOUR = 60 * MINUTE;
    public static final long DAY = 24 * HOUR;

    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private static final SimpleDateFormat dateWithWeekDayFormat = new SimpleDateFormat("EEEE, dd MMMM", Locale.getDefault());
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM", Locale.getDefault());
    private static final SimpleDateFormat weekDayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
    private static final SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
    private static final SimpleDateFormat simpleDateFormat3 = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
    private static final SimpleDateFormat simpleDateFormat4 = new SimpleDateFormat("dd MMMM, yyyy", Locale.getDefault());

    public static int convertMillisToHour(long millis) {
        return (int) (millis / 3600000);
    }

    public static int convertMillisToMin(long millis) {
        return (int) ((millis - convertMillisToHour(millis) * HOUR) / 60000);
    }

    public static String convertMillisToTime(long millis) {
        return timeFormat.format(new Date(millis));
    }

    public static String convertMillisToDateWithWeekDay(long millis) {
        return dateWithWeekDayFormat.format(new Date(millis));
    }

    public static String convertMillisToSimpleDate(long millis) {
        return simpleDateFormat.format(new Date(millis));
    }


    public static String convertMillisToSimpleDateWithYear(long millis) {
        return simpleDateFormat4.format(new Date(millis));
    }

    /**
     * @param millis - date
     * @return TRUE if week for this date is even and FALSE otherwise
     */
    public static boolean isWeekEven(long millis) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millis);
        int week = cal.get(Calendar.WEEK_OF_YEAR);
        return week % 2 == 0;
    }

    public static String convertMillisToWeekDay(long timeInMillis) {
        return weekDayFormat.format(new Date(timeInMillis));
    }

    public static Calendar resetHoursAndMinutes(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar;
    }

    public static boolean isToday(Date date) {
        return isTheSame(date, Calendar.getInstance().getTime());
    }

    public static boolean isTheSame(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    public static boolean isTomorrow(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        return isTheSame(calendar.getTime(), date);
    }

    /**
     * @param timeStr - 10:14
     * @return time in millis form day start
     */
    public static long convertTimeStringToTime(String timeStr) {
        long result = 0;
        String[] parts = timeStr.split(":");
        result = Integer.valueOf(parts[0]) * HOUR + Integer.valueOf(parts[1]) * MINUTE;
        return result;
    }

    /**
     * @param dateStr - 01.09.2015
     * @return date
     */
    public static Date convertStringDateToDate(String dateStr) {
        Date date = new Date();
        try {
            date = simpleDateFormat2.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * @return dateStr  - 18 сентября 2015
     */
    public static String convertDateToString(Date date) {
        String dateStr;
        dateStr = simpleDateFormat3.format(date);
        return dateStr;
    }

    public static int getDayLeft(long dateStart, long dateEnd) {
        return (int) ((dateEnd - dateStart) / DAY);
    }

}
