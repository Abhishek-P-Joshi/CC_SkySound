package com.example.amank.skysound;

/**
 * Created by Aman K on 10/29/2016.
 */

public class Constants {
    public interface ACTION{
        public static String MAIN_ACTION = "com.example.amank.skysound.action.main";
        public static String PREV_ACTION = "com.example.amank.skysound.action.prev";
        public static String PLAY_ACTION = "com.example.amank.skysound.action.play";
        public static String NEXT_ACTION = "com.example.amank.skysound.action.next";
        public static String STARTFOREGROUND_ACTION = "com.example.amank.skysound.action.startforeground";
        public static String STOPFOREGROUND_ACTION = "com.example.amank.skysound.action.stopforeground";
    }

    public interface NOTIFICATION_ID {
        public static int FOREGROUND_SERVICE = 101;
    }
}
