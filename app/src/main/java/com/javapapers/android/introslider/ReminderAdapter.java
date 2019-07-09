package com.javapapers.android.introslider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ReminderAdapter extends ArrayAdapter<Reminder> {
    public ReminderAdapter(Context context, ArrayList<Reminder> reminders) {
        super(context, 0, reminders);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Reminder reminder = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.card_event, parent, false);
        }
        // Lookup view for data population
        TextView event = (TextView) convertView.findViewById(R.id.event);
        TextView date = (TextView) convertView.findViewById(R.id.date);
        TextView place = (TextView) convertView.findViewById(R.id.place);
        TextView time = (TextView) convertView.findViewById(R.id.time);
        TextView time2 = (TextView) convertView.findViewById(R.id.time2);
        //TextView summary = (TextView)convertView.findViewById(R.id.summary);
        if (reminder.conf == 1){
            FloatingActionButton done = (FloatingActionButton) convertView.findViewById(R.id.fab3);
            done.setVisibility(View.VISIBLE);

        }
        // Populate the data into the template view using the data object
        event.setText(reminder.event);
        date.setText(reminder.date);
        time.setText(reminder.time);
        time2.setText(reminder.time2);
        place.setText(reminder.place);
        //summary.setText(reminder.summary);


        // Return the completed view to render on screen
        return convertView;
    }
}