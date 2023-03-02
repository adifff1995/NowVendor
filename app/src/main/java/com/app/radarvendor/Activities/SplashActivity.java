package com.app.radarvendor.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.app.radarvendor.Module.About;
import com.app.radarvendor.Module.ErrorModel;
import com.app.radarvendor.Module.PermissionUtils;
import com.app.radarvendor.Module.ResultAPIModel;
import com.app.radarvendor.Module.Vendor;
import com.app.radarvendor.R;
import com.app.radarvendor.Utils.Constant;
import com.app.radarvendor.Utils.GlobalMethods;
import com.app.radarvendor.Utils.NoConnectionDialog;
import com.app.radarvendor.databinding.ActivitySplashBinding;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.hawk.Hawk;

import java.net.NoRouteToHostException;
import java.net.UnknownHostException;

import cn.bingerz.android.fastlocation.FastLocation;
import cn.bingerz.android.fastlocation.LocationResultListener;
import cn.bingerz.android.fastlocation.location.LocationParams;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {
    private ActivitySplashBinding binding;
    private Intent intent;
    private LocationSettingsRequest.Builder builder;
    private SettingsClient client;
    private Task<LocationSettingsResponse> task;
    private LocationRequest locationRequest;
    private FastLocation fastLocation;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean permissionDenied = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//        Log.e( "onCreate: ",Hawk.get(Constant.fcm).toString() );
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(Priority.PRIORITY_HIGH_ACCURACY);
        builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        client = LocationServices.getSettingsClient(this);
        task = client.checkLocationSettings(builder.build());
        fastLocation = new FastLocation(this);
        about();
    }

    private void about() {
        Call<ResultAPIModel<About>> call = GlobalMethods.getApiInterface().about();
        call.enqueue(new Callback<ResultAPIModel<About>>() {
            @Override
            public void onResponse(Call<ResultAPIModel<About>> call, Response<ResultAPIModel<About>> response) {
                ResultAPIModel<About> result = response.body();
                if (response.isSuccessful()) {
                    assert result != null;
                    if (result.success == Constant.Status_successful) {
                        Hawk.put(Constant.about, result.data);
//                        Hawk.delete(Constant.isLogin);
                        enableMyLocation();

                    } else {
                        GlobalMethods.showErrorToast(SplashActivity.this, result.message);

                    }

                } else {
                    ErrorModel errorModel = null;
                    try {
                        assert response.errorBody() != null;
                        String error = response.errorBody().string();
                        Log.d("onResponse: ", error);
                        errorModel = new Gson().fromJson(error, new TypeToken<ErrorModel>() {
                        }.getType());
                        GlobalMethods.showErrorToast(SplashActivity.this, errorModel.message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResultAPIModel<About>> call, Throwable t) {
                t.printStackTrace();
                if (t instanceof UnknownHostException || t instanceof NoRouteToHostException) {
                    NoConnectionDialog.with(SplashActivity.this);
                }
            }
        });

    }

    @SuppressLint("MissingPermission")
    private void enableMyLocation() {
        // 1. Check if permissions are granted, if so, enable the my location layer
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            changeLocationSettings();
            return;
        }

        // 2. Otherwise, request location permissions from the user.
        PermissionUtils.requestLocationPermissions(SplashActivity.this, LOCATION_PERMISSION_REQUEST_CODE, true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION) || PermissionUtils
                .isPermissionGranted(permissions, grantResults,
                        Manifest.permission.ACCESS_COARSE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Permission was denied. Display an error message
            // [START_EXCLUDE]
            // Display the missing permission error dialog when the fragments resume.
            permissionDenied = true;
            // [END_EXCLUDE]
        }
    }
    // [END maps_check_location_permission_result]

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (permissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            permissionDenied = false;
        }
    }


    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

    private void getLastLocation() {
        Log.e("getLastLocation: ", "getLastLocation");
        boolean isSingle = true; //Single request location;
        LocationParams params = LocationParams.HIGH_ACCURACY; //other default params:MEDIUM_ACCURACY、LOW_ACCURACY
        fastLocation.getLocation(isSingle, params, new LocationResultListener() {
            @Override
            public void onResult(Location location) {
             Hawk.put(Constant.lat,location.getLatitude());
             Hawk.put(Constant.lng,location.getLongitude());

                if (Hawk.contains(Constant.isLogin)) {
                    if (Hawk.contains(Constant.vendor)) {
                        Vendor vendor = Hawk.get(Constant.vendor);
                        if (vendor.getType() == 1)
                            intent = new Intent(SplashActivity.this, MainActivity.class);
                        else
                            intent = new Intent(SplashActivity.this, MainServicesActivity.class);
                    } else if (Hawk.contains(Constant.delivery)) {
                        intent = new Intent(SplashActivity.this, MainDeliveryActivity.class);

                    }

                } else {
                    intent = new Intent(SplashActivity.this, OnBoardingActivity.class);
                }
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });

    }

    private void changeLocationSettings() {
        task.addOnSuccessListener(this, locationSettingsResponse -> getLastLocation());

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(SplashActivity.this,
                                100);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("onActivityResult", requestCode + "");
        if (requestCode == 100) {
            getLastLocation();
        }
    }
}