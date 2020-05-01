package com.ust.timetable;

import android.support.annotation.NonNull;
import androidx.core.app.Fragment;
import androidx.core.app.FragmentManager;
import androidx.core.app.FragmentStatePagerAdapter;

import java.util.List;

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
