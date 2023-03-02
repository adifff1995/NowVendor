package com.app.radarvendor.Activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.app.radarvendor.Adapter.ChooseTagAdapter;
import com.app.radarvendor.Adapter.WorkingDaysAdapter;
import com.app.radarvendor.Module.ErrorModel;
import com.app.radarvendor.Module.ResultAPIModel;
import com.app.radarvendor.Module.ServiceType;
import com.app.radarvendor.Module.Tag;
import com.app.radarvendor.Module.Vendor;
import com.app.radarvendor.Module.WorkingDay;
import com.app.radarvendor.R;
import com.app.radarvendor.Utils.Constant;
import com.app.radarvendor.Utils.GlobalMethods;
import com.app.radarvendor.Utils.NoConnectionDialog;
import com.app.radarvendor.databinding.ActivityAddServicesBinding;
import com.app.radarvendor.databinding.ActivityEditProfileBinding;
import com.app.radarvendor.databinding.DialogChooseProductsBinding;
import com.app.radarvendor.databinding.DialogChooseTagBinding;
import com.app.radarvendor.databinding.DialogViewBillBinding;
import com.app.radarvendor.databinding.FullMapDialogBinding;
import com.app.radarvendor.databinding.SuccessfulProccessSheetBinding;
import com.app.radarvendor.databinding.WokingDaySheetBinding;
import com.bumptech.glide.Glide;
import com.github.razir.progressbutton.ButtonTextAnimatorExtensionsKt;
import com.github.razir.progressbutton.DrawableButtonExtensionsKt;
import com.github.razir.progressbutton.ProgressButtonHolderKt;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.hawk.Hawk;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.NoRouteToHostException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {

    private ActivityEditProfileBinding binding;
    private SuccessfulProccessSheetBinding successfulPaymentSheetBinding;
    private BottomSheetDialog successfulPaymentSheetDialog;
    private Vendor vendor;
    private Context context = EditProfileActivity.this;
    private String time_from, time_to;
    private WokingDaySheetBinding wokingDaySheetBinding;
    private BottomSheetDialog wokingDaySheetDialog;
    private List<WorkingDay> workingDayList;
    private WorkingDaysAdapter workingDaysAdapter;
    private ArrayList<ServiceType> serviceTypeList;
    private List<String> serviceTypeNamesList;
    private int serviceIndex, service_id;
    private MediaType mediaType = MediaType.parse("multipart/form-data");
    private Uri image;
    private FullMapDialogBinding fullMapBinding;
    private Dialog popupView;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest mLocationRequest;
    private ActivityResultLauncher<IntentSenderRequest> launcher;
    private GoogleMap mMap;
    private double lat, lng;
    List<Tag> tagsList;
    private List<Integer> tagsIDs;
    private ChooseTagAdapter chooseTagAdapter;
    private DialogChooseTagBinding chooseTagsBinding;
    private AlertDialog alertDialog;
    private ProgressDialog progressDialog;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setUpActivity();
        setUpActions();
        setUpProfile();
        showWorkingDaysSheet();
        showFullMapDialog();
        getServiceType();
        getTags();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setUpProfile() {
        binding.edName.setText(vendor.getName());
        binding.edEmail.setText(vendor.getEmail());
        binding.edMobile.setText(vendor.getPhone());
        binding.edServiceDesc.setText(vendor.getService_description());
        binding.edRaduis.setText(String.valueOf(vendor.getRadius_work()));
//        binding.fromTime.setHint(vendor.getWork_time_from());
//        binding.toTime.setHint(vendor.getWork_time_to());
        lat = Double.parseDouble(vendor.getLatitude());
        lng = Double.parseDouble(vendor.getLongitude());
        binding.edLocation.setText(getLocationFromAddress(lat, lng));
        time_from = vendor.getWork_time_from();
        time_to = vendor.getWork_time_to();
        CircularProgressDrawable circularProgressDrawable = GlobalMethods.getPlaceHolder(context);
        circularProgressDrawable.start();
        Glide.with(context).load(vendor.getIcon_url()).placeholder(circularProgressDrawable).into(binding.img);

        ProgressButtonHolderKt.bindProgressButton(this, binding.btnEdit);
        ButtonTextAnimatorExtensionsKt.attachTextChangeAnimator(binding.btnEdit);

        ProgressButtonHolderKt.bindProgressButton(this, binding.btnChange);
        ButtonTextAnimatorExtensionsKt.attachTextChangeAnimator(binding.btnChange);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

//        startLocationUpdates();
        requestPermission();

        launcher = registerForActivityResult(
                new ActivityResultContracts.StartIntentSenderForResult(),
                result1 -> {
                    if (result1.getResultCode() == Activity.RESULT_OK) {
                        // All required changes were successfully made
//                        startLocationUpdates();

                    } else {
                        // The user was asked to change settings, but chose not to
                    }
                });

        binding.serviceType.setLifecycleOwner(this);
        binding.serviceType.setSpinnerOutsideTouchListener((view, motionEvent) -> {
            if (binding.serviceType.isShowing())
                binding.serviceType.dismiss();
        });

    }

    private void setUpActivity() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("انتظر قليلا");
        binding.showPass1.setTag(true);
        binding.showPass2.setTag(true);
        binding.showPass3.setTag(true);
        tagsList = new ArrayList<>();
        serviceTypeList = new ArrayList<>();
        serviceTypeNamesList = new ArrayList<>();
        binding.tool.tvMainTitle.setText(getString(R.string.edit_profile));
        vendor = (Vendor) getIntent().getSerializableExtra("profile");
        ProgressButtonHolderKt.bindProgressButton(this, binding.btnEdit);
        ButtonTextAnimatorExtensionsKt.attachTextChangeAnimator(binding.btnEdit);
    }

    private void setUpActions() {
        binding.tool.imgBack.setOnClickListener(view -> {
            if (binding.passwordSection.getVisibility() == View.VISIBLE) {
                binding.passwordSection.setVisibility(View.GONE);
                binding.linearMain.setVisibility(View.VISIBLE);
            } else
                finish();
        });

        binding.edLocation.setOnClickListener(view -> popupView.show());

        binding.img.setOnClickListener(view ->
                PickImageDialog.build(new PickSetup())
                        .setOnPickResult(r -> {
                            image = r.getUri();
                            binding.img.setImageURI(r.getUri());
                        })
                        .setOnPickCancel(() -> {
                        }).show(getSupportFragmentManager()));
        binding.btnEdit.setOnClickListener(view -> {
            if (GlobalMethods.setEditTextEmptyError(context, binding.edServiceDesc) &&
                    GlobalMethods.setEditTextEmptyError(context, binding.edName) &&
                    GlobalMethods.setEditTextEmptyError(context, binding.edEmail) &&
                    GlobalMethods.setEditTextEmptyError(context, binding.edMobile)
            )
                if (workingDaysAdapter.getCheckedList().size() != 0) {
//                        if (lat != -1 && lng != -1) {
                    updateProfile(binding.edName.getText().toString(),
                            binding.edEmail.getText().toString(),
                            binding.edMobile.getText().toString(),
                            String.valueOf(lat),
                            String.valueOf(lng),
                            Hawk.get(Constant.token),
                            String.valueOf(service_id),
                            binding.edServiceDesc.getText().toString(),
                            binding.edRaduis.getText().toString(),
                            workingDaysAdapter.getCheckedList(),
                            time_from,
                            time_to,
                            image
                    );


//                        } else
//                            GlobalMethods.showWarningToast(activity, "الرجاء اختيار موقع");


                } else
                    GlobalMethods.showWarningToast(EditProfileActivity.this, "عليك اختيار أيام العمل");

        });

        binding.workingDays.setOnClickListener(view -> wokingDaySheetDialog.show());
//        binding.fromTime.setOnClickListener(view -> {
//            Calendar mcurrentTime = Calendar.getInstance();
//            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
//            int minute = mcurrentTime.get(Calendar.MINUTE);
//            TimePickerDialog mTimePicker;
//            mTimePicker = new TimePickerDialog(context, (timePicker, houre, min) -> {
//                time_from = houre + ":" + min;
//                binding.fromTime.setText(time_from);
//            }, hour, minute, true);
//            mTimePicker.setTitle(getString(R.string.working_times));
//            mTimePicker.show();
//        });
//
//        binding.toTime.setOnClickListener(view -> {
//            Calendar mcurrentTime = Calendar.getInstance();
//            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
//            int minute = mcurrentTime.get(Calendar.MINUTE);
//            TimePickerDialog mTimePicker;
//            mTimePicker = new TimePickerDialog(context, (timePicker, houre, min) -> {
//                time_to = houre + ":" + min;
//                binding.toTime.setText(time_to);
//            }, hour, minute, true);
//            mTimePicker.setTitle(getString(R.string.working_times));
//            mTimePicker.show();
//        });

        binding.tvChangePassword.setOnClickListener(view -> {
            binding.linearMain.setVisibility(View.GONE);
            binding.passwordSection.setVisibility(View.VISIBLE);
        });


        binding.edOldPass.setOnFocusChangeListener((view, b) -> {
            if (b) {
                binding.edOldPass.setBackgroundResource(R.drawable.bg_button_bordered_app);
            } else {
                binding.edOldPass.setBackgroundResource(R.drawable.bg_button_secondry_shape);
            }
        });

        binding.edNewPass.setOnFocusChangeListener((view, b) -> {
            if (b) {
                binding.edNewPass.setBackgroundResource(R.drawable.bg_button_bordered_app);
            } else {
                binding.edNewPass.setBackgroundResource(R.drawable.bg_button_secondry_shape);
            }
        });

        binding.edConfirmNewPass.setOnFocusChangeListener((view, b) -> {
            if (b) {
                binding.edConfirmNewPass.setBackgroundResource(R.drawable.bg_button_bordered_app);
            } else {
                binding.edConfirmNewPass.setBackgroundResource(R.drawable.bg_button_secondry_shape);
            }
        });

        binding.btnChange.setOnClickListener(view -> {
            if (GlobalMethods.setEditTextEmptyError(context, binding.edOldPass) &&
                    GlobalMethods.setEditTextEmptyError(context, binding.edNewPass) &&
                    GlobalMethods.setEditTextEmptyError(context, binding.edConfirmNewPass)
            ) {

                changePassword(binding.edOldPass.getText().toString(),
                        binding.edNewPass.getText().toString(),
                        binding.edConfirmNewPass.getText().toString());
            }
        });

        binding.showPass1.setOnClickListener(v -> {
            boolean b = (boolean) binding.showPass1.getTag();
            if (b) {
                binding.showPass1.setImageResource(R.drawable.ic_eye);
                binding.edOldPass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                binding.edOldPass.setSelection(binding.edOldPass.length());
                binding.showPass1.setTag(false);
            } else {
                binding.showPass1.setImageResource(R.drawable.ic_baseline_remove_red_eye_24);
                binding.edOldPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                binding.edOldPass.setSelection(binding.edOldPass.length());
                binding.showPass1.setTag(true);
            }
        });


        binding.showPass2.setOnClickListener(v -> {
            boolean b = (boolean) binding.showPass2.getTag();
            if (b) {
                binding.showPass2.setImageResource(R.drawable.ic_eye);
                binding.edNewPass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                binding.edNewPass.setSelection(binding.edNewPass.length());
                binding.showPass2.setTag(false);
            } else {
                binding.showPass2.setImageResource(R.drawable.ic_baseline_remove_red_eye_24);
                binding.edNewPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                binding.edNewPass.setSelection(binding.edNewPass.length());
                binding.showPass2.setTag(true);
            }
        });

        binding.showPass3.setOnClickListener(v -> {
            boolean b = (boolean) binding.showPass3.getTag();
            if (b) {
                binding.showPass3.setImageResource(R.drawable.ic_eye);
                binding.edConfirmNewPass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                binding.edConfirmNewPass.setSelection(binding.edConfirmNewPass.length());
                binding.showPass3.setTag(false);
            } else {
                binding.showPass3.setImageResource(R.drawable.ic_baseline_remove_red_eye_24);
                binding.edConfirmNewPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                binding.edConfirmNewPass.setSelection(binding.edConfirmNewPass.length());
                binding.showPass3.setTag(true);
            }
        });
        binding.edTags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.show();
            }
        });
    }

    private void showFullMapDialog() {
        popupView = new Dialog(context);
        fullMapBinding = FullMapDialogBinding.inflate(getLayoutInflater());
        popupView.setContentView(fullMapBinding.getRoot());
        Objects.requireNonNull(popupView.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = ViewGroup.LayoutParams.MATCH_PARENT;
        popupView.getWindow().setLayout(width, height);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        fullMapBinding.imgClose.setOnClickListener(v -> popupView.dismiss());
        if (mapFragment != null) {
            mapFragment.getMapAsync(googleMap -> {
                mMap = googleMap;
                mMap.getUiSettings().setZoomControlsEnabled(false);
                mMap.getUiSettings().setIndoorLevelPickerEnabled(false);
                mMap.getUiSettings().setMapToolbarEnabled(false);

                mMap.getUiSettings().setMyLocationButtonEnabled(true);

                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mMap.setMyLocationEnabled(true);


                mMap.setOnMapClickListener(latLng -> {
                    lat = latLng.latitude;
                    lng = latLng.longitude;
                    mMap.addMarker(new MarkerOptions().position(latLng));
                    binding.edLocation.setText(lat + " , " + lng);
                });
            });
        }

    }


    private void showWorkingDaysSheet() {
        wokingDaySheetBinding = WokingDaySheetBinding.inflate(getLayoutInflater());
        wokingDaySheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialog);
        wokingDaySheetDialog.setContentView(wokingDaySheetBinding.getRoot());
        workingDayList = new ArrayList<>();
//        List<WorkingDay> workingDayList1 = new ArrayList<>();
//        workingDayList1.addAll(GlobalMethods.getWorkingDays(context));
//        for (int j = 0; j < vendor.getWork_days().size(); j++) {
//            if (vendor.getWork_days().get(j).getSelected() == 1) {
//                workingDayList1.get(j).setSelected(1);
//            }
//        }


//        workingDayList.addAll(workingDayList1);
        workingDaysAdapter = new WorkingDaysAdapter(EditProfileActivity.this, vendor.getWork_days(), (position, isChecked) -> {


        });
        wokingDaySheetBinding.rvWorkingDays.setAdapter(workingDaysAdapter);
        wokingDaySheetBinding.rvWorkingDays.setLayoutManager(new LinearLayoutManager(context));
        serDays();
        wokingDaySheetBinding.btnConfirm.setOnClickListener(view -> {
            serDays();
        });
        wokingDaySheetBinding.btnClose.setOnClickListener(view -> {
            wokingDaySheetDialog.dismiss();
        });

    }

    private void serDays() {
        StringBuilder builder = new StringBuilder();
        for (String star : workingDaysAdapter.getCheckedNamesList()) {
            builder.append(star);
            builder.append(", ");
        }
        binding.workingDays.setHint(builder.toString());
        wokingDaySheetDialog.dismiss();
    }

    private void showPaymentSuccessfulSheet() {
        successfulPaymentSheetBinding = SuccessfulProccessSheetBinding.inflate(getLayoutInflater());
        successfulPaymentSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialog);
        successfulPaymentSheetDialog.setContentView(successfulPaymentSheetBinding.getRoot());
        successfulPaymentSheetBinding.tvTitle.setText(getString(R.string.completed_successfully));
        successfulPaymentSheetDialog.show();
    }

    private void showTagsDialog(List<Tag> tagList) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        chooseTagsBinding = DialogChooseTagBinding.inflate(getLayoutInflater());
        chooseTagAdapter = new ChooseTagAdapter(context, tagList);
        chooseTagsBinding.rvTags.setAdapter(chooseTagAdapter);
        chooseTagsBinding.rvTags.setLayoutManager(new LinearLayoutManager(context));
        chooseTagsBinding.btnSave.setOnClickListener(view -> {
            alertDialog.dismiss();
            tagsIDs = chooseTagAdapter.getSelectedTags();
            binding.edTags.setText(chooseTagAdapter.getSelectedNamesTags().toString());
        });
        builder.setView(chooseTagsBinding.getRoot());
        alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.dismiss();
    }


    private void getServiceType() {
        Map<String, Object> params = GlobalMethods.getParams(context);
        params.put("type", String.valueOf(GlobalMethods.getAccountType()));
        Log.e("getServiceType: ", String.valueOf(GlobalMethods.getAccountType()));
        Call<ResultAPIModel<List<ServiceType>>> call = GlobalMethods.getApiInterface().getServiceType(params);
        call.enqueue(new Callback<ResultAPIModel<List<ServiceType>>>() {
            @Override
            public void onResponse(Call<ResultAPIModel<List<ServiceType>>> call, Response<ResultAPIModel<List<ServiceType>>> response) {
                ResultAPIModel<List<ServiceType>> result = response.body();
                if (response.isSuccessful()) {
                    assert result != null;
                    if (result.success == Constant.Status_successful) {
                        serviceTypeList.addAll(result.data);
                        GlobalMethods.printGson("getService_id", vendor.getService_id());
                        GlobalMethods.printGson("serviceTypeList", serviceTypeList);
                        for (int i = 0; i < serviceTypeList.size(); i++) {
                            serviceTypeNamesList.add(serviceTypeList.get(i).getName());
                            if (vendor.getService_id() != null) {
                                if (serviceTypeList.get(i).getId() == Integer.parseInt(vendor.getService_id()))
                                    serviceIndex = i;
                                GlobalMethods.printGson("serviceIndex", serviceIndex);

                            }

                        }
                        binding.serviceType.setItems(serviceTypeNamesList);

                        binding.serviceType.setOnSpinnerItemSelectedListener((i, o, i1, t1) -> {
                            Log.e("onResponse: ", serviceTypeList.get(i1).getId() + "");
                            service_id = serviceTypeList.get(i1).getId();
                        });
                        binding.serviceType.selectItemByIndex(serviceIndex);
                    } else {
                        GlobalMethods.showErrorToast(EditProfileActivity.this, result.message);

                    }

                } else {
                    ErrorModel errorModel = null;
                    try {
                        assert response.errorBody() != null;
                        String error = response.errorBody().string();
                        Log.d("onResponse: ", error);
                        errorModel = new Gson().fromJson(error, new TypeToken<ErrorModel>() {
                        }.getType());
                        GlobalMethods.showErrorToast(EditProfileActivity.this, errorModel.message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResultAPIModel<List<ServiceType>>> call, Throwable t) {
                t.printStackTrace();
                if (t instanceof UnknownHostException || t instanceof NoRouteToHostException) {
                    NoConnectionDialog.with(EditProfileActivity.this);
                }
            }
        });

    }

    private void getTags() {
        Call<ResultAPIModel<List<Tag>>> call = GlobalMethods.getApiInterface().getTags();
        progressDialog.show();
        call.enqueue(new Callback<ResultAPIModel<List<Tag>>>() {
            @Override
            public void onResponse(Call<ResultAPIModel<List<Tag>>> call, Response<ResultAPIModel<List<Tag>>> response) {
                ResultAPIModel<List<Tag>> result = response.body();
                if (response.isSuccessful()) {
                    assert result != null;
                    if (result.success == Constant.Status_successful) {
                        GlobalMethods.printGson("getTags", result.data);
                        tagsList.clear();
                        tagsList.addAll(result.data);
                        for (int i = 0; i < vendor.getTags().size(); i++) {
                            for (int j = 0; j < tagsList.size(); j++) {
                                if (tagsList.get(j).getId() == vendor.getTags().get(i).getId()) {
                                        tagsList.get(j).setChecked(true);
                                }

                            }
                        }
                        GlobalMethods.printGson("getTags",vendor.getTags());
                        showTagsDialog(tagsList);


//                        for (int i = 0; i < result.data.size(); i++) {
//                            tagNamesList.add(result.data.get(i).getName());
//                            hashMap.put(result.data.get(i).getName(), result.data.get(i).getId());
//                        }
//
//                        binding.spinnerMultiSpinner.setAdapterWithOutImage(context, tagNamesList, chosenItems -> {
//                            tagsIDs = new ArrayList<>();
//                            for (int i = 0; i < chosenItems.size(); i++) {
//                                tagsIDs.add(hashMap.get(chosenItems.get(i)));
//                            }
//                        });
//
//                        binding.spinnerMultiSpinner.initMultiSpinner(context,binding.spinnerMultiSpinner);


                    } else {
                        Log.d("onResponse: ", result.message);
                        GlobalMethods.showErrorToast(EditProfileActivity.this, result.message);
                    }

                } else {
                    ErrorModel errorModel = null;
                    try {
                        assert response.errorBody() != null;
                        String error = response.errorBody().string();
                        Log.d("onResponse: ", error);
                        errorModel = new Gson().fromJson(error, new TypeToken<ErrorModel>() {
                        }.getType());
                        GlobalMethods.showErrorToast(EditProfileActivity.this, errorModel.message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResultAPIModel<List<Tag>>> call, Throwable t) {
                t.printStackTrace();
                if (t instanceof UnknownHostException || t instanceof NoRouteToHostException) {
                    NoConnectionDialog.with(EditProfileActivity.this);
                }
            }
        });

    }

    public void updateProfile(String name, String email, String phone, String latitude, String longitude,
                              String fcm, String service_id, String service_description, String raduis,
                              List<WorkingDay> work_days, String work_time_from, String work_time_to, Uri photo) {
        MultipartBody.Part body = null;
        if (photo != null) {
            File file = null;
            try {
                file = new File(getPath(context, photo));
                RequestBody requestFile = RequestBody.create(mediaType, file);
                body = MultipartBody.Part.createFormData("icon_url", URLEncoder.encode(file.getName(), "utf-8"), requestFile);

            } catch (URISyntaxException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        Gson gson = new Gson();

        RequestBody nameRequestBody = RequestBody.create(mediaType, name);
        RequestBody emailRequestBody = RequestBody.create(mediaType, email);
        RequestBody phoneRequestBody = RequestBody.create(mediaType, phone);
        RequestBody latitudeRequestBody = RequestBody.create(mediaType, latitude);
        RequestBody longitudeRequestBody = RequestBody.create(mediaType, longitude);
        RequestBody fcmRequestBody = RequestBody.create(mediaType, fcm);
        RequestBody serviceIdRequestBody = RequestBody.create(mediaType, service_id);
        RequestBody serviceDescRequestBody = RequestBody.create(mediaType, service_description);
        RequestBody raduisRequestBody = RequestBody.create(mediaType, raduis);
        RequestBody workDaysRequestBody = RequestBody.create(mediaType, gson.toJson(work_days));
        RequestBody tagsRequestBody = RequestBody.create(mediaType, gson.toJson(tagsIDs));
//        RequestBody workTimeFromRequestBody = RequestBody.create(mediaType, work_time_from);
//        RequestBody workTimeToRequestBody = RequestBody.create(mediaType, work_time_to);

        Log.e("updateProfile: ", gson.toJson(tagsIDs));
        Call<ResultAPIModel<Vendor>> call = GlobalMethods.getApiInterface().updateProfile(
                GlobalMethods.getHeaders(),
                nameRequestBody, emailRequestBody,
                phoneRequestBody,
                latitudeRequestBody, longitudeRequestBody, fcmRequestBody,
                serviceIdRequestBody, serviceDescRequestBody, workDaysRequestBody,
                raduisRequestBody, tagsRequestBody,
                body);

        GlobalMethods.showProgress(binding.btnEdit);
        call.enqueue(new Callback<ResultAPIModel<Vendor>>() {
            @Override
            public void onResponse(Call<ResultAPIModel<Vendor>> call, Response<ResultAPIModel<Vendor>> response) {
                binding.btnEdit.setEnabled(true);
                DrawableButtonExtensionsKt.hideProgress(binding.btnEdit, R.string.edit);
                ResultAPIModel<Vendor> result = response.body();
                if (response.isSuccessful()) {
                    assert result != null;
                    if (result.success == Constant.Status_successful) {
                        GlobalMethods.showSuccessToast(EditProfileActivity.this, result.message);
                        finish();

                    } else {
                        GlobalMethods.showErrorToast(EditProfileActivity.this, result.message);
                    }


                } else {

                    ErrorModel errorModel = null;
                    try {
                        assert response.errorBody() != null;
                        String error = response.errorBody().string();
                        Log.d("onResponse: ", error);
                        errorModel = new Gson().fromJson(error, new TypeToken<ErrorModel>() {
                        }.getType());
                        GlobalMethods.showErrorToast(EditProfileActivity.this, errorModel.message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResultAPIModel<Vendor>> call, Throwable t) {
                t.printStackTrace();
                if (t instanceof UnknownHostException || t instanceof NoRouteToHostException) {
                    NoConnectionDialog.with(EditProfileActivity.this);
                }
            }
        });

    }

    private void changePassword(String old_pass, String new_pass, String new_pass_confirm) {
        Map<String, Object> params = GlobalMethods.getParams(context);
        params.put("old_password", old_pass);
        params.put("new_password", new_pass);
        params.put("new_password_confirmation", new_pass_confirm);

        Call<ResultAPIModel> call = GlobalMethods.getApiInterface().changePassword(GlobalMethods.getHeaders(), params);
        GlobalMethods.showProgress(binding.btnChange);
        call.enqueue(new Callback<ResultAPIModel>() {
            @Override
            public void onResponse(Call<ResultAPIModel> call, Response<ResultAPIModel> response) {
                binding.btnChange.setEnabled(true);
                DrawableButtonExtensionsKt.hideProgress(binding.btnChange, R.string.reset);
                ResultAPIModel result = response.body();
                if (response.isSuccessful()) {
                    assert result != null;
                    if (result.success == Constant.Status_successful) {
                        GlobalMethods.showSuccessToast(EditProfileActivity.this, result.message);
                        binding.passwordSection.setVisibility(View.GONE);
                        binding.linearMain.setVisibility(View.VISIBLE);

                    } else {
                        GlobalMethods.showErrorToast(EditProfileActivity.this, result.message);

                    }

                } else {
                    ErrorModel errorModel = null;
                    try {
                        assert response.errorBody() != null;
                        String error = response.errorBody().string();
                        Log.d("onResponse: ", error);
                        errorModel = new Gson().fromJson(error, new TypeToken<ErrorModel>() {
                        }.getType());
                        GlobalMethods.showErrorToast(EditProfileActivity.this, errorModel.message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResultAPIModel> call, Throwable t) {
                t.printStackTrace();
                if (t instanceof UnknownHostException || t instanceof NoRouteToHostException) {
                    NoConnectionDialog.with(EditProfileActivity.this);
                }
            }
        });

    }

    private String getLocationFromAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(
                    lat,
                    lng,
                    1);
        } catch (Exception ioException) {
            Log.e("", "Error in getting address for the location");
        }

        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "No address found for the location", Toast.LENGTH_SHORT).show();
            return "";
        } else {
            Address address = addresses.get(0);
            String str_postcode = address.getPostalCode();
            String str_Country = address.getCountryName();
            String str_state = address.getAdminArea();
            String str_district = address.getSubAdminArea();
            String str_locality = address.getLocality();
            String str_address = address.getFeatureName();
            Log.e("getLocationFromAddress: ", str_postcode + " " + str_Country + " " +
                    str_state + " " + str_address + " " + str_district + " " + str_locality);

            return str_address;

        }
    }

    public static String getPath(Context context, Uri uri) throws URISyntaxException {
        final boolean needToCheckUri = Build.VERSION.SDK_INT >= 19;
        String selection = null;
        String[] selectionArgs = null;
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        // deal with different Uris.
        if (needToCheckUri && DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("image".equals(type)) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[]{split[1]};
            }
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        Log.e("startLocationUpdates: ", "startLocationUpdates");
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermission();
            return;
        } else
            turnOnLocation();

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                // Logic to handle location object
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void requestPermission() {
        ActivityResultLauncher<String[]> locationPermissionRequest =
                registerForActivityResult(new ActivityResultContracts
                                .RequestMultiplePermissions(), result -> {
                            Boolean fineLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_FINE_LOCATION, false);
                            Boolean coarseLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_COARSE_LOCATION, false);
                            if (fineLocationGranted != null && fineLocationGranted) {
                                // Precise location access granted.
                                turnOnLocation();
                            } else if (coarseLocationGranted != null && coarseLocationGranted) {
                                // Only approximate location access granted.
//                                onMapReady(mMap);
                                turnOnLocation();

                            } else {
                                // No location access granted.
                            }
                        }
                );


// Before you perform the actual permission request, check whether your app
// already has the permissions, and whether your app needs to show a permission
// rationale dialog. For more details, see Request permissions.
        locationPermissionRequest.launch(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });
    }

    private void turnOnLocation() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(Priority.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        Task<LocationSettingsResponse> result =
                LocationServices.getSettingsClient(context).checkLocationSettings(builder.build());

        result.addOnCompleteListener(task -> {
            try {
                LocationSettingsResponse response = task.getResult(ApiException.class);
                // All location settings are satisfied. The client can initialize location
                // requests here.
                mMap.setMyLocationEnabled(true);
                fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                    if (location != null) {
                        // Logic to handle location object
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));

                    }
                });

            } catch (ApiException exception) {
                switch (exception.getStatusCode()) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the
                        // user a dialog.
                        try {
                            // Cast to a resolvable exception.
                            ResolvableApiException resolvable = (ResolvableApiException) exception;
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().

                            IntentSenderRequest intentSenderRequest = new IntentSenderRequest.Builder(resolvable.getResolution()).build();
                            launcher.launch(intentSenderRequest);
                            resolvable.startResolutionForResult(
                                    EditProfileActivity.this,
                                    100);
                        } catch (ClassCastException e) {
                            // Ignore, should be an impossible error.
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.

                        break;
                }
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (binding.passwordSection.getVisibility() == View.VISIBLE) {
            binding.passwordSection.setVisibility(View.GONE);
            binding.linearMain.setVisibility(View.VISIBLE);
        } else
            finish();
    }
}