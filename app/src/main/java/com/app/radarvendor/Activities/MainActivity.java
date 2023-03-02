package com.app.radarvendor.Activities;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.app.radarvendor.Adapter.NavItemsAdapter;
import com.app.radarvendor.Fragment.AboutAppFragment;
import com.app.radarvendor.Fragment.ArchivedProductsFragment;
import com.app.radarvendor.Fragment.CategoriesFragment;
import com.app.radarvendor.Fragment.ContactUsFragment;
import com.app.radarvendor.Fragment.HomeFragment;
import com.app.radarvendor.Fragment.MyAccountFragment;
import com.app.radarvendor.Fragment.NotificationsFragment;
import com.app.radarvendor.Fragment.OffersFragment;
import com.app.radarvendor.Fragment.ProductsByCategoryFragment;
import com.app.radarvendor.Fragment.ProductsFragment;
import com.app.radarvendor.Fragment.RatingsFragment;
import com.app.radarvendor.Fragment.SettingsFragment;
import com.app.radarvendor.Module.ErrorModel;
import com.app.radarvendor.Module.MessageEvent;
import com.app.radarvendor.Module.NavItems;
import com.app.radarvendor.Module.ResultAPIModel;
import com.app.radarvendor.Module.Vendor;
import com.app.radarvendor.R;
import com.app.radarvendor.Utils.Constant;
import com.app.radarvendor.Utils.FragmentUtil;
import com.app.radarvendor.Utils.GlobalMethods;
import com.app.radarvendor.Utils.NoConnectionDialog;
import com.app.radarvendor.databinding.ActivityMainBinding;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.hawk.Hawk;

import org.greenrobot.eventbus.EventBus;

import java.net.NoRouteToHostException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static ActivityMainBinding binding;
    private List<NavItems> navItemsList;
    private Context context = MainActivity.this;
    private NavItemsAdapter navItemsAdapter;
    private AlertDialog myDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        setUpNavigationDrawer();
        setUpActivity();
        setUpAction();
        getProfile();
    }

    private void setUpActivity() {
        setMainTitle(getString(R.string.my_orders));

        binding.bottomNav.getMenu().clear();
        binding.bottomNav.inflateMenu(R.menu.bottom_nav_menu);

    }

    private void setUpAction() {
        binding.imgBack.setOnClickListener(view -> getSupportFragmentManager().popBackStack());

//        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
//            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.main);
//
//            if (fragment != null) {
//                if (fragment instanceof HomeFragment) {
//                    binding.appTool.tvMainTitle.setText(R.string.home);
//                    hideShopToolbarItems();
//                    showNavDrawer();
//                } else if (fragment instanceof CategoriesFragment) {
//                    showShopToolbarItems();
//                } else if (fragment instanceof ShopsFragment) {
//                    showShopToolbarItems();
//                }
//            }
//        });

        binding.bottomNav.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    showSearch();
                    setMainTitle(getString(R.string.my_orders));
                    FragmentUtil.replaceFragmentWithBackStack(context, new HomeFragment(), R.id.main, "homeFragment");
                    break;

                case R.id.navigation_ratings:
                    showSearch();
                    setMainTitle(getString(R.string.ratings));
                    FragmentUtil.replaceFragmentWithBackStack(context, new RatingsFragment(), R.id.main, "productsFragment");
                    break;

                case R.id.navigation_categories:
                    showSearch();
                    setMainTitle(getString(R.string.categories));
                    FragmentUtil.replaceFragmentWithBackStack(context, new CategoriesFragment(), R.id.main, "categoriesFragment");
                    break;
                case R.id.navigation_profile:
                    hideSearch();
                    setMainTitle(getString(R.string.profile));
                    FragmentUtil.replaceFragmentWithBackStack(context, new MyAccountFragment(), R.id.main, "myAccountFragment");
                    break;
            }
            return true;
        });

        binding.imgSearch.setOnClickListener(view -> {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.main);
            if (fragment != null) {
                if (fragment instanceof CategoriesFragment) {
                    EventBus.getDefault().post(new MessageEvent(0));
                } else if (fragment instanceof ProductsFragment) {
                    EventBus.getDefault().post(new MessageEvent(0));
                } else if (fragment instanceof ArchivedProductsFragment) {
                    EventBus.getDefault().post(new MessageEvent(0));
                } else if (fragment instanceof ProductsByCategoryFragment) {
                    EventBus.getDefault().post(new MessageEvent(0));
                }
            }
        });

        binding.imgNotifications.setOnClickListener(view -> FragmentUtil.addFragmentWithBackStack(context, new NotificationsFragment(), R.id.main, "notificationsFragment"));

    }

    public static void setMainTitle(String title) {
        binding.tvMainTitle.setText(title);
    }

    public static void hideSearch() {
        binding.imgSearch.setVisibility(View.GONE);
    }

    public static void showSearch() {
        binding.imgSearch.setVisibility(View.VISIBLE);
    }


    private void setUpNavigationDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, binding.drawerLayout, binding.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        binding.drawerLayout.addDrawerListener(toggle);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.setDrawerSlideAnimationEnabled(true);
        toggle.syncState();
