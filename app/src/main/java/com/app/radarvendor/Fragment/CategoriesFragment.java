package com.app.radarvendor.Fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.radarvendor.Adapter.CategoriesAdapter;
import com.app.radarvendor.Module.Category;
import com.app.radarvendor.Module.ErrorModel;
import com.app.radarvendor.Module.MessageEvent;
import com.app.radarvendor.Module.ResultAPIModel;
import com.app.radarvendor.R;
import com.app.radarvendor.Utils.ApiInterface;
import com.app.radarvendor.Utils.Constant;
import com.app.radarvendor.Utils.FragmentUtil;
import com.app.radarvendor.Utils.GlobalMethods;
import com.app.radarvendor.Utils.NoConnectionDialog;
import com.app.radarvendor.databinding.AddCategorySheetBinding;
import com.app.radarvendor.databinding.FragmentAboutAppBinding;
import com.app.radarvendor.databinding.FragmentCategoriesBinding;
import com.app.radarvendor.databinding.SuccessfulProccessSheetBinding;
import com.github.razir.progressbutton.ButtonTextAnimatorExtensionsKt;
import com.github.razir.progressbutton.DrawableButtonExtensionsKt;
import com.github.razir.progressbutton.ProgressButtonHolderKt;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.net.NoRouteToHostException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CategoriesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CategoriesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FragmentCategoriesBinding binding;
    private Activity activity;
    private List<Category> categoriesList;
    private CategoriesAdapter categoriesAdapter;
    private SuccessfulProccessSheetBinding successfulPaymentSheetBinding;
    private BottomSheetDialog successfulPaymentSheetDialog;
    private AddCategorySheetBinding addCategorySheetBinding;
    private BottomSheetDialog addCategorySheetDialog;
    private ApiInterface apiService;

    public CategoriesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CategoriesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CategoriesFragment newInstance(String param1, String param2) {
        CategoriesFragment fragment = new CategoriesFragment();
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
        binding = FragmentCategoriesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpFragment();
        setUpAction();
        getCategoriesByUser();
    }

    private void setUpFragment() {
        categoriesList = new ArrayList<>();
        categoriesAdapter = new CategoriesAdapter(activity, categoriesList, position -> {
            FragmentUtil.addFragmentWithBackStack(activity,
                    ProductsByCategoryFragment.newInstance(categoriesList.get(position).getId(), ""),
                    R.id.main, "");
        });
        binding.rvCategories.setAdapter(categoriesAdapter);
        binding.rvCategories.setLayoutManager(new GridLayoutManager(activity, 3));

        binding.swipToRefresh.setOnRefreshListener(() -> getCategoriesByUser());
    }

    private void setUpAction() {
        binding.fbAdd.setOnClickListener(view -> showAddCategorySheet());

        binding.edSearch.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                searchCategory(binding.edSearch.getText().toString());
                return true;
            }
            return false;
        });
    }

    private void showPaymentSuccessfulSheet() {
        successfulPaymentSheetBinding = SuccessfulProccessSheetBinding.inflate(getLayoutInflater());
        successfulPaymentSheetDialog = new BottomSheetDialog(activity, R.style.BottomSheetDialog);
        successfulPaymentSheetDialog.setContentView(successfulPaymentSheetBinding.getRoot());
        successfulPaymentSheetBinding.tvTitle.setText(getString(R.string.added_done));
        successfulPaymentSheetDialog.show();
    }

    private void showAddCategorySheet() {
        addCategorySheetBinding = AddCategorySheetBinding.inflate(getLayoutInflater());
        addCategorySheetDialog = new BottomSheetDialog(activity, R.style.BottomSheetDialog);
        addCategorySheetDialog.setContentView(addCategorySheetBinding.getRoot());
        addCategorySheetBinding.btnAdd.setOnClickListener(view -> {
            if (GlobalMethods.setEditTextEmptyError(activity, addCategorySheetBinding.edName)) {
                addCategory(addCategorySheetBinding.edName.getText().toString());
            }

        });
        ProgressButtonHolderKt.bindProgressButton(this, addCategorySheetBinding.btnAdd);
        ButtonTextAnimatorExtensionsKt.attachTextChangeAnimator(addCategorySheetBinding.btnAdd);

        addCategorySheetBinding.btnClose.setOnClickListener(view -> {
            addCategorySheetDialog.dismiss();
        });
        addCategorySheetDialog.show();
    }


    private void getCategoriesByUser() {
        Call<ResultAPIModel<List<Category>>> call = GlobalMethods.getApiInterface().getCategoriesByUser(GlobalMethods.getHeaders());
        binding.rvCategories.showShimmerAdapter();
        call.enqueue(new Callback<ResultAPIModel<List<Category>>>() {
            @Override
            public void onResponse(Call<ResultAPIModel<List<Category>>> call, Response<ResultAPIModel<List<Category>>> response) {
                ResultAPIModel<List<Category>> result = response.body();
                binding.rvCategories.hideShimmerAdapter();
                if (binding.swipToRefresh.isRefreshing())
                    binding.swipToRefresh.setRefreshing(false);
                if (response.isSuccessful()) {
                    assert result != null;
                    if (result.success == Constant.Status_successful) {
                        categoriesList.clear();
                        categoriesList.addAll(result.data);
                        categoriesAdapter.notifyDataSetChanged();
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
            public void onFailure(Call<ResultAPIModel<List<Category>>> call, Throwable t) {
                t.printStackTrace();
                if (t instanceof UnknownHostException || t instanceof NoRouteToHostException) {
                    NoConnectionDialog.with(activity);
                }
            }
        });

    }

    private void searchCategory(String name) {
        Map<String, Object> params = GlobalMethods.getParams(activity);
        params.put("name", name);

        Call<ResultAPIModel<List<Category>>> call = GlobalMethods.getApiInterface().searchCategory(GlobalMethods.getHeaders(), params);
        binding.rvCategories.showShimmerAdapter();
        call.enqueue(new Callback<ResultAPIModel<List<Category>>>() {
            @Override
            public void onResponse(Call<ResultAPIModel<List<Category>>> call, Response<ResultAPIModel<List<Category>>> response) {
                ResultAPIModel<List<Category>> result = response.body();
                binding.rvCategories.hideShimmerAdapter();
                if (response.isSuccessful()) {
                    assert result != null;
                    if (result.success == Constant.Status_successful) {
                        categoriesList.clear();
                        categoriesList.addAll(result.data);
                        categoriesAdapter.notifyDataSetChanged();
                        binding.edSearch.setText("");
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
            public void onFailure(Call<ResultAPIModel<List<Category>>> call, Throwable t) {
                t.printStackTrace();
                if (t instanceof UnknownHostException || t instanceof NoRouteToHostException) {
                    NoConnectionDialog.with(activity);
                }
            }
        });

    }

    private void addCategory(String name) {
        Map<String, Object> params = GlobalMethods.getParams(activity);
        params.put("name", name);
        Call<ResultAPIModel> call = GlobalMethods.getApiInterface().addCategory(GlobalMethods.getHeaders(), params);
        GlobalMethods.printGson("verify", params);
        call.enqueue(new Callback<ResultAPIModel>() {
            @Override
            public void onResponse(Call<ResultAPIModel> call, Response<ResultAPIModel> response) {
                ResultAPIModel result = response.body();
                addCategorySheetBinding.btnAdd.setEnabled(true);
                DrawableButtonExtensionsKt.hideProgress(addCategorySheetBinding.btnAdd, R.string.add);
                if (response.isSuccessful()) {
                    assert result != null;
                    if (result.success == Constant.Status_successful) {
                        addCategorySheetDialog.dismiss();
                        GlobalMethods.showSuccessToast(activity, result.message);
                        getCategoriesByUser();
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        Log.e("onMessageEvent: ", event.getFlag() + "");
        if (binding.edSearch.getVisibility() == View.VISIBLE)
            binding.edSearch.setVisibility(View.GONE);
        else
            binding.edSearch.setVisibility(View.VISIBLE);

    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            activity = (Activity) context;
        }
    }
}