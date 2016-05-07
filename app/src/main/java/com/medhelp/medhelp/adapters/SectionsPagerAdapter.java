package com.medhelp.medhelp.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.medhelp.medhelp.PatientDoctorFragment;
import com.medhelp.medhelp.PatientFilesFragment;
import com.medhelp.medhelp.PatientProfileFragment;
import com.medhelp.medhelp.PatientSocialFragment;

public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

    private String[] tabTitles;

    public SectionsPagerAdapter(FragmentManager fm, String[] tabTitles) {
        super(fm);
        this.tabTitles = tabTitles;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new PatientProfileFragment();
            case 1:
                return new PatientDoctorFragment();
            case 2:
                return new PatientFilesFragment();
            case 3:
                return new PatientSocialFragment();
            default:
                return new PatientProfileFragment();
        }
    }

    @Override
    public int getCount() {
        return this.tabTitles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return this.tabTitles[position];
    }
}
