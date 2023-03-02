package com.app.radarvendor.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import com.app.radarvendor.Adapter.AddProductAdditionsAdapter;
import com.app.radarvendor.Adapter.ServiceDurationAdapter;
import com.app.radarvendor.Module.Additions;
import com.app.radarvendor.Module.ErrorModel;
import com.app.radarvendor.Module.ResultAPIModel;
import com.app.radarvendor.Module.Service;
import com.app.radarvendor.Module.Vendor;
import com.app.radarvendor.R;
import com.app.radarvendor.Utils.Constant;
import com.app.radarvendor.Utils.GlobalMethods;
import com.app.radarvendor.Utils.NoConnectionDialog;
import com.app.radarvendor.databinding.ActivityAddServicesBinding;
import com.app.radarvendor.databinding.ActivityEditProductBinding;
import com.app.radarvendor.databinding.ActivityEditServiceBinding;
import com.app.radarvendor.databinding.ChooseServiceTimeSheetBinding;
import com.bumptech.glide.Glide;
import com.github.razir.progressbutton.DrawableButtonExtensionsKt;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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

public class EditServiceActivity extends AppCompatActivity {

    private Context context = EditServiceActivity.this;
    private List<Additions> additionsList;
    private AddProductAdditionsAdapter addProductAdditionsAdapter;
    private MediaType mediaType = MediaType.parse("multipart/form-data");
    private Service product;
    private ActivityEditServiceBinding binding;
    private Uri image;
    private int serviceTime = -1;
    private ChooseServiceTimeSheetBinding chooseServiceDurationBinding;
    private BottomSheetDialog chooseServiceDurationDialog;
    private List<String> durationsList;
    private ServiceDurationAdapter serviceDurationAdapter;
    private Gson gson;
    private int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditServiceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setUpActivity();
        setUpActions();
        getServiceData();
    }

    private void setUpActivity() {
        gson = new Gson();
        product = (Service) getIntent().getSerializableExtra("service");
        binding.tool.tvMainTitle.setText(getString(R.string.edit_service));
        additionsList = new ArrayList<>();
        additionsList.add(new Additions());
        addProductAdditionsAdapter = new AddProductAdditionsAdapter(context, additionsList, position -> {

        });
        binding.rvAdditions.setAdapter(addProductAdditionsAdapter);
        binding.rvAdditions.setLayoutManager(new LinearLayoutManager(context));
    }

    private void setUpActions() {
        binding.framePickImage.setOnClickListener(view -> PickImageDialog.build(new PickSetup())
                .setOnPickResult(r -> {
                    image = r.getUri();
                    binding.framePickImage.setVisibility(View.GONE);
                    binding.card.setVisibility(View.VISIBLE);
                    binding.img.setImageURI(r.getUri());
                })
                .setOnPickCancel(() -> {
                }).show(getSupportFragmentManager()));


        binding.tvAddMoreAdditions.setOnClickListener(view -> {
            additionsList.add(new Additions());
            addProductAdditionsAdapter.notifyItemChanged(++index);
        });
        binding.tool.imgBack.setOnClickListener(view -> finish());

        binding.btnAdd.setOnClickListener(view -> {
            if (GlobalMethods.setEditTextEmptyError(context, binding.edName) &&
                    GlobalMethods.setEditTextEmptyError(context, binding.edDesc) &&
                    GlobalMethods.setEditTextEmptyError(context, binding.edPrice)
            ) {
//                if (image != null) {
                    if (serviceTime != -1) {
                        if (addProductAdditionsAdapter.getAdditions().size() != 0) {
                            if (addProductAdditionsAdapter.getAdditions().
                                    get(addProductAdditionsAdapter.getAdditions().size() - 1).getPrice() == null &&
                                    addProductAdditionsAdapter.getAdditions().
                                            get(addProductAdditionsAdapter.getAdditions().size() - 1).getName() == null
                            ) {
                                List<Additions> additionsList = addProductAdditionsAdapter.getAdditions();
                                additionsList.remove(additionsList.size() - 1);
                            }
                        }
                        updateService(binding.edName.getText().toString(),
                                String.valueOf(serviceTime),
                                binding.edPrice.getText().toString(),
                                binding.edDesc.getText().toString(),
                                addProductAdditionsAdapter.getAdditions(), image);
                    } else
                        GlobalMethods.showWarningToast(EditServiceActivity.this, "الرجاء اختيار مدة الخدمة");


//                } else
//                    GlobalMethods.showWarningToast(EditServiceActivity.this, "الرجاء اضافة صور للخدمة");

            }
        });
        binding.edDurations.setOnClickListener(view -> showServicesDurationsSheet());
        binding.imgRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.img.setImageDrawable(null);
                binding.card.setVisibility(View.GONE);
                binding.framePickImage.setVisibility(View.VISIBLE);
            }
        });
    }

    private void getServiceData() {
        binding.edName.setText(product.getName());
        binding.edDesc.setText(product.getDescription());
        binding.edPrice.setText(String.valueOf(product.getPrice()));
        if (product.getImage_url() != null) {
            Glide.with(context).load(product.getImage_url()).into(binding.img);
            binding.framePickImage.setVisibility(View.GONE);
            binding.card.setVisibility(View.VISIBLE);
        }
        if (product.getAdditions() != null) {
            additionsList.clear();
            additionsList.addAll(product.getAdditions());
            index = additionsList.size() - 1;
            addProductAdditionsAdapter.notifyDataSetChanged();
        }

    }

    private void showServicesDurationsSheet() {
        chooseServiceDurationBinding = ChooseServiceTimeSheetBinding.inflate(getLayoutInflater());
        chooseServiceDurationDialog = new BottomSheetDialog(context, R.style.BottomSheetDialog);
        chooseServiceDurationDialog.setContentView(chooseServiceDurationBinding.getRoot());
        durationsList = new ArrayList<>();
        durationsList.add("10 - 15");
        durationsList.add("15 - 30");
        durationsList.add("30 - 45");
        durationsList.add("45 - 60");
        serviceDurationAdapter = new ServiceDurationAdapter(context, durationsList, position -> {
            serviceTime = position + 1;
        });
        chooseServiceDurationBinding.rvPeriods.setAdapter(serviceDurationAdapter);
        chooseServiceDurationBinding.rvPeriods.setLayoutManager(new LinearLayoutManager(context));
        chooseServiceDurationBinding.btnDone.setOnClickListener(view -> {
            if (serviceTime == -1) {
                serviceTime = 1;
                binding.edDurations.setText(durationsList.get(0));
            } else
                binding.edDurations.setText(durationsList.get(serviceTime - 1));
            chooseServiceDurationDialog.dismiss();
        });

        chooseServiceDurationBinding.btnClose.setOnClickListener(view -> {
            chooseServiceDurationDialog.dismiss();
        });
        chooseServiceDurationDialog.show();
    }

    public void updateService(String name, String service_time, String price, String description,
                              List<Additions> additions, Uri photo) {
        MultipartBody.Part body = null;
        if (photo != null) {
            File file = null;
            try {
                file = new File(getPath(context, photo));
                RequestBody requestFile = RequestBody.create(mediaType, file);
                body = MultipartBody.Part.createFormData("Image", URLEncoder.encode(file.getName(), "utf-8"), requestFile);

            } catch (URISyntaxException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }


        RequestBody serviceIdRequestBody = RequestBody.create(mediaType, String.valueOf(product.getId()));
        RequestBody nameRequestBody = RequestBody.create(mediaType, name);
        RequestBody serviceTimeRequestBody = RequestBody.create(mediaType, service_time);
        RequestBody priceRequestBody = RequestBody.create(mediaType, price);
        RequestBody offerPriceRequestBody = RequestBody.create(mediaType, price);
        RequestBody descriptionRequestBody = RequestBody.create(mediaType, description);
        RequestBody additionsRequestBody = RequestBody.create(mediaType, gson.toJson(additions));


        Call<ResultAPIModel> call = GlobalMethods.getApiInterface().updateService(GlobalMethods.getHeaders(),
                serviceIdRequestBody, nameRequestBody, serviceTimeRequestBody,
                priceRequestBody, offerPriceRequestBody, descriptionRequestBody,
                additionsRequestBody, body);

        GlobalMethods.showProgress(binding.btnAdd);
        call.enqueue(new Callback<ResultAPIModel>() {
            @Override
            public void onResponse(Call<ResultAPIModel> call, Response<ResultAPIModel> response) {
                binding.btnAdd.setEnabled(true);
                DrawableButtonExtensionsKt.hideProgress(binding.btnAdd, R.string.publish);
                ResultAPIModel<Vendor> result = response.body();
                if (response.isSuccessful()) {
                    assert result != null;
                    if (result.success == Constant.Status_successful) {
                        GlobalMethods.showSuccessToast(EditServiceActivity.this, result.message);
                        finish();
                    } else {
                        GlobalMethods.showErrorToast(EditServiceActivity.this, result.message);
                    }


                } else {

                    ErrorModel errorModel = null;
                    try {
                        assert response.errorBody() != null;
                        String error = response.errorBody().string();
                        Log.d("onResponse: ", error);
                        errorModel = new Gson().fromJson(error, new TypeToken<ErrorModel>() {
                        }.getType());
                        GlobalMethods.showErrorToast(EditServiceActivity.this, errorModel.message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResultAPIModel> call, Throwable t) {
                t.printStackTrace();
                if (t instanceof UnknownHostException || t instanceof NoRouteToHostException) {
                    NoConnectionDialog.with(EditServiceActivity.this);
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