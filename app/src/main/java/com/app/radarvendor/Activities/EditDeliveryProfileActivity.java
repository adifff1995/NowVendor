package com.app.radarvendor.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.View;

import com.app.radarvendor.Module.City;
import com.app.radarvendor.Module.DeliveryType;
import com.app.radarvendor.Module.ErrorModel;
import com.app.radarvendor.Module.ResultAPIModel;
import com.app.radarvendor.Module.Vendor;
import com.app.radarvendor.R;
import com.app.radarvendor.Utils.Constant;
import com.app.radarvendor.Utils.GlobalMethods;
import com.app.radarvendor.Utils.NoConnectionDialog;
import com.app.radarvendor.databinding.ActivityEditDeliveryProfileBinding;
import com.bumptech.glide.Glide;
import com.github.razir.progressbutton.ButtonTextAnimatorExtensionsKt;
import com.github.razir.progressbutton.DrawableButtonExtensionsKt;
import com.github.razir.progressbutton.ProgressButtonHolderKt;
import com.google.firebase.messaging.FirebaseMessaging;
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
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditDeliveryProfileActivity extends AppCompatActivity {

    private ActivityEditDeliveryProfileBinding binding;
    private Vendor customer;
    private Uri image;
    private MediaType mediaType = MediaType.parse("multipart/form-data");
    private Context context = EditDeliveryProfileActivity.this;
    private int city_id, delivery_type;
    private List<DeliveryType> typesList;
    private List<City> citiesList;
    double lat, lng = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditDeliveryProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setUpActivity();
        setUpAction();
        getCity();
        getTypeDelivery();
        setUpProfile();

    }

    private void setUpAction() {
        binding.edName.setOnFocusChangeListener((view, b) -> {
            if (b) {
                binding.edName.setBackgroundResource(R.drawable.bg_button_bordered_app);
            } else {
                binding.edName.setBackgroundResource(R.drawable.bg_button_secondry_shape);

            }
        });

        binding.edMobile.setOnFocusChangeListener((view, b) -> {
            if (b) {
                binding.edMobile.setBackgroundResource(R.drawable.bg_button_bordered_app);
            } else {
                binding.edMobile.setBackgroundResource(R.drawable.bg_button_secondry_shape);

            }
        });

        binding.edIdNum.setOnFocusChangeListener((view, b) -> {
            if (b) {
                binding.edIdNum.setBackgroundResource(R.drawable.bg_button_bordered_app);
            } else {
                binding.edIdNum.setBackgroundResource(R.drawable.bg_button_secondry_shape);

            }
        });

        binding.edPassword.setOnFocusChangeListener((view, b) -> {
            if (b) {
                binding.edPassword.setBackgroundResource(R.drawable.bg_button_bordered_app);
            } else {
                binding.edPassword.setBackgroundResource(R.drawable.bg_button_secondry_shape);

            }
        });

        binding.tool.imgBack.setOnClickListener(view -> {
            if (binding.passwordSection.getVisibility() == View.VISIBLE) {
                binding.passwordSection.setVisibility(View.GONE);
                binding.linearMain.setVisibility(View.VISIBLE);
            } else
                finish();
        });
        binding.img.setOnClickListener(view ->
                PickImageDialog.build(new PickSetup())
                        .setOnPickResult(r -> {
                            image = r.getUri();
                            binding.img.setImageURI(r.getUri());
                        })
                        .setOnPickCancel(() -> {
                        }).show(getSupportFragmentManager()));

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

        binding.btnEdit.setOnClickListener(view -> {
            if (GlobalMethods.setEditTextEmptyError(context, binding.edName) &&
                    GlobalMethods.setEditTextEmptyError(context, binding.edMobile)
            ) {
                FirebaseMessaging.getInstance().getToken().addOnSuccessListener(s -> {
                    updateProfile(binding.edName.getText().toString(),
                            binding.edMobile.getText().toString(),
                            binding.edIdNum.getText().toString(),
                            String.valueOf(city_id),
                            s, String.valueOf(delivery_type),
                            image
                    );
                });

            }
        });

        binding.tvChangePassword.setOnClickListener(view -> {
            binding.linearMain.setVisibility(View.GONE);
            binding.passwordSection.setVisibility(View.VISIBLE);
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
    }

    private void setUpProfile() {
        binding.edName.setText(customer.getName());
        binding.edMobile.setText(customer.getPhone());
        binding.edIdNum.setText(customer.getId_no());

        if (customer.getIcon_url() != null) {
            CircularProgressDrawable circularProgressDrawable = GlobalMethods.getPlaceHolder(context);
            circularProgressDrawable.start();
            Glide.with(context).load(customer.getIcon_url()).placeholder(circularProgressDrawable).into(binding.img);
        }


        binding.spCities.setLifecycleOwner(this);
        binding.spCities.setSpinnerOutsideTouchListener((view, motionEvent) -> {
            if (binding.spCities.isShowing())
                binding.spCities.dismiss();
        });

        binding.spType.setLifecycleOwner(this);
        binding.spType.setSpinnerOutsideTouchListener((view, motionEvent) -> {
            if (binding.spType.isShowing())
                binding.spType.dismiss();
        });


    }


    private void setUpActivity() {
        binding.showPass1.setTag(true);
        binding.showPass2.setTag(true);
        binding.showPass3.setTag(true);
        lat = Hawk.get(Constant.lat);
        lng = Hawk.get(Constant.lng);
        binding.tool.imgSearch.setVisibility(View.GONE);
        binding.tool.tvMainTitle.setVisibility(View.VISIBLE);
        binding.tool.imgBack.setVisibility(View.VISIBLE);
        binding.tool.tvMainTitle.setText(getString(R.string.edit_profile));
        typesList = new ArrayList<>();
        customer = (Vendor) getIntent().getSerializableExtra("profile");
        ProgressButtonHolderKt.bindProgressButton(this, binding.btnEdit);
        ButtonTextAnimatorExtensionsKt.attachTextChangeAnimator(binding.btnEdit);

        ProgressButtonHolderKt.bindProgressButton(this, binding.btnChange);
        ButtonTextAnimatorExtensionsKt.attachTextChangeAnimator(binding.btnChange);
    }

    public void updateProfile(String name, String phone, String id_num, String city_id,
                              String fcm, String type,
                              Uri photo) {
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

        RequestBody nameRequestBody = RequestBody.create(mediaType, name);
        RequestBody phoneRequestBody = RequestBody.create(mediaType, phone);
        RequestBody idNumRequestBody = RequestBody.create(mediaType, id_num);
        RequestBody cityIdRequestBody = RequestBody.create(mediaType, city_id);
        RequestBody typeRequestBody = RequestBody.create(mediaType, type);
        RequestBody latRequestBody = RequestBody.create(mediaType, String.valueOf(lat));
        RequestBody lngIdRequestBody = RequestBody.create(mediaType, String.valueOf(lng));
        RequestBody fcmRequestBody = RequestBody.create(mediaType, fcm);


        Call<ResultAPIModel<Vendor>> call = GlobalMethods.getApiInterface().updateDeliveryProfile(
                GlobalMethods.getHeaders(),
                nameRequestBody,
                phoneRequestBody,
                idNumRequestBody,
                cityIdRequestBody,
                typeRequestBody,
                lngIdRequestBody,
                latRequestBody,
                fcmRequestBody
                , body);

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
                        GlobalMethods.showSuccessToast(EditDeliveryProfileActivity.this, result.message);
                        finish();

                    } else {
                        GlobalMethods.showErrorToast(EditDeliveryProfileActivity.this, result.message);
                    }


                } else {

                    ErrorModel errorModel = null;
                    try {
                        assert response.errorBody() != null;
                        String error = response.errorBody().string();
                        Log.d("onResponse: ", error);
                        errorModel = new Gson().fromJson(error, new TypeToken<ErrorModel>() {
                        }.getType());
                        GlobalMethods.showErrorToast(EditDeliveryProfileActivity.this, errorModel.message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResultAPIModel<Vendor>> call, Throwable t) {
                t.printStackTrace();
                if (t instanceof UnknownHostException || t instanceof NoRouteToHostException) {
                    NoConnectionDialog.with(EditDeliveryProfileActivity.this);
                }
            }
        });

    }

    private void changePassword(String old_pass, String new_pass, String new_pass_confirm) {
        Map<String, Object> params = GlobalMethods.getParams(context);
        params.put("old_password", old_pass);
        params.put("new_password", new_pass);
        params.put("new_password_confirmation", new_pass_confirm);

        Call<ResultAPIModel> call = GlobalMethods.getApiInterface().changePasswordD(GlobalMethods.getHeaders(), params);
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
                        GlobalMethods.showSuccessToast(EditDeliveryProfileActivity.this, result.message);
                        binding.passwordSection.setVisibility(View.GONE);
                        binding.linearMain.setVisibility(View.VISIBLE);

                    } else {
                        GlobalMethods.showErrorToast(EditDeliveryProfileActivity.this, result.message);

                    }

                } else {
                    ErrorModel errorModel = null;
                    try {
                        assert response.errorBody() != null;
                        String error = response.errorBody().string();
                        Log.d("onResponse: ", error);
                        errorModel = new Gson().fromJson(error, new TypeToken<ErrorModel>() {
                        }.getType());
                        GlobalMethods.showErrorToast(EditDeliveryProfileActivity.this, errorModel.message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResultAPIModel> call, Throwable t) {
                t.printStackTrace();
                if (t instanceof UnknownHostException || t instanceof NoRouteToHostException) {
                    NoConnectionDialog.with(EditDeliveryProfileActivity.this);
                }
            }
        });

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
                        citiesList = result.data;
                        List<String> stringList = new ArrayList<>();
                        for (int i = 0; i < result.data.size(); i++) {
                            stringList.add(result.data.get(i).getName());
                        }
                        binding.spCities.setItems(stringList);
                        binding.spCities.setOnSpinnerItemSelectedListener((i, o, i1, t1) -> {
                            city_id = result.data.get(i1).getId();
                        });

                        for (int i = 0; i < citiesList.size(); i++) {
                            if (customer.getCity_id() == citiesList.get(i).getId()) {
                                binding.spCities.selectItemByIndex(i);
                                city_id = customer.getCity_id();
                            }
                        }
                    } else {
                        Log.d("onResponse: ", result.message);
                        GlobalMethods.showErrorToast(EditDeliveryProfileActivity.this, result.message);
                    }

                } else {
                    ErrorModel errorModel = null;
                    try {
                        assert response.errorBody() != null;
                        String error = response.errorBody().string();
                        Log.d("onResponse: ", error);
                        errorModel = new Gson().fromJson(error, new TypeToken<ErrorModel>() {
                        }.getType());
                        GlobalMethods.showErrorToast(EditDeliveryProfileActivity.this, errorModel.message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResultAPIModel<List<City>>> call, Throwable t) {
                t.printStackTrace();
                if (t instanceof UnknownHostException || t instanceof NoRouteToHostException) {
                    NoConnectionDialog.with(EditDeliveryProfileActivity.this);
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
                        GlobalMethods.printGson("getTypeDelivery",typesList);
                        Log.e("getTypeDelivery", customer.getType()+"");
                        for (int i = 0; i < result.data.size(); i++) {
                            list.add(result.data.get(i).getName());
                        }
                        binding.spType.setItems(list);
                        binding.spType.setOnSpinnerItemSelectedListener((i, o, i1, t1) -> {
                            delivery_type = typesList.get(i1).getId();
                            Log.e("onResponse: ", delivery_type + "");
                        });


                        for (int i = 0; i < typesList.size(); i++) {
                            if (customer.getType() == typesList.get(i).getId()) {
                                binding.spType.selectItemByIndex(i);
                                delivery_type = customer.getType();
                            }
                        }
                    } else {
                        GlobalMethods.showErrorToast(EditDeliveryProfileActivity.this, result.message);

                    }

                } else {
                    ErrorModel errorModel = null;
                    try {
                        assert response.errorBody() != null;
                        String error = response.errorBody().string();
                        Log.d("onResponse: ", error);
                        errorModel = new Gson().fromJson(error, new TypeToken<ErrorModel>() {
                        }.getType());
                        GlobalMethods.showErrorToast(EditDeliveryProfileActivity.this, errorModel.message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResultAPIModel<List<DeliveryType>>> call, Throwable t) {
                t.printStackTrace();
                if (t instanceof UnknownHostException || t instanceof NoRouteToHostException) {
                    NoConnectionDialog.with(EditDeliveryProfileActivity.this);
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
}