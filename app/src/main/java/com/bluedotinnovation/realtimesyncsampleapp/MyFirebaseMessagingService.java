package com.bluedotinnovation.realtimesyncsampleapp;

import android.content.Intent;

import androidx.annotation.NonNull;

import com.bluedotinnovation.realtimesyncsampleapp.fragments.LogFragment;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


import java.util.Date;

import au.com.bluedot.point.net.engine.ServiceManager;


/*
 * @author Bluedot Innovation
 * Copyright (c) 2018 Bluedot Innovation. All rights reserved.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService{


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        logInfo("Push received at: " + new Date().toString());
        ServiceManager serviceManager = ServiceManager.getInstance(this);
        serviceManager.notifyPushUpdate(remoteMessage.getData());

    }

    @Override
    public void onNewToken(@NonNull String token) {
        logInfo("Token registerred: " + token);
    }

    private void logInfo(String logInfo){
        Intent intent = new Intent();
        intent.setAction(LogFragment.TEXT_LOG_BROADCAST);
        intent.putExtra("logInfo", logInfo);
        sendBroadcast(intent);
    }
}
