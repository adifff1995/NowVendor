package com.app.radarvendor.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.radarvendor.Activities.OrderDetailsActivity;
import com.app.radarvendor.Adapter.OrdersAdapter;
import com.app.radarvendor.Module.ErrorModel;
import com.app.radarvendor.Module.OrderResponse;
import com.app.radarvendor.Module.OrderStatus;
import com.app.radarvendor.Module.ResultAPIModel;
import com.app.radarvendor.Module.Vendor;
import com.app.radarvendor.R;
import com.app.radarvendor.Utils.Constant;
import com.app.radarvendor.Utils.GlobalMethods;
import com.app.radarvendor.Utils.NoConnectionDialog;
import com.app.radarvendor.databinding.FragmentContactUsBinding;
import com.app.radarvendor.databinding.FragmentHomeBinding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.hawk.Hawk;

import java.net.NoRouteToHostException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Activity activity;
    private FragmentHomeBinding binding;
    private List<OrderResponse> currentOrderItemList;
    private List<OrderResponse> completedOrderItemList;
    private OrdersAdapter currentOrdersAdapter;
    private OrdersAdapter completedOrdersAdapter;


    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpFragment();
        setUpAction();

    }

    @Override
    public void onResume() {
        super.onResume();
        getCompletedOrders();
        getCurrentOrders();
    }

    private void setUpFragment() {
        currentOrderItemList = new ArrayList<>();
        currentOrdersAdapter = new OrdersAdapter(activity, currentOrderItemList, new OrdersAdapter.OnItemSelect() {
            @Override
            public void onNotificationClicked(int position) {
                Intent intent = new Intent(activity, OrderDetailsActivity.class);
                intent.putExtra("order", currentOrderItemList.get(position));
                startActivity(intent);
                activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }

            @Override
            public void onOrderAcceptedClicked(int position) {
                orderAcceptance(String.valueOf(currentOrderItemList.get(position).getId()));
            }

            @Override
            public void onOrderReadyClicked(int position) {
                orderReady(String.valueOf(currentOrderItemList.get(position).getId()));
            }

            @Override
            public void onOrderRejectedClicked(int position) {
                orderReject(String.valueOf(currentOrderItemList.get(position).getId()));

            }
        });

        completedOrderItemList = new ArrayList<>();
        completedOrdersAdapter = new OrdersAdapter(activity, completedOrderItemList, new OrdersAdapter.OnItemSelect() {
            @Override
            public void onNotificationClicked(int position) {
                Intent intent = new Intent(activity, OrderDetailsActivity.class);
                intent.putExtra("order", completedOrderItemList.get(position));
                startActivity(intent);
                activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }

            @Override
            public void onOrderAcceptedClicked(int position) {

            }

            @Override
            public void onOrderReadyClicked(int position) {

            }

            @Override
            public void onOrderRejectedClicked(int position) {

            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(activity);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager2.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager2.setStackFromEnd(true);

        binding.rvCurrentOrders.setAdapter(currentOrdersAdapter);
        binding.rvCurrentOrders.setLayoutManager(linearLayoutManager);

        binding.rvPreviousOrders.setAdapter(completedOrdersAdapter);
        binding.rvPreviousOrders.setLayoutManager(linearLayoutManager2);

        binding.swipeToRefresh.setOnRefreshListener(() -> {
            getCompletedOrders();
            getCurrentOrders();
        });
        Vendor vendor = Hawk.get(Constant.profile);
        GlobalMethods.printGson("Vendor", vendor);
        if (vendor != null) {
            binding.btnState.setText(vendor.getStatus_store_name());
            binding.btnState.setTag(String.valueOf(vendor.getStatus_store()));
        } else
            binding.btnState.setVisibility(View.GONE);


    }

    private void setUpAction() {
        binding.btnNewOrders.setOnClickListener(view -> {
            if (binding.rvCurrentOrders.getVisibility() == View.GONE) {
                binding.btnPreviousOrders.setBackgroundResource(R.drawable.bg_transparent);
                binding.btnNewOrders.setBackgroundResource(R.drawable.bg_button_main_shape);
                binding.btnNewOrders.setTextColor(activity.getColor(R.color.white));
                binding.btnPreviousOrders.setTextColor(activity.getColor(R.color.text));
                binding.rvPreviousOrders.setVisibility(View.GONE);
                binding.rvCurrentOrders.setVisibility(View.VISIBLE);
            }
        });

        binding.btnPreviousOrders.setOnClickListener(view -> {
            if (binding.rvPreviousOrders.getVisibility() == View.GONE) {
                Log.e("setUpAction: ", "setUpAction");
                binding.btnNewOrders.setBackgroundResource(R.drawable.bg_transparent);
                binding.btnPreviousOrders.setBackgroundResource(R.drawable.bg_button_main_shape);
                binding.btnPreviousOrders.setTextColor(activity.getColor(R.color.white));
                binding.btnNewOrders.setTextColor(activity.getColor(R.color.text));
                binding.rvCurrentOrders.setVisibility(View.GONE);
                binding.rvPreviousOrders.setVisibility(View.VISIBLE);
            }
        });

        binding.btnState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String state = (String) binding.btnState.getTag();
                switch (state) {
                    case "1":
                        changeStoreStatus("2");
                        break;
                    case "2":
                        changeStoreStatus("3");
                        break;
                    case "3":
                        changeStoreStatus("1");

                        break;
                }


            }
        });
    }

    private void getCompletedOrders() {
        Map<String, Object> params = GlobalMethods.getParams(activity);
        params.put("store_status", OrderStatus.previous);

        Call<ResultAPIModel<List<OrderResponse>>> call = GlobalMethods.getApiInterface().getOrders(GlobalMethods.getHeaders(), params);
        binding.rvPreviousOrders.showShimmerAdapter();
        call.enqueue(new Callback<ResultAPIModel<List<OrderResponse>>>() {
            @Override
            public void onResponse(Call<ResultAPIModel<List<OrderResponse>>> call, Response<ResultAPIModel<List<OrderResponse>>> response) {
                ResultAPIModel<List<OrderResponse>> result = response.body();
                binding.rvPreviousOrders.hideShimmerAdapter();
                if (binding.swipeToRefresh.isRefreshing())
                    binding.swipeToRefresh.setRefreshing(false);
                if (response.isSuccessful()) {
                    assert result != null;
                    if (result.success == Constant.Status_successful) {
                        GlobalMethods.printGson("getCompletedOrders", result.data);
                        completedOrderItemList.clear();
                        completedOrderItemList.addAll(result.data);
                        completedOrdersAdapter.notifyDataSetChanged();
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
            public void onFailure(Call<ResultAPIModel<List<OrderResponse>>> call, Throwable t) {
                t.printStackTrace();
                if (t instanceof UnknownHostException || t instanceof NoRouteToHostException) {
                    NoConnectionDialog.with(activity);
                }
            }
        });

    }


    private void getCurrentOrders() {
        Map<String, Object> params = GlobalMethods.getParams(activity);
        params.put("store_status", OrderStatus.current);

        Call<ResultAPIModel<List<OrderResponse>>> call = GlobalMethods.getApiInterface().getOrders(GlobalMethods.getHeaders(), params);
        binding.rvCurrentOrders.showShimmerAdapter();
        call.enqueue(new Callback<ResultAPIModel<List<OrderResponse>>>() {
            @Override
            public void onResponse(Call<ResultAPIModel<List<OrderResponse>>> call, Response<ResultAPIModel<List<OrderResponse>>> response) {
                ResultAPIModel<List<OrderResponse>> result = response.body();
                binding.rvCurrentOrders.hideShimmerAdapter();
                if (binding.swipeToRefresh.isRefreshing())
                    binding.swipeToRefresh.setRefreshing(false);
                if (response.isSuccessful()) {
                    assert result != null;
                    if (result.success == Constant.Status_successful) {
                        GlobalMethods.printGson("getCurrentOrders", result.data);
                        currentOrderItemList.clear();
                        currentOrderItemList.addAll(result.data);
                        currentOrdersAdapter.notifyDataSetChanged();
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
            public void onFailure(Call<ResultAPIModel<List<OrderResponse>>> call, Throwable t) {
                t.printStackTrace();
                if (t instanceof UnknownHostException || t instanceof NoRouteToHostException) {
                    NoConnectionDialog.with(activity);
                }
            }
        });

    }

    private void orderAcceptance(String order_id) {
        Map<String, Object> params = GlobalMethods.getParams(activity);
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
//                        GlobalMethods.showSuccessToast(activity, result.message);
                        getCurrentOrders();
                        getCompletedOrders();


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
            public void onFailure(Call<ResultAPIModel<OrderResponse>> call, Throwable t) {
                t.printStackTrace();
                if (t instanceof UnknownHostException || t instanceof NoRouteToHostException) {
                    NoConnectionDialog.with(activity);
                }
            }
        });

    }

    private void orderReject(String order_id) {
        Map<String, Object> params = GlobalMethods.getParams(activity);
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
//                        GlobalMethods.showSuccessToast(activity, result.message);
                        getCurrentOrders();
                        getCompletedOrders();


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
            public void onFailure(Call<ResultAPIModel<OrderResponse>> call, Throwable t) {
                t.printStackTrace();
                if (t instanceof UnknownHostException || t instanceof NoRouteToHostException) {
                    NoConnectionDialog.with(activity);
                }
            }
        });
    }

    private void orderReady(String order_id) {
        Map<String, Object> params = GlobalMethods.getParams(activity);
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
                        GlobalMethods.showSuccessToast(activity, result.message);
//                        getCurrentOrders();
//                        getCompletedOrders();


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
            public void onFailure(Call<ResultAPIModel<OrderResponse>> call, Throwable t) {
                t.printStackTrace();
                if (t instanceof UnknownHostException || t instanceof NoRouteToHostException) {
                    NoConnectionDialog.with(activity);
                }
            }
        });

    }

    private void changeStoreStatus(String status_store) {
        Map<String, Object> params = GlobalMethods.getParams(activity);
        params.put("status_store", status_store);
        Call<ResultAPIModel<Vendor>> call = GlobalMethods.getApiInterface().changeStoreStatus(GlobalMethods.getHeaders(), params);
        binding.progress.setVisibility(View.VISIBLE);
        call.enqueue(new Callback<ResultAPIModel<Vendor>>() {
            @Override
            public void onResponse(Call<ResultAPIModel<Vendor>> call, Response<ResultAPIModel<Vendor>> response) {
                ResultAPIModel<Vendor> result = response.body();
                binding.progress.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    assert result != null;
                    if (result.success == Constant.Status_successful) {
                        GlobalMethods.printGson("changeStoreStatus", result.data);
                        binding.btnState.setText(result.data.getStatus_store_name());
                        binding.btnState.setTag(result.data.getStatus_store());
                        switch (result.data.getStatus_store()) {
                            case "1":
                                binding.btnState.setBackgroundResource(R.drawable.bg_button_shape_green);
                                break;
                            case "2":
                                binding.btnState.setBackgroundResource(R.drawable.bg_button_shape_red);
                                break;
                            case "3":
                                binding.btnState.setBackgroundResource(R.drawable.bg_button_main_shape);
                                break;
                        }


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


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            activity = (Activity) context;
        }
    }
}