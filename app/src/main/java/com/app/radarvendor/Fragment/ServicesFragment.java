package com.app.radarvendor.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.radarvendor.Activities.AddServicesActivity;
import com.app.radarvendor.Activities.ServiceDetailsActivity;
import com.app.radarvendor.Adapter.ServicesAdapter;
import com.app.radarvendor.Module.ErrorModel;
import com.app.radarvendor.Module.MessageEvent;
import com.app.radarvendor.Module.ResultAPIModel;
import com.app.radarvendor.Module.Service;
import com.app.radarvendor.R;
import com.app.radarvendor.Utils.Constant;
import com.app.radarvendor.Utils.GlobalMethods;
import com.app.radarvendor.Utils.NoConnectionDialog;
import com.app.radarvendor.databinding.FragmentProductsBinding;
import com.app.radarvendor.databinding.FragmentServicesBinding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
 * Use the {@link ServicesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ServicesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FragmentServicesBinding binding;
    private List<Service> productList;
    private ServicesAdapter productsAdapter;
    private Activity activity;

    public ServicesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ServicesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ServicesFragment newInstance(String param1, String param2) {
        ServicesFragment fragment = new ServicesFragment();
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
        binding = FragmentServicesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpFragment();
        setUpAction();
    }

    private void setUpFragment() {
        productList = new ArrayList<>();
        productsAdapter = new ServicesAdapter(activity, productList, position -> {
            Intent intent = new Intent(activity, ServiceDetailsActivity.class);
            intent.putExtra("id",productList.get(position).getId());
            startActivity(intent);
            activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });
        binding.rvServices.setAdapter(productsAdapter);
        binding.rvServices.setLayoutManager(new GridLayoutManager(activity, 2));

        binding.swipToRefresh.setOnRefreshListener(() -> getServices());

    }

    private void setUpAction() {
        binding.fbAdd.setOnClickListener(view -> {
            startActivity(new Intent(activity, AddServicesActivity.class));
            activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        binding.edSearch.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                searchServices(binding.edSearch.getText().toString());
                return true;
            }
            return false;
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getServices();
    }


    private void getServices() {
        Call<ResultAPIModel<List<Service>>> call = GlobalMethods.getApiInterface().getServices(GlobalMethods.getHeaders());
        binding.rvServices.showShimmerAdapter();
        call.enqueue(new Callback<ResultAPIModel<List<Service>>>() {
            @Override
            public void onResponse(Call<ResultAPIModel<List<Service>>> call, Response<ResultAPIModel<List<Service>>> response) {
                ResultAPIModel<List<Service>> result = response.body();
                binding.rvServices.hideShimmerAdapter();
                if (binding.swipToRefresh.isRefreshing())
                    binding.swipToRefresh.setRefreshing(false);
                if (response.isSuccessful()) {
                    assert result != null;
                    if (result.success == Constant.Status_successful) {
                        productList.clear();
                        productList.addAll(result.data);
                        productsAdapter.notifyDataSetChanged();
                        binding.edSearch.setText("");
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
            public void onFailure(Call<ResultAPIModel<List<Service>>> call, Throwable t) {
                t.printStackTrace();
                if (t instanceof UnknownHostException || t instanceof NoRouteToHostException) {
                    NoConnectionDialog.with(activity);
                }
            }
        });

    }

    private void searchServices(String name) {
        Map<String, Object> params = GlobalMethods.getParams(activity);
        params.put("name", name);
        Call<ResultAPIModel<List<Service>>> call = GlobalMethods.getApiInterface().searchServices(GlobalMethods.getHeaders(),params);
        binding.rvServices.showShimmerAdapter();
        call.enqueue(new Callback<ResultAPIModel<List<Service>>>() {
            @Override
            public void onResponse(Call<ResultAPIModel<List<Service>>> call, Response<ResultAPIModel<List<Service>>> response) {
                ResultAPIModel<List<Service>> result = response.body();
                binding.rvServices.hideShimmerAdapter();
                if (response.isSuccessful()) {
                    assert result != null;
                    if (result.success == Constant.Status_successful) {
                        productList.clear();
                        productList.addAll(result.data);
                        productsAdapter.notifyDataSetChanged();
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
            public void onFailure(Call<ResultAPIModel<List<Service>>> call, Throwable t) {
                t.printStackTrace();
                if (t instanceof UnknownHostException || t instanceof NoRouteToHostException) {
                    NoConnectionDialog.with(activity);
                }
            }
        });

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        Log.e("onMessageEvent: ", event.getFlag() + "");
        if (binding.edSearch.getVisibility() == View.VISIBLE)
            binding.edSearch.setVisibility(View.GONE);
        else
            binding.edSearch.setVisibility(View.VISIBLE);

    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            activity = (Activity) context;
        }
    }
}