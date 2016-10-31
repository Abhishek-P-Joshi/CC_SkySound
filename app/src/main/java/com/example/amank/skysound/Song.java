package com.example.amank.skysound;

/**
 * Created by Aman K on 10/30/2016.
 */

public class Song {

    int Id;
    String title;
    int numPlays;
    int numLikes;

    public Song(String Id,String title , String numPlays, String numLikes){
        try{
            this.Id = Integer.parseInt(Id);
        }catch (Exception e){
            this.Id= 0;
        }
        this.title = title;
        this.numPlays = Integer.parseInt(numPlays);
        this.numLikes = Integer.parseInt(numLikes);
    }

    public int getId(){
        return Id;
    }

    public String getTitle(){
        return title;
    }

    public int getNumPlays(){
        return numPlays;
    }

    public int getNumLikes(){
        return numLikes;
    }
}
