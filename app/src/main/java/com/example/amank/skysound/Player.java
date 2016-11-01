package com.example.amank.skysound;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;

/**
 * Created by Aman K on 10/25/2016.
 */

public class Player {

    MediaPlayer mediaPlayer = new MediaPlayer();
    public static Player player;
    String url = "";

    public Player(){
        this.player = this;
    }

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
                    //SkySoundSongs.flipPlayPauseButton(false);
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
            //SkySoundSongs.flipPlayPauseButton(false);

        }catch (Exception e){
            Log.d("EXCEPTION ","Failed to pause the media player");
        }
    }

    public void playPlayer(){
        try{
            mediaPlayer.start();
            //SkySoundSongs.flipPlayPauseButton(true);

        }catch (Exception e){
            Log.d("EXCEPTION ","Failed to Start the media player");
        }
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
}
