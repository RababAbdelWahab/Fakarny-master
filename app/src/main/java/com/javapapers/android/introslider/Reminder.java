package com.javapapers.android.introslider;

public class Reminder {
    String event,date  , time , time2, place, summary;
    int conf;

    public Reminder(String event, String date, String time, String time2, String place , int conf , String summary) {
        this.event = event;
        this.date = date;
        this.time = time;
        this.time2 = time2;
        this.place = place;
        this.conf = conf;
        this.summary = summary;
    }
}
