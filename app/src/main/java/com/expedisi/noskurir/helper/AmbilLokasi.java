package com.expedisi.noskurir.helper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.expedisi.noskurir.Fungsi;
import com.expedisi.noskurir.R;
import com.expedisi.noskurir.model.ModelKota;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class AmbilLokasi implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener{

    private LocationManager locationManager;
    Activity activity;
    Context context;
    public GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    private String TAG = "noskurir";
    private LatLng latLng;
    View center;
    private LocationListener locationListener;
    private DatabaseReference dr;
    private SharedPreferences sp;
    private AlertDialog wew;
    RelativeLayout rl, bsbloading;
    int batas = 0;
    GoogleMap mMap;
    Fungsi f;

    public AmbilLokasi() {
    }

    public AmbilLokasi(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        buildGoogleApiClient();
        locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
        dr = FirebaseDatabase.getInstance().getReference();
        f = new Fungsi(context,activity);
    }

    public Location getLocation(){
        return mLastLocation;
    }

    public void getLokasi(final NetworkInfo netInfo){
        if (mGoogleApiClient == null){
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(final Location kordinat) {
                mLastLocation = kordinat;
                if (netInfo == null){
                    new AlertDialog.Builder(context)
                            .setTitle(context.getResources().getString(R.string.app_name))
                            .setMessage(context.getResources().getString(R.string.internet_error))
                            .setCancelable(false)
                            .setPositiveButton("Restart App", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            System.exit(0);
                                        }
                                    }, 700);
                                }
                            }).show();
                }else{
                    dr.child("kota").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot kota) {
                            String kotanya = "";
                            for (DataSnapshot dskota : kota.getChildren()) {
                                ModelKota mKota = dskota.getValue(ModelKota.class);
                                Double lat = kordinat.getLatitude();
                                Double lon = kordinat.getLongitude();
                                if (lat <= Double.parseDouble(mKota.getSelatan()) ||
                                        lat >= Double.parseDouble(mKota.getUtara()) ||
                                        lon <= Double.parseDouble(mKota.getBarat()) ||
                                        lon >= Double.parseDouble(mKota.getTimur())) {
                                } else {
                                    kotanya = dskota.getKey();
                                }
                            }
                            if(TextUtils.isEmpty(kotanya)){
                                kotanya = "Diluar";
                            }
                            HashMap<String, String> posisikurir = new HashMap<>();
                            posisikurir.put("lat", String.valueOf(kordinat.getLatitude()));
                            posisikurir.put("lon", String.valueOf(kordinat.getLongitude()));
                            posisikurir.put("kota", kotanya);
                            HashMap<String, Object> simpan = new HashMap<>();
                            simpan.put("kurirLatLon/"+f.kurir("idku"), posisikurir);
                            dr.updateChildren(simpan);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                if (provider.equals("gps")){
                    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                            .addLocationRequest(mLocationRequest);

                    builder.setAlwaysShow(true);
                    PendingResult<LocationSettingsResult> result =
                            LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
                    result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                        @Override
                        public void onResult(LocationSettingsResult result) {
                            final Status status = result.getStatus();
                            switch (status.getStatusCode()) {
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    try {
                                        status.startResolutionForResult(activity, 8910);
                                    } catch (IntentSender.SendIntentException e) {
                                        // Ignore the error.
                                    }
                                    break;
                            }
                        }
                    });
                }
            }
        };
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 7000, 5, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 7000, 5, locationListener);
    }

    public void resumeConenct(){
        if(!mGoogleApiClient.isConnected() && !mGoogleApiClient.isConnecting()){
            mGoogleApiClient.connect();
        }
    }

    public void pauseConnect(){
        if(mGoogleApiClient.isConnected()){
            mGoogleApiClient.disconnect();
        }
    }


    protected synchronized void buildGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .build();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(7000);
        mLocationRequest.setFastestInterval(7000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        startLocationUpdates();
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult(activity, 8910);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                }
            }
        });
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        mLastLocation = location;
    }

}