//        binding.appTool.toolbar.setNavigationIcon(R.drawable.ic_menu_icon);

//        toggle.setToolbarNavigationClickListener(v -> {
//            if (binding.drawerLayout.isDrawerVisible(GravityCompat.START)) {
//                binding.drawerLayout.closeDrawer(GravityCompat.START);
//            } else {
//                binding.drawerLayout.openDrawer(GravityCompat.START);
//            }
//        });

        navItemsList = new ArrayList<>();
        navItemsList.add(new NavItems("1", getString(R.string.home), R.drawable.ic_iconly_light_home));
        navItemsList.add(new NavItems("2", getString(R.string.archived_products), R.drawable.ic_book_outlined));
        navItemsList.add(new NavItems("9", getString(R.string.offers), R.drawable.ic_offer));
        navItemsList.add(new NavItems("3", getString(R.string.settings), R.drawable.ic_settings_outlined));
        navItemsList.add(new NavItems("4", getString(R.string.notifications), R.drawable.ic_notifications_outlined_gray));
        navItemsList.add(new NavItems("5", getString(R.string.contact_us), R.drawable.ic_call_us));
        navItemsList.add(new NavItems("6", getString(R.string.about_app), R.drawable.ic_error_outlined));
        navItemsList.add(new NavItems("7", getString(R.string.privacy_policy), R.drawable.ic_settings_outlined));
        navItemsList.add(new NavItems("8", getString(R.string.logout), R.drawable.ic_sign_out));

        navItemsAdapter = new NavItemsAdapter(context, navItemsList, new NavItemsAdapter.OnItemSelect() {
            @Override
            public void onOrderClicked(int position) {
                switch (navItemsList.get(position).getId()) {
                    case "1":
                        setMainTitle(getString(R.string.home));
                        FragmentUtil.addFragment(context, new HomeFragment(), R.id.main);
                        break;
                    case "2":
                        setMainTitle(getString(R.string.archived_products));
                        FragmentUtil.addFragment(context, new ArchivedProductsFragment(), R.id.main);
                        break;
                    case "3":
                        setMainTitle(getString(R.string.profile));
                        hideSearch();
                        FragmentUtil.replaceFragmentWithBackStack(context, new SettingsFragment(), R.id.main, "SettingsFragment");
                        break;
                    case "4":
                        setMainTitle(getString(R.string.notifications));
                        hideSearch();
                        FragmentUtil.replaceFragmentWithBackStack(context, new NotificationsFragment(), R.id.main, "notificationsFragment");
                        break;
                    case "5":
                        setMainTitle(getString(R.string.contact_us));
                        hideSearch();
                        FragmentUtil.replaceFragmentWithBackStack(context, new ContactUsFragment(), R.id.main, "ContactUsFragment");
                        break;
                    case "6":
                        setMainTitle(getString(R.string.about_app));
                        hideSearch();
                        FragmentUtil.replaceFragmentWithBackStack(context, AboutAppFragment.newInstance(1), R.id.main, "AboutAppFragment");
                        break;
                    case "7":
                        setMainTitle(getString(R.string.privacy_policy));
                        hideSearch();
                        FragmentUtil.replaceFragmentWithBackStack(context, AboutAppFragment.newInstance(2), R.id.main, "AboutAppFragment");
                        break;
                    case "8":
                        showWarningDialog();
                        break;

                    case "9":
                        setMainTitle(getString(R.string.offers));
                        hideSearch();
                        FragmentUtil.replaceFragmentWithBackStack(context, new OffersFragment(), R.id.main, "ContactUsFragment");
                        break;
                }

                binding.drawerLayout.closeDrawer(GravityCompat.START);

            }
        });

        binding.navDrawer.rvNavItems.setAdapter(navItemsAdapter);
        binding.navDrawer.rvNavItems.setLayoutManager(new LinearLayoutManager(context));

    }

    private void getProfile() {
        Call<ResultAPIModel<Vendor>> call = GlobalMethods.getApiInterface().getProfile(GlobalMethods.getHeaders());
        call.enqueue(new Callback<ResultAPIModel<Vendor>>() {
            @Override
            public void onResponse(Call<ResultAPIModel<Vendor>> call, Response<ResultAPIModel<Vendor>> response) {
                ResultAPIModel<Vendor> result = response.body();
                if (response.isSuccessful()) {
                    assert result != null;
                    if (result.success == Constant.Status_successful) {
                        Hawk.put(Constant.profile, result.data);
                        binding.navDrawer.tvUsername.setText(result.data.getName());
                        if (result.data.getIcon_url() != null)
                            Glide.with(context).load(result.data.getIcon_url()).into(binding.navDrawer.imageProfile);

                        if (getIntent().hasExtra("products")) {
                            FragmentUtil.addFragment(context, new HomeFragment(), R.id.main, "homeFragment");
                            binding.bottomNav.setSelectedItemId(R.id.navigation_categories);
                            FragmentUtil.replaceFragmentWithBackStack(context, new CategoriesFragment(), R.id.main, "productsFragment");
                        } else
                            FragmentUtil.addFragment(context, new HomeFragment(), R.id.main, "homeFragment");


                    } else {
                        Log.d("onResponse: ", result.message);
                        GlobalMethods.showErrorToast(MainActivity.this, result.message);
                    }

                } else {
                    ErrorModel errorModel = null;
                    try {
                        assert response.errorBody() != null;
                        String error = response.errorBody().string();
                        Log.d("onResponse: ", error);
                        errorModel = new Gson().fromJson(error, new TypeToken<ErrorModel>() {
                        }.getType());
                        GlobalMethods.showErrorToast(MainActivity.this, errorModel.message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResultAPIModel<Vendor>> call, Throwable t) {
                t.printStackTrace();
                if (t instanceof UnknownHostException || t instanceof NoRouteToHostException) {
                    NoConnectionDialog.with(MainActivity.this);
                }
            }
        });

    }

    public void showWarningDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View mView = LayoutInflater.from(context).inflate(R.layout.dialog_warning, null);
        TextView tv_title = mView.findViewById(R.id.tv_title);
        TextView btn_yes = mView.findViewById(R.id.btn_yes);
        TextView btn_no = mView.findViewById(R.id.btn_no);
        btn_yes.setOnClickListener(view -> {
            myDialog.dismiss();
            String fcm = Hawk.get(Constant.fcm);
            Hawk.deleteAll();
            Hawk.put(Constant.fcm, fcm);
            startActivity(new Intent(context, SplashActivity.class));
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        });

        btn_no.setOnClickListener(view -> myDialog.dismiss());
        builder.setView(mView);
        myDialog = builder.create();
        myDialog.setCancelable(true);
        myDialog.show();

    }


}