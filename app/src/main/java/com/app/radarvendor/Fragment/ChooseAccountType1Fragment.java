package com.app.radarvendor.Fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.radarvendor.R;
import com.app.radarvendor.Utils.Constant;
import com.app.radarvendor.Utils.FragmentUtil;
import com.app.radarvendor.databinding.FragmentChooseAccountTypeBinding;
import com.app.radarvendor.databinding.FragmentContactUsBinding;
import com.orhanobut.hawk.Hawk;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChooseAccountType1Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChooseAccountType1Fragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FragmentChooseAccountTypeBinding binding;
    private Activity activity;
    int flag = 0;

    public ChooseAccountType1Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChooseAccountTypeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChooseAccountType1Fragment newInstance(String param1, String param2) {
        ChooseAccountType1Fragment fragment = new ChooseAccountType1Fragment();
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
        binding = FragmentChooseAccountTypeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpFragment();
        setUpAction();
    }

    private void setUpFragment() {
        binding.cardShop.setForeground(activity.getDrawable(R.drawable.bg_button_bordered_app));

    }

    private void setUpAction() {
        binding.cardShop.setOnClickListener(view -> {
            flag = 0;
            binding.cardDelivery.setForeground(null);
            binding.cardShop.setForeground(activity.getDrawable(R.drawable.bg_button_bordered_app));
        });

        binding.cardDelivery.setOnClickListener(view -> {
            flag = 3;
            binding.cardShop.setForeground(null);
            binding.cardDelivery.setForeground(activity.getDrawable(R.drawable.bg_button_bordered_app));
            Hawk.put(Constant.accountType, 3);
        });

        binding.btnContinue.setOnClickListener(view -> {
            if (flag == 0)
                FragmentUtil.addFragmentWithBackStack(activity, new ChooseAccountType2Fragment(), R.id.boarding, "ChooseAccountType2Fragment");
            else
                FragmentUtil.addFragmentWithBackStack(activity, new CreateAccountForDeliveryFragment(), R.id.boarding, "CreateAccountForDeliveryFragment");

        });
        binding.imgBack.setOnClickListener(view -> getParentFragmentManager().popBackStack());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            activity = (Activity) context;
        }
    }
}