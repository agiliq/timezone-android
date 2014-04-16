package com.agiliq.timezoneconverter.core;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.agiliq.timezone.core.R;
import com.agiliq.timezoneconverter.data.Utils;
import com.agiliq.timezoneconverter.model.TimeZoneModel;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class ListViewAdapter extends ArrayAdapter<TimeZoneModel> {

    final String TAG = "ListViewAdapter";
    Vector<TimeZoneModel> mZones;

    CustomClockLinear mSelectedClock;
    int pos;
    Context mContext;
    TimeZoneImpl mTimeZoneImpl;

    static Map<TimeZoneModel, CustomClockLinear> customClocks = new HashMap<TimeZoneModel, CustomClockLinear>();

    public ListViewAdapter(Context context, int emptyId, Vector<TimeZoneModel> zones) {
        super(context, emptyId, zones);
        this.mContext = context;
        this.mTimeZoneImpl = new TimeZoneImpl(context);
        this.mZones = zones;
    }

    public int getCount() {
        return mZones.size();
    }

    public TimeZoneModel getItem(int position) {
        return mZones.elementAt(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        TimeZoneHolder holder = null;
        pos = position;

        holder = new TimeZoneHolder();
        holder.customClock = new CustomClockLinear(mContext);

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.custom_clock_linear, null);
            holder.customClock = (CustomClockLinear) row.findViewById(R.id.customClockLinear);
            holder.customClock.setPadding(3, 2, 3, 2);
            row.setTag(holder);
        } else {
            holder = (TimeZoneHolder) row.getTag();
        }

        holder.customClock.setTimeZoneModel(getItem(position));
        holder.customClock.customHolder.timeImage.setVisibility(View.VISIBLE);

        row.setOnTouchListener(new ListItemOnTouchListener());
        customClocks.put(getItem(position), holder.customClock);

        return row;
    }


    public void editTime(final TimeZoneModel object) {
        resetTime();
        CustomClockLinear editClock = customClocks.get(object);

        if (mSelectedClock == null) {
            mSelectedClock = editClock;
        } else {
            mSelectedClock.customHolder.timeSwitcherBar.setVisibility(View.GONE);
            mSelectedClock = editClock;
        }

        mSelectedClock.customHolder.timeSwitcherBar.setVisibility(View.VISIBLE);
        mSelectedClock.customHolder.timeSwitcherBar.setProgress(object.getCalendar().get(Calendar.HOUR_OF_DAY));

        mSelectedClock.customHolder.timeSwitcherBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                updateTime(progress, mSelectedClock, object);
            }
        });
    }

    public void resetTime() {
        for (TimeZoneModel zone : mZones) {
            try {
                mTimeZoneImpl = new TimeZoneImpl(mContext);
                customClocks.get(zone).updateTime(Utils.getCalendar(zone.getTimeZoneId()), true);
                customClocks.get(zone).customHolder.timeSwitcherBar.setVisibility(View.GONE);
            } catch (NullPointerException e) {
                Log.d(TAG, "Reset Time Null");
            }
        }
    }

    public void updateTime(int hour, CustomClockLinear customClockLinear, TimeZoneModel timeZone) {

        Calendar calendar = timeZone.getCalendar();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        customClockLinear.updateTime(calendar, false);
        calendar.getTime();

        long timeInMillis = calendar.getTimeInMillis();
        for (TimeZoneModel zone : mZones) {
            try {
                CustomClockLinear newClock = customClocks.get(zone);
                zone.getCalendar().setTimeInMillis(timeInMillis);
                newClock.updateTime(zone.getCalendar(), false);
            } catch (NullPointerException e) {
                Log.d(TAG, "Update Time Null");
            }
        }
    }

    @Override
    public void remove(TimeZoneModel object) {
        super.remove(object);
    }

    class ListItemOnTouchListener implements OnTouchListener {

        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                v.setBackgroundColor(mContext.getResources().getColor(R.color.holo_blue_light));
            }
            return false;
        }

    }

    /**
     * Holder class for the list Item
     *
     * @author Agiliq
     */
    static class TimeZoneHolder {
        CustomClockLinear customClock;
    }
}