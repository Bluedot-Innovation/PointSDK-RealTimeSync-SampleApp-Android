package com.bluedotinnovation.realtimesyncsampleapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTabHost;

import com.bluedotinnovation.realtimesyncsampleapp.fragments.LogFragment;
import com.bluedotinnovation.realtimesyncsampleapp.fragments.MapFragment;

/*
 * @author Bluedot Innovation
 * Copyright (c) 2018 Bluedot Innovation. All rights reserved.
 */

public class MainActivity extends AppCompatActivity {

    private FragmentTabHost fragmentTabHost;
    private final static int TAB_LOG = 0;
    public static String LOG_DATA = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
        setupTabs();
    }

    private void setupTabs() {

        fragmentTabHost.addTab(fragmentTabHost.newTabSpec("Log").setIndicator("Log"), LogFragment.class,null);
        fragmentTabHost.addTab(fragmentTabHost.newTabSpec("Map").setIndicator("Map"), MapFragment.class,null);
        fragmentTabHost.setCurrentTab(TAB_LOG);

    }

    private void initUI() {

        fragmentTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        fragmentTabHost.setup(this,getSupportFragmentManager(),android.R.id.tabcontent);

    }
}
