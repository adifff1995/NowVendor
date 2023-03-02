package com.app.radarvendor.Fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.radarvendor.Adapter.DeliveryOrdersAdapter;
import com.app.radarvendor.Module.ErrorModel;
import com.app.radarvendor.Module.OrderResponse;
import com.app.radarvendor.Module.ResultAPIModel;
import com.app.radarvendor.R;
import com.app.radarvendor.Utils.Constant;
import com.app.radarvendor.Utils.GlobalMethods;
import com.app.radarvendor.Utils.NoConnectionDialog;
import com.app.radarvendor.databinding.FragmentViewDeliveryOrdersBinding;
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
 * Use the {@link ViewDeliveryOrdersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewDeliveryOrdersFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private List<OrderResponse> orderItemList;
    private DeliveryOrdersAdapter ordersAdapter;
    private Activity activity;
    private FragmentViewDeliveryOrdersBinding binding;

    public ViewDeliveryOrdersFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ViewDeliveryOrdersFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ViewDeliveryOrdersFragment newInstance(String param1, String param2) {
        ViewDeliveryOrdersFragment fragment = new ViewDeliveryOrdersFragment();
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
        binding = FragmentViewDeliveryOrdersBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpFragment();
        setUpAction();
        getOrders();
    }

    private void setUpFragment() {
        orderItemList = new ArrayList<>();
        ordersAdapter = new DeliveryOrdersAdapter(activity, orderItemList, position -> {
//            startActivity(new Intent(activity, OrderDetailsActivity.class));
//            activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        binding.rvOrders.setAdapter(ordersAdapter);
        binding.rvOrders.setLayoutManager(new LinearLayoutManager(activity));

    }

    private void setUpAction() {

    }

    private void getOrders() {
        Map<String, Object> params = GlobalMethods.getParams(activity);
        params.put("delivery_status", "");

        Call<ResultAPIModel<List<OrderResponse>>> call = GlobalMethods.getApiInterface().getDeliveryOrders(GlobalMethods.getHeaders(), params);
        binding.rvOrders.showShimmerAdapter();
        call.enqueue(new Callback<ResultAPIModel<List<OrderResponse>>>() {
            @Override
            public void onResponse(Call<ResultAPIModel<List<OrderResponse>>> call, Response<ResultAPIModel<List<OrderResponse>>> response) {
                ResultAPIModel<List<OrderResponse>> result = response.body();
                binding.rvOrders.hideShimmerAdapter();
                if (response.isSuccessful()) {
                    assert result != null;
                    if (result.success == Constant.Status_successful) {
                        GlobalMethods.printGson("getReservations", result.data);
                        orderItemList.clear();
                        orderItemList.addAll(result.data);
                        ordersAdapter.notifyDataSetChanged();
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



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            activity = (Activity) context;
        }
    }
}