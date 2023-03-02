package com.app.radarvendor.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.radarvendor.Activities.MainDeliveryActivity;
import com.app.radarvendor.Module.ErrorModel;
import com.app.radarvendor.Module.ResultAPIModel;
import com.app.radarvendor.Module.Vendor;
import com.app.radarvendor.R;
import com.app.radarvendor.Utils.Constant;
import com.app.radarvendor.Utils.GlobalMethods;
import com.app.radarvendor.Utils.NoConnectionDialog;
import com.app.radarvendor.databinding.FragmentVerifyCodeForDeliveryBinding;
import com.github.razir.progressbutton.ButtonTextAnimatorExtensionsKt;
import com.github.razir.progressbutton.DrawableButtonExtensionsKt;
import com.github.razir.progressbutton.ProgressButtonHolderKt;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.hawk.Hawk;

import java.net.NoRouteToHostException;
import java.net.UnknownHostException;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VerifyCodeForDeliveryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VerifyCodeForDeliveryFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mobile;
    private String mParam2;
    private FragmentVerifyCodeForDeliveryBinding binding;
    private Activity activity;
    private CountDownTimer countDownTimer;

    public VerifyCodeForDeliveryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment VerifyCodeForDeliveryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VerifyCodeForDeliveryFragment newInstance(String param1, String param2) {
        VerifyCodeForDeliveryFragment fragment = new VerifyCodeForDeliveryFragment();
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
            mobile = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentVerifyCodeForDeliveryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpFragment();
        setUpAction();
        prepareTimer();
    }

    private void setUpFragment() {
        binding.ed4.requestFocus();
        binding.tvPhone.setText(mobile);
        ProgressButtonHolderKt.bindProgressButton(this, binding.btnSend);
        ButtonTextAnimatorExtensionsKt.attachTextChangeAnimator(binding.btnSend);
    }


    private void setUpAction() {
        binding.ed4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0)
                    binding.ed3.requestFocus();


            }
        });

        binding.ed3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0)
                    binding.ed2.requestFocus();

            }
        });

        binding.ed2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0)
                    binding.ed1.requestFocus();
            }
        });

        binding.ed1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    if (GlobalMethods.setEditTextEmptyError(activity, binding.ed1) &&
                            GlobalMethods.setEditTextEmptyError(activity, binding.ed2) &&
                            GlobalMethods.setEditTextEmptyError(activity, binding.ed3) &&
                            GlobalMethods.setEditTextEmptyError(activity, binding.ed4)
                    ) {
                        String code = binding.ed4.getText().toString() + binding.ed3.getText().toString()
                                + binding.ed2.getText().toString() + binding.ed1.getText().toString();

                        verify(mobile, code);

                    }
                }
            }
        });

        binding.imgBack.setOnClickListener(view -> getParentFragmentManager().popBackStack());

        binding.tvResendCode.setOnClickListener(view -> resendCode(mobile));

    }

    private void prepareTimer() {
        binding.tvResendCode.setTextColor(activity.getColor(R.color.gray));
        countDownTimer = new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                binding.tvTimer.setText(millisUntilFinished / 1000 + " ");
                binding.tvResendCode.setEnabled(false);

            }

            public void onFinish() {
                binding.tvResendCode.setEnabled(true);
                binding.tvResendCode.setTextColor(activity.getColor(R.color.pink));
            }

        }.start();
    }

    private void verify(String mobile, String code) {
        Map<String, Object> params = GlobalMethods.getParams(activity);
        params.put("phone", mobile);
        params.put("code", code);
        Call<ResultAPIModel<Vendor>> call = GlobalMethods.getApiInterface().verifyDelivery(params);
        GlobalMethods.printGson("verifyDelivery", params);
        GlobalMethods.showProgress(binding.btnSend);
        call.enqueue(new Callback<ResultAPIModel<Vendor>>() {
            @Override
            public void onResponse(Call<ResultAPIModel<Vendor>> call, Response<ResultAPIModel<Vendor>> response) {
                ResultAPIModel<Vendor> result = response.body();
                binding.btnSend.setEnabled(true);
                DrawableButtonExtensionsKt.hideProgress(binding.btnSend, R.string.send);
                if (response.isSuccessful()) {
                    assert result != null;
                    if (result.success == Constant.Status_successful) {
                        GlobalMethods.printGson("verifyAdi", result.data);
                        Hawk.put(Constant.delivery, result.data);
                        Hawk.put(Constant.token, result.data.getToken());
                        Hawk.put(Constant.isLogin, true);
                        GlobalMethods.showSuccessToast(activity, result.message);
                        startActivity(new Intent(activity, MainDeliveryActivity.class));
                        activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        activity.finish();

                    } else {
                        Log.d("onResponse: ", result.message);
                        GlobalMethods.showErrorToast(activity, result.message);
                        binding.ed1.setText("");
                        binding.ed2.setText("");
                        binding.ed3.setText("");
                        binding.ed4.setText("");
                        binding.ed1.requestFocus();
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

    private void resendCode(String mobile) {
        Map<String, Object> params = GlobalMethods.getParams(activity);
        params.put("phone", mobile);

        Call<ResultAPIModel> call = GlobalMethods.getApiInterface().resendCodeDelivery(params);
        GlobalMethods.printGson("resendCodeDelivery", params);
        GlobalMethods.showProgress(binding.btnSend);
        call.enqueue(new Callback<ResultAPIModel>() {
            @Override
            public void onResponse(Call<ResultAPIModel> call, Response<ResultAPIModel> response) {
                ResultAPIModel result = response.body();
                binding.btnSend.setEnabled(true);
                DrawableButtonExtensionsKt.hideProgress(binding.btnSend, R.string.send);
                if (response.isSuccessful()) {
                    assert result != null;
                    if (result.success == Constant.Status_successful) {
                        GlobalMethods.showSuccessToast(activity, result.message);


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