package com.app.radarvendor.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.InputType;
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
import com.app.radarvendor.Utils.FragmentUtil;
import com.app.radarvendor.Utils.GlobalMethods;
import com.app.radarvendor.Utils.NoConnectionDialog;
import com.app.radarvendor.databinding.FragmentLoginBinding;
import com.app.radarvendor.databinding.FragmentLoginDeliveryBinding;
import com.github.razir.progressbutton.ButtonTextAnimatorExtensionsKt;
import com.github.razir.progressbutton.DrawableButtonExtensionsKt;
import com.github.razir.progressbutton.ProgressButtonHolderKt;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.hawk.Hawk;
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener;

import java.net.NoRouteToHostException;
import java.net.UnknownHostException;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginDeliveryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginDeliveryFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FragmentLoginDeliveryBinding binding;
    private Activity activity;
    private String phoneCode = "972";
    private double lat ,lng;
    public LoginDeliveryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginDeliveryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginDeliveryFragment newInstance(String param1, String param2) {
        LoginDeliveryFragment fragment = new LoginDeliveryFragment();
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
        binding = FragmentLoginDeliveryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpFragment();
        setUpAction();
    }

    private void setUpFragment() {
        lat = Hawk.get(Constant.lat);
        lng = Hawk.get(Constant.lng);
        ProgressButtonHolderKt.bindProgressButton(this, binding.btnLogin);
        ButtonTextAnimatorExtensionsKt.attachTextChangeAnimator(binding.btnLogin);
        binding.showPass1.setTag(true);
        binding.spPhone.selectItemByIndex(0);
        binding.spPhone.setLifecycleOwner(this);
        binding.spPhone.setSpinnerOutsideTouchListener((view, motionEvent) -> {
            if (binding.spPhone.isShowing())
                binding.spPhone.dismiss();
        });

    }

    private void setUpAction() {
        binding.edPhone.setOnFocusChangeListener((view, b) -> {
            if (b) {
                binding.edPhone.setBackgroundResource(R.drawable.bg_button_bordered_app);
            } else {
                binding.edPhone.setBackgroundResource(R.drawable.bg_button_secondry_shape);

            }
        });
        binding.edPassword.setOnFocusChangeListener((view, b) -> {
            if (b) {
                binding.edPassword.setBackgroundResource(R.drawable.bg_button_bordered_app);
            } else {
                binding.edPassword.setBackgroundResource(R.drawable.bg_button_secondry_shape);

            }
        });

        binding.btnLogin.setOnClickListener(view -> {
            if (GlobalMethods.setEditTextEmptyError(activity, binding.edPhone) &&
                    GlobalMethods.setEditTextEmptyError(activity, binding.edPassword)
            ) {
                FirebaseMessaging.getInstance().getToken().addOnSuccessListener(s -> {
                    login(binding.edPhone.getText().toString()
                            , binding.edPassword.getText().toString(),s);
                });


            }
        });
        binding.tvCreateAccount.setOnClickListener(view -> FragmentUtil.addFragmentWithBackStack(activity, new ChooseAccountType1Fragment(), R.id.boarding, ""));
        binding.tvForgetPassword.setOnClickListener(view -> FragmentUtil.addFragmentWithBackStack(activity, new ForgetPasswordFragment(), R.id.boarding, ""));

        binding.imgBack.setOnClickListener(view -> getParentFragmentManager().popBackStack());

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

        binding.spPhone.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener<Object>() {
            @Override
            public void onItemSelected(int i, @Nullable Object o, int i1, Object t1) {
                if (i1 == 0)
                    phoneCode = "972";
                else
                    phoneCode = "970";
            }
        });
    }

    private void login(String mobile, String password,String fcm) {
        Map<String, Object> params = GlobalMethods.getParams(activity);
        params.put("phone", phoneCode + mobile);
        params.put("password", password);
        params.put("fcm", fcm);
        params.put("longitude", String.valueOf(lat));
        params.put("latitude", String.valueOf(lng));
//        params.put("fcm", "1");


        Call<ResultAPIModel<Vendor>> call = GlobalMethods.getApiInterface().loginDelivery(params);
        GlobalMethods.printGson("login", params);
        GlobalMethods.showProgress(binding.btnLogin);
        call.enqueue(new Callback<ResultAPIModel<Vendor>>() {
            @Override
            public void onResponse(Call<ResultAPIModel<Vendor>> call, Response<ResultAPIModel<Vendor>> response) {
                ResultAPIModel<Vendor> result = response.body();
                binding.btnLogin.setEnabled(true);
                DrawableButtonExtensionsKt.hideProgress(binding.btnLogin, R.string.login);
                if (response.isSuccessful()) {
                    assert result != null;
                    if (result.success == Constant.Status_successful) {
                        GlobalMethods.printGson("loginAdi", result.data);
//                        Hawk.put(Constant.accountType, Integer.parseInt(result.data.getType()));
                        if (result.data.getVerified()) {
                            GlobalMethods.showSuccessToast(activity, result.message);
                            Hawk.put(Constant.delivery, result.data);
                            Hawk.put(Constant.token, result.data.getToken());
                            Hawk.put(Constant.isLogin, true);
                            startActivity(new Intent(activity, MainDeliveryActivity.class));
                            activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            activity.finish();
                        } else {
                            GlobalMethods.showSuccessToast(activity, result.message);
                            FragmentUtil.addFragmentWithBackStack(activity, VerifyCodeForDeliveryFragment.newInstance(phoneCode+mobile, ""), R.id.boarding, "");
                        }

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