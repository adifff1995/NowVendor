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

import com.app.radarvendor.Module.ErrorModel;
import com.app.radarvendor.Module.ResultAPIModel;
import com.app.radarvendor.R;
import com.app.radarvendor.Utils.Constant;
import com.app.radarvendor.Utils.GlobalMethods;
import com.app.radarvendor.Utils.NoConnectionDialog;
import com.app.radarvendor.databinding.FragmentContactUsBinding;
import com.github.razir.progressbutton.ButtonTextAnimatorExtensionsKt;
import com.github.razir.progressbutton.DrawableButtonExtensionsKt;
import com.github.razir.progressbutton.ProgressButtonHolderKt;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.net.NoRouteToHostException;
import java.net.UnknownHostException;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ContactUsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContactUsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FragmentContactUsBinding binding;
    private Activity activity;

    public ContactUsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ContactUsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ContactUsFragment newInstance(String param1, String param2) {
        ContactUsFragment fragment = new ContactUsFragment();
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
        binding = FragmentContactUsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpFragment();
        setUpAction();
    }

    private void setUpFragment() {
        ProgressButtonHolderKt.bindProgressButton(this, binding.btnSend);
        ButtonTextAnimatorExtensionsKt.attachTextChangeAnimator(binding.btnSend);
    }

    private void setUpAction() {
        binding.btnSend.setOnClickListener(view -> {
            if (GlobalMethods.setEditTextEmptyError(activity, binding.edName) &&
                    GlobalMethods.setEditTextEmptyError(activity, binding.edEmail) &&
                    GlobalMethods.setEditTextEmptyError(activity, binding.edMobile) &&
                    GlobalMethods.setEditTextEmptyError(activity, binding.edMsg)
            ) {
                contactUs(binding.edName.getText().toString(),
                        binding.edEmail.getText().toString(),
                        binding.edMobile.getText().toString(),
                        binding.edMsg.getText().toString()
                );
            }
        });
    }

    private void contactUs(String name, String email, String phone, String message) {
        Map<String, Object> params = GlobalMethods.getParams(activity);
        params.put("name", name);
        params.put("email", email);
        params.put("phone", phone);
        params.put("message", message);

        Call<ResultAPIModel> call = GlobalMethods.getApiInterface().contactUs(GlobalMethods.getHeaders(), params);
        GlobalMethods.showProgress(binding.btnSend);
        call.enqueue(new Callback<ResultAPIModel>() {
            @Override
            public void onResponse(Call<ResultAPIModel> call, Response<ResultAPIModel> response) {
                binding.btnSend.setEnabled(true);
                DrawableButtonExtensionsKt.hideProgress(binding.btnSend, R.string.send);
                ResultAPIModel result = response.body();
                if (response.isSuccessful()) {
                    assert result != null;
                    if (result.success == Constant.Status_successful) {

                        GlobalMethods.showSuccessToast(activity, result.message);
                        binding.edName.setText("");
                        binding.edEmail.setText("");
                        binding.edMobile.setText("");
                        binding.edMsg.setText("");

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