package com.app.radarvendor.Fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.app.radarvendor.Adapter.NotificationAdapter;
import com.app.radarvendor.Module.ErrorModel;
import com.app.radarvendor.Module.NotificationItem;
import com.app.radarvendor.Module.ResultAPIModel;
import com.app.radarvendor.R;
import com.app.radarvendor.Utils.Constant;
import com.app.radarvendor.Utils.FragmentUtil;
import com.app.radarvendor.Utils.GlobalMethods;
import com.app.radarvendor.Utils.NoConnectionDialog;
import com.app.radarvendor.databinding.FragmentNotificationsBinding;
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
 * Use the {@link NotificationsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotificationsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FragmentNotificationsBinding binding;
    private Activity activity;
    private List<NotificationItem> notificationItemList;
    private NotificationAdapter notificationAdapter;

    public NotificationsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NotificationsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NotificationsFragment newInstance(String param1, String param2) {
        NotificationsFragment fragment = new NotificationsFragment();
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
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpFragment();
        setUpAction();
        getNotifications();

    }

    private void setUpFragment() {
        notificationItemList = new ArrayList<>();
        notificationAdapter = new NotificationAdapter(activity, notificationItemList, position -> {
            readNotification(String.valueOf(notificationItemList.get(position).getId()),position,notificationItemList.get(position).getType());
        });

        binding.rvNotification.setAdapter(notificationAdapter);
        binding.rvNotification.setLayoutManager(new LinearLayoutManager(activity));
        binding.swipeToRefresh.setOnRefreshListener(this::getNotifications);

    }

    private void setUpAction() {

    }

    private void getNotifications() {
        Call<ResultAPIModel<List<NotificationItem>>> call = GlobalMethods.getApiInterface().getNotifications(GlobalMethods.getHeaders());
        binding.rvNotification.showShimmerAdapter();
        call.enqueue(new Callback<ResultAPIModel<List<NotificationItem>>>() {
            @Override
            public void onResponse(Call<ResultAPIModel<List<NotificationItem>>> call, Response<ResultAPIModel<List<NotificationItem>>> response) {
                ResultAPIModel<List<NotificationItem>> result = response.body();
                binding.rvNotification.hideShimmerAdapter();
                if (binding.swipeToRefresh.isRefreshing())
                    binding.swipeToRefresh.setRefreshing(false);
                if (response.isSuccessful()) {
                    assert result != null;
                    if (result.success == Constant.Status_successful) {
                        notificationItemList.clear();
                        notificationItemList.addAll(result.data);
                        notificationAdapter.notifyDataSetChanged();
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
            public void onFailure(Call<ResultAPIModel<List<NotificationItem>>> call, Throwable t) {
                t.printStackTrace();
                if (t instanceof UnknownHostException || t instanceof NoRouteToHostException) {
                    NoConnectionDialog.with(activity);
                }
            }
        });

    }

    private void readNotification(String notification_id,int position,int type) {
        Map<String, Object> params = GlobalMethods.getParams(activity);
        params.put("Notification_id", notification_id);

        Call<ResultAPIModel> call = GlobalMethods.getApiInterface().readNotification(GlobalMethods.getHeaders(), params);
        call.enqueue(new Callback<ResultAPIModel>() {
            @Override
            public void onResponse(Call<ResultAPIModel> call, Response<ResultAPIModel> response) {
                ResultAPIModel result = response.body();
                if (response.isSuccessful()) {
                    assert result != null;
                    if (result.success == Constant.Status_successful) {
                        GlobalMethods.showSuccessToast(activity, result.message);
                        notificationItemList.get(position).setIs_read(true);
                        notificationAdapter.notifyDataSetChanged();
                        switch (type) {
                            case 1:
                                FragmentUtil.replaceFragmentWithBackStack(activity, new HomeServiceFragment(), R.id.main, "");
                                break;
                            case 2:
                                FragmentUtil.replaceFragmentWithBackStack(activity, new HomeFragment(), R.id.main, "");

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
            public void onFailure(Call<ResultAPIModel> call, Throwable t) {
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