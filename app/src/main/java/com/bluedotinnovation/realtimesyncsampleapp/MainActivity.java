package com.bluedotinnovation.realtimesyncsampleapp;

import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.widget.TabHost;

import com.bluedotinnovation.realtimesyncsampleapp.fragments.LogFragment;
import com.bluedotinnovation.realtimesyncsampleapp.fragments.MapFragment;

import au.com.bluedot.point.net.engine.ServiceManager;

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
