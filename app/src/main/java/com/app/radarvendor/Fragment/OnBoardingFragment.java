package com.app.radarvendor.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.radarvendor.Activities.MainActivity;
import com.app.radarvendor.Activities.MainServicesActivity;
import com.app.radarvendor.Utils.FragmentUtil;
import com.app.radarvendor.Utils.GlobalMethods;
import com.app.radarvendor.R;
import com.app.radarvendor.databinding.FragmentOnBoardingBinding;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OnBoardingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OnBoardingFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FragmentOnBoardingBinding binding;
    private Activity activity;

    public OnBoardingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OnBoardingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OnBoardingFragment newInstance(String param1, String param2) {
        OnBoardingFragment fragment = new OnBoardingFragment();
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
        binding = FragmentOnBoardingBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpFragment();
        setUpAction();
    }

    private void setUpFragment() {

    }

    private void setUpAction() {
        binding.btnSignIn.setOnClickListener(view -> FragmentUtil.replaceFragmentWithBackStack(activity, new ChooseLoginTypeFragment(), R.id.boarding, ""));
        binding.btnCreateAccount.setOnClickListener(view -> FragmentUtil.replaceFragmentWithBackStack(activity, new ChooseAccountType1Fragment(), R.id.boarding, ""));
        binding.loginVisitor.setOnClickListener(view -> {
            if (GlobalMethods.getAccountType() == 1) {
                startActivity(new Intent(activity, MainActivity.class));
            } else {
                startActivity(new Intent(activity, MainServicesActivity.class));
            }
            activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            activity.finish();
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