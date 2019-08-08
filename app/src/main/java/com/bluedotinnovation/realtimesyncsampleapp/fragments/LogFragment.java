package com.bluedotinnovation.realtimesyncsampleapp.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bluedotinnovation.realtimesyncsampleapp.MainActivity;
import com.bluedotinnovation.realtimesyncsampleapp.R;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Date;
import java.util.List;

import au.com.bluedot.point.ServiceStatusListener;
import au.com.bluedot.point.net.engine.BDError;
import au.com.bluedot.point.net.engine.ServiceManager;
import au.com.bluedot.point.net.engine.ZoneInfo;

import static android.app.Notification.PRIORITY_MAX;

/*
 * @author Bluedot Innovation
 * Copyright (c) 2018 Bluedot Innovation. All rights reserved.
 */

public class LogFragment extends Fragment implements ServiceStatusListener {


    ServiceManager serviceManager;
    TextView tvLog;
    View view;
    LogReceiver logReceiver;
    public static final String TEXT_LOG_BROADCAST = "realtimesyncsampleapp.logtextbroadcast";
    Handler handler;

    //Bluedot Credentials
    private final String BLUEDOT_API_KEY = "0811c6a0-0251-11e9-aebf-02e673959816";

    private static final int PERMISSION_REQUEST_CODE = 101;

    public LogFragment() {
        // Required empty public constructor
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        handler = new Handler();
        logReceiver = new LogReceiver();
        getActivity().registerReceiver(logReceiver, new IntentFilter(TEXT_LOG_BROADCAST));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_log, container,false);
        return view;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUI();
        if(MainActivity.LOG_DATA!=null) {
            tvLog.append(MainActivity.LOG_DATA);
        }
        initBluedotSDK();

    }

    private void initUI() {
        tvLog = (TextView) getActivity().findViewById(R.id.tvLog);
        tvLog.setMovementMethod(new ScrollingMovementMethod());
    }


    @Override
    public void onBlueDotPointServiceStartedSuccess() {
        updateLog("Bluedot Point SDK authenticated");

    }

    @Override
    public void onBlueDotPointServiceStop() {

    }

    @Override
    public void onBlueDotPointServiceError(BDError bdError) {
        updateLog("Bluedot Point SDK error: " + bdError.getReason());
    }

    @Override
    public void onRuleUpdate(List<ZoneInfo> list) {
        updateLog("Zones updated at: " + new Date().toString() + "\nZoneInfos count: " + list.size());
    }


    private  void initBluedotSDK() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || (checkPermission() && Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP)) {
            serviceManager = ServiceManager.getInstance(getContext());
            if (!serviceManager.isBlueDotPointServiceRunning()) {
                // Setting Notification for foreground service, required for Android Oreo and above.
                // Setting targetAllAPIs to TRUE will display foreground notification for Android versions lower than Oreo
                serviceManager.setForegroundServiceNotification(createNotification(), false);
                serviceManager.sendAuthenticationRequest(BLUEDOT_API_KEY, this);
                FirebaseMessaging.getInstance().subscribeToTopic("/topics/" + BLUEDOT_API_KEY);
            }
        } else  {
            requestLocationPermission();
        }

    }

    private void updateLog(final String s) {
        handler.post(new Runnable() {
            @Override
            public void run() {

                if(!isVisible()) {
                    MainActivity.LOG_DATA = MainActivity.LOG_DATA + "\n" + s;
                } else {
                    tvLog.append("\n" + s);
                }

            }
        });

    }


    @Override
    public void onPause() {
        super.onPause();
        MainActivity.LOG_DATA = tvLog.getText().toString();
    }

    public final class LogReceiver extends BroadcastReceiver {

        public LogReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String logInfo = intent.getStringExtra("logInfo");
            updateLog(logInfo);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(logReceiver);
    }


    /**
     * Checks for status of required Location permission
     * @return - status of required permission
     */
    private boolean checkPermission() {
        int status_fine = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);
        int status_coarse = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION);
        return (status_fine == PackageManager.PERMISSION_GRANTED) && (status_coarse == PackageManager.PERMISSION_GRANTED);
    }

    /**
     * Displays user dialog for runtime permission request
     */
    private void requestLocationPermission() {
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:

                boolean permissionGranted = true;
                for(Integer i : grantResults) {
                    permissionGranted = permissionGranted && (i == PackageManager.PERMISSION_GRANTED);
                }

                if(permissionGranted) {
                    initBluedotSDK();
                } else {

                    if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) || shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION) ) {

                        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                        alertDialog.setTitle("Information");
                        alertDialog.setMessage(getResources().getString(R.string.permission_needed));
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                    } else {
                        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                        alertDialog.setTitle("Information");
                        alertDialog.setMessage(getResources().getString(R.string.location_permissions_mandatory));
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                                        intent.setData(uri);
                                        startActivityForResult(intent, PERMISSION_REQUEST_CODE);
                                    }
                                });
                        alertDialog.show();
                    }


                }
        }
    }


    /**
     * Creates notification channel and notification, required for foreground service notification.
     * @return notification
     */
    private Notification createNotification() {

        String channelId;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channelId = "Bluedot" + getString(R.string.app_name);
            String channelName = "Bluedot Service" + getString(R.string.app_name);
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.enableLights(false);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(false);
            NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);

            Notification.Builder notification = new Notification.Builder(getContext(), channelId)
                    .setContentTitle(getString(R.string.foreground_notification_title))
                    .setContentText(getString(R.string.foreground_notification_text))
                    .setStyle(new Notification.BigTextStyle().bigText(getString(R.string.foreground_notification_text)))
                    .setOngoing(true)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .setSmallIcon(R.mipmap.ic_launcher);

            return notification.build();
        } else {

            NotificationCompat.Builder notification = new NotificationCompat.Builder(getContext())
                    .setContentTitle(getString(R.string.foreground_notification_title))
                    .setContentText(getString(R.string.foreground_notification_text))
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(getString(R.string.foreground_notification_text)))
                    .setOngoing(true)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .setPriority(PRIORITY_MAX)
                    .setSmallIcon(R.mipmap.ic_launcher);

            return notification.build();
        }
    }

}
