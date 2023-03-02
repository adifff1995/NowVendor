package com.app.radarvendor.Activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.app.radarvendor.Adapter.NavItemsAdapter;
import com.app.radarvendor.Module.ActiveOrders;
import com.app.radarvendor.Module.ErrorModel;
import com.app.radarvendor.Module.MessageEvent;
import com.app.radarvendor.Module.NavItems;
import com.app.radarvendor.Module.OrderFromUser;
import com.app.radarvendor.Module.OrderResponse;
import com.app.radarvendor.Module.ResultAPIModel;
import com.app.radarvendor.Module.Vendor;
import com.app.radarvendor.R;
import com.app.radarvendor.Utils.Constant;
import com.app.radarvendor.Utils.GlobalMethods;
import com.app.radarvendor.Utils.NoConnectionDialog;
import com.app.radarvendor.Utils.PermissionUtils;
import com.app.radarvendor.databinding.ActivityAddProductBinding;
import com.app.radarvendor.databinding.ActivityMainDeliveryBinding;
import com.app.radarvendor.databinding.DialogViewBillBinding;
import com.app.radarvendor.databinding.NewOrderSheetBinding;
import com.bumptech.glide.Glide;
import com.github.razir.progressbutton.ButtonTextAnimatorExtensionsKt;
import com.github.razir.progressbutton.DrawableButtonExtensionsKt;
import com.github.razir.progressbutton.ProgressButtonHolderKt;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.hawk.Hawk;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.net.NoRouteToHostException;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainDeliveryActivity extends FragmentActivity implements OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private ActivityMainDeliveryBinding binding;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private ActivityResultLauncher<IntentSenderRequest> launcher;
    private Context context = MainDeliveryActivity.this;
    private NavItemsAdapter navItemsAdapter;
    private List<NavItems> navItemsList;

    private LocationSettingsRequest.Builder builder;
    private SettingsClient client;
    private Task<LocationSettingsResponse> task;
    private LocationRequest locationRequest;
    private boolean permissionDenied = false;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private BottomSheetDialog myDialog, myDialog2;
    private AlertDialog alertDialog;
    private NewOrderSheetBinding newOrderSheetBinding, newOrderSheetBinding2;
    private Vibrator v;
    private String status,status2;
    private OrderResponse orderResponse;
    private DialogViewBillBinding viewBillBinding;
    private View mapView;
    private ProgressDialog pd;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainDeliveryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setUpNavigationDrawer();
        setUpActivity();
        setUpActions();
        getProfile();
        orderActive();

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setUpActivity() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapView = mapFragment.getView();

        pd = new ProgressDialog(context);
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(Priority.PRIORITY_HIGH_ACCURACY);

        builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        client = LocationServices.getSettingsClient(this);
        task = client.checkLocationSettings(builder.build());
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

//        if (getIntent().hasExtra("type")) {
//            OrderResponse orderResponse = (OrderResponse) getIntent().getSerializableExtra("order");
//            GlobalMethods.printGson("orderResponse", orderResponse);
//            status = orderResponse.getDelivery_status();
//            if (orderResponse.getDelivery_status().equals("pending"))
//                showNewOrderDialogDialog(String.valueOf(orderResponse.getId()), orderResponse);
//        }


    }

    private void setUpActions() {

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMyLocationButtonClickListener(() -> false);
        mMap.setOnMyLocationClickListener(location -> {

        });
        if (mapView != null &&
                mapView.findViewById(Integer.parseInt("1")) != null) {
            // Get the button view
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            // and next place it, on bottom right (as Google Maps app)
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    locationButton.getLayoutParams();
            // position on right bottom
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 330, 330);
        }
        enableMyLocation();
    }

