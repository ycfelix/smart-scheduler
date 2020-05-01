package com.ust.timetable;


import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class TimetablePagerAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> fmList;

    public TimetablePagerAdapter(FragmentManager fm, List<Fragment> fmList) {
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

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }
}
