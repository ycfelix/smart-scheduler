package com.ust.appusagechart;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import java.util.List;

public class SlidePageAdatper extends FragmentStatePagerAdapter {

    private List<Fragment> fmList;

    public SlidePageAdatper(FragmentManager fm, List<Fragment> fmList) {
        super(fm);
        this.fmList=fmList;

    }

    @Override
    public Fragment getItem(int i) {
        return fmList.get(i);
    }

    @Override
    public int getCount() {
        return fmList.size();
    }

}
