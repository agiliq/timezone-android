package com.agiliq.timezoneconverter.core;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

import com.agiliq.timezone.core.R;
import com.agiliq.timezoneconverter.model.TimeZoneModel;
import com.flurry.android.FlurryAgent;
import com.google.ads.AdView;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.Vector;

public class MainActivity extends ActionBarListActivity {

    public static Map<String, Typeface> fonts = new HashMap<String, Typeface>();

    static final String TAG = "com.agiliq.timezone.core.MainActivity";
    static SharedPreferences timeZonePreferences;
    static String VALUE_CITIES;
    public static String KEY_CITIES = TAG + "cities";
    static String IS_SET = "isset";
    static String CURRENT_LOCATION = "current_city";
    private static String currentLocation;
    TimeZoneImpl timeZoneImpl;
    static ListViewAdapter listViewAdapter;
    AdView adView;

    private LocationManager locationManager;
    private String bestProvider;
    Context context;

    String defValue = "New York, NY;" + "Sydney;" + "Paris;";

    @Override
    protected void onStart() {
        super.onStart();
        FlurryAgent.onStartSession(this, "S26CGPC457YD579H6398");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        registerForContextMenu(getListView());

        timeZonePreferences = getPreferences(Context.MODE_PRIVATE);

        if (!timeZonePreferences.getBoolean(MainActivity.IS_SET, false)) {
            MainActivity.VALUE_CITIES = defValue;
            timeZonePreferences.edit().putString(MainActivity.KEY_CITIES, MainActivity.VALUE_CITIES).commit();
            timeZonePreferences.edit().putBoolean(MainActivity.IS_SET, true).commit();
        } else {
            MainActivity.VALUE_CITIES = timeZonePreferences.getString(KEY_CITIES, defValue);
        }

        getListView().setItemsCanFocus(true);

        //List view on Touch listener
        getListView().setOnItemSelectedListener(new OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                view.setBackgroundColor(context.getResources().getColor(R.color.holo_blue_light));
            }

            public void onNothingSelected(AdapterView<?> parent) {

            }

        });

        getListView().setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                openContextMenu(view);
            }

        });

        getTimeZoneData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_add_time_zone:
                goToSearchView();
                break;
            case R.id.menu_help:
                Intent intent = new Intent(this, HelpActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_refresh_time_zone:
                listViewAdapter.resetTime();
                getListView().setAdapter(listViewAdapter);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        if (item.getItemId() == R.id.deleteTimeZone) {
            listViewAdapter.remove(listViewAdapter.getItem((int) info.id));
            updatePreference();
        } else if (item.getItemId() == R.id.editTimeZone) {
            listViewAdapter.editTime(listViewAdapter.getItem((int) info.id));
        }
        return super.onContextItemSelected(item);
    }

    /**
     * Method to go to Search Activity
     */
    public void goToSearchView() {
        Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
        startActivity(intent);
    }

    /**
     * Method to get Time Zone data from the saved Shared Preferences
     */
    public void getTimeZoneData() {
        timeZoneImpl = new TimeZoneImpl(this);
        Vector<TimeZoneModel> zones = new Vector<TimeZoneModel>();
        zones = timeZoneImpl.setDefaultCities(MainActivity.VALUE_CITIES);
        zones.add(0, useLocation());

        listViewAdapter = new ListViewAdapter(this, 0, zones);
        setListAdapter(listViewAdapter);
    }

    public TimeZoneModel useLocation() {
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        bestProvider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(bestProvider);

        TimeZoneModel timeZoneModel = new TimeZoneModel();
        TimeZone timeZone = TimeZone.getDefault();
        currentLocation = "Current Zone";
        timeZoneModel.setCity(currentLocation);
        timeZoneModel.setCountry(timeZone.getDisplayName());
        timeZoneModel.setTimeZoneId(timeZone.getID());
        timeZoneModel.setCalendar(Calendar.getInstance());

        timeZoneImpl = new TimeZoneImpl(this);

        Geocoder geoCoder = new Geocoder(this);

        try {
            if (location != null) {
                List<Address> address = geoCoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                getPreferences(MODE_PRIVATE).edit().putString(MainActivity.CURRENT_LOCATION, address.get(0).getLocality()).commit();

                if (timeZoneImpl.getSingleCity(address.get(0).getLocality()) != null) {
                    currentLocation = address.get(0).getLocality();
                    timeZoneModel = timeZoneImpl.getSingleCity(address.get(0).getLocality());
                }
            } else {
                return timeZoneModel;
            }

        } catch (IOException e) {
            Log.d("GeoCoder", "Error getting location.");

            String city = getPreferences(MODE_PRIVATE).getString(MainActivity.CURRENT_LOCATION, null);
            if (timeZoneImpl.getSingleCity(city) != null) {
                timeZoneModel = timeZoneImpl.getSingleCity(city);
                currentLocation = city;
            }
        }

        return timeZoneModel;
    }

    /**
     * Method to update Preferences
     */
    public static void updatePreference() {
        String newPreference = null;
        if (MainActivity.listViewAdapter.getCount() > 0) {
            for (int i = 0; i < MainActivity.listViewAdapter.getCount(); i++) {
                if (!MainActivity.listViewAdapter.getItem(i).getCity().equals(currentLocation)) {
                    if (newPreference == null) {
                        newPreference = MainActivity.listViewAdapter.getItem(i).getCity() + ";";
                    } else {
                        newPreference = newPreference + MainActivity.listViewAdapter.getItem(i).getCity() + ";";
                    }
                }
            }
        }

        timeZonePreferences.edit().putString(KEY_CITIES, newPreference).commit();
    }

    @Override
    protected void onPause() {
        updatePreference();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adView.stopLoading();
        adView.destroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        FlurryAgent.onEndSession(this);
    }
}


