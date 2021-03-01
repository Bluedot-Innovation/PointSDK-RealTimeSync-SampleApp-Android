package com.bluedotinnovation.realtimesyncsampleapp;

import android.content.Context;

import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.List;

import au.com.bluedot.point.net.engine.GeoTriggeringEventReceiver;
import au.com.bluedot.point.net.engine.ZoneEntryEvent;
import au.com.bluedot.point.net.engine.ZoneExitEvent;
import au.com.bluedot.point.net.engine.ZoneInfo;

public class BluedotGeoTriggerReceiver extends GeoTriggeringEventReceiver {
    @Override
    public void onZoneInfoUpdate(@NotNull List<ZoneInfo> list, @NotNull Context context) {
        MainActivity.LOG_DATA = MainActivity.LOG_DATA
                + "\nZones updated at: " + new Date().toString()
                + "\nZoneInfos count: " + list.size();
    }

    @Override
    public void onZoneEntryEvent(@NotNull ZoneEntryEvent zoneEntryEvent, @NotNull Context context) {
        MainActivity.LOG_DATA = MainActivity.LOG_DATA
                + "\nZones entered at: " + new Date().toString()
                + "\n" + zoneEntryEvent.toString();
    }

    @Override
    public void onZoneExitEvent(@NotNull ZoneExitEvent zoneExitEvent, @NotNull Context context) {
        MainActivity.LOG_DATA = MainActivity.LOG_DATA
                + "\nZones exited at: " + new Date().toString()
                + "\n" + zoneExitEvent.toString();
    }
}
