package com.tang.binrry.mysimplemp3player;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.tang.binrry.mysimplemp3player.adapters.MusicListAdapter;
import com.tang.binrry.mysimplemp3player.adapters.MyViewPagerAdapter;
import com.tang.binrry.mysimplemp3player.services.PlayMusicService;
import com.tang.binrry.mysimplemp3player.utils.SMPConstants;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private ViewPager pager;
    private PagerAdapter mAdapter;
    private ArrayList<Fragment> fragments;
    public static MusicListAdapter musicListAdapter;
    private StatusReceiver statusReceiver;
    public static int currentIndex;
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(statusReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        statusReceiver=new StatusReceiver();
        registerReceiver(statusReceiver,new IntentFilter(SMPConstants.ACT_SERVICE_REQUEST_BROADCAST));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode!=1000){
            Toast.makeText(this,"请授予读取外部存储的权限", Toast.LENGTH_SHORT).show();
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1000);
        }
        /* if(PlayMusicService.musicData.size()>0) PlayMusicService.currentIndex=0;
         musicListAdapter = new MusicListAdapter(this,PlayMusicService.musicData);
 */
        //初始化控件，获取ViewPager对象
        pager = (ViewPager) findViewById(R.id.pager);
        Intent intent = new Intent(MainActivity.this, PlayMusicService.class);
        intent.putExtra("CMD", SMPConstants.CMP_GETINFORM);
        startService(intent);
        //  initViewPager();

    }


    private void initViewPager() {
        mAdapter = new MyViewPagerAdapter(getSupportFragmentManager(), fragments);
        pager.setAdapter(mAdapter);
        // 设置当前显示的是位置在第一个的view
        pager.setCurrentItem(0);

    }


    class StatusReceiver extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent) {
            currentIndex=intent.getIntExtra("index",-1);
            int mpstatus = intent.getIntExtra("status",-1);
            if(fragments ==null)
            {
                musicListAdapter=new MusicListAdapter(getApplicationContext(),PlayMusicService.musicData);
                fragments = new ArrayList<Fragment>();
                MusicPlayFragment f1 = new MusicPlayFragment();
                MusicListFragment f2 = new MusicListFragment();
                fragments.add(f1);
                fragments.add(f2);
                initViewPager();
            }
            ((MusicPlayFragment)fragments.get(0)).setMPStatus(mpstatus);
        }
    }
}
