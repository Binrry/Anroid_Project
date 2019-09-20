package com.tang.binrry.mysimplemp3player.beans;

/**
 * Created by User on 2019/5/23.
 */

public class MusicBean {

    private String musicName;
    private String singer;
    private int musicDuration;

    private int album_id;
    private String musicUrl;
    private String lrcUrl;

    public MusicBean(String musicName, String singer, int musicDuration, int album_id, String musicUrl, String lrcUrl) {
        this.musicName = musicName;
        this.singer = singer;
        this.musicDuration = musicDuration;
        this.album_id = album_id;
        this.musicUrl = musicUrl;
        lrcUrl = lrcUrl;
    }

    public String getMusicName() {
        return musicName;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public int getMusicDuration() {
        return musicDuration;
    }

    public void setMusicDuration(int musicDuration) {
        this.musicDuration = musicDuration;
    }

    public int getAlbum_id() {
        return album_id;
    }

    public void setAlbum_id(int album_id) {
        this.album_id = album_id;
    }

    public String getMusicUrl() {
        return musicUrl;
    }

    public void setMusicUrl(String musicUrl) {
        this.musicUrl = musicUrl;
    }

    public String getLrcUrl() {
        return lrcUrl;
    }

    public void setLrcUrl(String lrcUrl) {
        lrcUrl = lrcUrl;
    }
}