//    @RequiresApi(api = Build.VERSION_CODES.O)
//    private void showNewOrderDialogDialog(String order_id, OrderResponse orderResponse) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        newOrderSheetBinding = NewOrderSheetBinding.inflate(getLayoutInflater());
//        ProgressButtonHolderKt.bindProgressButton(this, newOrderSheetBinding.btnAccept);
//        ButtonTextAnimatorExtensionsKt.attachTextChangeAnimator(newOrderSheetBinding.btnAccept);
//        ProgressButtonHolderKt.bindProgressButton(this, newOrderSheetBinding.btnReject);
//        ButtonTextAnimatorExtensionsKt.attachTextChangeAnimator(newOrderSheetBinding.btnReject);
//        OffsetDateTime inst = OffsetDateTime.ofInstant(Instant.parse(orderResponse.getOrder_date()),
//                ZoneId.systemDefault());
//        newOrderSheetBinding.tvName.setText(orderResponse.getName());
//        newOrderSheetBinding.tvPhone.setText(orderResponse.getPhone());
//        newOrderSheetBinding.tvAddress.setText(orderResponse.getDtl_other());
//        newOrderSheetBinding.tvDate.setText(DateTimeFormatter.ofPattern("MMM dd, yyyy").format(inst));
//        newOrderSheetBinding.tvOrderNum.setText(String.valueOf(orderResponse.getId()));
//        newOrderSheetBinding.btnAccept.setOnClickListener(view -> {
//            switch (status) {
//                case 0:
//                    orderAcceptance(order_id);
//                    break;
//                case 1:
//                    orderDeliveryReceived(order_id);
//                    break;
//                case 2:
//                    orderDeliveryCompleted(order_id);
//                    break;
//            }
//        });
//        newOrderSheetBinding.btnReject.setOnClickListener(view -> myDialog.dismiss());
//        builder.setView(newOrderSheetBinding.getRoot());
//        myDialog = builder.create();
//        myDialog.setCancelable(false);
//        if (!myDialog.isShowing())
//            myDialog.show();
//
//        vibrate();
//        new Handler().postDelayed(() -> myDialog.dismiss(), 150000);
//
//    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showNewOrderDialogDialog(String order_id, OrderResponse orderResponse) {
        newOrderSheetBinding = NewOrderSheetBinding.inflate(getLayoutInflater());
        View view = newOrderSheetBinding.getRoot();
        myDialog = new BottomSheetDialog(context, R.style.BottomSheetDialog);
        myDialog.setContentView(view);
        ProgressButtonHolderKt.bindProgressButton(this, newOrderSheetBinding.btnAccept);
        ButtonTextAnimatorExtensionsKt.attachTextChangeAnimator(newOrderSheetBinding.btnAccept);
        ProgressButtonHolderKt.bindProgressButton(this, newOrderSheetBinding.btnReject);
        ButtonTextAnimatorExtensionsKt.attachTextChangeAnimator(newOrderSheetBinding.btnReject);
        OffsetDateTime inst = OffsetDateTime.ofInstant(Instant.parse(orderResponse.getOrder_date()),
                ZoneId.systemDefault());
        newOrderSheetBinding.tvName.setText(orderResponse.getName());
        newOrderSheetBinding.tvPhone.setText(orderResponse.getPhone());
        newOrderSheetBinding.tvAddress.setText(orderResponse.getDtl_other());
        newOrderSheetBinding.tvDate.setText(DateTimeFormatter.ofPattern("MMM dd, yyyy").format(inst));
        newOrderSheetBinding.tvOrderNum.setText(String.valueOf(orderResponse.getId()));
        newOrderSheetBinding.tvStoreName.setText(String.valueOf(orderResponse.getStore_date().getName()));
        status = orderResponse.getDelivery_status();
        switch (status) {
            case "pending":
                newOrderSheetBinding.btnAccept.setBackgroundResource(R.drawable.bg_button_shape_green);
                newOrderSheetBinding.btnAccept.setText(getString(R.string.accept));
                break;
            case "acceptance":
                newOrderSheetBinding.btnAccept.setBackgroundResource(R.drawable.bg_button_main_shape);
                newOrderSheetBinding.btnAccept.setText(getString(R.string.recived_done));
                break;
            case "received":
                newOrderSheetBinding.btnAccept.setBackgroundResource(R.drawable.bg_button_main_shape_blue);
                newOrderSheetBinding.btnAccept.setText(getString(R.string.end_order));
                break;
        }

        newOrderSheetBinding.btnAccept.setOnClickListener(view1 -> {
            switch (status) {
                case "pending":
                    orderAcceptance(order_id);
                    break;
                case "acceptance":
                    orderDeliveryReceived(order_id);
                    break;
                case "received":
                    orderDeliveryCompleted(order_id);
                    break;
            }
        });
        newOrderSheetBinding.btnReject.setOnClickListener(view2 -> myDialog.dismiss());
        myDialog.setCancelable(false);
        if (!myDialog.isShowing())
            myDialog.show();

        vibrate();
