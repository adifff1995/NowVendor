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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.radarvendor.Activities.AddOffersActivity;
import com.app.radarvendor.Adapter.OffersAdapter;
import com.app.radarvendor.Module.ErrorModel;
import com.app.radarvendor.Module.Offers;
import com.app.radarvendor.Module.ResultAPIModel;
import com.app.radarvendor.R;
import com.app.radarvendor.Utils.Constant;
import com.app.radarvendor.Utils.GlobalMethods;
import com.app.radarvendor.Utils.NoConnectionDialog;
import com.app.radarvendor.databinding.FragmentHomeBinding;
import com.app.radarvendor.databinding.FragmentOffersBinding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.net.NoRouteToHostException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OffersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OffersFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OffersAdapter offersAdapter;
    private List<Offers> offersList;
    private FragmentOffersBinding binding;
    private Activity activity;

    public OffersFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OffersFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OffersFragment newInstance(String param1, String param2) {
        OffersFragment fragment = new OffersFragment();
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
        binding = FragmentOffersBinding.inflate(inflater, container, false);
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
        getOffers();
    }

    private void setUpFragment() {
        offersList = new ArrayList<>();
        offersAdapter = new OffersAdapter(activity, offersList, position -> {

        });
        binding.rvOffers.setAdapter(offersAdapter);
        binding.rvOffers.setLayoutManager(new GridLayoutManager(activity, 2));
    }

    private void setUpAction() {
        binding.btnAddOffer.setOnClickListener(view -> {
            Intent intent = new Intent(activity, AddOffersActivity.class);
            startActivity(intent);
            activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });
    }

    private void getOffers() {
        Call<ResultAPIModel<List<Offers>>> call = GlobalMethods.getApiInterface().getOffers(GlobalMethods.getHeaders());
        binding.rvOffers.showShimmerAdapter();
        call.enqueue(new Callback<ResultAPIModel<List<Offers>>>() {
            @Override
            public void onResponse(Call<ResultAPIModel<List<Offers>>> call, Response<ResultAPIModel<List<Offers>>> response) {
                ResultAPIModel<List<Offers>> result = response.body();
                binding.rvOffers.hideShimmerAdapter();
                if (response.isSuccessful()) {
                    assert result != null;
                    if (result.success == Constant.Status_successful) {
                        offersList.clear();
                        GlobalMethods.printGson("getTags", result.data);
                        offersList.addAll(result.data);
                        offersAdapter.notifyDataSetChanged();

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
            public void onFailure(Call<ResultAPIModel<List<Offers>>> call, Throwable t) {
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