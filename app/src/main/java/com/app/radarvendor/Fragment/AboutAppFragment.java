package com.app.radarvendor.Fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.app.radarvendor.Module.About;
import com.app.radarvendor.R;
import com.app.radarvendor.Utils.Constant;
import com.app.radarvendor.databinding.FragmentAboutAppBinding;
import com.orhanobut.hawk.Hawk;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AboutAppFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AboutAppFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private int flag;
    private String mParam2;
    private FragmentAboutAppBinding binding;
    private Activity activity;
    private About about;

    public AboutAppFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment AboutAppFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AboutAppFragment newInstance(int param1) {
        AboutAppFragment fragment = new AboutAppFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            flag = getArguments().getInt(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAboutAppBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpFragment();
        setUpAction();
    }

    private void setUpFragment() {
        about = Hawk.get(Constant.about);

        if (flag == 1) {
            binding.tvTitle.setText(getString(R.string.about_app));
            binding.tvBody.setText(about.getAbout_us());
        } else {
            binding.tvTitle.setText(getString(R.string.privacy_policy));
            binding.tvBody.setText(about.getPrivacy_policy());
        }


    }

    private void setUpAction() {

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            activity = (Activity) context;
        }
    }
}