//        new Handler().postDelayed(() -> myDialog.dismiss(), 150000);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showNewOrderFromUserDialog(String order_id, OrderFromUser orderResponse) {
        newOrderSheetBinding2 = NewOrderSheetBinding.inflate(getLayoutInflater());
        View view = newOrderSheetBinding2.getRoot();
        myDialog2 = new BottomSheetDialog(context, R.style.BottomSheetDialog);
        myDialog2.setContentView(view);
        ProgressButtonHolderKt.bindProgressButton(this, newOrderSheetBinding2.btnAccept);
        ButtonTextAnimatorExtensionsKt.attachTextChangeAnimator(newOrderSheetBinding2.btnAccept);
        ProgressButtonHolderKt.bindProgressButton(this, newOrderSheetBinding2.btnReject);
        ButtonTextAnimatorExtensionsKt.attachTextChangeAnimator(newOrderSheetBinding2.btnReject);
        newOrderSheetBinding2.tvName.setText("طلب مرسل من مستخدم");
//        newOrderSheetBinding.tvPhone.setText(orderResponse.getPhone());
        newOrderSheetBinding2.tvAddress.setText(orderResponse.getNotes());
//        newOrderSheetBinding.tvDate.setText(DateTimeFormatter.ofPattern("MMM dd, yyyy").format(inst));
        newOrderSheetBinding2.tvOrderNum.setText(String.valueOf(orderResponse.getId()));
        newOrderSheetBinding2.tvStoreName.setText(String.valueOf(orderResponse.getDelivery_type_name()));
        status2 = orderResponse.getDelivery_status();
        switch (status2) {
            case "pending":
                newOrderSheetBinding2.btnAccept.setBackgroundResource(R.drawable.bg_button_shape_green);
                newOrderSheetBinding2.btnAccept.setText(getString(R.string.accept));
                break;
            case "acceptance":
                newOrderSheetBinding2.btnAccept.setBackgroundResource(R.drawable.bg_button_main_shape);
                newOrderSheetBinding2.btnAccept.setText(getString(R.string.recived_done));
                break;
            case "received":
                newOrderSheetBinding2.btnAccept.setBackgroundResource(R.drawable.bg_button_main_shape_blue);
                newOrderSheetBinding2.btnAccept.setText(getString(R.string.end_order));
                break;
        }

        newOrderSheetBinding2.btnAccept.setOnClickListener(view1 -> {
            switch (status2) {
                case "pending":
                    orderFromUserAcceptance(order_id);
                    break;
                case "acceptance":
                    orderFromUserReceived(order_id);
                    break;
                case "received":
                    orderFromUserCompleted(order_id);
                    break;
            }
        });
        newOrderSheetBinding2.btnReject.setOnClickListener(view2 -> myDialog2.dismiss());
        myDialog2.setCancelable(false);
        if (!myDialog2.isShowing())
            myDialog2.show();

        vibrate();
//        new Handler().postDelayed(() -> myDialog.dismiss(), 150000);

    }

    private void vibrate() {
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
// Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(15000, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(15000);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showBillDialog(OrderResponse orderResponse) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        viewBillBinding = DialogViewBillBinding.inflate(getLayoutInflater());
        OffsetDateTime inst = OffsetDateTime.ofInstant(Instant.parse(orderResponse.getOrder_date()),
                ZoneId.systemDefault());
        double final_total = Double.parseDouble(orderResponse.getTotal()) + 5;
        viewBillBinding.tvOrderNum.setText(String.valueOf(orderResponse.getId()));
        viewBillBinding.tvTripTime.setText(orderResponse.getDelivery_time());
        viewBillBinding.tvTripDistance.setText(new DecimalFormat("##.##").format(Double.parseDouble(orderResponse.getDistance())) + " M");
        viewBillBinding.tvOrderPrice.setText(orderResponse.getTotal() + "₪");
        viewBillBinding.tvFinalTotal.setText(final_total + "₪");
        viewBillBinding.tvDate.setText(DateTimeFormatter.ofPattern("MMM dd, yyyy").format(inst));
        viewBillBinding.tvOrderNum.setText(String.valueOf(orderResponse.getId()));
        viewBillBinding.btnDone.setOnClickListener(view -> alertDialog.dismiss());
        builder.setView(viewBillBinding.getRoot());
        alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        if (!alertDialog.isShowing())
            alertDialog.show();

    }

    private void enableMyLocation() {
        // 1. Check if permissions are granted, if so, enable the my location layer
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            changeLocationSettings();
            return;
        }

        // 2. Otherwise, request location permissions from the user.
        PermissionUtils.requestLocationPermissions(this, LOCATION_PERMISSION_REQUEST_CODE, true);
    }

