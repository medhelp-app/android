package com.medhelp.medhelp.activities.patient;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.medhelp.medhelp.R;
import com.medhelp.medhelp.views.adapters.SectionsPagerAdapter;

public class MainActivity extends AppCompatActivity {

    private TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTabLayout = (TabLayout) findViewById(R.id.sections_tab);
        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager_mainTab);

        viewPager.setAdapter(new SectionsPagerAdapter(getSupportFragmentManager(), getResources().getStringArray(R.array.titles_patientTab)));
        mTabLayout.setupWithViewPager(viewPager);

        setupTabIcons();
    }

    private void setupTabIcons() {
        mTabLayout.getTabAt(0).setIcon(R.drawable.ic_accessibility_white_24dp);
        mTabLayout.getTabAt(1).setIcon(R.drawable.ic_account_circle_white_24dp);
        mTabLayout.getTabAt(2).setIcon(R.drawable.ic_folder_white_24dp);
        mTabLayout.getTabAt(3).setIcon(R.drawable.ic_chat_white_24dp);
    }

}
