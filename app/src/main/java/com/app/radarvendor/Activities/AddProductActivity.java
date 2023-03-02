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
import com.app.radarvendor.Adapter.AddProductUniqueAdditionsAdapter;
import com.app.radarvendor.Adapter.ProductImagesAdapter2;
import com.app.radarvendor.Module.Additions;
import com.app.radarvendor.Module.Category;
import com.app.radarvendor.Module.ErrorModel;
import com.app.radarvendor.Module.ResultAPIModel;
import com.app.radarvendor.R;
import com.app.radarvendor.Utils.Constant;
import com.app.radarvendor.Utils.GlobalMethods;
import com.app.radarvendor.Utils.NoConnectionDialog;
import com.app.radarvendor.databinding.ActivityAddProductBinding;
import com.app.radarvendor.databinding.ActivityProductDetailsBinding;
import com.app.radarvendor.databinding.SuccessfulProccessSheetBinding;
import com.github.razir.progressbutton.ButtonTextAnimatorExtensionsKt;
import com.github.razir.progressbutton.DrawableButtonExtensionsKt;
import com.github.razir.progressbutton.ProgressButtonHolderKt;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;

import java.io.File;
import java.net.NoRouteToHostException;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddProductActivity extends AppCompatActivity {

    private ActivityAddProductBinding binding;
    private List<String> imagesList;
    private List<Uri> imagesListUri;
    private ProductImagesAdapter2 productImagesAdapter;
    private Context context = AddProductActivity.this;
    private List<Additions> additionsList;
    private List<Additions> additionsUniqueList;
    private AddProductAdditionsAdapter addProductAdditionsAdapter;
    private AddProductUniqueAdditionsAdapter addProductUniqueAdditionsAdapter;
    private SuccessfulProccessSheetBinding successfulPaymentSheetBinding;
    private BottomSheetDialog successfulPaymentSheetDialog;
    private MediaType mediaType = MediaType.parse("multipart/form-data");
    private String selectedCategory_id, category_id = null;
    private int index, uIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setUpActivity();
        setUpActions();
        getCategoriesByUser();
    }


    private void setUpActivity() {
        category_id = getIntent().getStringExtra("category_id");
        selectedCategory_id = category_id;
        binding.tool.tvMainTitle.setText(getString(R.string.new_product));
        additionsList = new ArrayList<>();
        additionsUniqueList = new ArrayList<>();
        additionsList.add(new Additions());
        addProductAdditionsAdapter = new AddProductAdditionsAdapter(context, additionsList, position -> {

        });
        binding.rvAdditions.setAdapter(addProductAdditionsAdapter);
        binding.rvAdditions.setLayoutManager(new LinearLayoutManager(context));

        addProductUniqueAdditionsAdapter = new AddProductUniqueAdditionsAdapter(context, additionsUniqueList, position -> {

        });
        binding.rvAdditionsUnique.setAdapter(addProductUniqueAdditionsAdapter);
        binding.rvAdditionsUnique.setLayoutManager(new LinearLayoutManager(context));

        imagesList = new ArrayList<>();
        imagesListUri = new ArrayList<>();
        productImagesAdapter = new ProductImagesAdapter2(context, imagesList, 0);
        binding.rvImages.setAdapter(productImagesAdapter);
        binding.rvImages.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

        ProgressButtonHolderKt.bindProgressButton(this, binding.btnAdd);
        ButtonTextAnimatorExtensionsKt.attachTextChangeAnimator(binding.btnAdd);
    }

    private void setUpActions() {
        binding.framePickImage.setOnClickListener(view -> PickImageDialog.build(new PickSetup())
                .setOnPickResult(r -> {
                    if (binding.rvImages.getVisibility() == View.GONE) {
                        binding.framePickImage.setVisibility(View.GONE);
                        binding.rvImages.setVisibility(View.VISIBLE);
                        binding.frameAddMore.setVisibility(View.VISIBLE);
                    }
                    imagesList.add(r.getUri().toString());
                    imagesListUri.add(r.getUri());
                    productImagesAdapter.notifyDataSetChanged();
                })
                .setOnPickCancel(() -> {
                }).show(getSupportFragmentManager()));

        binding.frameAddMore.setOnClickListener(view -> PickImageDialog.build(new PickSetup())
                .setOnPickResult(r -> {
                    imagesList.add(r.getUri().toString());
                    imagesListUri.add(r.getUri());
                    productImagesAdapter.notifyDataSetChanged();
                })
                .setOnPickCancel(() -> {
                }).show(getSupportFragmentManager()));

        binding.tvAddMoreAdditions.setOnClickListener(view -> {
            additionsList.add(new Additions());
            addProductAdditionsAdapter.notifyItemChanged(++index);
        });

        binding.tvAddMoreAdditionsUnique.setOnClickListener(view -> {
            additionsUniqueList.add(new Additions());
            addProductUniqueAdditionsAdapter.notifyItemChanged(++uIndex);
        });
        binding.tool.imgBack.setOnClickListener(view -> finish());

        binding.btnAdd.setOnClickListener(view -> {
            if (GlobalMethods.setEditTextEmptyError(context, binding.edName) &&
                    GlobalMethods.setEditTextEmptyError(context, binding.edDescp) &&
                    GlobalMethods.setEditTextEmptyError(context, binding.edPrice)

            ) {
                if (imagesList.size() != 0) {
                    if (selectedCategory_id != null) {

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
                        addProduct(binding.edName.getText().toString(),
                                binding.edDescp.getText().toString(), selectedCategory_id,
                                binding.edPrice.getText().toString(),
                                binding.edAmount.getText().toString(), imagesListUri,
                                additionsList, additionsUniqueList);
//                        GlobalMethods.printGson("addProduct", additionsList);
                    } else
                        GlobalMethods.showWarningToast(AddProductActivity.this, "الرجاء اضافة تصنيف لتتمكن من اضافة منتج");

                } else
                    GlobalMethods.showWarningToast(AddProductActivity.this, "الرجاء اضافة صور للمنتج");

            }
        });

        binding.spCategories.setLifecycleOwner(this);
        binding.spCategories.setSpinnerOutsideTouchListener((view, motionEvent) -> {
            if (binding.spCategories.isShowing())
                binding.spCategories.dismiss();
        });
    }

    private void showPaymentSuccessfulSheet() {
        successfulPaymentSheetBinding = SuccessfulProccessSheetBinding.inflate(getLayoutInflater());
        successfulPaymentSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialog);
        successfulPaymentSheetDialog.setContentView(successfulPaymentSheetBinding.getRoot());
        successfulPaymentSheetBinding.tvTitle.setText(getString(R.string.added_done));
        successfulPaymentSheetDialog.show();
    }

    public void addProduct(String name, String desc, String category_id, String price, String quantity,
                           List<Uri> photo, List<Additions> additions, List<Additions> additionsuniqe) {
        MultipartBody.Part[] body = null;
        if (photo != null) {
            File file = null;
            try {


                body = new MultipartBody.Part[photo.size()];

                for (int index = 0; index < photo.size(); index++) {
                    file = new File(getPath(context, photo.get(index)));

                    RequestBody surveyBody = RequestBody.create(MediaType.parse("image/*"), file);
                    body[index] = MultipartBody.Part.createFormData("Image[]", file.getName(),
                            surveyBody);
                }
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }

        Gson gson = new Gson();
        RequestBody additionsRequestBody;
        RequestBody hasDetailsRequestBody;

        RequestBody nameRequestBody = RequestBody.create(mediaType, name);
        RequestBody categoryRequestBody = RequestBody.create(mediaType, category_id);
        RequestBody priceRequestBody = RequestBody.create(mediaType, price);
        RequestBody offerPriceRequestBody = RequestBody.create(mediaType, price);
        RequestBody quantityRequestBody = RequestBody.create(mediaType, quantity);
        RequestBody descriptionRequestBody = RequestBody.create(mediaType, desc);
        if (binding.cbPizza.isChecked())
            hasDetailsRequestBody = RequestBody.create(mediaType, "1");
        else
            hasDetailsRequestBody = RequestBody.create(mediaType, "0");

        if (additions.size() == 1 && additions.get(0).getName() == null && additions.get(0).getPrice() == null)
            additionsRequestBody = RequestBody.create(mediaType, "null");
        else
            additionsRequestBody = RequestBody.create(mediaType, gson.toJson(additions));

        RequestBody additionsUniqueRequestBody = RequestBody.create(mediaType, gson.toJson(additionsuniqe));


        Log.e("addProduct:name ", name);
        Log.e("addProduct:category_id ", category_id);
        Log.e("addProduct:price ", price);
        Log.e("addProduct:quantity ", quantity);
        Log.e("addProduct:additions ", gson.toJson(additions));
        Log.e("addProduct:Uniqyeadditions ", gson.toJson(additionsuniqe));

        Call<ResultAPIModel> call = GlobalMethods.getApiInterface().addProduct(GlobalMethods.getHeaders(),
                nameRequestBody, categoryRequestBody,
                priceRequestBody, offerPriceRequestBody, quantityRequestBody, descriptionRequestBody,
                hasDetailsRequestBody, additionsRequestBody, additionsUniqueRequestBody, body);

        GlobalMethods.showProgress(binding.btnAdd);
        call.enqueue(new Callback<ResultAPIModel>() {
            @Override
            public void onResponse(Call<ResultAPIModel> call, Response<ResultAPIModel> response) {
                binding.btnAdd.setEnabled(true);
                DrawableButtonExtensionsKt.hideProgress(binding.btnAdd, R.string.add);
                ResultAPIModel result = response.body();
                if (response.isSuccessful()) {
                    assert result != null;
                    if (result.success == Constant.Status_successful) {
                        GlobalMethods.showSuccessToast(AddProductActivity.this, result.message);
                        finish();
                    } else {
                        GlobalMethods.showErrorToast(AddProductActivity.this, result.message);
                    }


                } else {

                    ErrorModel errorModel = null;
                    try {
                        assert response.errorBody() != null;
                        String error = response.errorBody().string();
                        Log.d("onResponse: ", error);
                        errorModel = new Gson().fromJson(error, new TypeToken<ErrorModel>() {
                        }.getType());
                        GlobalMethods.showErrorToast(AddProductActivity.this, gson.toJson(errorModel.message));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResultAPIModel> call, Throwable t) {
                t.printStackTrace();
                if (t instanceof UnknownHostException || t instanceof NoRouteToHostException) {
                    NoConnectionDialog.with(AddProductActivity.this);
                }
            }
        });

    }


    private void getCategoriesByUser() {
        Call<ResultAPIModel<List<Category>>> call = GlobalMethods.getApiInterface().getCategoriesByUser(GlobalMethods.getHeaders());
        call.enqueue(new Callback<ResultAPIModel<List<Category>>>() {
            @Override
            public void onResponse(Call<ResultAPIModel<List<Category>>> call, Response<ResultAPIModel<List<Category>>> response) {
                ResultAPIModel<List<Category>> result = response.body();
                if (response.isSuccessful()) {
                    assert result != null;
                    if (result.success == Constant.Status_successful) {
                        GlobalMethods.printGson("getCategoriesByUser", result.data);
                        List<String> categoriesNames = new ArrayList<>();
                        int index = 0;
                        for (int i = 0; i < result.data.size(); i++) {
                            categoriesNames.add(result.data.get(i).getName());
                            if (category_id != null && category_id.equals(result.data.get(i).getId()))
                                index = i;
                        }

                        binding.spCategories.setItems(categoriesNames);
                        binding.spCategories.selectItemByIndex(index);
                        binding.spCategories.setOnSpinnerItemSelectedListener((i, o, i1, t1) -> selectedCategory_id = result.data.get(i1).getId());
                    } else {
                        GlobalMethods.showErrorToast(AddProductActivity.this, result.message);

                    }

                } else {
                    ErrorModel errorModel = null;
                    try {
                        assert response.errorBody() != null;
                        String error = response.errorBody().string();
                        Log.d("onResponse: ", error);
                        errorModel = new Gson().fromJson(error, new TypeToken<ErrorModel>() {
                        }.getType());
                        GlobalMethods.showErrorToast(AddProductActivity.this, errorModel.message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResultAPIModel<List<Category>>> call, Throwable t) {
                t.printStackTrace();
                if (t instanceof UnknownHostException || t instanceof NoRouteToHostException) {
                    NoConnectionDialog.with(AddProductActivity.this);
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