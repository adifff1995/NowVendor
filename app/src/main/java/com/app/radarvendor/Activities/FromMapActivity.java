package com.app.radarvendor.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;

import com.app.radarvendor.Fragment.AboutAppFragment;
import com.app.radarvendor.Fragment.ContactUsFragment;
import com.app.radarvendor.Fragment.HomeFragment;
import com.app.radarvendor.Fragment.MyDeliveryAccountFragment;
import com.app.radarvendor.Fragment.NotificationsFragment;
import com.app.radarvendor.Fragment.SettingsFragment;
import com.app.radarvendor.Fragment.ViewDeliveryOrdersFragment;
import com.app.radarvendor.R;
import com.app.radarvendor.Utils.FragmentUtil;
import com.app.radarvendor.databinding.ActivityEditDeliveryProfileBinding;
import com.app.radarvendor.databinding.ActivityFromMapBinding;

public class FromMapActivity extends AppCompatActivity {

    private Context context = FromMapActivity.this;
    private ActivityFromMapBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFromMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (getIntent().hasExtra("flag"))
            switch (getIntent().getIntExtra("flag", -1)) {
                case 1:
                    FragmentUtil.addFragment(context, new HomeFragment(), R.id.main_map);
                    break;
                case 2:
                    FragmentUtil.addFragment(context, new ViewDeliveryOrdersFragment(), R.id.main_map);
                    break;
                case 3:
                    FragmentUtil.addFragment(context, new MyDeliveryAccountFragment(), R.id.main_map, "");
                    break;

                case 4:
                    FragmentUtil.addFragment(context, new SettingsFragment(), R.id.main_map, "");
                    break;
                case 5:
                    FragmentUtil.addFragment(context, new NotificationsFragment(), R.id.main_map, "");
                    break;
                case 6:
                    FragmentUtil.addFragment(context, new ContactUsFragment(), R.id.main_map, "");
                    break;
                case 7:
                    FragmentUtil.addFragment(context, AboutAppFragment.newInstance(1), R.id.main_map, "");
                    break;
                case 8:
                    FragmentUtil.addFragment(context, AboutAppFragment.newInstance(2), R.id.main, "");
                    break;


            }

        binding.imgBack.setOnClickListener(view -> finish());
    }
}