package com.agiliq.timezoneconverter.core;

import android.content.Context;
import android.database.ContentObserver;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.agiliq.timezone.core.R;
import com.agiliq.timezoneconverter.model.TimeZoneModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;


/**
 * Displays the time
 */
public class CustomClockLinear extends LinearLayout{

	public static Map<String, Typeface> fonts = new HashMap<String, Typeface>();

	//private String TAG = "CustomClockLinear";
	private final static String m12 = "h:mm";
	private final static String m24 = "k:mm";
    private Calendar mCalendar;
    private String mFormat;


    private Runnable mTicker;
	private boolean mTickerStopped = false;
    private ContentObserver mFormatChangeObserver;
    private boolean mLive = true;
    private boolean mAttached;
    private Context mContext;

    CustomHolder customHolder;

    private TimeZoneModel timeZoneModel;

    // called by system on minute ticks
    private final Handler mHandler = new Handler();

    private class FormatChangeObserver extends ContentObserver {
        public FormatChangeObserver() {
            super(new Handler());
        }
        @Override
        public void onChange(boolean selfChange) {
            setDateFormat();
            updateTime();
        }
    }

    public CustomClockLinear(Context context) {
        this(context, null);
        mContext = context;
        if(fonts.isEmpty()){
	        Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Light.ttf");
			fonts.put("Roboto-Light", tf);
			tf = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Medium.ttf");
			fonts.put("Roboto-Medium", tf);
			tf = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Regular.ttf");
			fonts.put("Roboto-Regular", tf);
			tf = Typeface.createFromAsset(context.getAssets(), "fonts/Clockopia.ttf");
			fonts.put("Clockopia", tf);
        }
    }

    public CustomClockLinear(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        getViews();
        mCalendar = Calendar.getInstance();
        setDateFormat();
    }

    /**
     * Method to get Views of the Custom Clock
     */
    private void getViews(){
    	customHolder = new CustomHolder();
    	customHolder.mTimeDisplay = (TextView) findViewById(R.id.timeDisplay);
    	customHolder.city = (TextView)findViewById(R.id.city);

    	customHolder.country = (TextView)findViewById(R.id.country);

    	customHolder.date = (TextView) findViewById(R.id.date);

    	customHolder.timeImage = (ImageView) findViewById(R.id.timeImage);

    	customHolder.mAmPm = new AmPm(this);

    	customHolder.timeSwitcherBar = (SeekBar) findViewById(R.id.time_switcher_seekbar);
    	customHolder.timeSwitcherBar.setVisibility(View.GONE);

    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (mAttached) return;
        mAttached = true;

         //monitor 12/24-hour display preference
        mFormatChangeObserver = new FormatChangeObserver();
        mContext.getContentResolver().registerContentObserver(
                Settings.System.CONTENT_URI, true, mFormatChangeObserver);

        mTickerStopped = false;

	    /**
	     * requests a tick on the next hard-second boundary
	     */
	    mTicker = new Runnable() {
	            public void run() {
	                if (mTickerStopped) return;
	                updateTime();
	                invalidate();
	                long now = SystemClock.uptimeMillis();
	                long next = now + (1000 - now % 1000);
	                mHandler.postAtTime(mTicker, next);
	            }
	        };
	    mTicker.run();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        mContext.getContentResolver().unregisterContentObserver(
                mFormatChangeObserver);
    }

    public void setTimeZoneModel(TimeZoneModel timeZoneModel){

    	this.timeZoneModel = timeZoneModel;

    	customHolder.city.setText(timeZoneModel.getCity());
    	customHolder.country.setText(timeZoneModel.getCountry());
    	customHolder.mTimeDisplay.setTypeface(fonts.get("Clockopia"));
    	customHolder.city.setTypeface(fonts.get("Roboto-Medium"));
    	customHolder.country.setTypeface(fonts.get("Roboto-Light"));
    	customHolder.country.setPadding(2, 3, 0, 5);
    	customHolder.date.setTypeface(fonts.get("Roboto-Light"));
    	customHolder.date.setPadding(0, 3, 5, 5);

    	timeZoneModel.getCalendar().getTime();
		mCalendar = timeZoneModel.getCalendar();
		updateTime();
    }

    /**
     * Method to Update Calendar
     * @param c
     */
    void updateTime(Calendar c, boolean live) {
    	mLive = live;
        mCalendar = c;
        updateTime();
    }

    /**
     * Method to Update time
     */
    private void updateTime() {
        if (mLive) {
            mCalendar.setTimeInMillis(System.currentTimeMillis());
        }

        CharSequence newTime = DateFormat.format(mFormat, mCalendar);
        customHolder.mTimeDisplay.setText(newTime);

        customHolder.mAmPm.setShowAmPm(mFormat == m12);
        timeZoneModel.setCalendar(mCalendar);
        //mCalendar.getTime();
        customHolder.mAmPm.setIsMorning(mCalendar.get(Calendar.AM_PM) == 0);

    	if(timeZoneModel.isMorning()){
    		customHolder.timeImage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.sun));
    		this.setBackgroundColor(getResources().getColor(R.color.list_item_day));
		}else{
			customHolder.timeImage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.moon));
			this.setBackgroundColor(getResources().getColor(R.color.list_item_night));
		}
    	java.text.DateFormat dF = SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM);
    	dF.setTimeZone(TimeZone.getTimeZone(timeZoneModel.getTimeZoneId()));
    	customHolder.date.setText(dF.format(mCalendar.getTime()));
    }

    /**
	 * Pulls 12/24 mode from system settings
	 */
	private boolean get24HourMode() {
	    return android.text.format.DateFormat.is24HourFormat(mContext);
	}

	/**
	 * Method to set Date format
	 */
    private void setDateFormat() {
    	 if (get24HourMode()) {
		        mFormat = m24;
		    } else {
		        mFormat = m12;
		    }
    	 customHolder.mAmPm.setShowAmPm(mFormat == m12);
    }

    /**
     * Class to set Am and Pm
     * @author Agiliq
     *
     */
    static class AmPm {
        private TextView mAmPm;

        AmPm(View parent) {

            mAmPm = (TextView) parent.findViewById(R.id.am_pm);
            mAmPm.setTypeface(fonts.get("Roboto-Medium"));
        }

        void setShowAmPm(boolean show) {
        	mAmPm.setVisibility(show ? View.VISIBLE : View.GONE);
        }

        void setIsMorning(boolean isMorning) {
        	mAmPm.setText(isMorning ? "AM" : "PM");
        }
    }

    static class CustomHolder{
    	AmPm mAmPm;
        TextView mTimeDisplay;
        TextView city;
    	TextView country;
    	TextView date;
    	ImageView timeImage;
    	SeekBar timeSwitcherBar;
    }
}
