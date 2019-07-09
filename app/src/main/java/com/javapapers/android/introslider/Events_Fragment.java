package com.javapapers.android.introslider;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

@SuppressLint("ValidFragment")
public class Events_Fragment extends Fragment {
    String[] Events ={};
    String[] Dates = {};
    String[] Times = {};
    String[] Times2 = {};
    String[] places ={};
    String [] summaries = {};
    int [] confirmed = {};
    ArrayList<Reminder> arrayOfReminders= new ArrayList<Reminder>();
   int user_id;
    @SuppressLint("ValidFragment")
    public Events_Fragment(int user_id, String[] events, String []dates,String[] times,  String[] times2 ,String[] places , int [] confirmed , String[] summaries) {
        this.Events = events;
        this.Dates=dates;
        this.Times = times;
        this.Times2 = times2;
        this.places = places;
        this.user_id = user_id;
        this.confirmed= confirmed;
        this.summaries = summaries;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events, container,false);
        Reminder newreminder;
        if(Events.length !=0){
            for (int i =0 ; i < Events.length;i++) {
                newreminder = new Reminder(Events[i], Dates[i], Times[i], Times2[i], places[i], confirmed[i] , summaries[i]);
                arrayOfReminders.add(newreminder);
            }

            ReminderAdapter adapter = new ReminderAdapter(getContext(), arrayOfReminders);
            ListView listView = (ListView)view.findViewById(R.id.list_events);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent event = new Intent(view.getContext(), EventActivity.class);
                    Reminder reminder = arrayOfReminders.get(i);
                    event.putExtra("content", reminder.event);
                    event.putExtra("location",reminder.place);
                    event.putExtra("confirmed", confirmed);
                    event.putExtra("date", reminder.date);
                    event.putExtra("time",reminder.time);
                    event.putExtra("time2" , reminder.time2);
                    event.putExtra("event_id", i);
                    event.putExtra("user_id", user_id);
                    event.putExtra("summary",reminder.summary);
                    startActivityForResult(event, i);
                }
            });
        }

        return view;
    }


}
