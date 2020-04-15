package com.bluedotinnovation.realtimesyncsampleapp.fragments;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.bluedotinnovation.realtimesyncsampleapp.model.BoundingBoxItem;
import com.bluedotinnovation.realtimesyncsampleapp.model.CircleItem;
import com.bluedotinnovation.realtimesyncsampleapp.model.LineStringItem;
import com.bluedotinnovation.realtimesyncsampleapp.model.PolygonItem;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import au.com.bluedot.model.geo.BoundingBox;
import au.com.bluedot.model.geo.Circle;
import au.com.bluedot.model.geo.LineString;
import au.com.bluedot.model.geo.Point;
import au.com.bluedot.model.geo.Polygon;
import au.com.bluedot.point.ServiceStatusListener;
import au.com.bluedot.point.net.engine.BDError;
import au.com.bluedot.point.net.engine.FenceInfo;
import au.com.bluedot.point.net.engine.ServiceManager;
import au.com.bluedot.point.net.engine.ZoneInfo;


/*
 * @author Bluedot Innovation
 * Copyright (c) 2018 Bluedot Innovation. All rights reserved.
 */

public class MapFragment extends SupportMapFragment implements ServiceStatusListener, LocationListener {


    GoogleMap map;
    ArrayList<ZoneInfo> zoneList = null;
    ServiceManager serviceManager;
    private LocationManager mLocationManager;
    Handler handler;

    // Acceptable accuracy for location updates, in meters
    private final static float ACCURACY_LEVEL_m = 50.0f;

