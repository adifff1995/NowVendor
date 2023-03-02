package com.app.radarvendor.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.app.radarvendor.Adapter.ChooseProductsAdapter;
import com.app.radarvendor.Module.ErrorModel;
import com.app.radarvendor.Module.Product;
import com.app.radarvendor.Module.ProductForOffer;
import com.app.radarvendor.Module.ResultAPIModel;
import com.app.radarvendor.R;
import com.app.radarvendor.Utils.Constant;
import com.app.radarvendor.Utils.GlobalMethods;
import com.app.radarvendor.Utils.NoConnectionDialog;
import com.app.radarvendor.databinding.ActivityAddOffersBinding;
import com.app.radarvendor.databinding.ActivityAddProductBinding;
import com.app.radarvendor.databinding.DialogChooseProductsBinding;
import com.app.radarvendor.databinding.DialogViewBillBinding;
import com.github.razir.progressbutton.ButtonTextAnimatorExtensionsKt;
import com.github.razir.progressbutton.DrawableButtonExtensionsKt;
import com.github.razir.progressbutton.ProgressButtonHolderKt;
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
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddOffersActivity extends AppCompatActivity {

    private ActivityAddOffersBinding binding;
    private MediaType mediaType = MediaType.parse("multipart/form-data");
    private Context context = AddOffersActivity.this;
    private Uri image;
    //    private List<Integer> productsIdsList;
    private DialogChooseProductsBinding chooseProductsBinding;
    private Dialog popupView;
    private List<Product> productList;
    private List<Product> productSelectList;
    private ChooseProductsAdapter chooseProductsAdapter;
    private ChooseProductsAdapter chooseProductsAdapter2;
    private List<ProductForOffer> productForOfferList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddOffersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setUpActivity();
        setUpAction();
        getProducts();
        showProductsDialog();
    }

    private void setUpActivity() {
        binding.tool.tvMainTitle.setText(getString(R.string.add_offer));
        ProgressButtonHolderKt.bindProgressButton(this, binding.btnAddOffer);
        ButtonTextAnimatorExtensionsKt.attachTextChangeAnimator(binding.btnAddOffer);
        productList = new ArrayList<>();
        productSelectList = new ArrayList<>();
        productForOfferList = new ArrayList<>();
        chooseProductsAdapter = new ChooseProductsAdapter(context, productSelectList, 0, new ChooseProductsAdapter.OnItemSelect() {
            @Override
            public void onProductClicked(int position, boolean isChecked) {

            }

            @Override
            public void onItemIncrease(int position) {

            }

            @Override
            public void onItemDecrease(int position) {

            }
        });
        chooseProductsAdapter2 = new ChooseProductsAdapter(context, productList, 1, new ChooseProductsAdapter.OnItemSelect() {
            @Override
            public void onProductClicked(int position, boolean isChecked) {

            }

            @Override
            public void onItemIncrease(int position) {

            }

            @Override
            public void onItemDecrease(int position) {

            }
        });

        binding.rvProdcuts.setAdapter(chooseProductsAdapter2);
        binding.rvProdcuts.setLayoutManager(new LinearLayoutManager(context));
    }

    private void setUpAction() {
        binding.frame.setOnClickListener(view -> PickImageDialog.build(new PickSetup())
                .setOnPickResult(r -> {
                    image = r.getUri();
                    binding.add.setVisibility(View.GONE);
                    binding.image.setVisibility(View.VISIBLE);
                    binding.image.setImageURI(r.getUri());
                })
                .setOnPickCancel(() -> {
                }).show(getSupportFragmentManager()));

        binding.tool.imgBack.setOnClickListener(view -> finish());

        binding.btnAddOffer.setOnClickListener(view -> {
            if (GlobalMethods.setEditTextEmptyError(context, binding.edName) &&
                    GlobalMethods.setEditTextEmptyError(context, binding.edNotes) &&
                    GlobalMethods.setEditTextEmptyError(context, binding.edNewPrice) &&
                    GlobalMethods.setEditTextEmptyError(context, binding.edOldPrice)
            ) {
                if (image != null) {
                    addOffer(binding.edName.getText().toString(),
                            binding.edNotes.getText().toString(),
                            binding.edOldPrice.getText().toString(),
                            binding.edNewPrice.getText().toString(),
                            image, productForOfferList

                    );


                } else
                    GlobalMethods.showWarningToast(AddOffersActivity.this, "الرجاء اضافة صور للخدمة");

            }
        });

        binding.btnChooseProduct.setOnClickListener(view -> {
            if (!popupView.isShowing())
                popupView.show();
        });
    }

    private void showProductsDialog() {
        popupView = new Dialog(context);
        chooseProductsBinding = DialogChooseProductsBinding.inflate(getLayoutInflater());
        popupView.setContentView(chooseProductsBinding.getRoot());
        Objects.requireNonNull(popupView.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = ViewGroup.LayoutParams.MATCH_PARENT;
        popupView.getWindow().setLayout(width, height);
        chooseProductsBinding.rvProducts.setAdapter(chooseProductsAdapter);
        chooseProductsBinding.rvProducts.setLayoutManager(new LinearLayoutManager(context));
        chooseProductsBinding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                productList.clear();
                for (int i = 0; i < chooseProductsAdapter.getCheckedProducts().size(); i++) {
                    Product product = chooseProductsAdapter.getCheckedProducts().get(i);
                    productForOfferList.add(new ProductForOffer(String.valueOf(product.getId()), String.valueOf(product.getQuantity())));
                }
//                productsIdsList = chooseProductsAdapter.getCheckedProductsIDs();
                productList.addAll(chooseProductsAdapter.getCheckedProducts());
                chooseProductsAdapter2.notifyDataSetChanged();
                binding.edOldPrice.setText(String.valueOf(chooseProductsAdapter.getTotalCheckedProducts()));
                popupView.dismiss();
            }
        });

    }


    public void addOffer(String name, String desc, String price, String offerPrice,
                         Uri photo, List<ProductForOffer> products) {
        MultipartBody.Part body = null;
        if (photo != null) {
            File file = null;
            try {
                file = new File(getPath(context, photo));
                RequestBody requestFile = RequestBody.create(mediaType, file);
                body = MultipartBody.Part.createFormData("image", URLEncoder.encode(file.getName(), "utf-8"), requestFile);

            } catch (URISyntaxException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        Gson gson = new Gson();

        Log.e("addOffer: ", gson.toJson(products));
        RequestBody nameRequestBody = RequestBody.create(mediaType, name);
        RequestBody priceRequestBody = RequestBody.create(mediaType, price);
        RequestBody offerPriceRequestBody = RequestBody.create(mediaType, offerPrice);
        RequestBody descriptionRequestBody = RequestBody.create(mediaType, desc);
        RequestBody productsRequestBody = RequestBody.create(mediaType, gson.toJson(products));


        Call<ResultAPIModel> call = GlobalMethods.getApiInterface().addOffer(GlobalMethods.getHeaders(),
                nameRequestBody, priceRequestBody, offerPriceRequestBody, descriptionRequestBody,
                productsRequestBody, body);

        GlobalMethods.showProgress(binding.btnAddOffer);
        call.enqueue(new Callback<ResultAPIModel>() {
            @Override
            public void onResponse(Call<ResultAPIModel> call, Response<ResultAPIModel> response) {
                binding.btnAddOffer.setEnabled(true);
                DrawableButtonExtensionsKt.hideProgress(binding.btnAddOffer, R.string.add_offer);
                ResultAPIModel result = response.body();
                if (response.isSuccessful()) {
                    assert result != null;
                    if (result.success == Constant.Status_successful) {
                        GlobalMethods.showSuccessToast(AddOffersActivity.this, result.message);
                        finish();
                    } else {
                        GlobalMethods.showErrorToast(AddOffersActivity.this, result.message);
                    }


                } else {

                    ErrorModel errorModel = null;
                    try {
                        assert response.errorBody() != null;
                        String error = response.errorBody().string();
                        Log.d("onResponse: ", error);
                        errorModel = new Gson().fromJson(error, new TypeToken<ErrorModel>() {
                        }.getType());
                        GlobalMethods.showErrorToast(AddOffersActivity.this, gson.toJson(errorModel.message));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResultAPIModel> call, Throwable t) {
                t.printStackTrace();
                if (t instanceof UnknownHostException || t instanceof NoRouteToHostException) {
                    NoConnectionDialog.with(AddOffersActivity.this);
                }
            }
        });

    }

    private void getProducts() {
        Call<ResultAPIModel<List<Product>>> call = GlobalMethods.getApiInterface().getProducts(GlobalMethods.getHeaders());
        call.enqueue(new Callback<ResultAPIModel<List<Product>>>() {
            @Override
            public void onResponse(Call<ResultAPIModel<List<Product>>> call, Response<ResultAPIModel<List<Product>>> response) {
                ResultAPIModel<List<Product>> result = response.body();

                if (response.isSuccessful()) {
                    assert result != null;
                    if (result.success == Constant.Status_successful) {
                        productSelectList.clear();
                        productSelectList.addAll(result.data);
                        for (int i = 0; i < productSelectList.size(); i++) {
                            productSelectList.get(i).setQuantity(1);
                        }
                        chooseProductsAdapter.notifyDataSetChanged();
                    } else {
                        GlobalMethods.showErrorToast(AddOffersActivity.this, result.message);

                    }

                } else {
                    ErrorModel errorModel = null;
                    try {
                        assert response.errorBody() != null;
                        String error = response.errorBody().string();
                        Log.d("onResponse: ", error);
                        errorModel = new Gson().fromJson(error, new TypeToken<ErrorModel>() {
                        }.getType());
                        GlobalMethods.showErrorToast(AddOffersActivity.this, errorModel.message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResultAPIModel<List<Product>>> call, Throwable t) {
                t.printStackTrace();
                if (t instanceof UnknownHostException || t instanceof NoRouteToHostException) {
                    NoConnectionDialog.with(AddOffersActivity.this);
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