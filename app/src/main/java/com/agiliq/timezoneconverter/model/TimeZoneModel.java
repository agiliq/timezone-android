package com.agiliq.timezoneconverter.model;

import java.util.Calendar;


public class TimeZoneModel{

    private int id;
	private String city;
	private String timeZoneId;
	private String country;
	private Calendar calendar;
	private boolean isMorning;

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
		if(calendar.get(Calendar.HOUR_OF_DAY) > 6 && calendar.get(Calendar.HOUR_OF_DAY) < 18){
			this.isMorning = true;
		}else{
			this.isMorning = false;
		}
	}

	public boolean isMorning() {
		return isMorning;
	}

	public void setMorning(boolean isMorning) {
		this.isMorning = isMorning;
	}
}
