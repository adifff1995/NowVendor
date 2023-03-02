package com.app.radarvendor.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.app.radarvendor.Adapter.CartItemAdditionsAdapter;
import com.app.radarvendor.Adapter.SliderAdapter;
import com.app.radarvendor.Module.Additions;
import com.app.radarvendor.Module.ErrorModel;
import com.app.radarvendor.Module.Product;
import com.app.radarvendor.Module.ResultAPIModel;
import com.app.radarvendor.Module.SliderItem;
import com.app.radarvendor.R;
import com.app.radarvendor.Utils.Constant;
import com.app.radarvendor.Utils.GlobalMethods;
import com.app.radarvendor.Utils.NoConnectionDialog;
import com.app.radarvendor.Utils.ProductStatus;
import com.app.radarvendor.databinding.ActivityProductDetailsBinding;
import com.github.razir.progressbutton.ButtonTextAnimatorExtensionsKt;
import com.github.razir.progressbutton.DrawableButtonExtensionsKt;
import com.github.razir.progressbutton.ProgressButtonHolderKt;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;

import java.net.NoRouteToHostException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailsActivity extends AppCompatActivity {

    private ActivityProductDetailsBinding binding;
    private Context context = ProductDetailsActivity.this;
    private List<Additions> productAdditionsList;
    private CartItemAdditionsAdapter productAdditionsAdapter;
    private SliderAdapter sliderAdapter;
    private int product_id;
    private List<SliderItem> sliderItemList;
    private Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setUpActivity();
        setUpActions();

    }

    private void setUpActivity() {
        if (getIntent().hasExtra("archived")){
            binding.btnArchive.setText(getText(R.string.republish));
        }
        ProgressButtonHolderKt.bindProgressButton(this, binding.btnEdit);
        ButtonTextAnimatorExtensionsKt.attachTextChangeAnimator(binding.btnEdit);
        sliderItemList = new ArrayList<>();
        product_id = getIntent().getIntExtra("id", -1);
        productAdditionsList = new ArrayList<>();
        productAdditionsAdapter = new CartItemAdditionsAdapter(context, productAdditionsList, position -> {

        });
        binding.rvProductsAddition.setAdapter(productAdditionsAdapter);
        binding.rvProductsAddition.setLayoutManager(new LinearLayoutManager(context));
    }

    private void setUpActions() {
        binding.btnEdit.setOnClickListener(view -> {
            Intent intent = new Intent(context, EditProductActivity.class);
            intent.putExtra("product", product);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        binding.btnArchive.setOnClickListener(view ->{
            if (getIntent().hasExtra("archived"))
                updateStatus(ProductStatus.active);
            else
                updateStatus(ProductStatus.archived);

        });


        binding.imgBack.setOnClickListener(view -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        getProductsById();

    }

    private void getProductsById() {
        Map<String, Object> params = GlobalMethods.getParams(context);
        params.put("product_id", product_id);
        Call<ResultAPIModel<Product>> call = GlobalMethods.getApiInterface().getProductsById(GlobalMethods.getHeaders(), params);
        binding.progress.setVisibility(View.VISIBLE);
        call.enqueue(new Callback<ResultAPIModel<Product>>() {
            @Override
            public void onResponse(Call<ResultAPIModel<Product>> call, Response<ResultAPIModel<Product>> response) {
                ResultAPIModel<Product> result = response.body();
                binding.progress.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    assert result != null;
                    if (result.success == Constant.Status_successful) {
                        product = result.data;
                        GlobalMethods.printGson("getProductsById",product);
                        binding.tvProductName.setText(result.data.getName());
                        binding.tvDescp.setText(result.data.getDescription());
                        binding.tvPrice.setText(String.valueOf(result.data.getPrice()));
                        if(result.data.getAdditions()!=null){
                            productAdditionsList.clear();
                            productAdditionsList.addAll(result.data.getAdditions());
                            productAdditionsAdapter.notifyDataSetChanged();
                        }


                        for (int i = 0; i < result.data.getImages().size(); i++)
                            sliderItemList.add(new SliderItem("", result.data.getImages().get(i).getImage_url()));
                        setUpSlider(sliderItemList);


                    } else {
                        GlobalMethods.showErrorToast(ProductDetailsActivity.this, result.message);

                    }

                } else {
                    ErrorModel errorModel = null;
                    try {
                        assert response.errorBody() != null;
                        String error = response.errorBody().string();
                        Log.d("onResponse: ", error);
                        errorModel = new Gson().fromJson(error, new TypeToken<ErrorModel>() {
                        }.getType());
                        GlobalMethods.showErrorToast(ProductDetailsActivity.this, errorModel.message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResultAPIModel<Product>> call, Throwable t) {
                t.printStackTrace();
                if (t instanceof UnknownHostException || t instanceof NoRouteToHostException) {
                    NoConnectionDialog.with(ProductDetailsActivity.this);
                }
            }
        });

    }

    private void updateStatus(ProductStatus status) {
        Map<String, Object> params = GlobalMethods.getParams(context);
        params.put("product_id", product_id);
        params.put("status", status);
        Call<ResultAPIModel> call = GlobalMethods.getApiInterface().updateStatus(GlobalMethods.getHeaders(), params);
        GlobalMethods.showProgress(binding.btnArchive);
        call.enqueue(new Callback<ResultAPIModel>() {
            @Override
            public void onResponse(Call<ResultAPIModel> call, Response<ResultAPIModel> response) {
                binding.btnArchive.setEnabled(true);
                DrawableButtonExtensionsKt.hideProgress(binding.btnArchive, R.string.archive);
                ResultAPIModel result = response.body();
                if (response.isSuccessful()) {
                    assert result != null;
                    if (result.success == Constant.Status_successful) {
                        GlobalMethods.showSuccessToast(ProductDetailsActivity.this, result.message);
                        finish();

                    } else {
                        GlobalMethods.showErrorToast(ProductDetailsActivity.this, result.message);

                    }

                } else {
                    ErrorModel errorModel = null;
                    try {
                        assert response.errorBody() != null;
                        String error = response.errorBody().string();
                        Log.d("onResponse: ", error);
                        errorModel = new Gson().fromJson(error, new TypeToken<ErrorModel>() {
                        }.getType());
                        GlobalMethods.showErrorToast(ProductDetailsActivity.this, errorModel.message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResultAPIModel> call, Throwable t) {
                t.printStackTrace();
                if (t instanceof UnknownHostException || t instanceof NoRouteToHostException) {
                    NoConnectionDialog.with(ProductDetailsActivity.this);
                }
            }
        });

    }

    private void setUpSlider(List<SliderItem> sliderItemList) {
        sliderAdapter = new SliderAdapter(context);
        binding.imageSlider.setSliderAdapter(sliderAdapter);
        sliderAdapter.renewItems(sliderItemList);
        binding.imageSlider.setIndicatorAnimation(IndicatorAnimationType.WORM);
        binding.imageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
//        binding.imageSlider.startAutoCycle();

    }
}