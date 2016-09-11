package com.thegads.uliza.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.thegads.uliza.fragment.MainFragment;
import com.thegads.uliza.fragment.MessageFragment;
import com.thegads.uliza.fragment.PastpaperFragment;
import com.thegads.uliza.fragment.TimetableFragment;

import java.util.ArrayList;

public class FragmentViewPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> fragments = new ArrayList<>();
    private Fragment currentFragment;

    public FragmentViewPagerAdapter(FragmentManager fm) {
        super(fm);
        fragments.clear();
        fragments.add(new MainFragment());
        fragments.add(new TimetableFragment());
        fragments.add(new PastpaperFragment());
        fragments.add(new MessageFragment());
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        if (getCurrentFragment() != object) {
            currentFragment = ((Fragment) object);
        }
        super.setPrimaryItem(container, position, object);
    }

    /**
     * Get the current fragment
     */
    public Fragment getCurrentFragment() {
        return currentFragment;
    }
}