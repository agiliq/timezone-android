package com.agiliq.timezoneconverter.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.agiliq.timezoneconverter.data.Utils;

import java.util.Calendar;


public class TimeZoneModel implements Parcelable {

    private int id;
    private String city;
    private String timeZoneId;
    private String country;
    private Calendar calendar;
    private boolean isMorning;

    public TimeZoneModel() {
    }

    TimeZoneModel(Parcel source) {
        readFromParcel(source);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getTimeZoneId() {
        return timeZoneId;
    }

    public void setTimeZoneId(String timeZoneId) {
        this.timeZoneId = timeZoneId;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
        if (calendar.get(Calendar.HOUR_OF_DAY) > 6 && calendar.get(Calendar.HOUR_OF_DAY) < 18) {
            this.isMorning = true;
        } else {
            this.isMorning = false;
        }
    }

    public boolean isMorning() {
        return isMorning;
    }

    public void setMorning(boolean isMorning) {
        this.isMorning = isMorning;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(id);
        dest.writeString(city);
        dest.writeString(timeZoneId);
        dest.writeString(country);
        dest.writeInt(isMorning ? 0 : 1);
    }

    private void readFromParcel(Parcel source) {
        this.id = source.readInt();
        this.city = source.readString();
        this.timeZoneId = source.readString();
        this.country = source.readString();
        this.isMorning = source.readInt() == 0;
        this.calendar = Utils.getCalendar(this.timeZoneId);
    }

    public static Creator<TimeZoneModel> CREATOR = new Creator<TimeZoneModel>() {
        @Override
        public TimeZoneModel createFromParcel(Parcel source) {
            return new TimeZoneModel(source);
        }

        @Override
        public TimeZoneModel[] newArray(int size) {
            return new TimeZoneModel[0];
        }
    };
}
