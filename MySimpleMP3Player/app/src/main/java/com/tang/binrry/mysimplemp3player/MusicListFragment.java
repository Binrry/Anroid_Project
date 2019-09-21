package com.tang.binrry.mysimplemp3player;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.tang.binrry.mysimplemp3player.services.PlayMusicService;
import com.tang.binrry.mysimplemp3player.utils.SMPConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by adminn on 2018/3/5.
 */

public class MusicListFragment extends Fragment {
    private ListView listView;
    public static int MpStatus;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_musiclist,container,false);
        listView= (ListView) view.findViewById(R.id.musiclist);
        listView.setAdapter(MainActivity.musicListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PlayMusicService.currentIndex=position;
                MainActivity.musicListAdapter.refreshSelectPosition();
                Intent i= new Intent(getActivity(),PlayMusicService.class);
                i.putExtra("CMD", SMPConstants.CMP_PLAYATPOSITION);
                i.putExtra("index", PlayMusicService.currentIndex);
                MpStatus=SMPConstants.STATUS_PLAY;
                i.putExtra("status",MpStatus);
                getActivity().startService(i);
            }
        });
        return view;
    }

}
