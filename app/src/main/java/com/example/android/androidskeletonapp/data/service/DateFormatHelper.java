package com.example.android.androidskeletonapp.data.service;

import com.example.android.androidskeletonapp.data.Sdk;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateFormatHelper {
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    private static SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd-MM-yyyy");

    private static SimpleDateFormat dateFormat3 = new SimpleDateFormat("MM-dd-yyyy");


    public static String formatDate(Date date) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd hh:mm:ss", Locale.US);
        return dateFormat.format(date);
    }

    public static String formatSimpleDate(Date date) {
        return dateFormat.format(date);
    }

    public static String formatEnglishDate(Date date) {
        return dateFormat2.format(date);
    }

    public static String formatUsaDate(Date date) {
        return dateFormat3.format(date);
    }

    public static Date parseSimpleDate(String date) throws ParseException {
        return dateFormat.parse(date);
    }

    public static Date parseDateAutoFormat(String date) throws ParseException {
        String[] dateparts = date.split("\\-");
        if (dateparts[2].length() == 4) {
            return dateFormat2.parse(date);
        }
        return dateFormat.parse(date);
    }

    public static String getFormat(String date){
        String[] dateparts = date.split("\\-");
        if (dateparts[2].length() == 4) {
            return "dd-MM-yyyy";
        }
        return "yyyy-MM-dd";
    }

    public static String getDateAsSystemFormat(Date date){
        String dateFormat = Sdk.d2().systemInfoModule().systemInfo().blockingGet().dateFormat();
        if(dateFormat.equalsIgnoreCase("dd-MM-yyyy"))
            return formatEnglishDate(date);
        return formatSimpleDate(date);
    }
}