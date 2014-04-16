package com.agiliq.timezoneconverter.core;


import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import com.agiliq.timezone.core.R;
import com.agiliq.timezoneconverter.data.PreferencesManager;
import com.agiliq.timezoneconverter.data.Utils;
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

    private final String CURRENT_LOCATION = "current_city";
    private String mCurrentLocation;
    TimeZoneImpl mTimeZoneImpl;
    ListViewAdapter mListViewAdapter;
    AdView adView;

    String mDefaultCities = "New York, NY;" + "Sydney;" + "Paris;";
    String mCities = mDefaultCities;

    @Override
    protected void onStart() {
        super.onStart();
        FlurryAgent.onStartSession(this, "S26CGPC457YD579H6398");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        PreferencesManager.initializeInstance(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registerForContextMenu(getListView());

        if (PreferencesManager.getInstance().getString(Utils.KEY_CITIES) == null) {
            PreferencesManager.getInstance().setString(Utils.KEY_CITIES, mDefaultCities);
        }

        getListView().setItemsCanFocus(true);

        //List view on Touch listener
        getListView().setOnItemSelectedListener(new OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                view.setBackgroundColor(getResources().getColor(R.color.holo_blue_light));
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
    protected void onResume() {
        super.onResume();
        mCities = PreferencesManager.getInstance().getString(Utils.KEY_CITIES);
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
                mListViewAdapter.resetTime();
                getListView().setAdapter(mListViewAdapter);
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
            mListViewAdapter.remove(mListViewAdapter.getItem((int) info.id));
        } else if (item.getItemId() == R.id.editTimeZone) {
            mListViewAdapter.editTime(mListViewAdapter.getItem((int) info.id));
        }
        return super.onContextItemSelected(item);
    }

    /**
     * Method to go to Search Activity
     */
    public void goToSearchView() {
        Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
        startActivityForResult(intent, REQUEST_CODE_SEARCH_ACTIVITY);
    }

    /**
     * Method to get Time Zone data from the saved Shared Preferences
     */
    public void getTimeZoneData() {
        mTimeZoneImpl = new TimeZoneImpl(this);
        Vector<TimeZoneModel> zones = new Vector<TimeZoneModel>();
        zones = mTimeZoneImpl.setDefaultCities(mCities);
        zones.add(0, useLocation());

        mListViewAdapter = new ListViewAdapter(this, 0, zones);
        setListAdapter(mListViewAdapter);
    }

    public TimeZoneModel useLocation() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(bestProvider);

        TimeZoneModel timeZoneModel = new TimeZoneModel();
        TimeZone timeZone = TimeZone.getDefault();
        mCurrentLocation = "Current Zone";
        timeZoneModel.setCity(mCurrentLocation);
        timeZoneModel.setCountry(timeZone.getDisplayName());
        timeZoneModel.setTimeZoneId(timeZone.getID());
        timeZoneModel.setCalendar(Calendar.getInstance());

        mTimeZoneImpl = new TimeZoneImpl(this);

        Geocoder geoCoder = new Geocoder(this);

        try {
            if (location != null) {
                List<Address> address = geoCoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                PreferencesManager.getInstance().setString(CURRENT_LOCATION, address.get(0).getLocality());

                if (mTimeZoneImpl.getSingleCity(address.get(0).getLocality()) != null) {
                    mCurrentLocation = address.get(0).getLocality();
                    timeZoneModel = mTimeZoneImpl.getSingleCity(address.get(0).getLocality());
                }
            } else {
                return timeZoneModel;
            }

        } catch (IOException e) {
            Log.d("GeoCoder", "Error getting location.");

            String city = PreferencesManager.getInstance().getString(CURRENT_LOCATION);
            if (mTimeZoneImpl.getSingleCity(city) != null) {
                timeZoneModel = mTimeZoneImpl.getSingleCity(city);
                mCurrentLocation = city;
            }
        }

        return timeZoneModel;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case RESULT_OK:
                processRequest(requestCode, data);
                break;
            case RESULT_CANCELED:
                break;
        }
    }

    private final int REQUEST_CODE_SEARCH_ACTIVITY = 1001;

    private void processRequest(int requestCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_SEARCH_ACTIVITY:
                TimeZoneModel zoneModel = (TimeZoneModel) data.getExtras().get(Utils.TIME_ZONE);
                if (mListViewAdapter.getPosition(zoneModel) == -1) {
                    mListViewAdapter.add(zoneModel);
                } else {
                    Toast.makeText(this, "City Exists", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    /**
     * Method to update Preferences
     */
    public void updatePreference() {
        String newPreference = null;
        if (mListViewAdapter.getCount() > 0) {
            for (int i = 0; i < mListViewAdapter.getCount(); i++) {
                if (!mListViewAdapter.getItem(i).getCity().equals(mCurrentLocation)) {
                    if (newPreference == null) {
                        newPreference = mListViewAdapter.getItem(i).getCity() + ";";
                    } else {
                        newPreference = newPreference + mListViewAdapter.getItem(i).getCity() + ";";
                    }
                }
            }
        }
        PreferencesManager.getInstance().setString(Utils.KEY_CITIES, newPreference);
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


