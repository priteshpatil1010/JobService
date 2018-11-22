package selfielife.life.selfie.com.jobservice;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static java.security.AccessController.getContext;

/**
 * JobService to be scheduled by the JobScheduler.
 * start another service
 */

public class TestJobService extends JobService {
    //private static final String TAG = "SyncService";
    private static final String TAG = TestJobService.class.getSimpleName() ;




    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;

    private String                      mLastUpdateTime;
    private Geocoder                    geocoder;
    private List<Address> addresses;
Context context;
    private String                      IMEI,
            simNo,
            _latitude  ="0.0",
            _longitude ="0.0",
            address    = "unknown",
            city       = "unknown",
            state      = "unknown",
            country    = "unknown",
            postalCode = "unknown";

    public static final long   UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    public static final long   FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 5000;
    public static final int    REQUEST_CHECK_SETTINGS = 100;

    @Override
    public boolean onStartJob(JobParameters params) {


        Intent service = new Intent(getApplicationContext(), MainActivity.class);
        getApplicationContext().startService(service);
        Toast.makeText(this, "job started", Toast.LENGTH_SHORT).show();
        //Log.d(TAG,"Pritesh Notification");
        initLocation();
        Util.scheduleJob(getApplicationContext()); // reschedule the job
        return true;

    }

    @Override
    public boolean onStopJob(JobParameters params) {
       // Util.scheduleJob(getApplicationContext());

        //true to indicate to the JobManager whether you'd like to reschedule this job based on the retry criteria provided at job creation-time;
        // or false to end the job entirely. Regardless of the value returned, your job must stop executing.
        return true;
    }




    //location function
    private void initLocation() {

        geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        mSettingsClient = LocationServices.getSettingsClient(getApplicationContext());

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                // location is received
                mCurrentLocation = locationResult.getLastLocation();
                mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());

                updateLocation();

            }
        };

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();

        startLocation();

    }

    private void startLocation() {
        mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener( new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {

                        Log.d(TAG, "Started location updates!");

                        //noinspection MissingPermission
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,mLocationCallback, Looper.myLooper());

                        updateLocation();
                    }
                })
                .addOnFailureListener( new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade location settings ");
                              /*  try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult((Activity) context, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.d(TAG, "PendingIntent unable to execute request.");
                                }*/
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);
                             //   Toasty.error(getContext(), errorMessage, Toast.LENGTH_LONG,true).show();
                        }

                        updateLocation();
                    }
                });
    }

    private void updateLocation(){
        if (mCurrentLocation != null) {
/*
            Log.d(TAG,"Lat: " + mCurrentLocation.getLatitude() + ", " +"Lng: " + mCurrentLocation.getLongitude());
            Log.d(TAG,"Last updated on: " + mLastUpdateTime);*/

            _latitude    = String.valueOf(mCurrentLocation.getLatitude());
            _longitude   = String.valueOf(mCurrentLocation.getLongitude());

            //MyConfiguration.setPreferences(context,PROFILE_LONGITUDE,_latitude);
           // MyConfiguration.setPreferences(context,PROFILE_LATTITUDE,_longitude);

         /*   Log.d(TAG,"_latitude    = "+_latitude);
            Log.d(TAG,"_longitude   = "+_longitude);
*/
            try
            {
                addresses = geocoder.getFromLocation(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5


                for(int i=0;i<addresses.size();i++){

                    address      = addresses.get(i).getAddressLine(i); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    city         = addresses.get(i).getLocality();
                    state        = addresses.get(i).getAdminArea();
                    country      = addresses.get(i).getCountryName();
                    postalCode   = addresses.get(i).getPostalCode();

                    Log.d(TAG,i+" address      = "+address);
                    Log.d(TAG,i+"city         = "+city);
                    Log.d(TAG,i+"state        = "+state);
                    Log.d(TAG,i+"country      = "+country);
                    Log.d(TAG,i+"postalCode   = "+postalCode);

                }

//                address      = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
//                city         = addresses.get(0).getLocality();
//                state        = addresses.get(0).getAdminArea();
//                country      = addresses.get(0).getCountryName();
//                postalCode   = addresses.get(0).getPostalCode();
//
//                Log.d(TAG,"address      = "+address);
//                Log.d(TAG,"city         = "+city);
//                Log.d(TAG,"state        = "+state);
//                Log.d(TAG,"country      = "+country);
//                Log.d(TAG,"postalCode   = "+postalCode);

             //   MyConfiguration.setPreferences(context,PROFILE_LOCATION,country);

            }
            catch (Exception e)
            {
                Log.d(TAG,"(updateLocation) catch error = "+ e.getMessage());
            }

        }

    }

    public void stopLocation() {
        try
        {
            // Removing location updates
            mFusedLocationClient
                    .removeLocationUpdates(mLocationCallback)
                    .addOnCompleteListener((Activity) getApplicationContext(), new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d(TAG,"Location updates stopped!");
                            //Toast.makeText(getApplicationContext(), "Location updates stopped!", Toast.LENGTH_SHORT).show();


                        }
                    });
        }
        catch (Exception e)
        {
            Log.d(TAG,"(stopLocation) catch error = "+e.getMessage().toString());
        }
    }


}