    // Animation timeout for zooming-in clicked cluster
    private final static int CAMERA_ANIMATION_ms = 500;

    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        serviceManager = ServiceManager.getInstance(getActivity());
        if(serviceManager.isBlueDotPointServiceRunning()) {
            serviceManager.addBlueDotPointServiceStatusListener(this);
        }
        handler = new Handler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mapView = super.onCreateView(inflater,container,savedInstanceState);
        FrameLayout frameLayout = new FrameLayout(getActivity());
        frameLayout.addView(mapView,new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT));

        return frameLayout;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                map.setBuildingsEnabled(true);
                if(getLastKnownPosition()!=null){
                    CameraPosition cameraPosition = CameraPosition.fromLatLngZoom(
                            getLastKnownPosition(), 12);
                    map.moveCamera(CameraUpdateFactory
                            .newCameraPosition(cameraPosition));
                }

                mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                map.getUiSettings().setZoomControlsEnabled(true);
                loadFences();

            }
        });
    }

    @Override
    public void onBlueDotPointServiceStartedSuccess() {

    }

    @Override
    public void onBlueDotPointServiceStop() {

    }

    @Override
    public void onBlueDotPointServiceError(BDError bdError) {

    }

    @Override
    public void onRuleUpdate(List<ZoneInfo> list) {

        handler.post(new Runnable() {
            @Override
            public void run() {
                loadFences();
            }
        });

    }


    public void loadFences() {
        map.clear();
        zoneList = serviceManager.getZonesAndFences();
        for (ZoneInfo zoneInfo : zoneList) {
            try {
                if (map != null) {
                    for (FenceInfo fence : zoneInfo.getFences()) {
                        displayFenceOnMap(fence, zoneInfo);
                    }

                }
            } catch (Exception e) {

                e.printStackTrace();
            }
        }

    }

    /**
     * Put the fence on the map
     */
    private void displayFenceOnMap(FenceInfo fence, ZoneInfo zoneInfo) {
        int color = 0x55880000;
        if (fence.getGeometry() instanceof Circle) {
            Circle circle = (Circle) fence.getGeometry();
            LatLng latLong = new LatLng(circle.getCenter().getLatitude(),
                    circle.getCenter().getLongitude());
            CircleOptions circleOptions = new CircleOptions().center(latLong)
                    .radius(circle.getRadius()).fillColor(color).strokeWidth(2)
                    .strokeColor(0x88888888);
            CircleItem circleItem = new CircleItem();
            circleItem.setGeometry(circleOptions);
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(
                            new LatLng(circle.getCenter().getLatitude(),
                                    circle.getCenter().getLongitude()))
                    .title("Zone Name : " + zoneInfo.getZoneName())
                    .snippet("Fence Name : " + fence.getName());

            circleItem.setMarkerOption(markerOptions);

            map.addCircle(circleOptions);

        } else if (fence.getGeometry() instanceof BoundingBox) {
            BoundingBox bbox = (BoundingBox) fence.getGeometry();
            PolygonOptions polygon = new PolygonOptions()
                    .add(new LatLng(bbox.getNorth(), bbox.getEast()))
                    .add(new LatLng(bbox.getNorth(), bbox.getWest()))
                    .add(new LatLng(bbox.getSouth(), bbox.getWest()))
                    .add(new LatLng(bbox.getSouth(), bbox.getEast()))
                    .fillColor(color).strokeWidth(2).strokeColor(0x88888888);
            BoundingBoxItem boundingBoxItem = new BoundingBoxItem();
            boundingBoxItem.setGeometry(polygon);
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(
                            new LatLng(bbox.getNorthEast().getLatitude(), bbox
                                    .getNorthEast().getLongitude()))
                    .title("Zone Name : " + zoneInfo.getZoneName())
                    .snippet("Fence Name : " + fence.getName());

            map.addPolygon((PolygonOptions) boundingBoxItem.getGeometry());
            boundingBoxItem.setMarkerOption(markerOptions);
        } else if (fence.getGeometry() instanceof Polygon) {

            Polygon truePolygon = (Polygon) fence.getGeometry();
            List<Point> points = truePolygon.getVertices();
            PolygonOptions truePolygonOptions = new PolygonOptions()
                    .fillColor(color).strokeWidth(2).strokeColor(0x88888888);
            for (Point p : points) {
                truePolygonOptions.add(new LatLng(p.getLatitude(), p
                        .getLongitude()));
            }
            PolygonItem polygonItem = new PolygonItem();
            polygonItem.setGeometry(truePolygonOptions);
            Point marker_position = ((Polygon) fence.getGeometry()).getVertices().get(0);
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(
                            new LatLng(marker_position.getLatitude(), marker_position.getLongitude()))
                    .title("Zone Name : " + zoneInfo.getZoneName())
                    .snippet("Fence Name : " + fence.getName());
            polygonItem.setMarkerOption(markerOptions);

            map.addPolygon((PolygonOptions)polygonItem.getGeometry());
        } else if (fence.getGeometry() instanceof LineString) {
            LineString trueLineString = (au.com.bluedot.model.geo.LineString) fence.getGeometry();
            List<Point> points = trueLineString.getVertices();
            PolylineOptions truePolylineOptions = new PolylineOptions()
                    .width(6).color(0x55880000);
            for (Point p : points) {
                truePolylineOptions.add(new LatLng(p.getLatitude(), p
                        .getLongitude()));
            }
            LineStringItem lineStringItem = new LineStringItem();
            lineStringItem.setGeometry(truePolylineOptions);
            Point marker_position = ((LineString) fence.getGeometry()).getStart();
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(
                            new LatLng(marker_position.getLatitude(), marker_position.getLongitude()))
                    .title("Zone Name : " + zoneInfo.getZoneName())
                    .snippet("Fence Name : " + fence.getName());
            lineStringItem.setMarkerOption(markerOptions);
            map.addPolyline((PolylineOptions) lineStringItem.getGeometry());
        }
    }

    /**
     * Called when the location has changed.
     * <p/>
     * <p> There are no restrictions on the use of the supplied Location object.
     *
     * @param location The new location, as a Location object.
     */
    @Override
    public void onLocationChanged(Location location) {
        boolean requestAnotherUpdate = true;
        if (location != null)
        {
            if (location.getAccuracy() < ACCURACY_LEVEL_m) {
                map.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())), CAMERA_ANIMATION_ms, null);
                requestAnotherUpdate = false;
            }
        }
        if (requestAnotherUpdate){
            if(checkPermission()) {
                mLocationManager.requestSingleUpdate(LocationManager.PASSIVE_PROVIDER, this, null);
            }
        }
    }


    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    /**
     * Called when the provider is enabled by the user.
     *
     * @param provider the name of the location provider associated with this
     *                 update.
     */
    @Override
    public void onProviderEnabled(String provider) {

    }

    /**
     * Called when the provider is disabled by the user. If requestLocationUpdates
     * is called on an already disabled provider, this method is called
     * immediately.
     *
     * @param provider the name of the location provider associated with this
     *                 update.
     */
    @Override
    public void onProviderDisabled(String provider) {

    }

    public LatLng getLastKnownPosition() {
        LatLng result = null;
        if (getActivity() != null) {
            LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            if (locationManager != null) {
                if (checkPermission()) {
                    Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location != null) {
                        result = new LatLng(location.getLatitude(), location.getLongitude());
                    }
                }
            }
        }
        return result;
    }


    private boolean checkPermission() {
        return ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

}
