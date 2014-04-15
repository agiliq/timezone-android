package com.agiliq.timezoneconverter.model;

/**
 * Created by agiliq on 04/07/13.
 */
public class City {

    public int id;
    public String name;
    public String country;
    public String timeZoneId;

    @Override
    public String toString() {
        return "City [id=" + id +
                ", name=" + name +
                ", country=" + country +
                ", timeZoneId=" + timeZoneId + "]";
    }
}
