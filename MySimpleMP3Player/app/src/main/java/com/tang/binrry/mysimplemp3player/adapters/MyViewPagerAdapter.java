package com.tang.binrry.mysimplemp3player.adapters;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by adminn on 2018/3/5.
 */

public class MyViewPagerAdapter extends FragmentPagerAdapter {
    //存储需要添加到ViewPager上的Fragment
    private ArrayList<Fragment> fragments;

    public MyViewPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
