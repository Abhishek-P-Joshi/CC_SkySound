package com.example.amank.skysound;

/**
 * Created by aniketwani on 11/2/16.
 */

public class Events {
    int id;
    String eventName;
    String date;
    String location;
    String genre;

    public Events(String id, String eventName, String date, String location, String genre){
        this.id = Integer.parseInt(id);
        this.eventName = eventName;
        this.date = date;
        this.location = location;
        this.genre = genre;
    }

    public int getId(){
        return id;
    }
    public String getEventName(){
        return eventName;
    }
    public String getDate(){
        return date;
    }
    public String getLocation(){
        return location;
    }
    public String getGenre(){
        return genre;
    }
}
