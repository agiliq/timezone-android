package com.agiliq.timezoneconverter.widget;

import android.app.ListActivity;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.agiliq.timezone.core.R;
import com.agiliq.timezoneconverter.data.PreferencesManager;
import com.agiliq.timezoneconverter.data.Utils;

import java.util.ArrayList;

public class TimeZoneWidgetConfigure extends ListActivity implements OnItemClickListener {

	private String[] mCityArray;
	private String mCities;

	boolean isButtonClicked = false;

    private static final String PREF_PREFIX_KEY = "prefix_";
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    private String selectedCity = null;

	public TimeZoneWidgetConfigure(){
		super();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        PreferencesManager.initializeInstance(getApplicationContext());
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

		mCities = PreferencesManager.getInstance().getString(Utils.KEY_CITIES);

		if(mCities != null){
			mCityArray = mCities.split(";");
			setListAdapter(new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_single_choice,
					mCityArray));
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
    static void saveCityPref(int appWidgetId, String text) {
        PreferencesManager.getInstance().setString(PREF_PREFIX_KEY + appWidgetId, text);
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static String loadCityPref(int appWidgetId) {
        return PreferencesManager.getInstance().getString(PREF_PREFIX_KEY + appWidgetId);
    }

    static void deleteCityPref(int appWidgetId) {
        PreferencesManager.getInstance().remove(PREF_PREFIX_KEY + appWidgetId);
    }

    static void loadAllCityPrefs(ArrayList<Integer> appWidgetIds,
            ArrayList<String> texts) {
    }

	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		selectedCity = (String) getListView().getItemAtPosition(position);
	}

	public void selectCity(View view){

		final Context context = TimeZoneWidgetConfigure.this;

		if(selectedCity != null){
			saveCityPref(mAppWidgetId, selectedCity);
			AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

			TimeZoneWidgetService t = new TimeZoneWidgetService(context, appWidgetManager, mAppWidgetId);
			t.updateWidget();

            isButtonClicked = true;

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
		if(!isButtonClicked){
			AppWidgetHost host = new AppWidgetHost(TimeZoneWidgetConfigure.this, 1);
			host.deleteAppWidgetId(mAppWidgetId);
		}
		super.onPause();
	}

}
