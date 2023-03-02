package com.app.radarvendor.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.app.radarvendor.Adapter.OrderDetailsItemsAdapter;
import com.app.radarvendor.Module.ErrorModel;
import com.app.radarvendor.Module.OrderItem;
import com.app.radarvendor.Module.OrderResponse;
import com.app.radarvendor.Module.ResultAPIModel;
import com.app.radarvendor.R;
import com.app.radarvendor.Utils.Constant;
import com.app.radarvendor.Utils.GlobalMethods;
import com.app.radarvendor.Utils.NoConnectionDialog;
import com.app.radarvendor.databinding.ActivityMainBinding;
import com.app.radarvendor.databinding.ActivityOrderDetailsBinding;
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

public class OrderDetailsActivity extends AppCompatActivity {

    private ActivityOrderDetailsBinding binding;
    private List<OrderItem> cartItemList;
    private OrderDetailsItemsAdapter cartItemsAdapter;
    private Context context = OrderDetailsActivity.this;
    private OrderResponse orderResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setUpActivity();
        setUpActions();
    }

    private void setUpActions() {
        binding.imgBack.setOnClickListener(view -> finish());
        binding.btnReject.setOnClickListener(view -> orderReject(String.valueOf(orderResponse.getId())));
        binding.btnAccept.setOnClickListener(view -> {
            if (binding.btnAccept.getText().equals(getString(R.string.accept)))
                orderAcceptance(String.valueOf(orderResponse.getId()));
            else
                orderReady(String.valueOf(orderResponse.getId()));

        });
    }

    private void setUpActivity() {
        orderResponse = (OrderResponse) getIntent().getSerializableExtra("order");
        GlobalMethods.printGson("orderResponse", orderResponse);
        binding.tvOrderNum.setText(String.valueOf(orderResponse.getId()));
        binding.tvCustomerName.setText(orderResponse.getName());
        binding.tvCustomerPhone.setText(orderResponse.getPhone());
        binding.tvTotal.setText(orderResponse.getSub_total() + " ₪");
        binding.tvFinalTotal.setText(orderResponse.getTotal() + " ₪");

        cartItemList = new ArrayList<>();
        cartItemList.addAll(orderResponse.getOrder_item());
        cartItemsAdapter = new OrderDetailsItemsAdapter(context, cartItemList, position -> {

        });
        binding.rvItems.setAdapter(cartItemsAdapter);
        binding.rvItems.setLayoutManager(new LinearLayoutManager(context));

        if (orderResponse.getStore_status().equals("acceptance")) {
            binding.btnAccept.setText(context.getString(R.string.ready));
        } else if (orderResponse.getStore_status().equals("complete")) {
            binding.btnAccept.setVisibility(View.GONE);
            binding.btnReject.setVisibility(View.GONE);
        } else {
            binding.btnAccept.setText(context.getString(R.string.accept));

        }
    }

    private void orderAcceptance(String order_id) {
        Map<String, Object> params = GlobalMethods.getParams(OrderDetailsActivity.this);
        params.put("order_id", String.valueOf(order_id));
        Call<ResultAPIModel<OrderResponse>> call = GlobalMethods.getApiInterface().orderAcceptance(GlobalMethods.getHeaders(), params);
        binding.progress.setVisibility(View.VISIBLE);
        call.enqueue(new Callback<ResultAPIModel<OrderResponse>>() {
            @Override
            public void onResponse(Call<ResultAPIModel<OrderResponse>> call, Response<ResultAPIModel<OrderResponse>> response) {
                ResultAPIModel<OrderResponse> result = response.body();
                binding.progress.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    assert result != null;
                    if (result.success == Constant.Status_successful) {
//                        product = result.data;
                        GlobalMethods.printGson("orderAcceptance", result.data);
//                        GlobalMethods.showSuccessToast(OrderDetailsActivity.this, result.message);
                        binding.btnAccept.setText(getString(R.string.ready));


                    } else {
                        GlobalMethods.showErrorToast(OrderDetailsActivity.this, result.message);

                    }

                } else {
                    ErrorModel errorModel = null;
                    try {
                        assert response.errorBody() != null;
                        String error = response.errorBody().string();
                        Log.d("onResponse: ", error);
                        errorModel = new Gson().fromJson(error, new TypeToken<ErrorModel>() {
                        }.getType());
                        GlobalMethods.showErrorToast(OrderDetailsActivity.this, errorModel.message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResultAPIModel<OrderResponse>> call, Throwable t) {
                t.printStackTrace();
                if (t instanceof UnknownHostException || t instanceof NoRouteToHostException) {
                    NoConnectionDialog.with(OrderDetailsActivity.this);
                }
            }
        });

    }

    private void orderReject(String order_id) {
        Map<String, Object> params = GlobalMethods.getParams(OrderDetailsActivity.this);
        params.put("order_id", String.valueOf(order_id));
        Call<ResultAPIModel<OrderResponse>> call = GlobalMethods.getApiInterface().orderReject(GlobalMethods.getHeaders(), params);
        binding.progress.setVisibility(View.VISIBLE);
        call.enqueue(new Callback<ResultAPIModel<OrderResponse>>() {
            @Override
            public void onResponse(Call<ResultAPIModel<OrderResponse>> call, Response<ResultAPIModel<OrderResponse>> response) {
                ResultAPIModel<OrderResponse> result = response.body();
                binding.progress.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    assert result != null;
                    if (result.success == Constant.Status_successful) {
//                        product = result.data;
                        GlobalMethods.printGson("orderReject", result.data);
//                        GlobalMethods.showSuccessToast(OrderDetailsActivity.this, result.message);


                    } else {
                        GlobalMethods.showErrorToast(OrderDetailsActivity.this, result.message);

                    }

                } else {
                    ErrorModel errorModel = null;
                    try {
                        assert response.errorBody() != null;
                        String error = response.errorBody().string();
                        Log.d("onResponse: ", error);
                        errorModel = new Gson().fromJson(error, new TypeToken<ErrorModel>() {
                        }.getType());
                        GlobalMethods.showErrorToast(OrderDetailsActivity.this, errorModel.message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResultAPIModel<OrderResponse>> call, Throwable t) {
                t.printStackTrace();
                if (t instanceof UnknownHostException || t instanceof NoRouteToHostException) {
                    NoConnectionDialog.with(OrderDetailsActivity.this);
                }
            }
        });
    }

    private void orderReady(String order_id) {
        Map<String, Object> params = GlobalMethods.getParams(OrderDetailsActivity.this);
        params.put("order_id", String.valueOf(order_id));
        Call<ResultAPIModel<OrderResponse>> call = GlobalMethods.getApiInterface().orderComplete(GlobalMethods.getHeaders(), params);
        binding.progress.setVisibility(View.VISIBLE);
        call.enqueue(new Callback<ResultAPIModel<OrderResponse>>() {
            @Override
            public void onResponse(Call<ResultAPIModel<OrderResponse>> call, Response<ResultAPIModel<OrderResponse>> response) {
                ResultAPIModel<OrderResponse> result = response.body();
                binding.progress.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    assert result != null;
                    if (result.success == Constant.Status_successful) {
//                        product = result.data;
                        GlobalMethods.printGson("orderReady", result.data);
                        finish();
//                        GlobalMethods.showSuccessToast(OrderDetailsActivity.this, result.message);
//                        getCurrentOrders();
//                        getCompletedOrders();


                    } else {
                        GlobalMethods.showErrorToast(OrderDetailsActivity.this, result.message);

                    }

                } else {
                    ErrorModel errorModel = null;
                    try {
                        assert response.errorBody() != null;
                        String error = response.errorBody().string();
                        Log.d("onResponse: ", error);
                        errorModel = new Gson().fromJson(error, new TypeToken<ErrorModel>() {
                        }.getType());
                        GlobalMethods.showErrorToast(OrderDetailsActivity.this, errorModel.message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResultAPIModel<OrderResponse>> call, Throwable t) {
                t.printStackTrace();
                if (t instanceof UnknownHostException || t instanceof NoRouteToHostException) {
                    NoConnectionDialog.with(OrderDetailsActivity.this);
                }
            }
        });

    }

}