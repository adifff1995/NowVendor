package com.app.radarvendor.Fragment;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import com.app.radarvendor.Activities.MainActivity;
import com.app.radarvendor.Activities.MainDeliveryActivity;
import com.app.radarvendor.Activities.MainServicesActivity;
import com.app.radarvendor.Adapter.ChooseCityAdapter;
import com.app.radarvendor.Module.City;
import com.app.radarvendor.Module.DeliveryType;
import com.app.radarvendor.Module.ErrorModel;
import com.app.radarvendor.Module.ResultAPIModel;
import com.app.radarvendor.Module.Vendor;
import com.app.radarvendor.R;
import com.app.radarvendor.Utils.Constant;
import com.app.radarvendor.Utils.FragmentUtil;
import com.app.radarvendor.Utils.GlobalMethods;
import com.app.radarvendor.Utils.NoConnectionDialog;
import com.app.radarvendor.databinding.ChooseServiceTimeSheetBinding;
import com.app.radarvendor.databinding.FragmentCreateAccountForDeliveryBinding;
import com.app.radarvendor.databinding.FragmentCreateNewAccountBinding;
import com.github.razir.progressbutton.ButtonTextAnimatorExtensionsKt;
import com.github.razir.progressbutton.DrawableButtonExtensionsKt;
import com.github.razir.progressbutton.ProgressButtonHolderKt;
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
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateAccountForDeliveryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateAccountForDeliveryFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FragmentCreateAccountForDeliveryBinding binding;
    private Activity activity;
    private ChooseServiceTimeSheetBinding chooseServiceDurationBinding;
    private BottomSheetDialog chooseServiceDurationDialog;
    private List<String> durationsList;
    private ChooseCityAdapter serviceDurationAdapter;
    private int city_id;
    private Uri image;
    private String phoneCode = "972";
    private MediaType mediaType = MediaType.parse("multipart/form-data");
    private List<DeliveryType> typesList;
    private int delivery_type;
    double lat ,lng;

    public CreateAccountForDeliveryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreateAccountForDeliveryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateAccountForDeliveryFragment newInstance(String param1, String param2) {
        CreateAccountForDeliveryFragment fragment = new CreateAccountForDeliveryFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCreateAccountForDeliveryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpFragment();
        setUpAction();
        getCity();
        getTypeDelivery();
    }

    private void setUpFragment() {
        binding.showPass1.setTag(true);
        binding.showPass2.setTag(true);
        lat = Hawk.get(Constant.lat);
        lng = Hawk.get(Constant.lng);
        binding.spPhone.selectItemByIndex(0);
        binding.spPhone.setLifecycleOwner(this);
        typesList = new ArrayList<>();
        binding.spPhone.setSpinnerOutsideTouchListener((view, motionEvent) -> {
            if (binding.spPhone.isShowing())
                binding.spPhone.dismiss();
        });

        binding.spCity.setLifecycleOwner(this);
        binding.spCity.setSpinnerOutsideTouchListener((view, motionEvent) -> {
            if (binding.spCity.isShowing())
                binding.spCity.dismiss();
        });

        binding.spType.setLifecycleOwner(this);
        binding.spType.setSpinnerOutsideTouchListener((view, motionEvent) -> {
            if (binding.spType.isShowing())
                binding.spType.dismiss();
        });
        ProgressButtonHolderKt.bindProgressButton(this, binding.btnSignUp);
        ButtonTextAnimatorExtensionsKt.attachTextChangeAnimator(binding.btnSignUp);
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

        binding.edIdNum.setOnFocusChangeListener((view, b) -> {
            if (b) {
                binding.edIdNum.setBackgroundResource(R.drawable.bg_button_bordered_app);
            } else {
                binding.edIdNum.setBackgroundResource(R.drawable.bg_button_secondry_shape);

            }
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

        binding.btnSignUp.setOnClickListener(view -> {
            if (GlobalMethods.setEditTextEmptyError(activity, binding.edName) &&
                    GlobalMethods.setEditTextEmptyError(activity, binding.edPhone) &&
                    GlobalMethods.setEditTextEmptyError(activity, binding.edIdNum) &&
                    GlobalMethods.setEditTextEmptyError(activity, binding.edPassword) &&
                    GlobalMethods.setEditTextEmptyError(activity, binding.edConfirmPass)
            )
                if (binding.cbRemember.isChecked()) {
                    if (delivery_type != 0) {

                        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(s -> {
                            register(binding.edName.getText().toString(),
                                    binding.edPhone.getText().toString(),
                                    binding.edPassword.getText().toString(),
                                    binding.edConfirmPass.getText().toString(),
                                    binding.edIdNum.getText().toString(),
                                    String.valueOf(city_id),
                                    String.valueOf(delivery_type),
                                    s,
                                    image
                            );
                        });
                    } else
                        GlobalMethods.showWarningToast(activity, "الرجاء اختيار النوع");


                } else
                    GlobalMethods.showWarningToast(activity, "يجب الموافقة على الشروط");


        });

        binding.tvLogin.setOnClickListener(view -> FragmentUtil.addFragmentWithBackStack(activity, new LoginFragment(), R.id.boarding, ""));
        binding.imgBack.setOnClickListener(view -> getParentFragmentManager().popBackStack());

    }

    private void showServicesDurationsSheet() {
        chooseServiceDurationBinding = ChooseServiceTimeSheetBinding.inflate(getLayoutInflater());
        chooseServiceDurationDialog = new BottomSheetDialog(activity, R.style.BottomSheetDialog);
        chooseServiceDurationDialog.setContentView(chooseServiceDurationBinding.getRoot());
        durationsList = new ArrayList<>();
        durationsList.add("غزة");
        durationsList.add("القدس");
        durationsList.add("خليل");
        durationsList.add("رفح");
        serviceDurationAdapter = new ChooseCityAdapter(activity, durationsList, position -> {

        });
        chooseServiceDurationBinding.rvPeriods.setAdapter(serviceDurationAdapter);
        chooseServiceDurationBinding.rvPeriods.setLayoutManager(new LinearLayoutManager(activity));
        chooseServiceDurationBinding.btnDone.setOnClickListener(view -> {
            chooseServiceDurationDialog.dismiss();
        });

        chooseServiceDurationBinding.btnClose.setOnClickListener(view -> {
            chooseServiceDurationDialog.dismiss();
        });
        chooseServiceDurationDialog.show();
    }

    private void getCity() {
        Call<ResultAPIModel<List<City>>> call = GlobalMethods.getApiInterface().getCity();
        call.enqueue(new Callback<ResultAPIModel<List<City>>>() {
            @Override
            public void onResponse(Call<ResultAPIModel<List<City>>> call, Response<ResultAPIModel<List<City>>> response) {
                ResultAPIModel<List<City>> result = response.body();
                if (response.isSuccessful()) {
                    assert result != null;
                    if (result.success == Constant.Status_successful) {
                        GlobalMethods.printGson("getCity", result.data);
                        Hawk.put(Constant.cities, result.data);
                        List<String> stringList = new ArrayList<>();
                        for (int i = 0; i < result.data.size(); i++) {
                            stringList.add(result.data.get(i).getName());
                        }
                        binding.spCity.setItems(stringList);
                        binding.spCity.setOnSpinnerItemSelectedListener((i, o, i1, t1) -> {
                            city_id = result.data.get(i1).getId();
                            Hawk.put(Constant.city_index, i1);
                        });
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
            public void onFailure(Call<ResultAPIModel<List<City>>> call, Throwable t) {
                t.printStackTrace();
                if (t instanceof UnknownHostException || t instanceof NoRouteToHostException) {
                    NoConnectionDialog.with(activity);
                }
            }
        });

    }

    private void getTypeDelivery() {
        Call<ResultAPIModel<List<DeliveryType>>> call = GlobalMethods.getApiInterface().getTypeDelivery();
        call.enqueue(new Callback<ResultAPIModel<List<DeliveryType>>>() {
            @Override
            public void onResponse(Call<ResultAPIModel<List<DeliveryType>>> call, Response<ResultAPIModel<List<DeliveryType>>> response) {
                ResultAPIModel<List<DeliveryType>> result = response.body();
                if (response.isSuccessful()) {
                    assert result != null;
                    if (result.success == Constant.Status_successful) {
                        List<String> list = new ArrayList<>();
                        typesList.addAll(result.data);
                        for (int i = 0; i < result.data.size(); i++) {
                            list.add(result.data.get(i).getName());
                        }
                        binding.spType.setItems(list);
                        binding.spType.setOnSpinnerItemSelectedListener((i, o, i1, t1) -> {
                            delivery_type = typesList.get(i1).getId();
                            Log.e("onResponse: ", delivery_type + "");
                        });
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
            public void onFailure(Call<ResultAPIModel<List<DeliveryType>>> call, Throwable t) {
                t.printStackTrace();
                if (t instanceof UnknownHostException || t instanceof NoRouteToHostException) {
                    NoConnectionDialog.with(activity);
                }
            }
        });

    }


    public void register(String name, String phone, String password,
                         String password_confirmation, String id_no, String city_id,
                         String type,
                         String fcm, Uri photo) {
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

//        Log.e("register: ", String.valueOf(lat) );

        RequestBody nameRequestBody = RequestBody.create(mediaType, name);
        RequestBody phoneRequestBody = RequestBody.create(mediaType, phoneCode + phone);
        RequestBody passwordRequestBody = RequestBody.create(mediaType, password);
        RequestBody passwordConfirmRequestBody = RequestBody.create(mediaType, password_confirmation);
        RequestBody idNoRequestBody = RequestBody.create(mediaType, id_no);
        RequestBody cityIdRequestBody = RequestBody.create(mediaType, city_id);
        RequestBody fcmRequestBody = RequestBody.create(mediaType, fcm);
        RequestBody typeRequestBody = RequestBody.create(mediaType, type);
        RequestBody latRequestBody = RequestBody.create(mediaType, String.valueOf(lat));
        RequestBody lngRequestBody = RequestBody.create(mediaType, String.valueOf(lng));

        Call<ResultAPIModel<Vendor>> call = GlobalMethods.getApiInterface().registerDelivery(
                nameRequestBody,
                phoneRequestBody, passwordRequestBody,
                passwordConfirmRequestBody,
                idNoRequestBody,
                cityIdRequestBody,
                typeRequestBody
                , fcmRequestBody,
                latRequestBody,
                lngRequestBody
                , body);

        GlobalMethods.showProgress(binding.btnSignUp);
        call.enqueue(new Callback<ResultAPIModel<Vendor>>() {
            @Override
            public void onResponse(Call<ResultAPIModel<Vendor>> call, Response<ResultAPIModel<Vendor>> response) {
                binding.btnSignUp.setEnabled(true);
                DrawableButtonExtensionsKt.hideProgress(binding.btnSignUp, R.string.sign_up);
                ResultAPIModel<Vendor> result = response.body();
                if (response.isSuccessful()) {
                    assert result != null;
                    if (result.success == Constant.Status_successful) {
                        GlobalMethods.showSuccessToast(activity, result.message);
                        GlobalMethods.printGson("register", result.data);
                        Hawk.put(Constant.delivery, result.data);
                        FragmentUtil.addFragmentWithBackStack(activity, VerifyCodeForDeliveryFragment.newInstance(phoneCode + phone, ""), R.id.boarding, "");
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


    private void goToHome() {
        if (GlobalMethods.getAccountType() == 1) {
            startActivity(new Intent(activity, MainActivity.class));
        } else if (GlobalMethods.getAccountType() == 2) {
            startActivity(new Intent(activity, MainServicesActivity.class));
        } else {
            startActivity(new Intent(activity, MainDeliveryActivity.class));
        }
        activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        activity.finish();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            activity = (Activity) context;
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


}