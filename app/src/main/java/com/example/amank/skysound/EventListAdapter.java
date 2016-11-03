package com.example.amank.skysound;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by aniketwani on 11/2/16.
 */

public class EventListAdapter extends BaseAdapter {

    private Activity event_activity;
    private List<Events> eventAdapterList;
    private static LayoutInflater eventInflater = null;

    public EventListAdapter(Activity a, List<Events> s){
        event_activity = a;
        eventAdapterList = s;
        eventInflater = (LayoutInflater) event_activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount(){
        return eventAdapterList.size();

    }
    public Object getItem(int position){
        return position;
    }

    public long getItemId(int position){
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        View v1 = convertView;
        if(convertView == null){
            v1 = eventInflater.inflate(R.layout.eventlistview_row, parent, false);
        }
        TextView title = (TextView) v1.findViewById(R.id.eventListRowText);
        //TextView date = (TextView) v1.findViewById(R.id.eventdate);
        Events event2 =  eventAdapterList.get(position);
        title.setText(event2.getEventName());
        //date.setText(event2.getDate());
        return v1;




    }



}
