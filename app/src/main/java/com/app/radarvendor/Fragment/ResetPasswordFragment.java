package com.app.radarvendor.Fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
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
import com.app.radarvendor.databinding.FragmentResetPasswordBinding;
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
 * Use the {@link ResetPasswordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResetPasswordFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String phone;
    private String mParam2;
    private FragmentResetPasswordBinding binding;
    private Activity activity;

    public ResetPasswordFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ResetPasswordFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ResetPasswordFragment newInstance(String param1, String param2) {
        ResetPasswordFragment fragment = new ResetPasswordFragment();
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
            phone = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentResetPasswordBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpFragment();
        setUpAction();
    }


    private void setUpFragment() {
        binding.showPass1.setTag(true);
        binding.showPass2.setTag(true);
        ProgressButtonHolderKt.bindProgressButton(this, binding.btnReset);
        ButtonTextAnimatorExtensionsKt.attachTextChangeAnimator(binding.btnReset);
    }

    private void setUpAction() {
        binding.edCode.setOnFocusChangeListener((view, b) -> {
            if (b) {
                binding.edCode.setBackgroundResource(R.drawable.bg_button_bordered_app);
            } else {
                binding.edCode.setBackgroundResource(R.drawable.bg_button_secondry_shape);
            }
        });

        binding.edPassword.setOnFocusChangeListener((view, b) -> {
            if (b) {
                binding.edPassword.setBackgroundResource(R.drawable.bg_button_bordered_app);
            } else {
                binding.edPassword.setBackgroundResource(R.drawable.bg_button_secondry_shape);
            }
        });

        binding.edConfirmPass.setOnFocusChangeListener((view, b) -> {
            if (b) {
                binding.edConfirmPass.setBackgroundResource(R.drawable.bg_button_bordered_app);
            } else {
                binding.edConfirmPass.setBackgroundResource(R.drawable.bg_button_secondry_shape);
            }
        });


        binding.imgBack.setOnClickListener(view -> getParentFragmentManager().popBackStack());

        binding.btnReset.setOnClickListener(view -> {
            if (GlobalMethods.setEditTextEmptyError(activity, binding.edCode) &&
                    GlobalMethods.setEditTextEmptyError(activity, binding.edPassword) &&
                    GlobalMethods.setEditTextEmptyError(activity, binding.edConfirmPass)
            ) {
                passwordReset(phone, binding.edCode.getText().toString(),
                        binding.edPassword.getText().toString(),
                        binding.edConfirmPass.getText().toString()
                );
            }
        });

        binding.showPass1.setOnClickListener(v -> {
            boolean b = (boolean) binding.showPass1.getTag();
            if (b) {
                binding.showPass1.setImageResource(R.drawable.ic_eye);
                binding.edPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                binding.edPassword.setSelection(binding.edPassword.length());
                binding.showPass1.setTag(false);
            } else {
                binding.showPass1.setImageResource(R.drawable.ic_baseline_remove_red_eye_24);
                binding.edPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                binding.edPassword.setSelection(binding.edPassword.length());
                binding.showPass1.setTag(true);
            }
        });


        binding.showPass2.setOnClickListener(v -> {
            boolean b = (boolean) binding.showPass2.getTag();
            if (b) {
                binding.showPass2.setImageResource(R.drawable.ic_eye);
                binding.edConfirmPass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                binding.edConfirmPass.setSelection(binding.edConfirmPass.length());
                binding.showPass2.setTag(false);
            } else {
                binding.showPass2.setImageResource(R.drawable.ic_baseline_remove_red_eye_24);
                binding.edConfirmPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                binding.edConfirmPass.setSelection(binding.edConfirmPass.length());
                binding.showPass2.setTag(true);
            }
        });
    }

    private void passwordReset(String mobile, String code, String new_password, String new_password_confirmation) {
        Map<String, Object> params = GlobalMethods.getParams(activity);
        params.put("phone", mobile);
        params.put("code", code);
        params.put("new_password", new_password);
        params.put("new_password_confirmation", new_password_confirmation);

        Call<ResultAPIModel> call = GlobalMethods.getApiInterface().passwordReset(params);
        GlobalMethods.printGson("verify", params);
        GlobalMethods.showProgress(binding.btnReset);
        call.enqueue(new Callback<ResultAPIModel>() {
            @Override
            public void onResponse(Call<ResultAPIModel> call, Response<ResultAPIModel> response) {
                ResultAPIModel result = response.body();
                binding.btnReset.setEnabled(true);
                DrawableButtonExtensionsKt.hideProgress(binding.btnReset, R.string.reset);
                if (response.isSuccessful()) {
                    assert result != null;
                    if (result.success == Constant.Status_successful) {
                        GlobalMethods.showSuccessToast(activity, result.message);
                        FragmentUtil.addFragmentWithBackStack(activity, new LoginFragment(), R.id.boarding, "");

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