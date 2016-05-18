package com.medhelp.medhelp.views.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.medhelp.medhelp.fragments.doctor.DoctorPatientsFragment;
import com.medhelp.medhelp.fragments.doctor.DoctorPrescriptionFragment;
import com.medhelp.medhelp.fragments.doctor.DoctorProfileFragment;
import com.medhelp.medhelp.fragments.doctor.DoctorSocialFragment;

public class DoctorPagerAdapter extends FragmentPagerAdapter{

    private String[] tabTitles;

    public DoctorPagerAdapter(FragmentManager fm, String[] tabTitles) {
        super(fm);
        this.tabTitles = tabTitles;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new DoctorProfileFragment();
            case 1:
                return new DoctorPatientsFragment();
            case 2:
                return new DoctorPrescriptionFragment();
            case 3:
                return new DoctorSocialFragment();
            default:
                return new DoctorProfileFragment();
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
