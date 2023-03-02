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
import android.view.WindowManager;

import com.app.radarvendor.Adapter.AddProductAdditionsAdapter;
import com.app.radarvendor.Adapter.AddProductUniqueAdditionsAdapter;
import com.app.radarvendor.Adapter.ProductImagesAdapter;
import com.app.radarvendor.Module.Additions;
import com.app.radarvendor.Module.Category;
import com.app.radarvendor.Module.ErrorModel;
import com.app.radarvendor.Module.Product;
import com.app.radarvendor.Module.ProductImages;
import com.app.radarvendor.Module.ResultAPIModel;
import com.app.radarvendor.R;
import com.app.radarvendor.Utils.Constant;
import com.app.radarvendor.Utils.GlobalMethods;
import com.app.radarvendor.Utils.NoConnectionDialog;
import com.app.radarvendor.databinding.ActivityAddProductBinding;
import com.app.radarvendor.databinding.ActivityEditProductBinding;
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

public class EditProductActivity extends AppCompatActivity {
    private ActivityEditProductBinding binding;
    private List<ProductImages> imagesList;
    private ProductImagesAdapter productImagesAdapter;
    private Context context = EditProductActivity.this;
    private List<Additions> additionsList;
    private List<Additions> additionsUniqueList;
    private AddProductUniqueAdditionsAdapter addProductUniqueAdditionsAdapter;
    private AddProductAdditionsAdapter addProductAdditionsAdapter;
    private SuccessfulProccessSheetBinding successfulPaymentSheetBinding;
    private BottomSheetDialog successfulPaymentSheetDialog;
    private MediaType mediaType = MediaType.parse("multipart/form-data");
    private String category_id;
    private Product product;
    private int index, uIndex = 0;
    Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setUpActivity();
        setUpActions();
        getCategoriesByUser();
    }


    private void setUpActivity() {
        gson = new Gson();
        product = (Product) getIntent().getSerializableExtra("product");
        binding.tool.tvMainTitle.setText(getString(R.string.edit_product));
        additionsList = new ArrayList<>();
        additionsUniqueList = new ArrayList<>();
        addProductAdditionsAdapter = new AddProductAdditionsAdapter(context, additionsList, position -> {

        });
        binding.rvAdditions.setAdapter(addProductAdditionsAdapter);
        binding.rvAdditions.setLayoutManager(new LinearLayoutManager(context));

        addProductUniqueAdditionsAdapter = new AddProductUniqueAdditionsAdapter(context, additionsUniqueList, position -> {

        });
        binding.rvAdditionsUnique.setAdapter(addProductUniqueAdditionsAdapter);
        binding.rvAdditionsUnique.setLayoutManager(new LinearLayoutManager(context));

        imagesList = new ArrayList<>();
        productImagesAdapter = new ProductImagesAdapter(context, imagesList, 1, new ProductImagesAdapter.OnItemSelect() {
            @Override
            public void onDeleteImage(int position) {
                deleteImageProductById(String.valueOf(imagesList.get(position).getId()), position);
            }
        });
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

                    uploadImageProduct(String.valueOf(product.getId()), r.getUri());
                })
                .setOnPickCancel(() -> {
                }).show(getSupportFragmentManager()));

        binding.frameAddMore.setOnClickListener(view -> PickImageDialog.build(new PickSetup())
                .setOnPickResult(r -> {
                    uploadImageProduct(String.valueOf(product.getId()), r.getUri());
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
            GlobalMethods.printGson("update_product", addProductAdditionsAdapter.getAdditions());
            if (GlobalMethods.setEditTextEmptyError(context, binding.edName) &&
                    GlobalMethods.setEditTextEmptyError(context, binding.edDescp) &&
                    GlobalMethods.setEditTextEmptyError(context, binding.edPrice) &&
                    GlobalMethods.setEditTextEmptyError(context, binding.edAmount)
            ) {
                if (imagesList.size() != 0) {
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
                    updateProduct(binding.edName.getText().toString(),
                            binding.edDescp.getText().toString(), category_id,
                            binding.edPrice.getText().toString(),
                            binding.edAmount.getText().toString(),
                            addProductAdditionsAdapter.getAdditions(),
                            addProductUniqueAdditionsAdapter.getAdditions()
                    );
                    GlobalMethods.printGson("printGson", addProductAdditionsAdapter.getAdditions());
                } else
                    GlobalMethods.showWarningToast(EditProductActivity.this, "الرجاء اضافة صور للمنتج");

            }
        });
    }

    private void getProductsData() {
        binding.edName.setText(product.getName());
        binding.edDescp.setText(product.getDescription());
        binding.edAmount.setText(String.valueOf(product.getQuantity()));
        binding.edPrice.setText(String.valueOf(product.getPrice()));
        binding.cbPizza.setChecked(product.isHas_details());
        binding.spCategories.selectItemByIndex(0);
        imagesList.addAll(product.getImages());
        productImagesAdapter.notifyDataSetChanged();
        binding.framePickImage.setVisibility(View.GONE);
        binding.rvImages.setVisibility(View.VISIBLE);
        binding.frameAddMore.setVisibility(View.VISIBLE);

        if (product.getAdditions() != null) {
            additionsList.clear();
            additionsList.addAll(product.getAdditions());
            index = additionsList.size() - 1;
            addProductAdditionsAdapter.notifyDataSetChanged();
        }

        if (product.getUnique_additions() != null) {
            additionsUniqueList.clear();
            additionsUniqueList.addAll(product.getUnique_additions());
            uIndex = additionsUniqueList.size() - 1;
            addProductUniqueAdditionsAdapter.notifyDataSetChanged();
        }

    }

    private void showPaymentSuccessfulSheet() {
        successfulPaymentSheetBinding = SuccessfulProccessSheetBinding.inflate(getLayoutInflater());
        successfulPaymentSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialog);
        successfulPaymentSheetDialog.setContentView(successfulPaymentSheetBinding.getRoot());
        successfulPaymentSheetBinding.tvTitle.setText(getString(R.string.added_done));
        successfulPaymentSheetDialog.show();
    }

