package com.tang.binrry.mysimplemp3player;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.tang.binrry.mysimplemp3player.beans.MusicBean;
import com.tang.binrry.mysimplemp3player.services.PlayMusicService;
import com.tang.binrry.mysimplemp3player.utils.SMPConstants;
import com.tang.binrry.mysimplemp3player.utils.Util;

import java.io.IOException;
import java.lang.reflect.GenericArrayType;


/**
 * Created by adminn on 2018/3/5.
 */

public class MusicPlayFragment extends Fragment implements View.OnClickListener {
    private MediaPlayer mp;
    private static int MpStatus;
    //播放按钮
    private ImageView btnPlay;
    //上一首按钮
    private ImageView btnPrev;
    //下一首按钮
    private ImageView btnNext;
    //显示歌曲名称
    private TextView tvMusicName;
    //显示歌曲时长
    private TextView tvDuration;
    //显示歌词
    private TextView tvLrc;
    //显示歌曲播放当前时间
    private TextView tvPlayTime;
    //显示进度条
    private SeekBar sbMusic;
    //显示专辑封面
    private ImageView imgShowPic;
    private LrcReceiver lrcReceiver;
    private PrgReceiver prgReceiver;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_music,container,false);

        tvMusicName = (TextView) view.findViewById(R.id.tvMusicName);
        tvPlayTime = (TextView) view.findViewById(R.id.tvPlayTime);
        tvDuration = (TextView) view.findViewById(R.id.tvDuration);
        tvLrc = (TextView) view.findViewById(R.id.tvLrc);
        sbMusic= (SeekBar) view.findViewById(R.id.sbMusic);
        sbMusic.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                pauseMusic();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Intent i= new Intent(getActivity(), PlayMusicService.class);
                i.putExtra("CMD", SMPConstants.CMP_CHANDEPROGRESS);
                i.putExtra("seekBar", seekBar.getProgress());
                getActivity().startService(i);
                btnPlay.setImageResource(R.drawable.pause_selector);
            }
        });
        imgShowPic= (ImageView) view.findViewById(R.id.imgShowPic);
        btnNext= (ImageView) view.findViewById(R.id.btnNext);
        btnPrev= (ImageView) view.findViewById(R.id.btnPrev);
        btnPlay= (ImageView) view.findViewById(R.id.btnPlay);
        iniView(PlayMusicService.currentIndex);
        btnPlay.setOnClickListener(this);
        btnPrev.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        MpStatus = SMPConstants.STATUS_STOP;
        lrcReceiver=new LrcReceiver();
        getActivity().registerReceiver(lrcReceiver,new IntentFilter(SMPConstants.ACT_LRC_RETURN_BROADCAST));
        prgReceiver=new PrgReceiver();
        getActivity().registerReceiver(prgReceiver,new IntentFilter(SMPConstants.ACT_PROGRESS_RETURN_BROADCAST));
        mp=new MediaPlayer();
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
           @Override
           public void onCompletion(MediaPlayer mp) {
               MusicBean bean=PlayMusicService.musicData.get(PlayMusicService.currentIndex);
               tvMusicName.setText(bean.getMusicName());
               Bitmap bmp=getAlbumArt(bean.getAlbum_id());
               if(bmp!=null)
               {
                   imgShowPic.setImageBitmap(bmp);
               }
               else
               {
                   imgShowPic.setImageResource(R.drawable.nopic);
               }
               nextMusic();
           }
       });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(lrcReceiver);
        getActivity().unregisterReceiver(prgReceiver);
    }

    private void iniView(int music_index)
   {
       if (music_index>-1)
       {
           MusicBean bean=PlayMusicService.musicData.get(music_index);
           tvMusicName.setText(bean.getMusicName());
           tvPlayTime.setText("0:0");
           tvDuration.setText(Util.toTime(bean.getMusicDuration()));
           tvLrc.setText("");

           sbMusic.setIndeterminate(false);
           sbMusic.setMax(bean.getMusicDuration());
        //   int album_id=bean.getAlbum_id();
           Bitmap bmp=getAlbumArt(bean.getAlbum_id());
           if(bmp!=null)
           {
               imgShowPic.setImageBitmap(bmp);
           }
           else
           {
               imgShowPic.setImageResource(R.drawable.nopic);
           }
       }


   }

   private Bitmap getAlbumArt(int album_id)
   {
       Bitmap bmp = null;
       ContentResolver cr = getActivity().getContentResolver();
       Cursor cursor=cr.query(ContentUris.withAppendedId(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,album_id),null,null,null,null);
       if(cursor.moveToNext())
       {
           String imgurl=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
           bmp= BitmapFactory.decodeFile(imgurl);
       }
       return bmp;
   }

   private void pauseMusic()
   {
       Intent i= new Intent(getActivity(),PlayMusicService.class);
       i.putExtra("CMD",SMPConstants.CMP_PAUSE);
       getActivity().startService(i);
       MpStatus=SMPConstants.STATUS_PAUSE;
       btnPlay.setImageResource(R.drawable.play_selector);
   }
    private void continueMusic()
    {
        Intent i= new Intent(getActivity(),PlayMusicService.class);
        i.putExtra("CMD",SMPConstants.CMP_CONTINUE);
        getActivity().startService(i);
        MpStatus=SMPConstants.STATUS_PLAY;
        btnPlay.setImageResource(R.drawable.pause_selector);
    }
    private void playMusic()
    {
        Intent i= new Intent(getActivity(),PlayMusicService.class);
        i.putExtra("CMD",SMPConstants.CMP_PLAY);
        getActivity().startService(i);
        MpStatus=SMPConstants.STATUS_PLAY;
        btnPlay.setImageResource(R.drawable.pause_selector);
        tvLrc.setText("");
    }
    private void prevMusic()
    {
        Intent i= new Intent(getActivity(),PlayMusicService.class);
        i.putExtra("CMD",SMPConstants.CMP_PREV);
        getActivity().startService(i);
        if(MainActivity.currentIndex<=0)
        {
            MainActivity.currentIndex=PlayMusicService.musicData.size()-1;
        }
        else
        {
            MainActivity.currentIndex--;
        }
        MpStatus=SMPConstants.STATUS_PLAY;
        tvLrc.setText("");
        btnPlay.setImageResource(R.drawable.pause_selector);
        iniView(MainActivity.currentIndex);
    }
    private void nextMusic()
    {
        Intent i= new Intent(getActivity(),PlayMusicService.class);
        i.putExtra("CMD",SMPConstants.CMP_NEXT);
        getActivity().startService(i);
        if(MainActivity.currentIndex>=PlayMusicService.musicData.size()-1)
        {
            MainActivity.currentIndex=0;
        }
        else
        {
            MainActivity.currentIndex++;
        }
        MpStatus=SMPConstants.STATUS_PLAY;
        btnPlay.setImageResource(R.drawable.pause_selector);
        iniView(MainActivity.currentIndex);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btnPlay:
                switch (MpStatus)
                {
                    case SMPConstants.STATUS_PAUSE:
                        continueMusic();
                        break;
                    case SMPConstants.STATUS_PLAY:
                        pauseMusic();
                        break;
                    case SMPConstants.STATUS_STOP:
                        playMusic();
                        break;
                    default:
                        break;
                }
                break;
            case R.id.btnPrev:
                prevMusic();
                break;
            case R.id.btnNext:
                nextMusic();
                break;
            default:
                break;

        }
        MainActivity.musicListAdapter.refreshSelectPosition();

    }
    public void setMPStatus(int mpstatus) {
        iniView(MainActivity.currentIndex);
        MpStatus=mpstatus;
        if(mpstatus==SMPConstants.STATUS_PLAY)
        {
            btnPlay.setImageResource(R.drawable.pause_selector);
        }
        else
        {
            btnPlay.setImageResource(R.drawable.play_selector);
        }
    }

    class LrcReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String msg=intent.getStringExtra("LRC");
            tvLrc.setText(msg);
        }
    }

    class PrgReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            int time=intent.getIntExtra("PROGRESS",0);
            sbMusic.setProgress(time);
            tvPlayTime.setText(Util.toTime(time));
            MusicBean bean=PlayMusicService.musicData.get(PlayMusicService.currentIndex);
            tvMusicName.setText(bean.getMusicName());
            int cmd=intent.getIntExtra("CMD",-1);
           // btnPlay.setImageResource(R.drawable.pause_selector);

            Bitmap bmp=getAlbumArt(bean.getAlbum_id());
            if(bmp!=null)
            {
                imgShowPic.setImageBitmap(bmp);
            }
            else
            {
                imgShowPic.setImageResource(R.drawable.nopic);
            }
        }
    }

}
