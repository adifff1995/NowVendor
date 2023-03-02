package com.app.radarvendor.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.app.radarvendor.Activities.EditProfileActivity;
import com.app.radarvendor.Activities.MainActivity;
import com.app.radarvendor.Activities.MainServicesActivity;
import com.app.radarvendor.Module.ErrorModel;
import com.app.radarvendor.Module.ResultAPIModel;
import com.app.radarvendor.Module.Vendor;
import com.app.radarvendor.R;
import com.app.radarvendor.Utils.Constant;
import com.app.radarvendor.Utils.GlobalMethods;
import com.app.radarvendor.Utils.NoConnectionDialog;
import com.app.radarvendor.databinding.FragmentMyAccountBinding;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.net.NoRouteToHostException;
import java.net.UnknownHostException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyAccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyAccountFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FragmentMyAccountBinding binding;
    private Activity activity;
    private Vendor vendor;

    public MyAccountFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyAccountFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyAccountFragment newInstance(String param1, String param2) {
        MyAccountFragment fragment = new MyAccountFragment();
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
        binding = FragmentMyAccountBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpFragment();
        setUpAction();
    }


    private void setUpFragment() {
        if (GlobalMethods.getAccountType() == 1)
            binding.tvMyProducts.setText(getString(R.string.my_products));
        else
            binding.tvMyProducts.setText(getString(R.string.my_services));

    }

    @Override
    public void onResume() {
        super.onResume();
        getProfile();
    }

    private void setUpAction() {
        binding.cardPersonalProfile.setOnClickListener(view -> {
            Intent intent = new Intent(activity, EditProfileActivity.class);
            intent.putExtra("profile", vendor);
            startActivity(intent);
            activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        binding.tvMyProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                if (GlobalMethods.getAccountType() == 1) {
                    intent = new Intent(activity, MainActivity.class);
                    intent.putExtra("products", 0);
                } else {
                    intent = new Intent(activity, MainServicesActivity.class);
                    intent.putExtra("bookings", 0);
                }

                startActivity(intent);
                activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                activity.finish();
            }
        });
    }

    private void getProfile() {
        Call<ResultAPIModel<Vendor>> call = GlobalMethods.getApiInterface().getProfile(GlobalMethods.getHeaders());
        binding.progress.setVisibility(View.VISIBLE);
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        call.enqueue(new Callback<ResultAPIModel<Vendor>>() {
            @Override
            public void onResponse(Call<ResultAPIModel<Vendor>> call, Response<ResultAPIModel<Vendor>> response) {
                ResultAPIModel<Vendor> result = response.body();
                activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                binding.progress.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    assert result != null;
                    if (result.success == Constant.Status_successful) {
                        vendor = result.data;
                        GlobalMethods.printGson("getProfile",vendor);
                        binding.tvName.setText(result.data.getName());
                        binding.tvAddress.setText(result.data.getEmail());
                        CircularProgressDrawable circularProgressDrawable = GlobalMethods.getPlaceHolder(activity);
                        circularProgressDrawable.start();
                        if (result.data.getIcon_url() != null)
                            Glide.with(activity).load(vendor.getIcon_url()).placeholder(circularProgressDrawable).into(binding.img);

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