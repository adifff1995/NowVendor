package com.app.radarvendor.Fragment;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.app.radarvendor.Activities.MainActivity;
import com.app.radarvendor.Activities.MainServicesActivity;
import com.app.radarvendor.Adapter.WorkingDaysAdapter;
import com.app.radarvendor.Module.ErrorModel;
import com.app.radarvendor.Module.ResultAPIModel;
import com.app.radarvendor.Module.ServiceType;
import com.app.radarvendor.Module.Tag;
import com.app.radarvendor.Module.Vendor;
import com.app.radarvendor.Module.WorkingDay;
import com.app.radarvendor.R;
import com.app.radarvendor.Utils.Constant;
import com.app.radarvendor.Utils.FragmentUtil;
import com.app.radarvendor.Utils.GlobalMethods;
import com.app.radarvendor.Utils.NoConnectionDialog;
import com.app.radarvendor.databinding.ChooseServiceTimeSheetBinding;
import com.app.radarvendor.databinding.FragmentCreateNewAccountBinding;
import com.app.radarvendor.databinding.FullMapDialogBinding;
import com.app.radarvendor.databinding.WokingDaySheetBinding;
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
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.hawk.Hawk;
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.NoRouteToHostException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateNewAccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateNewAccountFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FragmentCreateNewAccountBinding binding;
    private Activity activity;
    private List<WorkingDay> workingDayList;
    private WorkingDaysAdapter workingDaysAdapter;
    private WokingDaySheetBinding wokingDaySheetBinding;
    private BottomSheetDialog wokingDaySheetDialog;
    private List<ServiceType> serviceTypeList;
    private List<String> serviceTypeNamesList;
    private int service_id;
    private MediaType mediaType = MediaType.parse("multipart/form-data");
    private Uri image;
    //    private String time_from, time_to = null;
    private Dialog popupView;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest mLocationRequest;
    private ActivityResultLauncher<IntentSenderRequest> launcher;
    private ActivityResultLauncher<Intent> mActivityResult;
    private GoogleMap mMap;
    private double lat, lng = -1;
    private SupportMapFragment mapFragment;
    private boolean isClicked = false;
    private String phoneCode = "972";
    private Gson gson;
    private List<Integer> tagsIDs;
    private List<Tag> tagList;
    List<String> tagNamesList;
    private HashMap<String, Integer> hashMap;

    public CreateNewAccountFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreateNewAccountFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateNewAccountFragment newInstance(String param1, String param2) {
        CreateNewAccountFragment fragment = new CreateNewAccountFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);


        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
                if (binding.linearStep1.getVisibility() == View.VISIBLE) {
                    getParentFragmentManager().popBackStack();
                } else {
                    binding.tvStep2.setBackgroundResource(R.drawable.ic_gray_circle);
                    binding.linearStep2.setVisibility(View.INVISIBLE);
                    binding.linearStep1.setVisibility(View.VISIBLE);
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

        // The callback can be enabled or disabled here or in handleOnBackPressed()
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCreateNewAccountBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpFragment();
        setUpAction();
        getServiceType();
        showWorkingDaysSheet();
        getTags();
//        startLocationUpdates();

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setUpFragment() {
        serviceTypeList = new ArrayList<>();
        tagList = new ArrayList<>();
        serviceTypeNamesList = new ArrayList<>();
        binding.showPass1.setTag(true);
        binding.showPass2.setTag(true);
        binding.spPhone.selectItemByIndex(0);
        hashMap = new HashMap<>();
        gson = new Gson();
        ProgressButtonHolderKt.bindProgressButton(this, binding.btnSignup);
        ButtonTextAnimatorExtensionsKt.attachTextChangeAnimator(binding.btnSignup);

        ProgressButtonHolderKt.bindProgressButton(this, binding.btnContinue);
        ButtonTextAnimatorExtensionsKt.attachTextChangeAnimator(binding.btnContinue);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
        requestPermission();

        launcher = registerForActivityResult(
                new ActivityResultContracts.StartIntentSenderForResult(),
                result1 -> {
                    if (result1.getResultCode() == Activity.RESULT_OK) {
                        // All required changes were successfully made
//                        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
//                            if (location != null) {
//                                // Logic to handle location object
//                                Log.e("turnOnLocation2", "turnOnLocation2: ");
//                                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
//
//                            }
//                        });

                    } else {
                        // The user was asked to change settings, but chose not to
                    }
                });
        binding.spPhone.setLifecycleOwner(this);
        binding.spPhone.setSpinnerOutsideTouchListener((view, motionEvent) -> {
            if (binding.spPhone.isShowing())
                binding.spPhone.dismiss();
        });
    }

    private void setUpAction() {
        binding.edName.setOnFocusChangeListener((view, b) -> {
            if (b) {
                binding.edName.setBackgroundResource(R.drawable.bg_button_bordered_app);
            } else {
                binding.edName.setBackgroundResource(R.drawable.bg_button_secondry_shape);

            }
        });

        binding.edPhone.setOnFocusChangeListener((view, b) -> {
            if (b) {
                binding.edPhone.setBackgroundResource(R.drawable.bg_button_bordered_app);
            } else {
                binding.edPhone.setBackgroundResource(R.drawable.bg_button_secondry_shape);

            }
        });
        binding.edPassword.setOnFocusChangeListener((view, b) -> {
            if (b) {
                binding.edPassword.setBackgroundResource(R.drawable.bg_button_bordered_app);
            } else {
                binding.edPassword.setBackgroundResource(R.drawable.bg_button_secondry_shape);

            }
        });

        binding.edConfirmPass.setOnFocusChangeListener((view, b) -> {
            if (b) {
                binding.edConfirmPass.setBackgroundResource(R.drawable.bg_button_bordered_app);
            } else {
                binding.edConfirmPass.setBackgroundResource(R.drawable.bg_button_secondry_shape);

            }
        });

        binding.edEmail.setOnFocusChangeListener((view, b) -> {
            if (b) {
                binding.edEmail.setBackgroundResource(R.drawable.bg_button_bordered_app);
            } else {
                binding.edEmail.setBackgroundResource(R.drawable.bg_button_secondry_shape);

            }
        });

        binding.edLocation.setOnFocusChangeListener((view, b) -> {
            if (b) {
                binding.edLocation.setBackgroundResource(R.drawable.bg_button_bordered_app);
            } else {
                binding.edLocation.setBackgroundResource(R.drawable.bg_button_secondry_shape);

            }
        });

        binding.serviceType.setOnFocusChangeListener((view, b) -> {
            if (b) {
                binding.serviceType.setBackgroundResource(R.drawable.bg_button_bordered_app);
            } else {
                binding.serviceType.setBackgroundResource(R.drawable.bg_button_secondry_shape);

            }
        });

        binding.workingDays.setOnFocusChangeListener((view, b) -> {
            if (b) {
                binding.workingDays.setBackgroundResource(R.drawable.bg_button_bordered_app);
            } else {
                binding.workingDays.setBackgroundResource(R.drawable.bg_button_secondry_shape);

            }
        });


//        binding.toTime.setOnFocusChangeListener((view, b) -> {
//            if (b) {
//                binding.toTime.setBackgroundResource(R.drawable.bg_button_bordered_app);
//            } else {
//                binding.toTime.setBackgroundResource(R.drawable.bg_button_secondry_shape);
//
//            }
//        });

//        binding.fromTime.setOnFocusChangeListener((view, b) -> {
//            if (b) {
//                binding.fromTime.setBackgroundResource(R.drawable.bg_button_bordered_app);
//            } else {
//                binding.fromTime.setBackgroundResource(R.drawable.bg_button_secondry_shape);
//
//            }
//        });

        binding.btnContinue.setOnClickListener(view -> {
            if (GlobalMethods.setEditTextEmptyError(activity, binding.edName) &&
                    GlobalMethods.setEditTextEmptyError(activity, binding.edEmail) &&
                    GlobalMethods.setEditTextEmptyError(activity, binding.edPhone) &&
                    GlobalMethods.setEditTextEmptyError(activity, binding.edPassword) &&
                    GlobalMethods.setEditTextEmptyError(activity, binding.edConfirmPass)
            ) {

                if (image != null) {
                    checkRegister(binding.edName.getText().toString(),
                            binding.edEmail.getText().toString(),
                            binding.edPhone.getText().toString(),
                            binding.edPassword.getText().toString(),
                            binding.edConfirmPass.getText().toString(),
                            image
                    );
                } else
                    GlobalMethods.showErrorToast(activity, getString(R.string.img_validation));
            }

        });

        binding.frameUploadImage.setOnClickListener(view ->
                PickImageDialog.build(new PickSetup())
                        .setOnPickResult(r -> {
                            image = r.getUri();
                            binding.img.setImageURI(r.getUri());
                            binding.tvUplaod.setVisibility(View.GONE);
                            binding.img.setVisibility(View.VISIBLE);
                        })
                        .setOnPickCancel(() -> {
                        }).show(getParentFragmentManager()));

        binding.edLocation.setOnClickListener(view -> {
            if (isClicked) {
                popupView.show();
            } else {
                showFullMapDialog();
                isClicked = true;
            }
        });

        binding.btnSignup.setOnClickListener(view -> {
            if (GlobalMethods.setEditTextEmptyError(activity, binding.edServiceDesc))
                if (workingDaysAdapter.getCheckedList().size() != 0) {
//                    if (time_from != null && time_to != null) {
                    if (lat != -1 && lng != -1) {
                        if (binding.cbRemember.isChecked()) {

                            FirebaseMessaging.getInstance().getToken().addOnSuccessListener(s -> {
                                register(binding.edName.getText().toString(),
                                        binding.edEmail.getText().toString(),
                                        binding.edPhone.getText().toString(),
                                        binding.edPassword.getText().toString(),
                                        binding.edConfirmPass.getText().toString(),
                                        binding.edMinOrder.getText().toString(),
                                        binding.edRaduis.getText().toString(),
                                        String.valueOf(lat),
                                        String.valueOf(lng),
                                        s,
                                        String.valueOf(GlobalMethods.getAccountType()),
                                        String.valueOf(service_id),
                                        binding.edServiceDesc.getText().toString(),
                                        workingDaysAdapter.getCheckedList(),
                                        image
                                );
                            });

                        } else
                            GlobalMethods.showWarningToast(activity, "يجب الموافقة على الشروط");
                    } else
                        GlobalMethods.showWarningToast(activity, "الرجاء اختيار موقع");


//                    } else
//                        GlobalMethods.showWarningToast(activity, "عليك اختيار الوقت من الى");

                } else
                    GlobalMethods.showWarningToast(activity, "عليك اختيار أيام العمل");


            {

                if (image != null) {
                    binding.tvStep2.setBackgroundResource(R.drawable.ic_step);
                    binding.linearStep1.setVisibility(View.INVISIBLE);
                    binding.linearStep2.setVisibility(View.VISIBLE);
                } else
                    GlobalMethods.showErrorToast(activity, getString(R.string.img_validation));
            }

        });
        binding.workingDays.setOnClickListener(view -> wokingDaySheetDialog.show());
//        binding.fromTime.setOnClickListener(view -> {
//            Calendar mcurrentTime = Calendar.getInstance();
//            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
//            int minute = mcurrentTime.get(Calendar.MINUTE);
//            TimePickerDialog mTimePicker;
//            mTimePicker = new TimePickerDialog(activity, (timePicker, houre, min) -> {
//                time_from = houre + ":" + min;
//                binding.fromTime.setText(time_from);
//            }, hour, minute, true);
//            mTimePicker.setTitle(getString(R.string.working_times));
//            mTimePicker.show();
//        });

//        binding.toTime.setOnClickListener(view -> {
//            Calendar mcurrentTime = Calendar.getInstance();
//            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
//            int minute = mcurrentTime.get(Calendar.MINUTE);
//            TimePickerDialog mTimePicker;
//            mTimePicker = new TimePickerDialog(activity, (timePicker, houre, min) -> {
//                time_to = houre + ":" + min;
//                binding.toTime.setText(time_to);
//            }, hour, minute, true);
//            mTimePicker.setTitle(getString(R.string.working_times));
//            mTimePicker.show();
//        });

        binding.tvLogin.setOnClickListener(view -> FragmentUtil.addFragmentWithBackStack(activity, new LoginFragment(), R.id.boarding, ""));
        binding.imgBack.setOnClickListener(view -> {
            if (binding.linearStep1.getVisibility() == View.VISIBLE) {
                getParentFragmentManager().popBackStack();
            } else {
                binding.tvStep2.setBackgroundResource(R.drawable.ic_gray_circle);
                binding.linearStep2.setVisibility(View.INVISIBLE);
                binding.linearStep1.setVisibility(View.VISIBLE);
            }
        });

        binding.serviceType.setLifecycleOwner(this);
        binding.serviceType.setSpinnerOutsideTouchListener((view, motionEvent) -> {
            if (binding.serviceType.isShowing())
                binding.serviceType.dismiss();
        });

        binding.showPass1.setOnClickListener(v -> {
            boolean b = (boolean) binding.showPass1.getTag();
            if (b) {
                binding.showPass1.setImageResource(R.drawable.ic_eye);
                binding.edPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                binding.edPassword.setSelection(binding.edPassword.length());
                binding.showPass1.setTag(false);
            } else {
                binding.showPass1.setImageResource(R.drawable.ic_baseline_remove_red_eye_24);
                binding.edPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                binding.edPassword.setSelection(binding.edPassword.length());
                binding.showPass1.setTag(true);
            }
        });


        binding.showPass2.setOnClickListener(v -> {
            boolean b = (boolean) binding.showPass2.getTag();
            if (b) {
                binding.showPass2.setImageResource(R.drawable.ic_eye);
                binding.edConfirmPass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                binding.edConfirmPass.setSelection(binding.edConfirmPass.length());
                binding.showPass2.setTag(false);
            } else {
                binding.showPass2.setImageResource(R.drawable.ic_baseline_remove_red_eye_24);
                binding.edConfirmPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                binding.edConfirmPass.setSelection(binding.edConfirmPass.length());
                binding.showPass2.setTag(true);
            }
        });

        binding.spPhone.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener<Object>() {
            @Override
            public void onItemSelected(int i, @Nullable Object o, int i1, Object t1) {
                if (i1 == 0)
                    phoneCode = "972";
                else
                    phoneCode = "970";
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermission();
            return;
        }

        turnOnLocation();
        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                // Logic to handle location object
                Log.e("startLocationUpdates: ", "Yes");
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));

            }
        });

    }

    private void showWorkingDaysSheet() {
        wokingDaySheetBinding = WokingDaySheetBinding.inflate(getLayoutInflater());
        wokingDaySheetDialog = new BottomSheetDialog(activity, R.style.BottomSheetDialog);
        wokingDaySheetDialog.setContentView(wokingDaySheetBinding.getRoot());
        workingDayList = new ArrayList<>();
        workingDayList.addAll(GlobalMethods.getWorkingDays(activity));
        workingDaysAdapter = new WorkingDaysAdapter(activity, workingDayList, (position, isChecked) -> {


        });
        wokingDaySheetBinding.rvWorkingDays.setAdapter(workingDaysAdapter);
        wokingDaySheetBinding.rvWorkingDays.setLayoutManager(new LinearLayoutManager(activity));

        wokingDaySheetBinding.btnConfirm.setOnClickListener(view -> {
            StringBuilder builder = new StringBuilder();
            for (String star : workingDaysAdapter.getCheckedNamesList()) {
                builder.append(star);
                builder.append(", ");
            }
            binding.workingDays.setHint(builder.toString());
            wokingDaySheetDialog.dismiss();
        });
        wokingDaySheetBinding.btnClose.setOnClickListener(view -> {
            wokingDaySheetDialog.dismiss();
        });
    }

    private void goToHome() {
        if (GlobalMethods.getAccountType() == 1) {
            startActivity(new Intent(activity, MainActivity.class));
        } else {
            startActivity(new Intent(activity, MainServicesActivity.class));
        }
        activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        activity.finish();
    }

    private void getServiceType() {
        Map<String, Object> params = GlobalMethods.getParams(activity);
        params.put("type", String.valueOf(GlobalMethods.getAccountType()));
        Call<ResultAPIModel<List<ServiceType>>> call = GlobalMethods.getApiInterface().getServiceType(params);
        call.enqueue(new Callback<ResultAPIModel<List<ServiceType>>>() {
            @Override
            public void onResponse(Call<ResultAPIModel<List<ServiceType>>> call, Response<ResultAPIModel<List<ServiceType>>> response) {
                ResultAPIModel<List<ServiceType>> result = response.body();
                if (response.isSuccessful()) {
                    assert result != null;
                    if (result.success == Constant.Status_successful) {
                        serviceTypeList.addAll(result.data);
                        for (int i = 0; i < serviceTypeList.size(); i++) {
                            serviceTypeNamesList.add(serviceTypeList.get(i).getName());
                        }
                        binding.serviceType.setItems(serviceTypeNamesList);
                        binding.serviceType.setOnSpinnerItemSelectedListener((i, o, i1, t1) -> service_id = serviceTypeList.get(i1).getId());

                    } else {
                        GlobalMethods.showErrorToast(activity, result.message);

                    }

                } else {
                    ErrorModel errorModel = null;
                    try {
                        assert response.errorBody() != null;
                        String error = response.errorBody().string();
                        Log.d("onResponse: ", error);
                        errorModel = new Gson().fromJson(error, new TypeToken<ErrorModel>() {
                        }.getType());
                        GlobalMethods.showErrorToast(activity, errorModel.message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResultAPIModel<List<ServiceType>>> call, Throwable t) {
                t.printStackTrace();
                if (t instanceof UnknownHostException || t instanceof NoRouteToHostException) {
                    NoConnectionDialog.with(activity);
                }
            }
        });

    }


    private void getTags() {
        Call<ResultAPIModel<List<Tag>>> call = GlobalMethods.getApiInterface().getTags();
        call.enqueue(new Callback<ResultAPIModel<List<Tag>>>() {
            @Override
            public void onResponse(Call<ResultAPIModel<List<Tag>>> call, Response<ResultAPIModel<List<Tag>>> response) {
                ResultAPIModel<List<Tag>> result = response.body();
                if (response.isSuccessful()) {
                    assert result != null;
                    if (result.success == Constant.Status_successful) {
                        GlobalMethods.printGson("getTags", result.data);
                        tagList = result.data;
                        tagNamesList = new ArrayList<>();
                        for (int i = 0; i < result.data.size(); i++) {
                            tagNamesList.add(result.data.get(i).getName());
                            hashMap.put(result.data.get(i).getName(), result.data.get(i).getId());
                        }
                        binding.spinnerMultiSpinner.setAdapterWithOutImage(activity, tagNamesList, chosenItems -> {
                            tagsIDs = new ArrayList<>();
                            for (int i = 0; i < chosenItems.size(); i++) {
                                tagsIDs.add(hashMap.get(chosenItems.get(i)));
                            }
                        });
                        binding.spinnerMultiSpinner.initMultiSpinner(activity,binding.spinnerMultiSpinner);



                    } else {
                        Log.d("onResponse: ", result.message);
                        GlobalMethods.showErrorToast(activity, result.message);
                    }

                } else {
                    ErrorModel errorModel = null;
                    try {
                        assert response.errorBody() != null;
                        String error = response.errorBody().string();
                        Log.d("onResponse: ", error);
                        errorModel = new Gson().fromJson(error, new TypeToken<ErrorModel>() {
                        }.getType());
                        GlobalMethods.showErrorToast(activity, errorModel.message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResultAPIModel<List<Tag>>> call, Throwable t) {
                t.printStackTrace();
                if (t instanceof UnknownHostException || t instanceof NoRouteToHostException) {
                    NoConnectionDialog.with(activity);
                }
            }
        });

    }


    public void register(String name, String email, String phone, String password,
                         String password_confirmation, String min_order,String raduis, String latitude, String longitude,
                         String fcm, String type, String service_id, String service_description,
                         List<WorkingDay> work_days, Uri photo) {
        MultipartBody.Part body = null;
        if (photo != null) {
            File file = null;
            try {
                file = new File(getPath(activity, photo));
                RequestBody requestFile = RequestBody.create(mediaType, file);
                body = MultipartBody.Part.createFormData("icon_url", URLEncoder.encode(file.getName(), "utf-8"), requestFile);

            } catch (URISyntaxException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        RequestBody minOrderRequestBody;

        RequestBody nameRequestBody = RequestBody.create(mediaType, name);
        RequestBody emailRequestBody = RequestBody.create(mediaType, email);
        RequestBody phoneRequestBody = RequestBody.create(mediaType, phoneCode + phone);
        RequestBody passwordRequestBody = RequestBody.create(mediaType, password);
        RequestBody passwordConfirmRequestBody = RequestBody.create(mediaType, password_confirmation);
        RequestBody latitudeRequestBody = RequestBody.create(mediaType, latitude);
        RequestBody longitudeRequestBody = RequestBody.create(mediaType, longitude);
        RequestBody fcmRequestBody = RequestBody.create(mediaType, fcm);
        RequestBody typeRequestBody = RequestBody.create(mediaType, type);
        RequestBody serviceIdRequestBody = RequestBody.create(mediaType, service_id);
        RequestBody tagsRequestBody = RequestBody.create(mediaType, gson.toJson(tagsIDs));
        RequestBody serviceDescRequestBody = RequestBody.create(mediaType, service_description);
        RequestBody raduisRequestBody = RequestBody.create(mediaType, raduis);
        if (!min_order.isEmpty() || min_order.equals(""))
            minOrderRequestBody = RequestBody.create(mediaType, "0");
        else
            minOrderRequestBody = RequestBody.create(mediaType, min_order);

        Log.e("register1: ", work_days.toString());
        Log.e("register2: ", gson.toJson(work_days));
        RequestBody workDaysRequestBody = RequestBody.create(mediaType, gson.toJson(work_days));
//        RequestBody workTimeFromRequestBody = RequestBody.create(mediaType, work_time_from);
//        RequestBody workTimeToRequestBody = RequestBody.create(mediaType, work_time_to);


        Call<ResultAPIModel<Vendor>> call = GlobalMethods.getApiInterface().register(nameRequestBody, emailRequestBody,
                phoneRequestBody, passwordRequestBody, passwordConfirmRequestBody,
                latitudeRequestBody, longitudeRequestBody, fcmRequestBody, typeRequestBody,
                serviceIdRequestBody, serviceDescRequestBody, workDaysRequestBody, minOrderRequestBody,
                raduisRequestBody , tagsRequestBody
                , body);

        GlobalMethods.showProgress(binding.btnSignup);
        call.enqueue(new Callback<ResultAPIModel<Vendor>>() {
            @Override
            public void onResponse(Call<ResultAPIModel<Vendor>> call, Response<ResultAPIModel<Vendor>> response) {
                binding.btnSignup.setEnabled(true);
                DrawableButtonExtensionsKt.hideProgress(binding.btnSignup, R.string.sign_up);
                ResultAPIModel<Vendor> result = response.body();
                if (response.isSuccessful()) {
                    assert result != null;
                    if (result.success == Constant.Status_successful) {
                        GlobalMethods.showSuccessToast(activity, result.message);
                        GlobalMethods.printGson("register", result.data);
//                        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(s -> refreshFirebaseToken(s));
                        Hawk.put(Constant.vendor, result.data);
                        FragmentUtil.addFragmentWithBackStack(activity, VerifyCodeFragment.newInstance(phoneCode + phone, ""), R.id.boarding, "");
                    } else {
                        GlobalMethods.showErrorToast(activity, result.message);
                    }


                } else {

                    ErrorModel errorModel = null;
                    try {
                        assert response.errorBody() != null;
                        String error = response.errorBody().string();
                        Log.d("onResponse: ", error);
                        errorModel = new Gson().fromJson(error, new TypeToken<ErrorModel>() {
                        }.getType());
                        GlobalMethods.showErrorToast(activity, errorModel.message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResultAPIModel<Vendor>> call, Throwable t) {
                t.printStackTrace();
                if (t instanceof UnknownHostException || t instanceof NoRouteToHostException) {
                    NoConnectionDialog.with(activity);
                }
            }
        });

    }

    public void checkRegister(String name, String email, String phone, String password,
                              String password_confirmation, Uri photo) {
        MultipartBody.Part body = null;
        if (photo != null) {
            File file = null;
            try {
                file = new File(getPath(activity, photo));
                RequestBody requestFile = RequestBody.create(mediaType, file);
                body = MultipartBody.Part.createFormData("icon_url", URLEncoder.encode(file.getName(), "utf-8"), requestFile);

            } catch (URISyntaxException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }


        RequestBody nameRequestBody = RequestBody.create(mediaType, name);
        RequestBody emailRequestBody = RequestBody.create(mediaType, email);
        RequestBody phoneRequestBody = RequestBody.create(mediaType, phoneCode + phone);
        RequestBody passwordRequestBody = RequestBody.create(mediaType, password);
        RequestBody passwordConfirmRequestBody = RequestBody.create(mediaType, password_confirmation);


        Call<ResultAPIModel> call = GlobalMethods.getApiInterface().checkRegister(nameRequestBody, emailRequestBody,
                phoneRequestBody, passwordRequestBody, passwordConfirmRequestBody,
                body);

        GlobalMethods.showProgress(binding.btnContinue);
        call.enqueue(new Callback<ResultAPIModel>() {
            @Override
            public void onResponse(Call<ResultAPIModel> call, Response<ResultAPIModel> response) {
                binding.btnContinue.setEnabled(true);
                DrawableButtonExtensionsKt.hideProgress(binding.btnContinue, R.string.continue_);
                ResultAPIModel result = response.body();
                if (response.isSuccessful()) {
                    assert result != null;
                    if (result.success == Constant.Status_successful) {
                        binding.tvStep2.setBackgroundResource(R.drawable.ic_step);
                        binding.linearStep1.setVisibility(View.INVISIBLE);
                        binding.linearStep2.setVisibility(View.VISIBLE);
                    } else {
                        GlobalMethods.showErrorToast(activity, result.message);
                    }


                } else {

                    ErrorModel errorModel = null;
                    try {
                        assert response.errorBody() != null;
                        String error = response.errorBody().string();
                        Log.d("onResponse: ", error);
                        errorModel = new Gson().fromJson(error, new TypeToken<ErrorModel>() {
                        }.getType());
                        GlobalMethods.showErrorToast(activity, errorModel.message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResultAPIModel> call, Throwable t) {
                t.printStackTrace();
                if (t instanceof UnknownHostException || t instanceof NoRouteToHostException) {
                    NoConnectionDialog.with(activity);
                }
            }
        });

    }

    private void showFullMapDialog() {
        popupView = new Dialog(requireContext());
        FullMapDialogBinding fullMapBinding = FullMapDialogBinding.inflate(getLayoutInflater());
        popupView.setContentView(fullMapBinding.getRoot());
        Objects.requireNonNull(popupView.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = ViewGroup.LayoutParams.MATCH_PARENT;
        popupView.getWindow().setLayout(width, height);
        mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);

        fullMapBinding.imgClose.setOnClickListener(v -> popupView.dismiss());
        fullMapBinding.btnSave.setOnClickListener(v -> popupView.dismiss());
        if (mapFragment != null) {
            mapFragment.getMapAsync(googleMap -> {
                mMap = googleMap;
                mMap.getUiSettings().setZoomControlsEnabled(false);
                mMap.getUiSettings().setIndoorLevelPickerEnabled(false);
                mMap.getUiSettings().setMapToolbarEnabled(false);

                mMap.getUiSettings().setMyLocationButtonEnabled(true);

                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mMap.setMyLocationEnabled(true);

//                mMap.setOnMyLocationButtonClickListener(() -> {
//                    turnOnLocation();
//                    return true;
//                });

                mMap.setOnMapClickListener(latLng -> {
                    lat = latLng.latitude;
                    lng = latLng.longitude;
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(latLng));
                    binding.edLocation.setText(lat + " , " + lng);
                });

                fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                    if (location != null) {
                        // Logic to handle location object
                        Log.e("startLocationUpdates: ", "Yes");
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));

                    }
                });
            });
        }

        popupView.show();

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
//                                onMapReady(mMap);
                                Log.e("requestPermission: ", "1");
                                turnOnLocation();
                            } else if (coarseLocationGranted != null && coarseLocationGranted) {
                                // Only approximate location access granted.
//                                onMapReady(mMap);
                                Log.e("requestPermission: ", "2");
                                turnOnLocation();

                            } else {
                                // No location access granted.
                                Log.e("requestPermission: ", "3");
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
                LocationServices.getSettingsClient(activity).checkLocationSettings(builder.build());

        result.addOnCompleteListener(task -> {
            try {
                LocationSettingsResponse response = task.getResult(ApiException.class);
                // All location settings are satisfied. The client can initialize location
                // requests here.

                Log.e("turnOnLocation", "turnOnLocation: ");
//                mMap.setMyLocationEnabled(true);
//                fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
//                    if (location != null) {
//                        // Logic to handle location object
//                        Log.e("turnOnLocation2", "turnOnLocation2: ");
//                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
//
//                    }
//                });

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
                                    activity,
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

                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.e("turnOnLocation: ", "SUCCESS");
                        break;
                }
            }
        });
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
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            activity = (Activity) context;

        }
    }
}