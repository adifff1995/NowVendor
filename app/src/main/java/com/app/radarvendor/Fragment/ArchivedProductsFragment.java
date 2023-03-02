package com.app.radarvendor.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

import com.app.radarvendor.Activities.EditProductActivity;
import com.app.radarvendor.Activities.ProductDetailsActivity;
import com.app.radarvendor.Adapter.ArchivedProductsAdapter;
import com.app.radarvendor.Module.ErrorModel;
import com.app.radarvendor.Module.MessageEvent;
import com.app.radarvendor.Module.Product;
import com.app.radarvendor.Module.ResultAPIModel;
import com.app.radarvendor.R;
import com.app.radarvendor.Utils.Constant;
import com.app.radarvendor.Utils.GlobalMethods;
import com.app.radarvendor.Utils.NoConnectionDialog;
import com.app.radarvendor.Utils.ProductStatus;
import com.app.radarvendor.databinding.AddCategorySheetBinding;
import com.app.radarvendor.databinding.ArchivedProductSheetBinding;
import com.app.radarvendor.databinding.FragmentAboutAppBinding;
import com.app.radarvendor.databinding.FragmentArchivedProductsBinding;
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
 * Use the {@link ArchivedProductsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ArchivedProductsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FragmentArchivedProductsBinding binding;
    private Activity activity;
    private List<Product> productList;
    private ArchivedProductsAdapter productsAdapter;
    private SuccessfulProccessSheetBinding successfulPaymentSheetBinding;
    private BottomSheetDialog successfulPaymentSheetDialog;
    private ArchivedProductSheetBinding archivedProductSheetBinding;
    private BottomSheetDialog archivedProductSheetDialog;

    public ArchivedProductsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ArchivedProductsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ArchivedProductsFragment newInstance(String param1, String param2) {
        ArchivedProductsFragment fragment = new ArchivedProductsFragment();
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
        binding = FragmentArchivedProductsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpFragment();
        setUpAction();
    }

    private void setUpFragment() {
        productList = new ArrayList<>();

        productsAdapter = new ArchivedProductsAdapter(activity, productList, new ArchivedProductsAdapter.OnItemSelect() {
            @Override
            public void onProductClicked(int position) {
                Intent intent = new Intent(activity, ProductDetailsActivity.class);
                intent.putExtra("id", productList.get(position).getId());
                intent.putExtra("archived", true);
                startActivity(intent);
                activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }

            @Override
            public void onMenuClicked(int position) {
                showProductOptionsSheet(position);
            }
        });
        binding.rvProducts.setAdapter(productsAdapter);
        binding.rvProducts.setLayoutManager(new GridLayoutManager(activity, 2));
        binding.swipToRefresh.setOnRefreshListener(() -> getArchivedProducts());

    }

    private void setUpAction() {
        binding.edSearch.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                searchProducts(binding.edSearch.getText().toString());
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

    private void showProductOptionsSheet(int position) {
        archivedProductSheetBinding = ArchivedProductSheetBinding.inflate(getLayoutInflater());
        archivedProductSheetDialog = new BottomSheetDialog(activity, R.style.BottomSheetDialog);
        archivedProductSheetDialog.setContentView(archivedProductSheetBinding.getRoot());
        ProgressButtonHolderKt.bindProgressButton(this, archivedProductSheetBinding.btnRepublish);
        ButtonTextAnimatorExtensionsKt.attachTextChangeAnimator(archivedProductSheetBinding.btnRepublish);
        archivedProductSheetBinding.btnRepublish.setOnClickListener(view -> {
            updateStatus(position);
        });

        archivedProductSheetBinding.btnEdit.setOnClickListener(view -> {
            archivedProductSheetDialog.dismiss();
            Intent intent = new Intent(activity, EditProductActivity.class);
            intent.putExtra("product", productList.get(position));
            startActivity(intent);
            activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });
        archivedProductSheetDialog.show();
    }

    private void getArchivedProducts() {
        Map<String, Object> params = GlobalMethods.getParams(activity);
        params.put("status", ProductStatus.archived);
        Call<ResultAPIModel<List<Product>>> call = GlobalMethods.getApiInterface().getArchivedProducts(GlobalMethods.getHeaders(), params);
        binding.rvProducts.showShimmerAdapter();
        call.enqueue(new Callback<ResultAPIModel<List<Product>>>() {
            @Override
            public void onResponse(Call<ResultAPIModel<List<Product>>> call, Response<ResultAPIModel<List<Product>>> response) {
                ResultAPIModel<List<Product>> result = response.body();
                binding.rvProducts.hideShimmerAdapter();
                if (binding.swipToRefresh.isRefreshing())
                    binding.swipToRefresh.setRefreshing(false);
                if (response.isSuccessful()) {
                    assert result != null;
                    if (result.success == Constant.Status_successful) {
                        productList.clear();
                        productList.addAll(result.data);
                        productsAdapter.notifyDataSetChanged();
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
            public void onFailure(Call<ResultAPIModel<List<Product>>> call, Throwable t) {
                t.printStackTrace();
                if (t instanceof UnknownHostException || t instanceof NoRouteToHostException) {
                    NoConnectionDialog.with(activity);
                }
            }
        });

    }

    private void updateStatus(int position) {
        Map<String, Object> params = GlobalMethods.getParams(activity);
        params.put("product_id", String.valueOf(productList.get(position).getId()));
        params.put("status", ProductStatus.active);
        Call<ResultAPIModel> call = GlobalMethods.getApiInterface().updateStatus(GlobalMethods.getHeaders(), params);
        GlobalMethods.showProgress(archivedProductSheetBinding.btnRepublish);
        call.enqueue(new Callback<ResultAPIModel>() {
            @Override
            public void onResponse(Call<ResultAPIModel> call, Response<ResultAPIModel> response) {
                archivedProductSheetBinding.btnRepublish.setEnabled(true);
                DrawableButtonExtensionsKt.hideProgress(archivedProductSheetBinding.btnRepublish, R.string.republish);
                ResultAPIModel result = response.body();
                if (response.isSuccessful()) {
                    assert result != null;
                    if (result.success == Constant.Status_successful) {
                        GlobalMethods.showSuccessToast(activity, result.message);
                        archivedProductSheetDialog.dismiss();
                        getArchivedProducts();

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
            public void onFailure(Call<ResultAPIModel> call, Throwable t) {
                t.printStackTrace();
                if (t instanceof UnknownHostException || t instanceof NoRouteToHostException) {
                    NoConnectionDialog.with(activity);
                }
            }
        });

    }

    private void searchProducts(String name) {
        Map<String, Object> params = GlobalMethods.getParams(activity);
        params.put("name", name);
        params.put("status", ProductStatus.archived);
        Call<ResultAPIModel<List<Product>>> call = GlobalMethods.getApiInterface().searchProducts(GlobalMethods.getHeaders(), params);
        binding.rvProducts.showShimmerAdapter();
        call.enqueue(new Callback<ResultAPIModel<List<Product>>>() {
            @Override
            public void onResponse(Call<ResultAPIModel<List<Product>>> call, Response<ResultAPIModel<List<Product>>> response) {
                ResultAPIModel<List<Product>> result = response.body();
                binding.rvProducts.hideShimmerAdapter();
                if (response.isSuccessful()) {
                    assert result != null;
                    if (result.success == Constant.Status_successful) {
                        productList.clear();
                        productList.addAll(result.data);
                        productsAdapter.notifyDataSetChanged();
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
            public void onFailure(Call<ResultAPIModel<List<Product>>> call, Throwable t) {
                t.printStackTrace();
                if (t instanceof UnknownHostException || t instanceof NoRouteToHostException) {
                    NoConnectionDialog.with(activity);
                }
            }
        });

    }


    @Override
    public void onResume() {
        super.onResume();
        getArchivedProducts();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
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