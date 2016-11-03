package com.example.amank.skysound;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.cleveroad.audiowidget.AudioWidget;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class UploadedSongs extends AppCompatActivity {

    private FloatingActionButton playPauseButton;
    PlayerService mBoundService;
    boolean mServiceBound = false;
    List<Song> songs = new ArrayList<>();
    ListView songsListView;
    public String androidId;

    public static AudioWidget audioWidget;
    private static final int OVERLAY_PERMISSION_REQ_CODE = 1;


    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            PlayerService.MyBinder myBinder = (PlayerService.MyBinder) service;
            mBoundService = myBinder.getService();
            mServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mServiceBound = false;
        }
    };

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isPlaying = intent.getBooleanExtra("isPlaying",false);
            flipPlayPauseButton(isPlaying);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploaded_songs);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
        }

        playPauseButton = (FloatingActionButton) findViewById(R.id.playpauseforuploadedsongs);
        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mServiceBound){
                    mBoundService.togglePlayer();
                }
            }
        });

        String url = "http://79.170.40.180/cloudatlas.com/Music_files/bensound-cute.mp3";
        songsListView = (ListView) findViewById(R.id.SongsListView);
        androidId = android.provider.Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        fetchSongsFromWeb(androidId);

        audioWidget = new AudioWidget.Builder(this).build();
    }

    public static AudioWidget sendAudioWidget(){
        return audioWidget;
    }
    //-----------------------------------------Checking for permissions to float the widget-------------------------------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OVERLAY_PERMISSION_REQ_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(UploadedSongs.this)) {
                onPermissionsNotGranted();
            }

        }
    }

    private void onPermissionsNotGranted() {
        Toast.makeText(this, "Permission Not granted", Toast.LENGTH_SHORT).show();
        finish();
    }

    //--------------------------------------------------------------------------------------------------------------------------

    private void startStreamingService(String url,String songtitle){

        Intent i = new Intent(this,PlayerService.class);
        i.putExtra("url",url);
        i.putExtra("Song_Title",songtitle);
        i.putExtra("UploadedSongs","UploadedSongs");
        i.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
        startService(i);
        bindService(i,mServiceConnection,Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mServiceBound){
            unbindService(mServiceConnection);
            mServiceBound = false;
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,new IntentFilter("changePlayButton"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

    public void flipPlayPauseButton (boolean isPlaying){
        if(isPlaying){
            playPauseButton.setImageResource(android.R.drawable.ic_media_pause);
        }else{
            playPauseButton.setImageResource(android.R.drawable.ic_media_play);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //----------------------------------------------------Fetching the songs from the web-----------------------------------------
    private void fetchSongsFromWeb( final String androidId){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection urlConnection = null;
                InputStream inputStream = null;

                try{
                    URL url = new URL("http://79.170.40.180/cloudatlas.com/uploads/getUploadedSongs.php?id="+androidId);
                    urlConnection = (HttpURLConnection)url.openConnection();
                    urlConnection.setRequestMethod("GET");

                    int statuscode= urlConnection.getResponseCode();
                    if(statuscode == 200){
                        inputStream = new BufferedInputStream((urlConnection.getInputStream()));
                        String response = convertInputStreamToString(inputStream);
                        Log.i("Got the songs",response);
                        parseIntoSongs (response,androidId);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }finally{
                    if(urlConnection != null){
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

        while((line = bufferedReader.readLine()) != null){
            result += line;
        }
        if(inputStream != null){
            inputStream.close();
        }
        return result;
    }

    private void parseIntoSongs (String data, String androidId){
        String[] dataArray = data.split(",,,");
        int i=0;

        for(i=0; i<dataArray.length;i++){
            String[] songArray = dataArray[i].split(",");
            Song song = new Song(null,songArray[0],"0","0");
            songs.add(song);
        }

        for (i=0;i<songs.size();i++){
            Log.i("GOT SONG",songs.get(i).getTitle());
        }

        populateSongsListView(androidId);
    }


    //--------------------------------------------Populating the List View in the app-------------------------------------------
    private void populateSongsListView(final String androidId){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SongListAdapter adapter = new SongListAdapter(UploadedSongs.this,songs);
                songsListView.setAdapter(adapter);
                songsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Song song = songs.get(position);
                        String songAddress = "http://79.170.40.180/cloudatlas.com/uploads/"+androidId+"/"+song.getTitle();
                        startStreamingService(songAddress,song.getTitle());
                    }
                });
            }
        });
    }

    //-----------------------------------------------Counting the number of plays of a song---------------------------------------
    private void markSongPlayed(final int chosenId){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                InputStream inputStream = null;
                HttpURLConnection urlConnection = null;

                try{
                    URL url = new URL("http://79.170.40.180/cloudatlas.com/Music_files/addplay.php?id="+ Integer.toString(chosenId));
                    urlConnection = (HttpURLConnection)url.openConnection();
                    urlConnection.setRequestMethod("GET");

                    int statuscode = urlConnection.getResponseCode();
                    if(statuscode == 200){
                        inputStream = new BufferedInputStream(urlConnection.getInputStream());
                        String response = convertInputStreamToString(inputStream);
                        Log.i("Played Song ID",response);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
                finally{
                    if(urlConnection !=null)
                        urlConnection.disconnect();
                }
            }
        });
        thread.start();

    }

    //-----------------------------------------Adding a dialog box to like a song-----------------------------------------------
    private void askForLikes(final Song song){
        new AlertDialog.Builder(this)
                .setTitle(song.getTitle())
                .setMessage("Do you like this song?")
                .setPositiveButton("YES!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        likeSong(song.getId());
                    }
                })
                .setNegativeButton("NO :(", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }

    //------------------------------------------------------Counting the number of Likes of a song--------------------------------
    private void likeSong(final int chosenId){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                InputStream inputStream = null;
                HttpURLConnection urlConnection = null;

                try{
                    URL url = new URL("http://79.170.40.180/cloudatlas.com/Music_files/addlike.php?id="+ Integer.toString(chosenId));
                    urlConnection = (HttpURLConnection)url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.getResponseCode();
                }catch(Exception e){
                    e.printStackTrace();
                }
                finally{
                    if(urlConnection !=null)
                        urlConnection.disconnect();
                }
            }
        });
        thread.start();

    }

}
