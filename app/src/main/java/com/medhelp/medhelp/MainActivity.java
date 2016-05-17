package com.medhelp.medhelp;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.medhelp.medhelp.views.adapters.SectionsPagerAdapter;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.sections_tab);
        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager_mainTab);

        viewPager.setAdapter(new SectionsPagerAdapter(getSupportFragmentManager(), getResources().getStringArray(R.array.titles_patientTab)));
        tabLayout.setupWithViewPager(viewPager);
    }

}
