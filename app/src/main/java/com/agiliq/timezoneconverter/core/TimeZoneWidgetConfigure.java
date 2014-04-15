package com.agiliq.timezoneconverter.core;

import android.app.ListActivity;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.agiliq.timezone.core.R;

import java.util.ArrayList;

public class TimeZoneWidgetConfigure extends ListActivity implements OnItemClickListener {

	private String[] cityArray;
	private String cities;


	static final String TAG = "SelectCityActivty";

	static boolean buttonClicked = false;

    private static final String PREFS_NAME = "com.agiliq.timezone.core.SelectCityActivity";
    private static final String PREF_PREFIX_KEY = "prefix_";
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    private String selectedCity = null;

	public TimeZoneWidgetConfigure(){
		super();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setResult(RESULT_CANCELED);
		setContentView(R.layout.widget_configuration);

		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		if (extras != null) {
			mAppWidgetId= extras.getInt(
		            AppWidgetManager.EXTRA_APPWIDGET_ID,
		            AppWidgetManager.INVALID_APPWIDGET_ID);
		}

		SharedPreferences prefs = getSharedPreferences("MainActivity", Context.MODE_PRIVATE);
		cities = prefs.getString(MainActivity.KEY_CITIES, null);

		if(cities != null){
			cityArray = cities.split(";");
			setListAdapter(new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_single_choice,
					cityArray));
			getListView().setItemsCanFocus(false);
			getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		}else{
			finish();
		}

		if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }

		getListView().setOnItemClickListener(this);
	}


    // Write the prefix to the SharedPreferences object for this widget
    static void saveCityPref(Context context, int appWidgetId, String text) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + appWidgetId, text);
        prefs.commit();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static String loadCityPref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String prefix = prefs.getString(PREF_PREFIX_KEY + appWidgetId, null);
        return prefix;

    }

    static void deleteCityPref(Context context, int appWidgetId) {
    	SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
    	if(prefs.contains(PREF_PREFIX_KEY + appWidgetId)){
    		prefs.edit().remove(PREF_PREFIX_KEY + appWidgetId).commit();
    	}
    }

    static void loadAllCityPrefs(Context context, ArrayList<Integer> appWidgetIds,
            ArrayList<String> texts) {
    }

	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		selectedCity = (String) getListView().getItemAtPosition(position);
	}

	public void selectCity(View view){

		final Context context = TimeZoneWidgetConfigure.this;

		if(selectedCity != null){
			saveCityPref(context, mAppWidgetId, selectedCity);
			AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

			TimeZoneWidgetService t = new TimeZoneWidgetService(context, appWidgetManager, mAppWidgetId);
			t.updateWidget();

			buttonClicked = true;

			Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();

		}else{
			Toast.makeText(context, "Please select a city", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onPause() {
		if(!buttonClicked){
			AppWidgetHost host = new AppWidgetHost(TimeZoneWidgetConfigure.this, 1);
			host.deleteAppWidgetId(mAppWidgetId);
		}
		super.onPause();
	}

}
