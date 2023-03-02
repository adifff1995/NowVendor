package com.app.radarvendor.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;

import com.app.radarvendor.Module.ErrorModel;
import com.app.radarvendor.Module.Reservations;
import com.app.radarvendor.Module.ResultAPIModel;
import com.app.radarvendor.R;
import com.app.radarvendor.Utils.Constant;
import com.app.radarvendor.Utils.GlobalMethods;
import com.app.radarvendor.Utils.NoConnectionDialog;
import com.app.radarvendor.databinding.ActivityOrderDetailsBinding;
import com.app.radarvendor.databinding.ActivityReservationDetailsBinding;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.net.NoRouteToHostException;
import java.net.UnknownHostException;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReservationDetailsActivity extends AppCompatActivity {

    private ActivityReservationDetailsBinding binding;
    private Reservations reservations;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReservationDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setUpActivity();
        setUpActions();
    }

    private void setUpActivity() {
        reservations = (Reservations) getIntent().getSerializableExtra("reserve");
        pd = new ProgressDialog(this);
        pd.setMessage("انتظر قليلا");
        binding.tvOrderNum.setText(String.valueOf(reservations.getId()));
        binding.tvName.setText(reservations.getService_name());
        binding.tvDate.setText(reservations.getDateAndTime());
        binding.tvInfo.setText(reservations.getService_description());
        binding.tvRating.setText(String.valueOf(reservations.getRating_avg()));
        Glide.with(this).load(reservations.getService_image_url()).into(binding.imgMeal);
    }

    private void setUpActions() {
        binding.imgBack.setOnClickListener(view -> finish());
        binding.btnAccept.setOnClickListener(view -> reservationStatus(String.valueOf(reservations.getId()),"acceptance"));

        binding.btnReject.setOnClickListener(view -> reservationStatus(String.valueOf(reservations.getId()),"reject"));
    }


    private void reservationStatus(String reservation_id,String store_status) {
        Map<String, Object> params = GlobalMethods.getParams(ReservationDetailsActivity.this);
        params.put("reservation_id", reservation_id);
        params.put("store_status", store_status);
        Call<ResultAPIModel<Reservations>> call = GlobalMethods.getApiInterface().reservationStatus(GlobalMethods.getHeaders(),params);
        pd.show();
        call.enqueue(new Callback<ResultAPIModel<Reservations>>() {
            @Override
            public void onResponse(Call<ResultAPIModel<Reservations>> call, Response<ResultAPIModel<Reservations>> response) {
                ResultAPIModel<Reservations> result = response.body();
                pd.dismiss();
                if (response.isSuccessful()) {
                    assert result != null;
                    if (result.success == Constant.Status_successful) {
                        GlobalMethods.printGson("orderActive", result.data);
                        finish();

                    } else {
                        GlobalMethods.showErrorToast(ReservationDetailsActivity.this, result.message);

                    }

                } else {
                    ErrorModel errorModel =null;
                    try {
                        assert response.errorBody() != null;
                        String error = response.errorBody().string();
                        Log.d("onResponse: ", error);
                        errorModel = new Gson().fromJson(error, new TypeToken<ErrorModel>() {
                        }.getType());
                        GlobalMethods.showErrorToast(ReservationDetailsActivity.this, errorModel.message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResultAPIModel<Reservations>> call, Throwable t) {
                t.printStackTrace();
                if (t instanceof UnknownHostException || t instanceof NoRouteToHostException) {
                    NoConnectionDialog.with(ReservationDetailsActivity.this);
                }
            }
        });

    }

}