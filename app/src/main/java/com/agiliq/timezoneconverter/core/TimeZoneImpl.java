package com.agiliq.timezoneconverter.core;

import android.content.Context;

import com.agiliq.timezoneconverter.data.DataBaseAccess;
import com.agiliq.timezoneconverter.model.TimeZoneModel;

import java.util.Vector;


public class TimeZoneImpl {

    //Array of time Zones class.
    Vector<TimeZoneModel> zones;
    Context context;
    String[] defaultCities = {"New York, NY", "Sydney", "Paris"};
    DataBaseAccess dataBaseAccess;


    public TimeZoneImpl(Context context) {
        zones = new Vector<TimeZoneModel>();
        this.context = context;
        dataBaseAccess = new DataBaseAccess(context);
    }

    /**
     * Method to Set Default cities from Shared preferences.
     *
     * @param defCities
     * @return
     */
    public Vector<TimeZoneModel> setDefaultCities(String defCities) {
        String[] cities = defCities.split(";");
        dataBaseAccess.open();
        for (String dCity : cities) {
            TimeZoneModel zone = dataBaseAccess.getTimeZoneDataByCity(dCity);
            zones.add(zone);
        }
        dataBaseAccess.close();
        if (zones == null) {
            return null;
        }
        return zones;
    }


    /**
     * Method to get Zone with relation to the location
     *
     * @param city
     * @return
     */
    public TimeZoneModel getSingleCity(String city) {

        zones = new Vector<TimeZoneModel>();
        dataBaseAccess.open();
        zones = dataBaseAccess.getTimeZonesByQuery(city);
        dataBaseAccess.close();
        if (zones == null) {
            return null;
        }
        return zones.elementAt(0);
    }

    /**
     * Method to get time zone data for Search from a query
     *
     * @param query
     * @return
     */
    public Vector<TimeZoneModel> setZoneData(String query) {
        zones = new Vector<TimeZoneModel>();
        dataBaseAccess.open();
        zones = dataBaseAccess.getTimeZonesByQuery(query);
        dataBaseAccess.close();
        if (zones == null) {
            return null;
        }
        return zones;
    }
}
