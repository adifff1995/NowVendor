package com.app.radarvendor.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.app.radarvendor.Module.ErrorModel;
import com.app.radarvendor.Module.ResultAPIModel;
import com.app.radarvendor.Module.Service;
import com.app.radarvendor.R;
import com.app.radarvendor.Utils.Constant;
import com.app.radarvendor.Utils.GlobalMethods;
import com.app.radarvendor.Utils.NoConnectionDialog;
import com.app.radarvendor.databinding.ActivityNewReservationBinding;
import com.github.razir.progressbutton.ButtonTextAnimatorExtensionsKt;
import com.github.razir.progressbutton.DrawableButtonExtensionsKt;
import com.github.razir.progressbutton.ProgressButtonHolderKt;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.net.NoRouteToHostException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewReservationActivity extends AppCompatActivity {

    private ActivityNewReservationBinding binding;
    private Context context = NewReservationActivity.this;
    private String time, date;
    private int service_id = -1;
    private List<String> servicesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewReservationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setUpActivity();
        setUpActions();
        getServices();
    }

    private void setUpActivity() {
        binding.tool.tvMainTitle.setText(getString(R.string.add_new_reserve));
        servicesList = new ArrayList<>();
        ProgressButtonHolderKt.bindProgressButton(this, binding.reserveNow);
        ButtonTextAnimatorExtensionsKt.attachTextChangeAnimator(binding.reserveNow);
        binding.spServiceType.setLifecycleOwner(this);
        binding.spServiceType.setSpinnerOutsideTouchListener((view, motionEvent) -> {
            if (binding.spServiceType.isShowing())
                binding.spServiceType.dismiss();
        });
    }

    private void setUpActions() {
        binding.spTime.setOnClickListener(view -> {
            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(context, (timePicker, houre, min) -> {
                time = String.format(Locale.ENGLISH, "%02d:%02d", houre, min);
                binding.spTime.setText(time);
            }, hour, minute, true);
            mTimePicker.setTitle(getString(R.string.reservation_time));
            mTimePicker.show();
        });

        binding.spDate.setOnClickListener(view -> {
            Calendar mcurrentTime = Calendar.getInstance();
            int month = mcurrentTime.get(Calendar.MONTH);
            int year = mcurrentTime.get(Calendar.YEAR);
            int day = mcurrentTime.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog;
            datePickerDialog = new DatePickerDialog(context, (datePicker, i, i1, i2) -> {
                date = i + "-" + (i1 + 1) + "-" + i2;
                binding.spDate.setText(date);
            }, year, month, day);
            datePickerDialog.setTitle(getString(R.string.reservation_time));
            datePickerDialog.show();
        });

        binding.reserveNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (service_id != -1) {
                    if (time != null && date != null) {
                        storeReservations(binding.edDescrip.getText().toString());
                    } else
                        GlobalMethods.showWarningToast(NewReservationActivity.this, "الرجاء اختيار تاريخ ووقت للخدمة");

                } else
                    GlobalMethods.showWarningToast(NewReservationActivity.this, "الرجاء اختيار الخدمة المراد حجزها");

            }
        });

        binding.tool.imgBack.setOnClickListener(view -> finish());
    }

    private void storeReservations(String desc) {
        Map<String, Object> params = GlobalMethods.getParams(context);
        params.put("service_id", service_id);
        params.put("booking_date", date);
        params.put("booking_time", time);
        params.put("notes", desc);

        GlobalMethods.printGson("storeReservations",params);

        Call<ResultAPIModel> call = GlobalMethods.getApiInterface().storeReservations(GlobalMethods.getHeaders(), params);
        GlobalMethods.showProgress(binding.reserveNow);
        call.enqueue(new Callback<ResultAPIModel>() {
            @Override
            public void onResponse(Call<ResultAPIModel> call, Response<ResultAPIModel> response) {
                ResultAPIModel result = response.body();
                binding.reserveNow.setEnabled(true);
                DrawableButtonExtensionsKt.hideProgress(binding.reserveNow, R.string.book_now);
                if (response.isSuccessful()) {
                    assert result != null;
                    if (result.success == Constant.Status_successful) {
                        GlobalMethods.showSuccessToast(NewReservationActivity.this, result.message);
                        finish();
                    } else {
                        GlobalMethods.showErrorToast(NewReservationActivity.this, result.message);

                    }

                } else {
                    ErrorModel errorModel = null;
                    try {
                        assert response.errorBody() != null;
                        String error = response.errorBody().string();
                        Log.d("onResponse: ", error);
                        errorModel = new Gson().fromJson(error, new TypeToken<ErrorModel>() {
                        }.getType());
                        GlobalMethods.showErrorToast(NewReservationActivity.this, errorModel.message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResultAPIModel> call, Throwable t) {
                t.printStackTrace();
                if (t instanceof UnknownHostException || t instanceof NoRouteToHostException) {
                    NoConnectionDialog.with(NewReservationActivity.this);
                }
            }
        });

    }

    private void getServices() {
        Call<ResultAPIModel<List<Service>>> call = GlobalMethods.getApiInterface().getServices(GlobalMethods.getHeaders());
        call.enqueue(new Callback<ResultAPIModel<List<Service>>>() {
            @Override
            public void onResponse(Call<ResultAPIModel<List<Service>>> call, Response<ResultAPIModel<List<Service>>> response) {
                ResultAPIModel<List<Service>> result = response.body();

                if (response.isSuccessful()) {
                    assert result != null;
                    if (result.success == Constant.Status_successful) {
                        for (int i = 0; i < result.data.size(); i++) {
                            servicesList.add(result.data.get(i).getName());
                        }
                        binding.spServiceType.setItems(servicesList);
                        binding.spServiceType.setOnSpinnerItemSelectedListener((i, o, i1, t1) ->{
                            service_id = result.data.get(i1).getId() ;
                        } );
                    } else {
                        GlobalMethods.showErrorToast(NewReservationActivity.this, result.message);

                    }

                } else {
                    ErrorModel errorModel = null;
                    try {
                        assert response.errorBody() != null;
                        String error = response.errorBody().string();
                        Log.d("onResponse: ", error);
                        errorModel = new Gson().fromJson(error, new TypeToken<ErrorModel>() {
                        }.getType());
                        GlobalMethods.showErrorToast(NewReservationActivity.this, errorModel.message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResultAPIModel<List<Service>>> call, Throwable t) {
                t.printStackTrace();
                if (t instanceof UnknownHostException || t instanceof NoRouteToHostException) {
                    NoConnectionDialog.with(NewReservationActivity.this);
                }
            }
        });

    }
}