//    public void updateProduct(String name, String desc, String category_id, String price, String quantity,
//                              List<Uri> photo, List<Additions> additions) {
//        MultipartBody.Part[] body = null;
//        if (photo != null) {
//            File file = null;
//            try {
//
//
//                body = new MultipartBody.Part[photo.size()];
//
//                for (int index = 0; index < photo.size(); index++) {
//                    file = new File(getPath(context, photo.get(index)));
//
//                    RequestBody surveyBody = RequestBody.create(MediaType.parse("image/*"), file);
//                    body[index] = MultipartBody.Part.createFormData("Image[]", file.getName(),
//                            surveyBody);
//                }
//            } catch (URISyntaxException e) {
//                e.printStackTrace();
//            }
//        }
//
//        Gson gson = new Gson();
//
//        RequestBody productIdRequestBody = RequestBody.create(mediaType, String.valueOf(product.getId()));
//        RequestBody nameRequestBody = RequestBody.create(mediaType, name);
//        RequestBody categoryRequestBody = RequestBody.create(mediaType, category_id);
//        RequestBody priceRequestBody = RequestBody.create(mediaType, price);
//        RequestBody offerPriceRequestBody = RequestBody.create(mediaType, price);
//        RequestBody quantityRequestBody = RequestBody.create(mediaType, quantity);
//        RequestBody descriptionRequestBody = RequestBody.create(mediaType, desc);
//        RequestBody additionsRequestBody = RequestBody.create(mediaType, gson.toJson(additions));
//
//        Log.e("addProduct:name ", name);
//        Log.e("addProduct:category_id ", category_id);
//        Log.e("addProduct:price ", price);
//        Log.e("addProduct:quantity ", quantity);
//        Log.e("addProduct:additions ", gson.toJson(additions));
//
//        Call<ResultAPIModel> call = GlobalMethods.getApiInterface().
//                updateProduct(GlobalMethods.getHeaders()
//                        , productIdRequestBody, nameRequestBody, categoryRequestBody,
//                        priceRequestBody, offerPriceRequestBody, quantityRequestBody, descriptionRequestBody,
//                        additionsRequestBody, body);
//
//        GlobalMethods.showProgress(binding.btnAdd);
//        call.enqueue(new Callback<ResultAPIModel>() {
//            @Override
//            public void onResponse(Call<ResultAPIModel> call, Response<ResultAPIModel> response) {
//                binding.btnAdd.setEnabled(true);
//                DrawableButtonExtensionsKt.hideProgress(binding.btnAdd, R.string.add);
//                ResultAPIModel result = response.body();
//                if (response.isSuccessful()) {
//                    assert result != null;
//                    if (result.success == Constant.Status_successful) {
//                        GlobalMethods.showSuccessToast(EditProductActivity.this, result.message);
//                        finish();
//                    } else {
//                        GlobalMethods.showErrorToast(EditProductActivity.this, result.message);
//                    }
//
//
//                } else {
//
//                    ErrorModel errorModel = null;
//                    try {
//                        assert response.errorBody() != null;
//                        String error = response.errorBody().string();
//                        Log.d("onResponse: ", error);
//                        errorModel = new Gson().fromJson(error, new TypeToken<ErrorModel>() {
//                        }.getType());
//                        GlobalMethods.showErrorToast(EditProductActivity.this, gson.toJson(errorModel.message));
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResultAPIModel> call, Throwable t) {
//                t.printStackTrace();
//                if (t instanceof UnknownHostException || t instanceof NoRouteToHostException) {
//                    NoConnectionDialog.with(EditProductActivity.this);
//                }
//            }
//        });
//
//    }

    private void updateProduct(String name, String desc, String category_id, String price, String quantity,
                               List<Additions> additions, List<Additions> uadditions) {
        Map<String, Object> params = GlobalMethods.getParams(context);
        params.put("product_id", String.valueOf(product.getId()));
        params.put("name", name);
        params.put("category_id", category_id);
        params.put("price", price);
        params.put("quantity", quantity);
        params.put("offer_price", price);
        params.put("description", desc);
        params.put("additions[]", gson.toJson(additions));
        params.put("unique_additions[]", gson.toJson(uadditions));
        if (binding.cbPizza.isChecked())
            params.put("has_details", "1");
        else
            params.put("has_details", "0");
        Call<ResultAPIModel> call = GlobalMethods.getApiInterface().updateProduct(GlobalMethods.getHeaders(),
                params);
        GlobalMethods.printGson("verify", params);
        GlobalMethods.showProgress(binding.btnAdd);
        call.enqueue(new Callback<ResultAPIModel>() {
            @Override
            public void onResponse(Call<ResultAPIModel> call, Response<ResultAPIModel> response) {
                ResultAPIModel result = response.body();
                binding.btnAdd.setEnabled(true);
                DrawableButtonExtensionsKt.hideProgress(binding.btnAdd, R.string.add);
                if (response.isSuccessful()) {
                    assert result != null;
                    if (result.success == Constant.Status_successful) {
                        GlobalMethods.showSuccessToast(EditProductActivity.this, result.message);
                        finish();


                    } else {
                        Log.d("onResponse: ", result.message);
                        GlobalMethods.showErrorToast(EditProductActivity.this, result.message);
                    }

                } else {
                    ErrorModel errorModel = null;
                    try {
                        assert response.errorBody() != null;
                        String error = response.errorBody().string();
                        Log.d("onResponse: ", error);
                        errorModel = new Gson().fromJson(error, new TypeToken<ErrorModel>() {
                        }.getType());
                        GlobalMethods.showErrorToast(EditProductActivity.this, errorModel.message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResultAPIModel> call, Throwable t) {
                t.printStackTrace();
                if (t instanceof UnknownHostException || t instanceof NoRouteToHostException) {
                    NoConnectionDialog.with(EditProductActivity.this);
                }
            }
        });

    }


    private void deleteImageProductById(String image_id, int position) {
        Map<String, Object> params = GlobalMethods.getParams(context);
        params.put("image_id", image_id);


        Call<ResultAPIModel> call = GlobalMethods.getApiInterface().deleteImageProductById(GlobalMethods.getHeaders(),
                params);
        GlobalMethods.printGson("deleteImageProductById", params);
        binding.progress.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        call.enqueue(new Callback<ResultAPIModel>() {
            @Override
            public void onResponse(Call<ResultAPIModel> call, Response<ResultAPIModel> response) {
                ResultAPIModel result = response.body();
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                binding.progress.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    assert result != null;
                    if (result.success == Constant.Status_successful) {
                        GlobalMethods.showSuccessToast(EditProductActivity.this, result.message);
                        productImagesAdapter.removeImage(position);
                    } else {
                        Log.d("onResponse: ", result.message);
                        GlobalMethods.showErrorToast(EditProductActivity.this, result.message);
                    }

                } else {
                    ErrorModel errorModel = null;
                    try {
                        assert response.errorBody() != null;
                        String error = response.errorBody().string();
                        Log.d("onResponse: ", error);
                        errorModel = new Gson().fromJson(error, new TypeToken<ErrorModel>() {
                        }.getType());
                        GlobalMethods.showErrorToast(EditProductActivity.this, errorModel.message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResultAPIModel> call, Throwable t) {
                t.printStackTrace();
                if (t instanceof UnknownHostException || t instanceof NoRouteToHostException) {
                    NoConnectionDialog.with(EditProductActivity.this);
                }
            }
        });

    }


    public void uploadImageProduct(String product_id, Uri photo) {
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

        Gson gson = new Gson();

        RequestBody productIdRequestBody = RequestBody.create(mediaType, product_id);


        Call<ResultAPIModel<ProductImages>> call = GlobalMethods.getApiInterface().
                uploadImageProduct(GlobalMethods.getHeaders(), productIdRequestBody, body);
        binding.progress.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        call.enqueue(new Callback<ResultAPIModel<ProductImages>>() {
            @Override
            public void onResponse(Call<ResultAPIModel<ProductImages>> call, Response<ResultAPIModel<ProductImages>> response) {
                ResultAPIModel<ProductImages> result = response.body();
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                binding.progress.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    assert result != null;
                    if (result.success == Constant.Status_successful) {
                        GlobalMethods.showSuccessToast(EditProductActivity.this, result.message);
                        imagesList.add(result.data);
                        productImagesAdapter.notifyDataSetChanged();
                    } else {
                        GlobalMethods.showErrorToast(EditProductActivity.this, result.message);
                    }


                } else {

                    ErrorModel errorModel = null;
                    try {
                        assert response.errorBody() != null;
                        String error = response.errorBody().string();
                        Log.d("onResponse: ", error);
                        errorModel = new Gson().fromJson(error, new TypeToken<ErrorModel>() {
                        }.getType());
                        GlobalMethods.showErrorToast(EditProductActivity.this, gson.toJson(errorModel.message));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResultAPIModel<ProductImages>> call, Throwable t) {
                t.printStackTrace();
                if (t instanceof UnknownHostException || t instanceof NoRouteToHostException) {
                    NoConnectionDialog.with(EditProductActivity.this);
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
                        for (int i = 0; i < result.data.size(); i++) {
                            categoriesNames.add(result.data.get(i).getName());
                        }
                        binding.spCategories.setItems(categoriesNames);
                        binding.spCategories.setOnSpinnerItemSelectedListener((i, o, i1, t1) -> category_id = result.data.get(i1).getId());
                        getProductsData();

                    } else {
                        GlobalMethods.showErrorToast(EditProductActivity.this, result.message);

                    }

                } else {
                    ErrorModel errorModel = null;
                    try {
                        assert response.errorBody() != null;
                        String error = response.errorBody().string();
                        Log.d("onResponse: ", error);
                        errorModel = new Gson().fromJson(error, new TypeToken<ErrorModel>() {
                        }.getType());
                        GlobalMethods.showErrorToast(EditProductActivity.this, errorModel.message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResultAPIModel<List<Category>>> call, Throwable t) {
                t.printStackTrace();
                if (t instanceof UnknownHostException || t instanceof NoRouteToHostException) {
                    NoConnectionDialog.with(EditProductActivity.this);
                }
            }
        });

    }

//    private void getProductsById() {
//        Map<String, Object> params = GlobalMethods.getParams(context);
//        params.put("product_id", String.valueOf(product_id));
//        Call<ResultAPIModel<Product>> call = GlobalMethods.getApiInterface().getProductsById(GlobalMethods.getHeaders(), params);
//        call.enqueue(new Callback<ResultAPIModel<Product>>() {
//            @Override
//            public void onResponse(Call<ResultAPIModel<Product>> call, Response<ResultAPIModel<Product>> response) {
//                ResultAPIModel<Product> result = response.body();
//                if (response.isSuccessful()) {
//                    assert result != null;
//                    if (result.success == Constant.Status_successful) {
//                        binding.edName.setText(result.data.getName());
//                        binding.edDescp.setText(result.data.getDescription());
//                        binding.edAmount.setText(String.valueOf(result.data.getQuantity()));
//                        binding.edPrice.setText(String.valueOf(result.data.getPrice()));
//                        binding.spCategories.selectItemByIndex(0);
//                        if (result.data.getAdditions() != null) {
//                            additionsList.clear();
//                            additionsList.addAll(result.data.getAdditions());
//                            addProductAdditionsAdapter.notifyDataSetChanged();
//                        }
//
//
//                    } else {
//                        GlobalMethods.showErrorToast(EditProductActivity.this, result.message);
//
//                    }
//
//                } else {
//                    ErrorModel errorModel = null;
//                    try {
//                        assert response.errorBody() != null;
//                        String error = response.errorBody().string();
//                        Log.d("onResponse: ", error);
//                        errorModel = new Gson().fromJson(error, new TypeToken<ErrorModel>() {
//                        }.getType());
//                        GlobalMethods.showErrorToast(EditProductActivity.this, errorModel.message.get(0));
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResultAPIModel<Product>> call, Throwable t) {
//                t.printStackTrace();
//                if (t instanceof UnknownHostException || t instanceof NoRouteToHostException) {
//                    NoConnectionDialog.with(EditProductActivity.this);
//                }
//            }
//        });
//
//    }


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