package com.app.radarvendor.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.app.radarvendor.Adapter.CartItemAdditionsAdapter;
import com.app.radarvendor.Module.Additions;
import com.app.radarvendor.Module.ErrorModel;
import com.app.radarvendor.Module.ResultAPIModel;
import com.app.radarvendor.Module.Service;
import com.app.radarvendor.R;
import com.app.radarvendor.Utils.Constant;
import com.app.radarvendor.Utils.GlobalMethods;
import com.app.radarvendor.Utils.NoConnectionDialog;
import com.app.radarvendor.databinding.ActivityProductDetailsBinding;
import com.app.radarvendor.databinding.ActivityServiceDetailsBinding;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.net.NoRouteToHostException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServiceDetailsActivity extends AppCompatActivity {

    private ActivityServiceDetailsBinding binding;
    private Context context = ServiceDetailsActivity.this;
    private List<Additions> productAdditionsList;
    private CartItemAdditionsAdapter productAdditionsAdapter;
    private int service_id;
    private Service product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityServiceDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setUpActivity();
        setUpActions();
    }

    private void setUpActivity() {
        service_id = getIntent().getIntExtra("id", -1);
        productAdditionsList = new ArrayList<>();

        productAdditionsAdapter = new CartItemAdditionsAdapter(context, productAdditionsList, position -> {

        });
        binding.rvProductsAddition.setAdapter(productAdditionsAdapter);
        binding.rvProductsAddition.setLayoutManager(new LinearLayoutManager(context));
    }

    private void setUpActions() {
        binding.btnEdit.setOnClickListener(view -> {
            Intent intent = new Intent(context, EditServiceActivity.class);
            intent.putExtra("service", product);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        binding.imgBack.setOnClickListener(view -> finish());
    }

    private void getServiceById() {
        Map<String, Object> params = GlobalMethods.getParams(context);
        params.put("service_id", String.valueOf(service_id));
        Call<ResultAPIModel<Service>> call = GlobalMethods.getApiInterface().getServiceById(GlobalMethods.getHeaders(), params);
        binding.progress.setVisibility(View.VISIBLE);
        call.enqueue(new Callback<ResultAPIModel<Service>>() {
            @Override
            public void onResponse(Call<ResultAPIModel<Service>> call, Response<ResultAPIModel<Service>> response) {
                ResultAPIModel<Service> result = response.body();
                binding.progress.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    assert result != null;
                    if (result.success == Constant.Status_successful) {
                        product = result.data;
                        GlobalMethods.printGson("getProductsById", product);
                        binding.tvProductName.setText(result.data.getName());
                        binding.tvDescp.setText(result.data.getDescription());
                        if (result.data.getAdditions() != null) {
                            productAdditionsList.clear();
                            productAdditionsList.addAll(result.data.getAdditions());
                            productAdditionsAdapter.notifyDataSetChanged();
                        }

                        CircularProgressDrawable circularProgressDrawable = GlobalMethods.getPlaceHolder(context);
                        circularProgressDrawable.start();
                        if (product.getImage_url() != null)
                            Glide.with(context).load(product.getImage_url()).placeholder(circularProgressDrawable).into(binding.serviceImg);


                    } else {
                        GlobalMethods.showErrorToast(ServiceDetailsActivity.this, result.message);

                    }

                } else {
                    ErrorModel errorModel = null;
                    try {
                        assert response.errorBody() != null;
                        String error = response.errorBody().string();
                        Log.d("onResponse: ", error);
                        errorModel = new Gson().fromJson(error, new TypeToken<ErrorModel>() {
                        }.getType());
                        GlobalMethods.showErrorToast(ServiceDetailsActivity.this, errorModel.message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResultAPIModel<Service>> call, Throwable t) {
                t.printStackTrace();
                if (t instanceof UnknownHostException || t instanceof NoRouteToHostException) {
                    NoConnectionDialog.with(ServiceDetailsActivity.this);
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        getServiceById();
    }
}