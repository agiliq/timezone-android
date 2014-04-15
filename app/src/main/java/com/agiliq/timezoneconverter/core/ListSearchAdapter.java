package com.agiliq.timezoneconverter.core;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.agiliq.timezone.core.R;
import com.agiliq.timezoneconverter.model.TimeZoneModel;

import java.util.Vector;

public class ListSearchAdapter extends ArrayAdapter<TimeZoneModel> {

	Vector<TimeZoneModel> zones;
	Context context;
	TimeZoneImpl timeZoneImpl;

	public ListSearchAdapter(Context context, int textViewResourceId, Vector<TimeZoneModel> zones) {
		super(context, textViewResourceId);

		this.context = context;
		this.zones = zones;
		timeZoneImpl = new TimeZoneImpl(context);
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

		 row.setOnTouchListener( new ListItemOnTouchListener());

		return row;
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


	/*class SearchFilter extends Filter{

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			FilterResults results = new FilterResults();
			String query = constraint.toString().toLowerCase(Locale.ENGLISH);

			Vector<TimeZoneModel> fZones = new Vector<TimeZoneModel>(zones);

			if( query.length() == 0 || query == null){
				results.values = fZones;
				results.count = fZones.size();

			}else{

				Vector<TimeZoneModel> sZones = new Vector<TimeZoneModel>();

				for(TimeZoneModel zone: fZones){
					if(zone.getCity().startsWith(query)){
						sZones.add(zone);
					}
				}
				results.values = sZones;
				results.count = sZones.size();
			}

			return results;
		}

		@Override
		protected void publishResults(CharSequence constraint, FilterResults result) {

		}

	}*/

}
