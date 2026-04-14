package com.library.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
    
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    
    public static String formatDate(Date date) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        return sdf.format(date);
    }
    
    public static String formatDateTime(Date date) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(DATETIME_FORMAT);
        return sdf.format(date);
    }
    
    public static Date parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            return null;
        }
    }
    
    public static Date parseDateTime(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATETIME_FORMAT);
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            return null;
        }
    }
    
    public static String addDays(String dateStr, int days) {
        Date date = parseDate(dateStr);
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, days);
        return formatDate(calendar.getTime());
    }
    
    public static boolean isOverdue(String dueDate) {
        if (dueDate == null || dueDate.trim().isEmpty()) {
            return false;
        }
        Date due = parseDate(dueDate);
        if (due == null) {
            return false;
        }
        Date today = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        try {
            today = sdf.parse(sdf.format(today));
        } catch (ParseException e) {
            return false;
        }
        return today.after(due);
    }
    
    public static String getCurrentDate() {
        return formatDate(new Date());
    }
    
    public static String getCurrentDateTime() {
        return formatDateTime(new Date());
    }
}
