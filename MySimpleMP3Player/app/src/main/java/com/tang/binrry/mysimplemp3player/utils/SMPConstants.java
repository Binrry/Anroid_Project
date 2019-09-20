package com.tang.binrry.mysimplemp3player.utils;

/**
 * Created by User on 2019/5/27.
 */

public class SMPConstants {
    public static final int STATUS_STOP=0;
    public static final int STATUS_PLAY=1;
    public static final int STATUS_PAUSE=2;

    public static final int CMP_PLAY=1;
    public static final int CMP_PAUSE=2;
    public static final int CMP_CONTINUE=3;
    public static final int CMP_PREV=4;
    public static final int CMP_NEXT=5;
    public static final int CMP_GETINFORM=6;
    public static final int CMP_CHANDEPROGRESS=7;
    public static final int CMP_PLAYATPOSITION=8;

    public static final String ACT_SERVICE_REQUEST_BROADCAST=
            "cn.edu.szpt.MySimpleMP3Player.ResponseInform";
    public static final String ACT_LRC_RETURN_BROADCAST=
            "cn.edu.szpt.MySimpleMP3Player.ACT_LRC_RETURN_BROADCAST";
    public static final String ACT_PROGRESS_RETURN_BROADCAST=
            "cn.edu.szpt.MySimpleMP3Player.ACT_PROGRESS_RETURN_BROADCAST";

}
