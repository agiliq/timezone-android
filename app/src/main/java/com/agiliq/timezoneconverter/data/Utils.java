package com.agiliq.timezoneconverter.data;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class Utils {

    public static final String KEY_CITIES = "com.agiliq.timezone.core.MainActivity" + "cities";
    public static final String TIME_ZONE = "search_time_zone_model";
    /**
     * Method to compute time using the time zone Id selected.
     *
     * @param timeZoneId
     * @return
     */
    public static Calendar getCalendar(String timeZoneId) {
        Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone(timeZoneId));
        calendar.getTime();
        return calendar;
    }
}
