package com.tang.binrry.mysimplemp3player.utils;

/**
 * Created by User on 2019/5/23.
 */

public class Util {
    public static String toTime(int time)
    {
        time /= 1000;
        int mintue = time / 60;
        int hour = mintue / 60;
        mintue = mintue % 60;
        int second = time % 60;

        if(hour>0)
        {
            return String.format("%02d:%02d:02d",hour,mintue,second);
        }
        else
            return String.format("%02d:%02d",mintue,second);
    }
}
