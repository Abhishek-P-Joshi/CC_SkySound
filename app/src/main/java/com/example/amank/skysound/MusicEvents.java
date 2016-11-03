package com.example.amank.skysound;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MusicEvents extends AppCompatActivity {

    List<Events> eventsList = new ArrayList<>();
    ListView eventListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_events);

        eventListView = (ListView) findViewById(R.id.eventListView);
        fetchEventsFromWeb();


    }

    private void fetchEventsFromWeb() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection urlConnection = null;
                InputStream inputStream = null;

                try {
                    URL url = new URL("http://79.170.40.180/cloudatlas.com/Events/getevent.php");
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");

                    int statuscode = urlConnection.getResponseCode();
                    if (statuscode == 200) {
                        inputStream = new BufferedInputStream((urlConnection.getInputStream()));
                        String response = convertInputStreamToString(inputStream);
                        Log.i("Got the songs", response);
                        parseIntoSongs(response);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }
            }
        });
        thread.start();
    }

    private String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        String line = "";
        String result = "";

        while ((line = bufferedReader.readLine()) != null) {
            result += line;
        }
        if (inputStream != null) {
            inputStream.close();
        }
        return result;
    }

    private void parseIntoSongs(String data) {
        String[] dataArray1 = data.split("\\*");
        int i = 0;

        for (i = 0; i < dataArray1.length; i++) {
            String[] eventArray = dataArray1[i].split(",");
            Events events = new Events(eventArray[0], eventArray[1], eventArray[2], eventArray[3], eventArray[4]);
            eventsList.add(events);
        }

        for (i = 0; i < eventsList.size(); i++) {
            Log.i("GOT EVENT", eventsList.get(i).getDate());
        }
        populateEventsListView();

    }

    private void populateEventsListView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                EventListAdapter adapter = new EventListAdapter(MusicEvents.this, eventsList);
                eventListView.setAdapter(adapter);
                eventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Events events = eventsList.get(position);
                        new AlertDialog.Builder(MusicEvents.this).setTitle(events.getEventName()).setMessage(events.getDate() + "\n " + "Genre : " +events.getGenre() + " \n Place : " + events.getLocation()).show();
                    }
                });
            }
        });
    }

}

