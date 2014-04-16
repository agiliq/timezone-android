package com.agiliq.timezoneconverter.core;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.agiliq.timezone.core.R;
import com.agiliq.timezoneconverter.data.Utils;
import com.agiliq.timezoneconverter.model.TimeZoneModel;

import java.util.Locale;
import java.util.Vector;

public class SearchActivity extends ListActivity {

    ListSearchAdapter mListViewSearchAdapter;
    TimeZoneImpl mTimeZoneImpl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mTimeZoneImpl = new TimeZoneImpl(this);

        TextView textView = (TextView) findViewById(R.id.textViewSearch);
        getListView().setItemsCanFocus(true);

        //List view on Touch listener
        getListView().setOnTouchListener(new OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    hideSoftKeyboard();
                }
                return false;
            }
        });

        //listView on Click listener
        getListView().setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {

                Intent resultIntent = new Intent();
                resultIntent.putExtra(Utils.TIME_ZONE, mListViewSearchAdapter.getItem(position));
                setResult(RESULT_OK, resultIntent);
                finish();

            }

        });


        //get data when change in textView
        TextWatcher watcher = new TextWatcher() {

            @SuppressLint("DefaultLocale")
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null) {
                    Vector<TimeZoneModel> result = mTimeZoneImpl.setZoneData(s.toString().toLowerCase(Locale.ENGLISH));
                    if (result != null) {
                        mListViewSearchAdapter = new ListSearchAdapter(SearchActivity.this, 0, result);
                        setListAdapter(mListViewSearchAdapter);
                    }
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        };
        textView.addTextChangedListener(watcher);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
    private class GetTimeZoneData extends AsyncTask<String, Void, Vector<TimeZoneModel>>{

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Vector<TimeZoneModel> doInBackground(String... params) {
			return timeZoneImpl.setZoneData(params[0]);
		}

		@Override
		protected void onPostExecute(Vector<TimeZoneModel> result) {
			if(result == null)
            {
                //no zones available
            }
            else{
            	//timeZoneImpl.setZoneData(params[0]);
            	listViewSearchAdapter = new ListSearchAdapter(context, 0, result);
                setListAdapter(listViewSearchAdapter);
            }

			super.onPostExecute(result);
		}
    }*/

    public void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }
}
