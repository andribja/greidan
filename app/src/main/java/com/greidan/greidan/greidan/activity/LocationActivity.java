package com.greidan.greidan.greidan.activity;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public abstract class LocationActivity extends ProgressActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    protected GoogleApiClient mGoogleApiClient;
    protected Location mCurrentLocation;
    protected LocationRequest mLocationRequest;

    protected boolean locationUpdatesEnabled = false;
    protected long currentUpdateStart = -1;

    protected void updateLocation() {
        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    }

    protected void startLocationUpdates() {
        if(!locationUpdatesEnabled){
            Log.i("LocationActivity", "Starting location updates");
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            locationUpdatesEnabled = !locationUpdatesEnabled;
            currentUpdateStart = System.currentTimeMillis();
        }
    }

    protected void stopLocationUpdates() {
        if(locationUpdatesEnabled) {
            Log.i("LocationActivity", "Stopping location updates");
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            locationUpdatesEnabled = !locationUpdatesEnabled;
            currentUpdateStart = -1;
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i("LocationActivity", "New location: " + location.toString());
        mCurrentLocation = location;
        handleLocationUpdate();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        handleLocationUpdate();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    protected abstract void handleLocationUpdate();
}