//    @RequiresApi(api = Build.VERSION_CODES.N)
//    @SuppressLint("MissingPermission")
//    private void startLocationUpdates() {
//        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            requestPermission();
//            return;
//        }
//        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
//            if (location != null) {
//                // Logic to handle location object
//                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
//
//            }
//        });
//    }

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
                        resolvable.startResolutionForResult(MainDeliveryActivity.this,
                                100);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
                        }
                    }
                });
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

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
//        orderResponse = event.getOrderResponse();
//        status = orderResponse.getDelivery_status();
////        if (orderResponse.getDelivery_status().equals("pending"))
//            showNewOrderDialogDialog(String.valueOf(event.getFlag()), orderResponse);

    }

    private void setUpNavigationDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, binding.drawerLayout, binding.mapLayout.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        toggle.setDrawerIndicatorEnabled(true);
        toggle.setDrawerSlideAnimationEnabled(true);
        binding.mapLayout.toolbar.setNavigationIcon(R.drawable.ic_menu_icon);

        toggle.setToolbarNavigationClickListener(v -> {
            if (binding.drawerLayout.isDrawerVisible(GravityCompat.START)) {
                binding.drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                binding.drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        navItemsList = new ArrayList<>();
        navItemsList.add(new NavItems("1", getString(R.string.home), R.drawable.ic_iconly_light_home));
        navItemsList.add(new NavItems("2", getString(R.string.my_orders), R.drawable.ic_book_outlined));
        navItemsList.add(new NavItems("3", getString(R.string.profile), R.drawable.ic_iconly_light_profile));
        navItemsList.add(new NavItems("4", getString(R.string.settings), R.drawable.ic_settings_outlined));
        navItemsList.add(new NavItems("5", getString(R.string.notifications), R.drawable.ic_notifications_outlined_gray));
        navItemsList.add(new NavItems("6", getString(R.string.contact_us), R.drawable.ic_call_us));
        navItemsList.add(new NavItems("7", getString(R.string.about_app), R.drawable.ic_error_outlined));
        navItemsList.add(new NavItems("8", getString(R.string.privacy_policy), R.drawable.ic_settings_outlined));
        navItemsList.add(new NavItems("9", getString(R.string.logout), R.drawable.ic_sign_out));
        Intent intent = new Intent(context, FromMapActivity.class);
        navItemsAdapter = new NavItemsAdapter(context, navItemsList, position -> {
            switch (navItemsList.get(position).getId()) {
                case "1":
                    intent.putExtra("flag", 1);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    break;
                case "2":
                    intent.putExtra("flag", 2);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    break;
                case "3":
                    intent.putExtra("flag", 3);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    break;
                case "4":
                    intent.putExtra("flag", 4);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    break;
                case "5":
                    intent.putExtra("flag", 5);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    break;
                case "6":
                    intent.putExtra("flag", 6);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    break;
                case "7":
                    intent.putExtra("flag", 7);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    break;
                case "8":
                    intent.putExtra("flag", 8);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    break;
                case "9":
                    Hawk.deleteAll();
                    startActivity(new Intent(context, SplashActivity.class));
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    finish();
                    break;
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START);

        });

        binding.navDrawer.rvNavItems.setAdapter(navItemsAdapter);
        binding.navDrawer.rvNavItems.setLayoutManager(new LinearLayoutManager(context));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("onActivityResult", "onActivityResult:100 ");
        if (resultCode == 100) {
            getLastLocation();
        }
    }

    private void getProfile() {
        Call<ResultAPIModel<Vendor>> call = GlobalMethods.getApiInterface().getDeliveryProfile(GlobalMethods.getHeaders());

        call.enqueue(new Callback<ResultAPIModel<Vendor>>() {
            @Override
            public void onResponse(Call<ResultAPIModel<Vendor>> call, Response<ResultAPIModel<Vendor>> response) {
                ResultAPIModel<Vendor> result = response.body();
                if (response.isSuccessful()) {
                    assert result != null;
                    if (result.success == Constant.Status_successful) {
                        Vendor vendor = result.data;
                        binding.navDrawer.tvUsername.setText(result.data.getName());
                        binding.mapLayout.tvName.setText(result.data.getName());
                        binding.mapLayout.tvAddress.setText(result.data.getCity_name());
                        CircularProgressDrawable circularProgressDrawable = GlobalMethods.getPlaceHolder(context);
                        circularProgressDrawable.start();
                        if (result.data.getIcon_url() != null) {
                            Glide.with(context).load(vendor.getIcon_url()).placeholder(circularProgressDrawable).into(binding.navDrawer.imageProfile);
                            Glide.with(context).load(vendor.getIcon_url()).placeholder(circularProgressDrawable).into(binding.mapLayout.img);
                        }

                    } else {
                        Log.d("onResponse: ", result.message);
                        GlobalMethods.showErrorToast(MainDeliveryActivity.this, result.message);
                    }

                } else {
                    ErrorModel errorModel = null;
                    try {
                        assert response.errorBody() != null;
                        String error = response.errorBody().string();
                        Log.d("onResponse: ", error);
                        errorModel = new Gson().fromJson(error, new TypeToken<ErrorModel>() {
                        }.getType());
                        GlobalMethods.showErrorToast(MainDeliveryActivity.this, errorModel.message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResultAPIModel<Vendor>> call, Throwable t) {
                t.printStackTrace();
                if (t instanceof UnknownHostException || t instanceof NoRouteToHostException) {
                    NoConnectionDialog.with(MainDeliveryActivity.this);
                }
            }
        });

    }

    private void orderAcceptance(String order_id) {
        Map<String, Object> params = GlobalMethods.getParams(MainDeliveryActivity.this);
        params.put("order_id", String.valueOf(order_id));
        Call<ResultAPIModel<OrderResponse>> call = GlobalMethods.getApiInterface().orderDeliveryAcceptance(GlobalMethods.getHeaders(), params);
        GlobalMethods.showProgress(newOrderSheetBinding.btnAccept);
        call.enqueue(new Callback<ResultAPIModel<OrderResponse>>() {
            @Override
            public void onResponse(Call<ResultAPIModel<OrderResponse>> call, Response<ResultAPIModel<OrderResponse>> response) {
                ResultAPIModel<OrderResponse> result = response.body();
                newOrderSheetBinding.btnAccept.setEnabled(true);
                DrawableButtonExtensionsKt.hideProgress(newOrderSheetBinding.btnAccept, R.string.recived_done);
                if (response.isSuccessful()) {
                    assert result != null;
                    if (result.success == Constant.Status_successful) {
                        GlobalMethods.printGson("orderAcceptance", result.data);
                        newOrderSheetBinding.btnAccept.setBackgroundResource(R.drawable.bg_button_main_shape);
                        newOrderSheetBinding.btnAccept.setText(getString(R.string.recived_done));
                        status = result.data.getDelivery_status();
                        if (v.hasVibrator())
                            v.cancel();

                        openWaze(result.data.getStore_latitude(), result.data.getStore_longitude());
                    } else {
                        GlobalMethods.showErrorToast(MainDeliveryActivity.this, result.message);

                    }

                } else {
                    ErrorModel errorModel = null;
                    try {
                        assert response.errorBody() != null;
                        String error = response.errorBody().string();
                        Log.d("onResponse: ", error);
                        errorModel = new Gson().fromJson(error, new TypeToken<ErrorModel>() {
                        }.getType());
                        if (myDialog.isShowing())
                            myDialog.dismiss();
                        GlobalMethods.showErrorToast(MainDeliveryActivity.this, errorModel.message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResultAPIModel<OrderResponse>> call, Throwable t) {
                t.printStackTrace();
                if (t instanceof UnknownHostException || t instanceof NoRouteToHostException) {
                    NoConnectionDialog.with(MainDeliveryActivity.this);
                }
            }
        });

    }

    private void orderFromUserAcceptance(String order_id) {
        Log.e( "orderFromUserAcceptance: ","orderFromUserAcceptance" );
        Map<String, Object> params = GlobalMethods.getParams(MainDeliveryActivity.this);
        params.put("delivery_services_id", String.valueOf(order_id));
        Call<ResultAPIModel<OrderFromUser>> call = GlobalMethods.getApiInterface().deliveryAcceptance(GlobalMethods.getHeaders(), params);
        GlobalMethods.showProgress(newOrderSheetBinding2.btnAccept);
        call.enqueue(new Callback<ResultAPIModel<OrderFromUser>>() {
            @Override
            public void onResponse(Call<ResultAPIModel<OrderFromUser>> call, Response<ResultAPIModel<OrderFromUser>> response) {
                ResultAPIModel<OrderFromUser> result = response.body();
                newOrderSheetBinding2.btnAccept.setEnabled(true);
                DrawableButtonExtensionsKt.hideProgress(newOrderSheetBinding2.btnAccept, R.string.recived_done);
                if (response.isSuccessful()) {
                    assert result != null;
                    if (result.success == Constant.Status_successful) {
                        GlobalMethods.printGson("orderAcceptance", result.data);
                        newOrderSheetBinding2.btnAccept.setBackgroundResource(R.drawable.bg_button_main_shape);
                        newOrderSheetBinding2.btnAccept.setText(getString(R.string.recived_done));
                        status2 = result.data.getDelivery_status();
                        if (v.hasVibrator())
                            v.cancel();

                        openWaze(Double.parseDouble(result.data.getLatitude_from()), Double.parseDouble(result.data.getLongitude_from()));
                    } else {
                        GlobalMethods.showErrorToast(MainDeliveryActivity.this, result.message);

                    }

                } else {
                    ErrorModel errorModel = null;
                    try {
                        assert response.errorBody() != null;
                        String error = response.errorBody().string();
                        Log.d("onResponse: ", error);
                        errorModel = new Gson().fromJson(error, new TypeToken<ErrorModel>() {
                        }.getType());
                        if (myDialog2.isShowing())
                            myDialog2.dismiss();
                        GlobalMethods.showErrorToast(MainDeliveryActivity.this, errorModel.message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResultAPIModel<OrderFromUser>> call, Throwable t) {
                t.printStackTrace();
                if (t instanceof UnknownHostException || t instanceof NoRouteToHostException) {
                    NoConnectionDialog.with(MainDeliveryActivity.this);
                }
            }
        });

    }


    private void orderDeliveryReceived(String order_id) {
        Map<String, Object> params = GlobalMethods.getParams(MainDeliveryActivity.this);
        params.put("order_id", String.valueOf(order_id));
        Call<ResultAPIModel<OrderResponse>> call = GlobalMethods.getApiInterface().orderDeliveryReceived(GlobalMethods.getHeaders(), params);
        GlobalMethods.showProgress(newOrderSheetBinding.btnAccept);
        call.enqueue(new Callback<ResultAPIModel<OrderResponse>>() {
            @Override
            public void onResponse(Call<ResultAPIModel<OrderResponse>> call, Response<ResultAPIModel<OrderResponse>> response) {
                ResultAPIModel<OrderResponse> result = response.body();
                newOrderSheetBinding.btnAccept.setEnabled(true);
                DrawableButtonExtensionsKt.hideProgress(newOrderSheetBinding.btnAccept, R.string.end_order);
                if (response.isSuccessful()) {
                    assert result != null;
                    if (result.success == Constant.Status_successful) {
                        GlobalMethods.printGson("orderDeliveryReceived", result.data);
                        newOrderSheetBinding.btnAccept.setBackgroundResource(R.drawable.bg_button_main_shape_blue);
                        newOrderSheetBinding.btnAccept.setText(getString(R.string.end_order));
                        status = result.data.getDelivery_status();
                        openWaze(result.data.getUser_latitude(), result.data.getUser_longitude());

                    } else {
                        GlobalMethods.showErrorToast(MainDeliveryActivity.this, result.message);

                    }

                } else {
                    ErrorModel errorModel = null;
                    try {
                        assert response.errorBody() != null;
                        String error = response.errorBody().string();
                        Log.d("onResponse: ", error);
                        errorModel = new Gson().fromJson(error, new TypeToken<ErrorModel>() {
                        }.getType());
                        GlobalMethods.showErrorToast(MainDeliveryActivity.this, errorModel.message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResultAPIModel<OrderResponse>> call, Throwable t) {
                t.printStackTrace();
                if (t instanceof UnknownHostException || t instanceof NoRouteToHostException) {
                    NoConnectionDialog.with(MainDeliveryActivity.this);
                }
            }
        });

    }

    private void orderFromUserReceived(String order_id) {
        Map<String, Object> params = GlobalMethods.getParams(MainDeliveryActivity.this);
        params.put("delivery_services_id", String.valueOf(order_id));
        Call<ResultAPIModel<OrderFromUser>> call = GlobalMethods.getApiInterface().deliveryReceived(GlobalMethods.getHeaders(), params);
        GlobalMethods.showProgress(newOrderSheetBinding2.btnAccept);
        call.enqueue(new Callback<ResultAPIModel<OrderFromUser>>() {
            @Override
            public void onResponse(Call<ResultAPIModel<OrderFromUser>> call, Response<ResultAPIModel<OrderFromUser>> response) {
                ResultAPIModel<OrderFromUser> result = response.body();
                newOrderSheetBinding2.btnAccept.setEnabled(true);
                DrawableButtonExtensionsKt.hideProgress(newOrderSheetBinding2.btnAccept, R.string.end_order);
                if (response.isSuccessful()) {
                    assert result != null;
                    if (result.success == Constant.Status_successful) {
                        GlobalMethods.printGson("orderDeliveryReceived", result.data);
                        newOrderSheetBinding2.btnAccept.setBackgroundResource(R.drawable.bg_button_main_shape_blue);
                        newOrderSheetBinding2.btnAccept.setText(getString(R.string.end_order));
                        status2 = result.data.getDelivery_status();
                        openWaze(Double.parseDouble(result.data.getLatitude_to()), Double.parseDouble(result.data.getLongitude_to()));

                    } else {
                        GlobalMethods.showErrorToast(MainDeliveryActivity.this, result.message);

                    }

                } else {
                    ErrorModel errorModel = null;
                    try {
                        assert response.errorBody() != null;
                        String error = response.errorBody().string();
                        Log.d("onResponse: ", error);
                        errorModel = new Gson().fromJson(error, new TypeToken<ErrorModel>() {
                        }.getType());
                        GlobalMethods.showErrorToast(MainDeliveryActivity.this, errorModel.message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResultAPIModel<OrderFromUser>> call, Throwable t) {
                t.printStackTrace();
                if (t instanceof UnknownHostException || t instanceof NoRouteToHostException) {
                    NoConnectionDialog.with(MainDeliveryActivity.this);
                }
            }
        });

    }

    private void orderDeliveryCompleted(String order_id) {
        Map<String, Object> params = GlobalMethods.getParams(MainDeliveryActivity.this);
        params.put("order_id", String.valueOf(order_id));
        Call<ResultAPIModel<OrderResponse>> call = GlobalMethods.getApiInterface().orderDeliveryCompleted(GlobalMethods.getHeaders(), params);
        GlobalMethods.showProgress(newOrderSheetBinding.btnAccept);
        call.enqueue(new Callback<ResultAPIModel<OrderResponse>>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<ResultAPIModel<OrderResponse>> call, Response<ResultAPIModel<OrderResponse>> response) {
                ResultAPIModel<OrderResponse> result = response.body();
                newOrderSheetBinding.btnAccept.setEnabled(true);
                DrawableButtonExtensionsKt.hideProgress(newOrderSheetBinding.btnAccept, R.string.recived_done);
                if (response.isSuccessful()) {
                    assert result != null;
                    if (result.success == Constant.Status_successful) {
                        GlobalMethods.printGson("orderDeliveryReceived", result.data);
                        myDialog.dismiss();
                        showBillDialog(result.data);

                    } else {
                        GlobalMethods.showErrorToast(MainDeliveryActivity.this, result.message);

                    }

                } else {
                    ErrorModel errorModel = null;
                    try {
                        assert response.errorBody() != null;
                        String error = response.errorBody().string();
                        Log.d("onResponse: ", error);
                        errorModel = new Gson().fromJson(error, new TypeToken<ErrorModel>() {
                        }.getType());
                        GlobalMethods.showErrorToast(MainDeliveryActivity.this, errorModel.message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResultAPIModel<OrderResponse>> call, Throwable t) {
                t.printStackTrace();
                if (t instanceof UnknownHostException || t instanceof NoRouteToHostException) {
                    NoConnectionDialog.with(MainDeliveryActivity.this);
                }
            }
        });

    }

    private void orderFromUserCompleted(String order_id) {
        Map<String, Object> params = GlobalMethods.getParams(MainDeliveryActivity.this);
        params.put("delivery_services_id", String.valueOf(order_id));
        Call<ResultAPIModel<OrderFromUser>> call = GlobalMethods.getApiInterface().deliveryCompleted(GlobalMethods.getHeaders(), params);
        GlobalMethods.showProgress(newOrderSheetBinding2.btnAccept);
        call.enqueue(new Callback<ResultAPIModel<OrderFromUser>>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<ResultAPIModel<OrderFromUser>> call, Response<ResultAPIModel<OrderFromUser>> response) {
                ResultAPIModel<OrderFromUser> result = response.body();
                newOrderSheetBinding2.btnAccept.setEnabled(true);
                DrawableButtonExtensionsKt.hideProgress(newOrderSheetBinding2.btnAccept, R.string.recived_done);
                if (response.isSuccessful()) {
                    assert result != null;
                    if (result.success == Constant.Status_successful) {
                        GlobalMethods.printGson("orderDeliveryReceived", result.data);
                        myDialog2.dismiss();
//                        showBillDialog(result.data);

                    } else {
                        GlobalMethods.showErrorToast(MainDeliveryActivity.this, result.message);

                    }

                } else {
                    ErrorModel errorModel = null;
                    try {
                        assert response.errorBody() != null;
                        String error = response.errorBody().string();
                        Log.d("onResponse: ", error);
                        errorModel = new Gson().fromJson(error, new TypeToken<ErrorModel>() {
                        }.getType());
                        GlobalMethods.showErrorToast(MainDeliveryActivity.this, errorModel.message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResultAPIModel<OrderFromUser>> call, Throwable t) {
                t.printStackTrace();
                if (t instanceof UnknownHostException || t instanceof NoRouteToHostException) {
                    NoConnectionDialog.with(MainDeliveryActivity.this);
                }
            }
        });

    }

    private void orderActive() {
        Call<ResultAPIModel<ActiveOrders>> call = GlobalMethods.getApiInterface().orderActive(GlobalMethods.getHeaders());
        pd.show();
        call.enqueue(new Callback<ResultAPIModel<ActiveOrders>>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<ResultAPIModel<ActiveOrders>> call, Response<ResultAPIModel<ActiveOrders>> response) {
                ResultAPIModel<ActiveOrders> result = response.body();
                pd.dismiss();
                if (response.isSuccessful()) {
                    assert result != null;
                    if (result.success == Constant.Status_successful) {
                        OrderResponse order = null;
                        OrderFromUser orderFromUser = null;
                        TypeToken<OrderFromUser> responseTypeToken2 = null;
                        Gson gson = new GsonBuilder().create();

                        GlobalMethods.printGson("orderActive", result.data);
                        if (result.data.getOrders().size() != 0)
                            showNewOrderDialogDialog(String.valueOf(result.data.getOrders().get(0).getId())
                                    , result.data.getOrders().get(0));

                        else if (result.data.getNotification() != null) {
                            if (result.data.getNotification().getType() == 7) {
//                                order =  result.data.getNotification().getOrders();
                                TypeToken<OrderResponse> responseTypeToken = new TypeToken<OrderResponse>() {
                                };
                                order = gson.fromJson(gson.toJson(result.data.getNotification().getOrders()), responseTypeToken.getType());
                                showNewOrderDialogDialog(String.valueOf(order.getId()), order);
                            } else {
//                                orderFromUser = result.data.getNotification().getOrders2();
                                responseTypeToken2 = new TypeToken<OrderFromUser>() {
                                };
                                orderFromUser = gson.fromJson(gson.toJson(result.data.getNotification().getOrders()), responseTypeToken2.getType());
                                showNewOrderFromUserDialog(String.valueOf(orderFromUser.getId()), orderFromUser);
                            }

                        } else if (result.data.getDelivery_services().size() != 0)
                        showNewOrderFromUserDialog(String.valueOf(result.data.getDelivery_services().get(0).getId()), result.data.getDelivery_services().get(0));

                    } else {
                        GlobalMethods.showErrorToast(MainDeliveryActivity.this, result.message);

                    }

                } else {
                    ErrorModel errorModel = null;
                    try {
                        assert response.errorBody() != null;
                        String error = response.errorBody().string();
                        Log.d("onResponse: ", error);
                        errorModel = new Gson().fromJson(error, new TypeToken<ErrorModel>() {
                        }.getType());
                        GlobalMethods.showErrorToast(MainDeliveryActivity.this, errorModel.message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResultAPIModel<ActiveOrders>> call, Throwable t) {
                t.printStackTrace();
                if (t instanceof UnknownHostException || t instanceof NoRouteToHostException) {
                    NoConnectionDialog.with(MainDeliveryActivity.this);
                }
            }
        });

    }

    private void openWaze(double lat, double lng) {
        try {
            // Launch Waze to look for Hawaii:
            String url = "https://www.waze.com/ul?ll=+" + lat + "%2C" + lng + "&navigate=yes&zoom=17";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            // If Waze is not installed, open it in Google Play:
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.waze"));
            startActivity(intent);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }


}