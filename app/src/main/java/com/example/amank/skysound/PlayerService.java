package com.example.amank.skysound;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.cleveroad.audiowidget.AudioWidget;

import java.io.IOException;

public class PlayerService extends Service {

    MediaPlayer mediaPlayer = new MediaPlayer();
    private final IBinder mBinder = new MyBinder();
    private String songtitle;
    public AudioWidget audioWidget;

    public class MyBinder extends Binder{
        PlayerService getService(){
            return PlayerService.this;
        }
    }


    public PlayerService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent.getStringExtra("url")!=null)
            playStream(intent.getStringExtra("url"));

        if(intent.getStringExtra("Song_Title")!=null)
            songtitle = intent.getStringExtra("Song_Title");


        if(intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION)){
            Log.i("INFO", "Start foreground Service");
            showNotification(songtitle);
        }
        else if(intent.getAction().equals(Constants.ACTION.PREV_ACTION)){
            Log.i("INFO", "Prev Pressed");
        }
        else if(intent.getAction().equals(Constants.ACTION.PLAY_ACTION)){
            Log.i("INFO", "Play Pressed");
            togglePlayer();
        }
        else if(intent.getAction().equals(Constants.ACTION.NEXT_ACTION)){
            Log.i("INFO", "Next Pressed");
        }
        else if(intent.getAction().equals(Constants.ACTION.STOPFOREGROUND_ACTION)){
            Log.i("INFO", "Stop Foreground Received");
            stopForeground(true);
            stopSelf();
        }
//-------------------------------------Creating a floating audio widget--------------------------------------------------------
        if(intent.getStringExtra("SkySoundSongsIntent")!= null)
            audioWidget = SkySoundSongs.sendAudioWidget();
        else
            audioWidget = UploadedSongs.sendAudioWidget();

        audioWidget.controller().onControlsClickListener(new AudioWidget.OnControlsClickListener() {
            @Override
            public boolean onPlaylistClicked() {
                return false;
            }

            @Override
            public void onPreviousClicked() {

            }

            @Override
            public boolean onPlayPauseClicked() {
                togglePlayer();
                return true;
            }

            @Override
            public void onNextClicked() {

            }

            @Override
            public void onAlbumClicked() {

            }
        });

        audioWidget.controller().onWidgetStateChangedListener(new AudioWidget.OnWidgetStateChangedListener() {
            @Override
            public void onWidgetStateChanged(@NonNull AudioWidget.State state) {

            }

            @Override
            public void onWidgetPositionChanged(int cx, int cy) {

            }
        });

        audioWidget.show(100,100);

        return START_STICKY;
    }

//-----------------------------------------------Displaying the notification bar-------------------------------------------------
    private void showNotification(String songtitle){
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);
        //notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        Intent previousIntent = new Intent(this, PlayerService.class);
        previousIntent.setAction(Constants.ACTION.PREV_ACTION);
        PendingIntent ppreviousIntent = PendingIntent.getService(this,0,previousIntent,0);

        Intent playIntent = new Intent(this, PlayerService.class);
        playIntent.setAction(Constants.ACTION.PLAY_ACTION);
        PendingIntent pplayIntent = PendingIntent.getService(this,0,playIntent,0);

        Intent nextIntent = new Intent(this, PlayerService.class);
        nextIntent.setAction(Constants.ACTION.NEXT_ACTION);
        PendingIntent pnextIntent = PendingIntent.getService(this,0,nextIntent,0);

        Bitmap icon = BitmapFactory.decodeResource(getResources(),R.drawable.skysound);

        int playPauseButtonID = android.R.drawable.ic_media_play;
        if(mediaPlayer !=null && mediaPlayer.isPlaying())
            playPauseButtonID = android.R.drawable.ic_media_pause;

        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("SkySound")
                .setTicker("Playing Music")
                .setContentText(songtitle)
                .setSmallIcon(R.drawable.skysound)
                .setLargeIcon(Bitmap.createScaledBitmap(icon,128,128,false))
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .addAction(android.R.drawable.ic_media_previous,"Previous",ppreviousIntent)
                .addAction(playPauseButtonID,"Play",pplayIntent)
                .addAction(android.R.drawable.ic_media_next,"Next",pnextIntent)
                .build();

        startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,notification);

    }
//--------------------------------------------------------------------------------------------------------------------------------
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    //------------------------------------------------Music Player Streaming Functions-----------------------------------------------
    public void playStream(String url){
        if(mediaPlayer != null){
            try{
                mediaPlayer.stop();
            }catch (Exception e){

            }
            mediaPlayer = null;
        }
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try{
            mediaPlayer.setDataSource(url);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    playPlayer();
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    flipPlayPauseButton(false);
                }
            });
            mediaPlayer.prepareAsync();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void pausePlayer(){
        try{
            mediaPlayer.pause();
            flipPlayPauseButton(false);
            showNotification(songtitle);
            unregisterReceiver(noisyAudioStreamReceiver);

        }catch (Exception e){
            Log.d("EXCEPTION ","Failed to pause the media player");
        }
    }

    public void playPlayer(){
        try{
            getAudioFocusAndPlay();
            flipPlayPauseButton(true);
            showNotification(songtitle);

        }catch (Exception e){
            Log.d("EXCEPTION ","Failed to Start the media player");
        }
    }

    public void flipPlayPauseButton (boolean isPlaying){
        Intent intent = new Intent("changePlayButton");
        intent.putExtra("isPlaying",isPlaying);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public void togglePlayer(){
        try{
            if(mediaPlayer.isPlaying()){
                pausePlayer();
            }else{
                playPlayer();
            }
        }catch (Exception e){
            Log.d("EXCEPTION ","Failed to toggle the media player");
        }
    }

    //-------------------------------------------Requesting or Relinquishing Audio Focus---------------------------------------

    private AudioManager am;
    private boolean playingBeforeInterruption = false;

    public void getAudioFocusAndPlay(){
        am = (AudioManager) this.getBaseContext().getSystemService(Context.AUDIO_SERVICE);

        //request Audio Focus
        int result = am.requestAudioFocus(afChangeListener,AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN);

        if(result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED){
            mediaPlayer.start();
            registerReceiver(noisyAudioStreamReceiver,intentFilter);
        }
    }

    AudioManager.OnAudioFocusChangeListener afChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int i) {
            if(i == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT){
                if(mediaPlayer.isPlaying()){
                    playingBeforeInterruption = true;
                }else{
                    playingBeforeInterruption = false;
                }
                pausePlayer();
            }else if(i == AudioManager.AUDIOFOCUS_GAIN){
                if(playingBeforeInterruption)
                    playPlayer();
            }else if(i == AudioManager.AUDIOFOCUS_LOSS){
                pausePlayer();
                am.abandonAudioFocus(afChangeListener);
            }
        }
    };

    //------------------------------------------Audio Rerouted(Earphone Interruption handling)---------------------------------
    private class NoisyAudioStreamReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if(AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())){
                pausePlayer();
            }
        }
    }

    private IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
    private NoisyAudioStreamReceiver noisyAudioStreamReceiver = new NoisyAudioStreamReceiver();

}
