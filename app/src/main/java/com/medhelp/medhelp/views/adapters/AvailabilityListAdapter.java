package com.medhelp.medhelp.views.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.medhelp.medhelp.R;
import com.medhelp.medhelp.model.Availability;
import com.medhelp.medhelp.model.EWeekDays;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class AvailabilityListAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private String[] availabilityKeys;
    private HashMap<String, List<Availability>> availabilities;

    public AvailabilityListAdapter(Activity activity, HashMap<String, List<Availability>> availabilities) {
        this.activity = activity;
        this.availabilityKeys = availabilities.keySet().toArray(new String[availabilities.size()]);
        this.availabilities = availabilities;
    }

    @Override
    public int getCount() {
        return availabilities.size();
    }

    @Override
    public Object getItem(int location) {
        return availabilities.get(availabilityKeys[location]);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (view == null)
            view = inflater.inflate(R.layout.dialog_availabilities_list, null);

        String key = availabilityKeys[i];
        List<Availability> availabilitiesList = availabilities.get(key);

        TextView weekday = (TextView) view.findViewById(R.id.text_weekday_availabilitiesList);
        weekday.setText(EWeekDays.valueOf(availabilitiesList.get(0).getWeekday()).getValue());

        StringBuilder hours = new StringBuilder();
        for (Availability availability : availabilitiesList) {
            hours.append(availability.getStartHour() + " - " + availability.getEndHour() + "\n");
        }

        ArrayAdapter<String> list = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_list_item_1, Arrays.asList(hours.toString()));
        ListView lv = (ListView) view.findViewById(R.id.list_hours_availabilitiesList);
        lv.setAdapter(list);

        return view;
    }

}
