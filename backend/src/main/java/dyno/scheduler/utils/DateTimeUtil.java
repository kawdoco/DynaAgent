/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dyno.scheduler.utils;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 *
 * @author Prabash
 */
public class DateTimeUtil
{

    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String TIME_FORMAT = "HH:mm:ss";
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String DATE_TIME_FORMAT_JSON = "yyyy-MM-dd HH:mm:ss";

    public static DateTimeFormatter getDateFormat()
    {
        return DateTimeFormat.forPattern(DATE_FORMAT);
    }

    public static DateTimeFormatter getTimeFormat()
    {
        return DateTimeFormat.forPattern(TIME_FORMAT);
    }

    public static DateTimeFormatter getDateTimeFormat()
    {
        return DateTimeFormat.forPattern(DATE_TIME_FORMAT);
    }

    public static DateTimeFormatter getDateTimeFormatJson()
    {
        return DateTimeFormat.forPattern(DATE_TIME_FORMAT_JSON);
    }

    public static DateTime concatenateDateTime(String date, String time)
    {
        String concatStringDate = date + " " + time;
        return getDateTimeFormat().parseDateTime(concatStringDate);
    }

    public static DateTime concatenateDateTime(DateTime date, String time)
    {
        String concatStringDate = date.toString(getDateFormat()) + " " + time;
        return getDateTimeFormat().parseDateTime(concatStringDate);
    }

    public static DateTime concatenateDateTime(String date, DateTime time)
    {
        String concatStringDate = date + " " + time.toString(getTimeFormat());
        return getDateTimeFormat().parseDateTime(concatStringDate);
    }

    public static DateTime concatenateDateTime(DateTime date, DateTime time)
    {
        String concatStringDate = date.toString(getDateFormat()) + " " + time.toString(getTimeFormat());
        return getDateTimeFormat().parseDateTime(concatStringDate);
    }

    public static DateTime concatenateDateTime(LocalDate date, LocalTime time)
    {
        String concatStringDate = date.toString(getDateFormat()) + " " + time.toString(getTimeFormat());
        return getDateTimeFormat().parseDateTime(concatStringDate);
    }

    public static Date convertDatetoSqlDate(DateTime date)
    {
        java.util.Date parsedUtilDate;
        java.sql.Date parsedSqlDate = null;
        try
        {
            String dateString = date.toString(getDateFormat());
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            parsedUtilDate = sdf.parse(dateString);
            parsedSqlDate = new java.sql.Date(parsedUtilDate.getTime());

        } catch (ParseException ex)
        {
            Logger.getLogger(DateTimeUtil.class.getName()).log(Level.SEVERE, null, ex);
        }

        return parsedSqlDate;
    }

    public static Time convertTimetoSqlTime(DateTime time)
    {
        java.util.Date parsedUtilDate;
        java.sql.Time parsedSqlTime = null;
        try
        {
            String timeString = time.toString(getTimeFormat());
            SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT);
            parsedUtilDate = sdf.parse(timeString);
            parsedSqlTime = new java.sql.Time(parsedUtilDate.getTime());

        } catch (ParseException ex)
        {
            Logger.getLogger(DateTimeUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return parsedSqlTime;
    }

    public static DateTime convertSqlDatetoDateTime(Date date)
    {
        String dateValue = date.toString();
        DateTime dateTime = DateTime.parse(dateValue);
        return dateTime;
    }

    public static DateTime convertSqlTimetoDateTime(Time time)
    {
        String timeValue = time.toString();
        DateTime dateTime = DateTime.parse(timeValue, getTimeFormat());
        return dateTime;
    }
    
    public static DateTime convertSqlTimestampToDateTime(Timestamp timestamp)
    {
        String timestampVal =  new SimpleDateFormat(DATE_TIME_FORMAT_JSON).format(timestamp);
        DateTime dateTime = DateTime.parse(timestampVal, getDateTimeFormat());
        return dateTime;
    }
    
    public static DateTime convertStringDateToDateTime(String date)
    {
        return DateTime.parse(date, getDateFormat());
    }
    
    public static DateTime convertStringTimeToDateTime(String time)
    {
        return DateTime.parse(time, getTimeFormat());
    }
    
    public static DateTime convertStringDateTimeToDateTime(String datetime)
    {
        return DateTime.parse(datetime, getDateTimeFormat());
    }
    
    public static DateTime convertJsonDateTimeToDateTime(String datetime)
    {
        return DateTime.parse(datetime, getDateTimeFormatJson());
    }
}
