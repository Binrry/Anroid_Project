package com.tang.binrry.mysimplemp3player.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tang.binrry.mysimplemp3player.R;
import com.tang.binrry.mysimplemp3player.beans.MusicBean;
import com.tang.binrry.mysimplemp3player.services.PlayMusicService;
import com.tang.binrry.mysimplemp3player.utils.Util;

import java.util.List;


/**
 * Created by User on 2019/5/23.
 */

public class MusicListAdapter extends BaseAdapter{
    private Context context;
    private List<MusicBean> data;

    public MusicListAdapter(Context context, List<MusicBean> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView==null)
        {
            convertView= LayoutInflater.from(context).inflate(R.layout.item_music,parent,false);
            holder=new ViewHolder();
            holder.item_imgShowPic= (ImageView) convertView.findViewById(R.id.item_imgShowPic);
            holder.item_tvMusicName= (TextView) convertView.findViewById(R.id.item_tvMusicName);
            holder.item_tvMusicSinger= (TextView) convertView.findViewById(R.id.item_tvMusicSinger);
            holder.item_tvMusicDuration= (TextView) convertView.findViewById(R.id.item_tvMusicDuration);
            convertView.setTag(holder);

        }
        else
        {
            holder= (ViewHolder) convertView.getTag();
        }

        MusicBean bean=data.get(position);


        holder.item_tvMusicName.setText(bean.getMusicName());
        holder.item_tvMusicSinger.setText(bean.getSinger());
        holder.item_tvMusicDuration.setText(Util.toTime(bean.getMusicDuration()));

        if(position== PlayMusicService.currentIndex)
        {
            holder.item_imgShowPic.setImageResource(R.drawable.isplaying);
            convertView.setBackgroundColor(Color.parseColor("#ff0000"));
        }
        else
        {
            holder.item_imgShowPic.setImageResource(R.drawable.item);
            convertView.setBackgroundColor(Color.TRANSPARENT);
        }

        return convertView;
    }

    public void refreshSelectPosition() {
        notifyDataSetChanged();
    }

    private class ViewHolder {

       ImageView item_imgShowPic;
        TextView item_tvMusicName;
        TextView item_tvMusicSinger;
        TextView item_tvMusicDuration;

    }
}
