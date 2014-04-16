package com.agiliq.timezoneconverter.widget;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.text.format.DateFormat;
import android.widget.RemoteViews;

import com.agiliq.timezone.core.R;
import com.agiliq.timezoneconverter.core.TimeZoneImpl;
import com.agiliq.timezoneconverter.model.TimeZoneModel;

import java.util.Calendar;

public class TimeZoneWidgetService extends Service {

	private static String m24 = "k:mm";
	private Runnable mTicker;
	private final static Handler mHandler = new Handler();
	private boolean mTickerStopped = false;
	//private TimeZoneWidgetProvider timeZoneWidgetProvider;

	private RemoteViews zoneView;
	private TimeZoneImpl timeZoneImpl;

	//private static ComponentName thisComponent;
	private TimeZoneModel timeZoneModel;


	private int appWidgetId;
	String city;
	boolean update = true;

	private AppWidgetManager sAppWidgetManager;


	public TimeZoneWidgetService(Context context,
			AppWidgetManager appWidgetManager,int mAppWidgetId){
		this.appWidgetId = mAppWidgetId;
		this.sAppWidgetManager = appWidgetManager;
		this.timeZoneImpl = new TimeZoneImpl(context);
		this.city = TimeZoneWidgetConfigure.loadCityPref(this.appWidgetId);
		if(this.city != null){
			update = true;
			this.timeZoneModel = timeZoneImpl.getSingleCity(this.city);
			this.zoneView = new RemoteViews(context.getPackageName(),
					R.layout.custom_clock_item_widget);
			this.zoneView.setTextViewText(R.id.widget_city, timeZoneModel.getCity());
		}else{
			update = false;
		}
		/*
		// Create an Intent to launch ExampleActivity
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        zoneView.setOnClickPendingIntent(R.id.widget_time_display, pendingIntent);*/
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}


	public void updateWidget() {



		if(update){
			mTickerStopped = false;
			zoneView.setTextViewText(R.id.widget_city, timeZoneModel.getCity());

			mTicker = new Runnable() {
				public void run() {
					if (mTickerStopped)
						return;
					updateTime();
					sAppWidgetManager.updateAppWidget(appWidgetId, zoneView);
					long now = SystemClock.uptimeMillis();
					long next = now + (1000 - now % 1000);
					mHandler.postAtTime(mTicker, next);
				}
			};
			mTicker.run();
		}
	}

	public void updateTime(){

		timeZoneModel.getCalendar().setTimeInMillis(Calendar.getInstance().getTimeInMillis());
		zoneView.setTextViewText(R.id.widget_time_display,
				DateFormat.format(m24, timeZoneModel.getCalendar()));
		CharSequence amPm = timeZoneModel.getCalendar().get(Calendar.AM_PM) == 0 ? "AM" : "PM";
		zoneView.setTextViewText(R.id.widget_am_pm,  amPm);

	}

}
