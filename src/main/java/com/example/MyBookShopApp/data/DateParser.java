package com.example.MyBookShopApp.data;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateParser {

    private static DateFormat format = new SimpleDateFormat("dd MMMM yyyy HH:mm");

    public static String parseDate (Date date){
        return format.format(date);
    }
}
