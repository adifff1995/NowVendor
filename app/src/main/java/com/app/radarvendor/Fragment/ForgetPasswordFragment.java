package com.app.radarvendor.Fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.app.radarvendor.Utils.FragmentUtil;
import com.app.radarvendor.Utils.GlobalMethods;
import com.app.radarvendor.Utils.NoConnectionDialog;
import com.app.radarvendor.databinding.FragmentForgetPasswordBinding;
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
 * Use the {@link ForgetPasswordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ForgetPasswordFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FragmentForgetPasswordBinding binding;
    private Activity activity;
    private String phoneCode = "972";

    public ForgetPasswordFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ForgetPasswordFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ForgetPasswordFragment newInstance(String param1, String param2) {
        ForgetPasswordFragment fragment = new ForgetPasswordFragment();
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
        binding = FragmentForgetPasswordBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpFragment();
        setUpAction();
    }


    private void setUpFragment() {
        binding.spPhone.selectItemByIndex(0);
        binding.spPhone.setLifecycleOwner(this);
        binding.spPhone.setSpinnerOutsideTouchListener((view, motionEvent) -> {
            if (binding.spPhone.isShowing())
                binding.spPhone.dismiss();
        });

        ProgressButtonHolderKt.bindProgressButton(this, binding.btnSend);
        ButtonTextAnimatorExtensionsKt.attachTextChangeAnimator(binding.btnSend);
    }

    private void setUpAction() {
        binding.edPhone.setOnFocusChangeListener((view, b) -> {
            if (b) {
                binding.edPhone.setBackgroundResource(R.drawable.bg_button_bordered_app);
            } else {
                binding.edPhone.setBackgroundResource(R.drawable.bg_button_secondry_shape);
            }
        });

        binding.edPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0)
                    binding.btnSend.setBackgroundResource(R.drawable.bg_button_main_shape);
            }
        });

        binding.btnSend.setOnClickListener(view -> {
            if (GlobalMethods.setEditTextEmptyError(activity, binding.edPhone))
                resendCode(binding.edPhone.getText().toString());
        });
        binding.imgBack.setOnClickListener(view -> getParentFragmentManager().popBackStack());

        binding.spPhone.setOnSpinnerItemSelectedListener((i, o, i1, t1) -> {
            if (i1 == 0)
                phoneCode = "972";
            else
                phoneCode = "970";
        });
    }

    private void resendCode(String mobile) {
        Map<String, Object> params = GlobalMethods.getParams(activity);
        params.put("phone", phoneCode + mobile);

        Call<ResultAPIModel> call = GlobalMethods.getApiInterface().resendCode(params);
        GlobalMethods.printGson("resendCode", params);
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
                        FragmentUtil.addFragmentWithBackStack(activity, ResetPasswordFragment.newInstance(binding.edPhone.getText().toString(), ""), R.id.boarding, "");


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