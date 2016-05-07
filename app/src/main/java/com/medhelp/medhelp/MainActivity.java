package com.medhelp.medhelp;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.medhelp.medhelp.adapters.SectionsPagerAdapter;

public class MainActivity extends AppCompatActivity {

    private TabLayout _tabLayout;
    private ViewPager _viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        _tabLayout = (TabLayout) findViewById(R.id.sections_tab);
        _viewPager = (ViewPager) findViewById(R.id.view_pager_mainTab);

        _viewPager.setAdapter(new SectionsPagerAdapter(getSupportFragmentManager(), getResources().getStringArray(R.array.titles_patientTab)));
        _tabLayout.setupWithViewPager(_viewPager);
    }

}
