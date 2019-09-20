package com.tang.binrry.mysimplemp3player.services;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.tang.binrry.mysimplemp3player.beans.LrcBean;
import com.tang.binrry.mysimplemp3player.beans.MusicBean;
import com.tang.binrry.mysimplemp3player.lrc.LrcProcessor;
import com.tang.binrry.mysimplemp3player.utils.SMPConstants;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import java.util.logging.LogRecord;


import static android.media.CamcorderProfile.get;
import static android.widget.Toast.*;

/**
 * Created by User on 2019/5/27.
 */

public class PlayMusicService extends Service {
    public static List<MusicBean> musicData;
    public static int currentIndex=-1;
    public int MpStatus;
    private MediaPlayer mp;

    private ArrayList<LrcBean>lrcs;
    private int nextTimeMil=0;
    private int LrcPos;
    private String message;
    private LrcCallBack r=null;
    private Handler lrcHandler= new Handler();

    private Handler prgHandler=new Handler();
    private PrgCallBack pr=null;

    class PrgCallBack implements Runnable{

        @Override
        public void run() {
            int time=mp.getCurrentPosition();
            Intent i=new Intent(SMPConstants.ACT_PROGRESS_RETURN_BROADCAST);
            i.putExtra("PROGRESS",time);
            sendBroadcast(i);
            prgHandler.postDelayed(this,300);
        }
    }

    class LrcCallBack implements Runnable{
        private ArrayList<LrcBean>lrcList;

        public LrcCallBack(ArrayList<LrcBean> lrcList) {
            this.lrcList = lrcList;
            LrcPos=0;
        }

        @Override
        public void run() {
            try {
                int time=mp.getCurrentPosition();
                Log.i("TIME",time+"");
                if(nextTimeMil==0)
                {
                    nextTimeMil=lrcList.get(LrcPos).getBeginTime();
                    message=lrcList.get(LrcPos).getLrcMsg();
                }
                Log.i("nextTimeMil",nextTimeMil+"");
                if(time>nextTimeMil)
                {
                    Intent i=new Intent(SMPConstants.ACT_LRC_RETURN_BROADCAST);
                    i.putExtra("LRC",message);
                    Log.i("test",message);
                    sendBroadcast(i);
                    LrcPos++;
                    nextTimeMil=lrcList.get(LrcPos).getBeginTime();
                    message=lrcList.get(LrcPos).getLrcMsg();
                }
                if(time<mp.getDuration())
                {
                    lrcHandler.postDelayed(this,10);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        musicData=new ArrayList<MusicBean>();
        setMusicData();
        if(musicData.size()>0) currentIndex=0;
        MpStatus=SMPConstants.STATUS_STOP;
        mp=new MediaPlayer();
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                nextMusic();

                sendPMSInform();
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent,int flags, int startId) {
        if(intent!=null)
        {
            int cmd=intent.getIntExtra("CMD",-1);
            int seekBar=intent.getIntExtra("seekBar",-1);
            switch(cmd)
            {
                case SMPConstants.CMP_GETINFORM:
                    Intent i= new Intent(SMPConstants.ACT_SERVICE_REQUEST_BROADCAST);
                    i.putExtra("index",currentIndex);
                    i.putExtra("status",MpStatus);
                    sendBroadcast(i);
                    break;
                case SMPConstants.CMP_PLAY:
                    playMusic();
                    break;
                case SMPConstants.CMP_NEXT:
                    nextMusic();
                    break;
                case SMPConstants.CMP_PAUSE:
                    pauseMusic();
                    break;
                case SMPConstants.CMP_CONTINUE:
                    continueMusic();
                    break;
                case SMPConstants.CMP_PREV:
                    prevMusic();
                    break;
                case SMPConstants.CMP_CHANDEPROGRESS:
                    mp.seekTo(seekBar);
                    changeMusic();
                    continueMusic();
                    break;
                case SMPConstants.CMP_PLAYATPOSITION:
                    playMusic();
                    break;
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void changeMusic() {
        String musicPath=musicData.get(currentIndex).getMusicUrl();
        initLrc(musicPath.substring(0,musicPath.length()-3)+"lrc");
        lrcHandler.post(r);
        prgHandler.post(pr);
        mp.start();
        MpStatus= SMPConstants.STATUS_PLAY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mp!=null)
        {
            mp.stop();
            mp.release();
        }
    }
    private void setMusicData(){
        musicData.clear();

        ContentResolver cr=getContentResolver();

        Cursor cursor= cr.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Media.TITLE,MediaStore.Audio.Media.DURATION,
                        MediaStore.Audio.Media.ARTIST,MediaStore.Audio.Media.ALBUM_ID,
                        MediaStore.Audio.Media.ALBUM_ID, MediaStore.Audio.Media.DATA},null,null,null);


        while(cursor.moveToNext()){
            String musicName=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
            String singer=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
            int musicDuration=cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
            int albumid=cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
            String musicUrl=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            String lrcUrl=musicUrl.substring(0,musicUrl.length()-3) + "lrc";

            MusicBean bean = new MusicBean(musicName,singer,musicDuration,albumid,musicUrl,lrcUrl);

            musicData.add(bean);
        }

        cursor.close();

    }

    private void sendPMSInform()
    {
        Intent i=new Intent(SMPConstants.ACT_SERVICE_REQUEST_BROADCAST);
        i.putExtra("index",currentIndex);
        i.putExtra("status", MpStatus);
        sendBroadcast(i);
    }

    private void pauseMusic()
    {
        mp.pause();
        MpStatus=SMPConstants.STATUS_PAUSE;
    }
    private void continueMusic()
    {
        mp.start();
        MpStatus=SMPConstants.STATUS_PLAY;
    }
    private void playMusic()
    {

        String musicPath=musicData.get(currentIndex).getMusicUrl();
        try {
            mp.reset();
            mp.setDataSource(musicPath);
            mp.prepare();
            initLrc(musicPath.substring(0,musicPath.length()-3)+"lrc");
            lrcHandler.post(r);
            prgHandler.post(pr);
            mp.start();
            MpStatus=SMPConstants.STATUS_PLAY;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void prevMusic()
    {
        if(currentIndex<=0)
        {
            currentIndex=musicData.size()-1;
        }
        else
        {
            currentIndex--;
        }
        playMusic();
        MpStatus=SMPConstants.STATUS_PLAY;
    }

    private void nextMusic()
    {
        if(currentIndex>=musicData.size()-1)
        {
            currentIndex=0;
        }
        else
        {
            currentIndex++;
        }
        playMusic();
        MpStatus=SMPConstants.STATUS_PLAY;
    }

    private void initLrc(String lrcPath)
    {

        InputStream in;
        try {
            String charset= LrcProcessor.getCharSet(new FileInputStream(lrcPath));
            LrcProcessor lrcProc=new LrcProcessor();
            in=new FileInputStream(lrcPath);
            lrcs=lrcProc.process(in,charset);
            if(r!=null)
            {
                lrcHandler.removeCallbacks(r);
            }
            r=new LrcCallBack(lrcs);
            if(pr!=null)
            {
                prgHandler.removeCallbacks(pr);
            }
            pr=new PrgCallBack();
            nextTimeMil=0;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}


















