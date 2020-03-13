package com.ust.timetable;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import java.util.List;

public class TimetableAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> fmList;

    public TimetableAdapter(FragmentManager fm, List<Fragment> fmList) {
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
