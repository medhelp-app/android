package com.medhelp.medhelp;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.medhelp.medhelp.adapters.SectionsPagerAdapter;

public class MainActivity extends AppCompatActivity {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mTabLayout = (TabLayout) findViewById(R.id.sections_tab);
        mViewPager = (ViewPager) findViewById(R.id.view_pager_mainTab);

        mViewPager.setAdapter(new SectionsPagerAdapter(getSupportFragmentManager(), getResources().getStringArray(R.array.titles_patientTab)));
        mTabLayout.setupWithViewPager(mViewPager);
    }

}
