package com.agiliq.timezoneconverter.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.agiliq.timezoneconverter.model.City;
import com.agiliq.timezoneconverter.model.TimeZoneModel;

import java.util.HashSet;
import java.util.Vector;

public class DataBaseAccess {

    final String[] TIMEZONE_COLUMNS = {
            "_id",
            "city",
            "country",
            "timezone_id"
    };
    DataBaseHelper mDataBaseHelper;
    SQLiteDatabase mDataBase;
    Context mContext;

    public DataBaseAccess(Context context) {
        this.mContext = context;
    }

    public void open() {
        mDataBaseHelper = DataBaseHelper.getInstance(mContext);
        mDataBaseHelper.openDataBase();
        mDataBase = mDataBaseHelper.getReadableDatabase();
    }

    public Vector<TimeZoneModel> getTimeZonesByQuery(String query) {
        if (mDataBase != null) {
            Vector<TimeZoneModel> zoneModels = new Vector<TimeZoneModel>();
            String table = "data";
            String selection = "city " + "like \"" + query + "%\"";
            String orderBy = "city";
            Cursor mCursor = mDataBase.query(table, TIMEZONE_COLUMNS, selection, null, null, null, orderBy);
            if (mCursor.getCount() > 0) {
                mCursor.moveToFirst();
                while (!mCursor.isAfterLast()) {
                    TimeZoneModel zone = new TimeZoneModel();
                    zone.setId(mCursor.getInt(mCursor.getColumnIndex("_id")));
                    zone.setCity(mCursor.getString(mCursor.getColumnIndex("city")));
                    zone.setTimeZoneId(mCursor.getString(mCursor.getColumnIndex("timezone_id")));
                    zone.setCountry(mCursor.getString(mCursor.getColumnIndex("country")));
                    zone.setCalendar(Utils.getCalendar(zone.getTimeZoneId()));
                    zoneModels.add(zone);
                    mCursor.moveToNext();
                }
                return zoneModels;
            }
        }
        return null;
    }

    public HashSet<City> getCitiesByQuery(String query) {

        if (mDataBase != null) {
            HashSet<City> cities = new HashSet<City>();
            String table = "data";
            String selection = "city " + "like \"" + query + "%\"";
            String orderBy = "city";
            Cursor mCursor = mDataBase.query(table, TIMEZONE_COLUMNS, selection, null, null, null, orderBy);
            if (mCursor.getCount() > 0) {
                mCursor.moveToFirst();
                while (!mCursor.isAfterLast()) {
                    City city = new City();
                    city.id = mCursor.getInt(mCursor.getColumnIndex("_id"));
                    city.name = mCursor.getString(mCursor.getColumnIndex("city"));
                    city.country = mCursor.getString(mCursor.getColumnIndex("country"));
                    city.timeZoneId = mCursor.getString(mCursor.getColumnIndex("timezone_id"));
                    cities.add(city);
                    mCursor.moveToNext();
                }
                return cities;
            }
        }
        return null;
    }

    public Cursor getAllCities() {
        if (mDataBase != null) {
            String table = "data";
            String orderBy = "city";
            Cursor mCursor = mDataBase.query(table, TIMEZONE_COLUMNS, null, null, null, null, orderBy);
            return mCursor;
        }
        return null;
    }

    public Cursor searchForCity(String query) {

        if (mDataBase != null) {
            String table = "data";
            String selection = "city " + "like \"" + query + "%\"";
            String orderBy = "city";
            Cursor mCursor = mDataBase.query(table, TIMEZONE_COLUMNS, selection, null, null, null, orderBy);
            if (mCursor.getCount() > 0) {
                return mCursor;
            }
        }
        return null;
    }

    public City getCity(String city) {
        if (mDataBase != null) {
            String table = "data";
            String selection = "city " + "= \"" + city + "\"";
            Cursor mCursor = mDataBase.query(table, TIMEZONE_COLUMNS, selection, null, null, null, null);
            if (mCursor.getCount() > 0) {
                mCursor.moveToFirst();
                City cityT = new City();
                cityT.id = mCursor.getInt(mCursor.getColumnIndex("_id"));
                cityT.name = mCursor.getString(mCursor.getColumnIndex("city"));
                cityT.country = mCursor.getString(mCursor.getColumnIndex("country"));
                cityT.timeZoneId = mCursor.getString(mCursor.getColumnIndex("timezone_id"));
                mCursor.close();
                return cityT;
            }
        }
        return null;
    }

    public TimeZoneModel getTimeZoneDataByCity(String city){
        if (mDataBase != null) {
            String table = "data";
            String selection = "city " + "= \"" + city + "\"";
            Cursor cursor = mDataBase.query(table, TIMEZONE_COLUMNS, selection, null, null, null, null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                TimeZoneModel zone = new TimeZoneModel();
                zone.setId(cursor.getInt(cursor.getColumnIndex("_id")));
                zone.setCity(cursor.getString(cursor.getColumnIndex("city")));
                zone.setTimeZoneId(cursor.getString(cursor.getColumnIndex("timezone_id")));
                zone.setCountry(cursor.getString(cursor.getColumnIndex("country")));
                zone.setCalendar(Utils.getCalendar(zone.getTimeZoneId()));
                cursor.close();
                return zone;
            }
        }
        return null;
    }

    public City getCityById(int id, Cursor cursor){
        if (mDataBase != null) {
            String table = "data";
            String selection = "_id " + "= \"" + id + "\"";
            cursor = mDataBase.query(table, TIMEZONE_COLUMNS, selection, null, null, null, null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                City cityT = new City();
                cityT.id = cursor.getInt(cursor.getColumnIndex("_id"));
                cityT.name = cursor.getString(cursor.getColumnIndex("city"));
                cityT.country = cursor.getString(cursor.getColumnIndex("country"));
                cityT.timeZoneId = cursor.getString(cursor.getColumnIndex("timezone_id"));
                cursor.close();
                return cityT;
            }
        }
        return null;
    }



    public void close() {
        mDataBaseHelper.close();
    }

}
