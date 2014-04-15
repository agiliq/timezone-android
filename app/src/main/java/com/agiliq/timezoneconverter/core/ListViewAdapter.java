package com.agiliq.timezoneconverter.core;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.agiliq.timezone.core.R;
import com.agiliq.timezoneconverter.model.TimeZoneModel;
import com.agiliq.timezoneconverter.model.Utils;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class ListViewAdapter extends ArrayAdapter<TimeZoneModel> {

	final String TAG = "ListViewAdapter";
	Vector<TimeZoneModel> zones;

	CustomClockLinear selectedCustomClock;
	int pos;
    Context context;
    int emptyId;
    TimeZoneImpl timeZoneImpl;
    Filter filter;

    static Map<TimeZoneModel, CustomClockLinear> customClocks = new HashMap<TimeZoneModel, CustomClockLinear>();

	public ListViewAdapter(Context context, int emptyId, Vector<TimeZoneModel> zones)
	{
		super(context,emptyId, zones);
		this.context = context;
		timeZoneImpl = new TimeZoneImpl(context);
		this.zones = zones;
	}

	public int getCount() {
		return zones.size();
	}

	public TimeZoneModel getItem(int position) {
		return zones.elementAt(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		View row = convertView;
		TimeZoneHolder holder = null;
		pos = position;

		 holder = new TimeZoneHolder();
         holder.customClock = new CustomClockLinear(context);

		if(row == null){
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.custom_clock_linear, null);
            holder.customClock = (CustomClockLinear) row.findViewById(R.id.customClockLinear);
            holder.customClock.setPadding(3, 2, 3, 2);
            row.setTag(holder);
		}
		else{
			holder = (TimeZoneHolder)row.getTag();
		}

		 holder.customClock.setTimeZoneModel(zones.elementAt(position));
		 holder.customClock.customHolder.timeImage.setVisibility(View.VISIBLE);

		 row.setOnTouchListener(new ListItemOnTouchListener());
		 customClocks.put(zones.elementAt(position), holder.customClock);

		return row;
	}


	public void editTime(final TimeZoneModel object){
		resetTime();
		CustomClockLinear editClock = customClocks.get(object);

		if(selectedCustomClock == null){
			selectedCustomClock = editClock;
		}else{
			selectedCustomClock.customHolder.timeSwitcherBar.setVisibility(View.GONE);
			selectedCustomClock = editClock;
		}

		selectedCustomClock.customHolder.timeSwitcherBar.setVisibility(View.VISIBLE);
		selectedCustomClock.customHolder.timeSwitcherBar.setProgress(object.getCalendar().get(Calendar.HOUR_OF_DAY));

		selectedCustomClock.customHolder.timeSwitcherBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			public void onStopTrackingTouch(SeekBar seekBar) {}

			public void onStartTrackingTouch(SeekBar seekBar) {}

			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				updateTime(progress, selectedCustomClock, object);
			}
		});
	}

	public void resetTime(){
		for(TimeZoneModel zone : zones){
			try{
				timeZoneImpl = new TimeZoneImpl(context);
				customClocks.get(zone).updateTime(Utils.getCalendar(zone.getTimeZoneId()), true);
				customClocks.get(zone).customHolder.timeSwitcherBar.setVisibility(View.GONE);
			}catch(NullPointerException e){
				Log.d(TAG, "Reset Time Null");
				continue;
			}
		}
	}

	public void updateTime(int hour, CustomClockLinear customClockLinear, TimeZoneModel timeZone){

        Calendar calendar = timeZone.getCalendar();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        customClockLinear.updateTime(calendar, false);
        calendar.getTime();

        long timeInMillis = calendar.getTimeInMillis();
    	for(TimeZoneModel zone: zones){
    		try {
    			CustomClockLinear newClock = customClocks.get(zone);
        		zone.getCalendar().setTimeInMillis(timeInMillis);
        		newClock.updateTime(zone.getCalendar(), false);
			} catch (NullPointerException e) {
				Log.d(TAG, "Update Time Null");
				continue;
			}
    	}
	}

	@Override
	public void remove(TimeZoneModel object) {
		MainActivity.updatePreference();
		super.remove(object);
	}

	class ListItemOnTouchListener implements OnTouchListener{

		public boolean onTouch(View v, MotionEvent event) {
			if(event.getAction() == MotionEvent.ACTION_DOWN)
			{
				v.setBackgroundColor(context.getResources().getColor(R.color.holo_blue_light));
			}else if(event.getAction() == MotionEvent.ACTION_UP){}
			else if (event.getAction() == MotionEvent.ACTION_CANCEL){}
			return false;
		}

	}

	/**
	 * Holder class for the list Item
	 * @author Agiliq
	 *
	 */
	static class TimeZoneHolder
	{
		CustomClockLinear customClock;
	}
}