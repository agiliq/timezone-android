package com.agiliq.timezoneconverter.model;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class Utils {

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
