package com.app.radarvendor.Fragment;

import android.app.Activity;
import android.app.ProgressDialog;
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

import com.app.radarvendor.Activities.NewReservationActivity;
import com.app.radarvendor.Activities.ReservationDetailsActivity;
import com.app.radarvendor.Adapter.ServicesOrdersAdapter;
import com.app.radarvendor.Module.ErrorModel;
import com.app.radarvendor.Module.OrderStatus;
import com.app.radarvendor.Module.Reservations;
import com.app.radarvendor.Module.ResultAPIModel;
import com.app.radarvendor.R;
import com.app.radarvendor.Utils.Constant;
import com.app.radarvendor.Utils.GlobalMethods;
import com.app.radarvendor.Utils.NoConnectionDialog;
import com.app.radarvendor.databinding.FragmentHomeBinding;
import com.app.radarvendor.databinding.FragmentHomeServiceBinding;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeServiceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeServiceFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FragmentHomeServiceBinding binding;
    private List<Reservations> currentOrderList;
    private ServicesOrdersAdapter currentOrdersAdapter;
    private ServicesOrdersAdapter completedOrdersAdapter;
    private Activity activity;
    private List<Reservations> completedOrderItemList;
    private ProgressDialog pd;

    public HomeServiceFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeServiceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeServiceFragment newInstance(String param1, String param2) {
        HomeServiceFragment fragment = new HomeServiceFragment();
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
        binding = FragmentHomeServiceBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpFragment();
        setUpAction();
        getCompletedOrders();
        getCurrentOrders();
    }

    private void setUpFragment() {
        currentOrderList = new ArrayList<>();
        completedOrderItemList = new ArrayList<>();
        pd = new ProgressDialog(activity);
        pd.setMessage("انتظر قليلا");

        currentOrdersAdapter = new ServicesOrdersAdapter(activity, currentOrderList, new ServicesOrdersAdapter.OnItemSelect() {
            @Override
            public void onNotificationClicked(int position) {
                Intent intent = new Intent(activity, ReservationDetailsActivity.class);
                intent.putExtra("reserve",currentOrderList.get(position));
                startActivity(intent);
                activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }

            @Override
            public void onAcceptClicked(int position) {
            reservationStatus(String.valueOf(currentOrderList.get(position).getId()),"acceptance");
            }

            @Override
            public void onRejectClicked(int position) {
                reservationStatus(String.valueOf(currentOrderList.get(position).getId()),"reject");

            }
        });

        completedOrdersAdapter = new ServicesOrdersAdapter(activity, completedOrderItemList, new ServicesOrdersAdapter.OnItemSelect() {
            @Override
            public void onNotificationClicked(int position) {
                Intent intent = new Intent(activity, ReservationDetailsActivity.class);
                intent.putExtra("reserve",currentOrderList.get(position));
                startActivity(intent);
                activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }

            @Override
            public void onAcceptClicked(int position) {

            }

            @Override
            public void onRejectClicked(int position) {

            }
        });

        binding.rvCurrentOrders.setAdapter(currentOrdersAdapter);
        binding.rvCurrentOrders.setLayoutManager(new LinearLayoutManager(activity));

        binding.rvPreviousOrders.setAdapter(completedOrdersAdapter);
        binding.rvPreviousOrders.setLayoutManager(new LinearLayoutManager(activity));
    }

    private void setUpAction() {
        binding.btnNewOrders.setOnClickListener(view -> {
            if (binding.rvCurrentOrders.getVisibility()==View.GONE){
                binding.btnPreviousOrders.setBackgroundResource(R.drawable.bg_transparent);
                binding.btnNewOrders.setBackgroundResource(R.drawable.bg_button_main_shape);
                binding.btnNewOrders.setTextColor(activity.getColor(R.color.white));
                binding.btnPreviousOrders.setTextColor(activity.getColor(R.color.text));
                binding.rvPreviousOrders.setVisibility(View.GONE);
                binding.rvCurrentOrders.setVisibility(View.VISIBLE);
            }
        });

        binding.btnPreviousOrders.setOnClickListener(view -> {
            if (binding.rvPreviousOrders.getVisibility()==View.GONE){
                binding.btnNewOrders.setBackgroundResource(R.drawable.bg_transparent);
                binding.btnPreviousOrders.setBackgroundResource(R.drawable.bg_button_main_shape);
                binding.btnPreviousOrders.setTextColor(activity.getColor(R.color.white));
                binding.btnNewOrders.setTextColor(activity.getColor(R.color.text));
                binding.rvCurrentOrders.setVisibility(View.GONE);
                binding.rvPreviousOrders.setVisibility(View.VISIBLE);
            }
        });
        binding.fbAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(activity,NewReservationActivity.class));
            }
        });
        binding.swipeToRefresh.setOnRefreshListener(() -> {
            getCompletedOrders();
            getCurrentOrders();
        });
    }

    private void getCompletedOrders() {
        Map<String, Object> params = GlobalMethods.getParams(activity);
        params.put("type_reservation", OrderStatus.previous);

        Call<ResultAPIModel<List<Reservations>>> call = GlobalMethods.getApiInterface().getReservations(GlobalMethods.getHeaders(), params);
        binding.rvPreviousOrders.showShimmerAdapter();
        call.enqueue(new Callback<ResultAPIModel<List<Reservations>>>() {
            @Override
            public void onResponse(Call<ResultAPIModel<List<Reservations>>> call, Response<ResultAPIModel<List<Reservations>>> response) {
                ResultAPIModel<List<Reservations>> result = response.body();
                binding.rvPreviousOrders.hideShimmerAdapter();
                if (binding.swipeToRefresh.isRefreshing())binding.swipeToRefresh.setRefreshing(false);
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
            public void onFailure(Call<ResultAPIModel<List<Reservations>>> call, Throwable t) {
                t.printStackTrace();
                if (t instanceof UnknownHostException || t instanceof NoRouteToHostException) {
                    NoConnectionDialog.with(activity);
                }
            }
        });

    }


    private void getCurrentOrders() {
        Map<String, Object> params = GlobalMethods.getParams(activity);
        params.put("type_reservation", OrderStatus.current);

        Call<ResultAPIModel<List<Reservations>>> call = GlobalMethods.getApiInterface().getReservations(GlobalMethods.getHeaders(), params);
        binding.rvCurrentOrders.showShimmerAdapter();
        call.enqueue(new Callback<ResultAPIModel<List<Reservations>>>() {
            @Override
            public void onResponse(Call<ResultAPIModel<List<Reservations>>> call, Response<ResultAPIModel<List<Reservations>>> response) {
                ResultAPIModel<List<Reservations>> result = response.body();
                binding.rvCurrentOrders.hideShimmerAdapter();
                if (binding.swipeToRefresh.isRefreshing())binding.swipeToRefresh.setRefreshing(false);
                if (response.isSuccessful()) {
                    assert result != null;
                    if (result.success == Constant.Status_successful) {
                        GlobalMethods.printGson("getCurrentOrders", result.data);
                        currentOrderList.clear();
                        currentOrderList.addAll(result.data);
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
            public void onFailure(Call<ResultAPIModel<List<Reservations>>> call, Throwable t) {
                t.printStackTrace();
                if (t instanceof UnknownHostException || t instanceof NoRouteToHostException) {
                    NoConnectionDialog.with(activity);
                }
            }
        });

    }

    private void reservationStatus(String reservation_id,String store_status) {
        Map<String, Object> params = GlobalMethods.getParams(activity);
        params.put("reservation_id", reservation_id);
        params.put("store_status", store_status);
        Call<ResultAPIModel<Reservations>> call = GlobalMethods.getApiInterface().reservationStatus(GlobalMethods.getHeaders(),params);
        pd.show();
        call.enqueue(new Callback<ResultAPIModel<Reservations>>() {
            @Override
            public void onResponse(Call<ResultAPIModel<Reservations>> call, Response<ResultAPIModel<Reservations>> response) {
                ResultAPIModel<Reservations> result = response.body();
                pd.dismiss();
                if (response.isSuccessful()) {
                    assert result != null;
                    if (result.success == Constant.Status_successful) {
                        GlobalMethods.printGson("orderActive", result.data);
                        getCurrentOrders();
                        getCompletedOrders();

                    } else {
                        GlobalMethods.showErrorToast(activity, result.message);

                    }

                } else {
                    ErrorModel errorModel =null;
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
            public void onFailure(Call<ResultAPIModel<Reservations>> call, Throwable t) {
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