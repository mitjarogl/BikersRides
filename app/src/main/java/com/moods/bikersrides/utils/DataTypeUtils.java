package com.moods.bikersrides.utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class DataTypeUtils
{
    //FIXME refactor renaming
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATE_FORMAT_DISPLAY = "d.M.yyyy";
    public static final String TIMESTAMP_FORMAT = "dd.MM.yyyy HH:mm:ss";
    public static final String TIME_FORMAT_12 = "hh:mm aa";
    public static final String TIME_FORMAT_24 = "HH:mm";

    private static String formatDate(String pattern, Date date)
    {
        if (pattern == null || date == null)
        {
            return null;
        }

        try
        {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            return simpleDateFormat.format(date);
        } catch (Exception e)
        {
            System.err.println("Timestamp \"" + date + "\" is unparsable!");
            return null;
        }
    }

    private static Date formatDate(String pattern, String dateString)
    {
        if (StringUtils.isNullOrWhitespace(dateString))
        {
            return null;
        }

        try
        {
            return new SimpleDateFormat(pattern).parse(dateString);
        } catch (Exception e)
        {
            System.err.println("Date \"" + dateString + "\" is unparsable!");
            return null;
        }
    }

    public static String tryGet(Map<String, Object> record, String key)
    {
        Object object = tryGetObject(record, key);
        return object != null ? object.toString() : null;
    }

    public static Object tryGetObject(Map<String, Object> result, String key)
    {
        return result.get(key.toUpperCase().trim());
    }

    public static Date tryString2Date(Map<String, Object> result, String key)
    {
        String value = tryGet(result, key);
        return formatDate(DATE_FORMAT, value);
    }

    public static String tryDate2Display(Map<String, Object> result, String key)
    {
        Date value = tryString2Date(result, key);
        return date2DisplayFormat(value);
    }

    public static String date2DisplayFormat(Date date)
    {
        String formatDate = formatDate(DATE_FORMAT_DISPLAY, date);
        return formatDate == null ? "" : formatDate;
    }

    public static String date2Time12(Date date)
    {
        String formatDate = formatDate(TIME_FORMAT_12, date);
        return formatDate == null ? "" : formatDate;
    }

    public static String date2Time24(Date date)
    {
        String formatDate = formatDate(TIME_FORMAT_24, date);
        return formatDate == null ? "" : formatDate;
    }

    public static Date string2Date(String date)
    {
        return formatDate(DATE_FORMAT, date);
    }

    public static Date displayDate2Date(String display)
    {
        return formatDate(DATE_FORMAT_DISPLAY, display);
    }

    public static String displayDate2StringDate(String date)
    {
        Date formatDate = formatDate(DATE_FORMAT_DISPLAY, date);
        return formatDate(DATE_FORMAT, formatDate);
    }

    public static Calendar millisToCalendar(long millisecs)
    {
        Calendar cal = Calendar.getInstance();
        Date resultDate = new Date(millisecs);
        cal.setTime(resultDate);
        return cal;
    }

    public static Calendar resetTimeToZero(Calendar cal)
    {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }

    public static Timestamp currentDate2Timestamp()
    {
        Date currentDate = new Date();
        return new Timestamp(currentDate.getTime());
    }

    public static Timestamp date2Timestamp(Date date)
    {
        return new Timestamp(date.getTime());
    }

    public static String currentTimestamp2Display()
    {
        Timestamp currentDate2Timestamp = currentDate2Timestamp();
        return formatDate(TIMESTAMP_FORMAT, currentDate2Timestamp);
    }

    public static String timestamp2Display(Date date)
    {
        return formatDate(TIMESTAMP_FORMAT, date);
    }

    public static Timestamp tryString2Timestamp(Map<String, Object> result, String key)
    {
        String value = tryGet(result, key);
        return string2StampOfTheTime(value);
    }

    public static Timestamp string2StampOfTheTime(String time)
    {
        return time == null ? null : Timestamp.valueOf(time);
    }

    public static java.sql.Date date2SqlDate(Date date)
    {
        return date == null ? null : new java.sql.Date(date.getTime());
    }

    public static Float tryString2Float(Map<String, Object> result, String key)
    {
        String value = tryGet(result, key);
        return string2Float(value);
    }

    public static Float tryString2Float(Map<String, Object> result, String key, float returnOnNull)
    {
        String value = tryGet(result, key);
        return string2Float(value, returnOnNull);
    }

    public static Float string2Float(String value)
    {
        if (StringUtils.isNullOrWhitespace(value)) return null;

        value = value.replace(',', '.');
        return Float.parseFloat(value);
    }

    public static Float string2Float(String value, float returnOnNull)
    {
        if (StringUtils.isNullOrWhitespace(value)) return returnOnNull;

        value = value.replace(',', '.');
        return Float.parseFloat(value);
    }

    public static Double tryString2Double(Map<String, Object> record, String key)
    {
        String value = tryGet(record, key);
        return string2Double(value);
    }

    public static Double tryString2Double(Map<String, Object> record, String key, double returnOnNull)
    {
        String value = tryGet(record, key);
        return string2Double(value, returnOnNull);
    }

    public static Double string2Double(String value)
    {
        if (StringUtils.isNullOrWhitespace(value))
        {
            return null;
        }

        value = value.replace(',', '.');
        return Double.parseDouble(value);
    }

    public static Double string2Double(String value, Double returnOnNull)
    {
        if (StringUtils.isNullOrWhitespace(value))
        {
            return returnOnNull;
        }

        value = value.replace(',', '.');
        return Double.parseDouble(value);
    }

    public static Long tryString2Long(Map<String, Object> result, String key)
    {
        String value = tryGet(result, key);
        return string2Long(value);
    }

    public static Long string2Long(String longString)
    {
        if (StringUtils.isNullOrWhitespace(longString)) return null;

        return Long.parseLong(longString);
    }

    public static Integer tryString2Integer(Map<String, Object> result, String key)
    {
        String value = tryGet(result, key);
        return string2Int(value);
    }

    public static Integer string2Int(String strInt)
    {
        if (StringUtils.isNullOrWhitespace(strInt))
        {
            return null;
        }

        try
        {
            return Integer.valueOf(strInt);
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static Boolean tryString2Boolean(Map<String, Object> result, String key)
    {
        String value = tryGet(result, key);
        return string2Boolean(value);
    }

    public static Boolean string2Boolean(String bool)
    {
        if (StringUtils.isNullOrWhitespace(bool))
        {
            return null;
        }

        return bool.toLowerCase().contains("true");
    }

    public static Enum<?> tryString2Enum(Map<String, Object> result, String key, Class<?> enumType)
    {
        String enumValue = tryGet(result, key);
        return string2Enum(enumType, enumValue);
    }

    @SuppressWarnings("unchecked")
    public static Enum<?> string2Enum(Class<?> enumType, String enumValue)
    {
        if (StringUtils.isNullOrWhitespace(enumValue))
        {
            return null;
        }

        try
        {
            return Enum.valueOf(enumType.asSubclass(Enum.class), enumValue);
        } catch (IllegalArgumentException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static String toEmptyStringIfNull(Map<String, Object> row, String colName)
    {
        String value = tryGet(row, colName);
        return value != null ? value : "";
    }
}
