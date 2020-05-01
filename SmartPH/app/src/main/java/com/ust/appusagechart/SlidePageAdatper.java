package com.ust.appusagechart;


import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

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
