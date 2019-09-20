package com.tang.binrry.mysimplemp3player.beans;


import androidx.annotation.NonNull;

/**
 * Created by User on 2019/6/10.
 */

public class LrcBean implements Comparable<LrcBean>{
    private int beginTime;
    private String lrcMsg;

    public LrcBean(int beginTime, String lrcMsg) {
        this.beginTime = beginTime;
        this.lrcMsg = lrcMsg;
    }

    public int getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(int beginTime) {
        this.beginTime = beginTime;
    }

    public String getLrcMsg() {
        return lrcMsg;
    }

    public void setLrcMsg(String lrcMsg) {
        this.lrcMsg = lrcMsg;
    }

    public int compareTo(@NonNull LrcBean another)
    {
        return this.beginTime-another.beginTime;
    }
}
