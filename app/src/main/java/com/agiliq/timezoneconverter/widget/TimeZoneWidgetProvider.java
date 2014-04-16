package com.agiliq.timezoneconverter.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class TimeZoneWidgetProvider extends AppWidgetProvider {

	String city;

	public TimeZoneWidgetProvider(){
		super();
	}


	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		super.onDeleted(context, appWidgetIds);
		final int N = appWidgetIds.length;
		for (int i = 0; i < N; i++) {
			TimeZoneWidgetConfigure.deleteCityPref(appWidgetIds[i]);
		}
	}

	@Override
	public void onDisabled(Context context) {
		super.onDisabled(context);
		Log.d("Widget", "Disabled");
	}

	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
		Log.d("Widget", "Enabled");
		// updateWidget();

	}

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		Log.d("Widget", "Recieved");
		//updateWidget();
		// updateWidget(context);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {

		final int N = appWidgetIds.length;
		if (N > 0) {
			Log.d("Widget", "Update");
			for (int i = 0; i < N; i++) {
				int appWidgetId = appWidgetIds[i];
				TimeZoneWidgetService t = new TimeZoneWidgetService(context, appWidgetManager, appWidgetId);
				t.updateWidget();
			}
		}
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

}
