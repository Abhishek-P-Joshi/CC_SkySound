package com.example.amank.skysound;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private List<String> ThingsYouCanDo = new ArrayList<>();
    private ListView ThingsYouCanDoList;
    private ArrayAdapter<String> ThingsToDoAdapter;

//    static FloatingActionButton playPauseButton;
//    PlayerService mBoundService;
//    boolean mServiceBound = false;
//    List<Song> songs = new ArrayList<>();
//    ListView songsListView;
//
//
//    private ServiceConnection mServiceConnection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName componentName, IBinder service) {
//            PlayerService.MyBinder myBinder = (PlayerService.MyBinder) service;
//            mBoundService = myBinder.getService();
//            mServiceBound = true;
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName componentName) {
//            mServiceBound = false;
//        }
//    };
//
//    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            boolean isPlaying = intent.getBooleanExtra("isPlaying",false);
//            flipPlayPauseButton(isPlaying);
//        }
//    };
//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ThingsYouCanDoList = findViewById()
        ThingsYouCanDo.add("SkySound Songs");
        ThingsYouCanDo.add("Upload Your Own Song");
        ThingsYouCanDo.add("Uploaded Songs");
        ThingsYouCanDo.add("Events");

        ThingsYouCanDoList = (ListView) findViewById(R.id.thingsToDo);

        ThingsToDoAdapter = new ArrayAdapter<String>(this, R.layout.mainactivitylist_row,ThingsYouCanDo);

        ThingsYouCanDoList.setAdapter(ThingsToDoAdapter);

        ThingsYouCanDoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    Intent intent = new Intent(MainActivity.this,SkySoundSongs.class);
                    startActivity(intent);
                }
                if(position == 1){
                    Intent intent = new Intent(MainActivity.this,UploadYourSong.class);
                    startActivity(intent);
                }
                if(position == 2){
                    Intent intent = new Intent(MainActivity.this,UploadedSongs.class);
                    startActivity(intent);
                }
                if(position == 3){
                    Intent intent = new Intent(MainActivity.this,MusicEvents.class);
                    startActivity(intent);
                }
            }
        });

    }
//
//    private void startStreamingService(String url){
//
//        Intent i = new Intent(this,PlayerService.class);
//        i.putExtra("url",url);
//        i.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
//        startService(i);
//        bindService(i,mServiceConnection,Context.BIND_AUTO_CREATE);
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        if(mServiceBound){
//            unbindService(mServiceConnection);
//            mServiceBound = false;
//        }
//    }
//
//    @Override
//    protected void onResume(){
//        super.onResume();
//        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,new IntentFilter("changePlayButton"));
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
//    }
//
//    public static void flipPlayPauseButton (boolean isPlaying){
//        if(isPlaying){
//            playPauseButton.setImageResource(android.R.drawable.ic_media_pause);
//        }else{
//            playPauseButton.setImageResource(android.R.drawable.ic_media_play);
//        }
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
//
//    private void fetchSongsFromWeb(){
//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                HttpURLConnection urlConnection = null;
//                InputStream inputStream = null;
//
//                try{
//                    URL url = new URL("http://79.170.40.180/cloudatlas.com/Music_files/getmusic.php");
//                    urlConnection = (HttpURLConnection)url.openConnection();
//                    urlConnection.setRequestMethod("GET");
//
//                    int statuscode= urlConnection.getResponseCode();
//                    if(statuscode == 200){
//                        inputStream = new BufferedInputStream((urlConnection.getInputStream()));
//                        String response = convertInputStreamToString(inputStream);
//                        Log.i("Got the songs",response);
//                        parseIntoSongs (response);
//                    }
//                }catch(Exception e){
//                    e.printStackTrace();
//                }finally{
//                    if(urlConnection != null){
//                        urlConnection.disconnect();
//                    }
//                }
//            }
//        });
//        thread.start();
//    }
//
//    private String convertInputStreamToString(InputStream inputStream) throws IOException{
//        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
//
//        String line = "";
//        String result = "";
//
//        while((line = bufferedReader.readLine()) != null){
//            result += line;
//        }
//        if(inputStream != null){
//            inputStream.close();
//        }
//        return result;
//    }
//
//    private void parseIntoSongs (String data){
//        String[] dataArray = data.split("\\*");
//        int i=0;
//
//        for(i=0; i<dataArray.length;i++){
//            String[] songArray = dataArray[i].split(",");
//            Song song = new Song(songArray[0],songArray[1],songArray[2],songArray[3]);
//            songs.add(song);
//        }
//
//        for (i=0;i<songs.size();i++){
//            Log.i("GOT SONG",songs.get(i).getTitle());
//        }
//
//        populateSongsListView();
//    }
//
//    private void populateSongsListView(){
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                SongListAdapter adapter = new SongListAdapter(MainActivity.this,songs);
//                songsListView.setAdapter(adapter);
//                songsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                        Song song = songs.get(position);
//                        String songAddress = "http://79.170.40.180/cloudatlas.com/Music_files/"+song.getTitle();
//                        startStreamingService(songAddress);
//                        markSongPlayed(song.getId());
//                        askForLikes(song);
//
//                    }
//                });
//            }
//        });
//    }
//
//    private void markSongPlayed(final int chosenId){
//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                InputStream inputStream = null;
//                HttpURLConnection urlConnection = null;
//
//                try{
//                    URL url = new URL("http://79.170.40.180/cloudatlas.com/Music_files/addplay.php?id="+ Integer.toString(chosenId));
//                    urlConnection = (HttpURLConnection)url.openConnection();
//                    urlConnection.setRequestMethod("GET");
//
//                    int statuscode = urlConnection.getResponseCode();
//                    if(statuscode == 200){
//                        inputStream = new BufferedInputStream(urlConnection.getInputStream());
//                        String response = convertInputStreamToString(inputStream);
//                        Log.i("Played Song ID",response);
//                    }
//                }catch(Exception e){
//                    e.printStackTrace();
//                }
//                finally{
//                    if(urlConnection !=null)
//                        urlConnection.disconnect();
//                }
//            }
//        });
//        thread.start();
//
//    }
//
//    private void askForLikes(final Song song){
//        new AlertDialog.Builder(this)
//                .setTitle(song.getTitle())
//                .setMessage("Do you like this song?")
//                .setPositiveButton("YES!", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        likeSong(song.getId());
//                    }
//                })
//                .setNegativeButton("NO :(", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//
//                    }
//                })
//                .setIcon(android.R.drawable.ic_dialog_alert)
//                .show();
//
//    }
//
//    private void likeSong(final int chosenId){
//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                InputStream inputStream = null;
//                HttpURLConnection urlConnection = null;
//
//                try{
//                    URL url = new URL("http://79.170.40.180/cloudatlas.com/Music_files/addlike.php?id="+ Integer.toString(chosenId));
//                    urlConnection = (HttpURLConnection)url.openConnection();
//                    urlConnection.setRequestMethod("GET");
//                    urlConnection.getResponseCode();
//                }catch(Exception e){
//                    e.printStackTrace();
//                }
//                finally{
//                    if(urlConnection !=null)
//                        urlConnection.disconnect();
//                }
//            }
//        });
//        thread.start();
//
//    }
}
