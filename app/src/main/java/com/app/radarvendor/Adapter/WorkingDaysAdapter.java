package com.app.radarvendor.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.TimePickerDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.radarvendor.Module.WorkingDay;
import com.app.radarvendor.R;
import com.app.radarvendor.Utils.GlobalMethods;
import com.app.radarvendor.databinding.RvServicePeriodBinding;
import com.app.radarvendor.databinding.RvWorkingDayBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class WorkingDaysAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Activity context;
    private List<WorkingDay> workingDayList;
    private RvWorkingDayBinding binding;
    private OnItemSelect itemClick;
    private int select = 0;

    public WorkingDaysAdapter(Activity context, List<WorkingDay> workingDayList, OnItemSelect itemClick) {
        this.context = context;
        this.workingDayList = workingDayList;
        this.itemClick = itemClick;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RvWorkingDayBinding.inflate(LayoutInflater.from(context), parent, false);
        return new MyViewHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        WorkingDay item = workingDayList.get(position);
        MyViewHolder myViewHolder = (MyViewHolder) holder;

        Log.e("onBindViewHolder: ", item.getFrom());
        myViewHolder.binding.tvDay.setText(item.getDay_name());
        if (item.getSelected() == 1){
            myViewHolder.binding.edTimeFrom.setText(item.getFrom());
            myViewHolder.binding.edTimeTo.setText(item.getTo());
        }

        myViewHolder.binding.cb.setChecked(item.getSelected() == 1);
        myViewHolder.binding.edTimeFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Locale.setDefault(Locale.ENGLISH);
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(context, (timePicker, houre, min) -> {
                    String time = String.format(Locale.ENGLISH, "%02d:%02d", houre, min);
                    myViewHolder.binding.edTimeFrom.setText(time);
                    item.setFrom(time);
                }, hour, minute, true);
                mTimePicker.setTitle(context.getString(R.string.from_time));
                mTimePicker.show();
            }
        });

        myViewHolder.binding.edTimeTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Locale.setDefault(Locale.ENGLISH);
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(context, (timePicker, houre, min) -> {
                    String time = String.format(Locale.ENGLISH, "%02d:%02d", houre, min);
                    myViewHolder.binding.edTimeTo.setText(time);
                    item.setTo(time);
                }, hour, minute, true);
                mTimePicker.setTitle(context.getString(R.string.from_time));
                mTimePicker.show();
            }
        });

        myViewHolder.binding.cb.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                if (item.getFrom() != null && item.getTo() != null) {
                    item.setSelected(1);
                    itemClick.onDayChecked(position, b);
                } else {
                    myViewHolder.binding.cb.setChecked(false);
                    GlobalMethods.showWarningToast(context, "الرجاء اختيار توقيت من الى");
                }

            } else
                item.setSelected(0);


        });


    }

    public List<WorkingDay> getCheckedList() {
//        List<Integer> checkedIdList = new ArrayList<>();
//        for (int i = 0; i < workingDayList.size(); i++) {
//            if (workingDayList.get(i).getSelected().equals("1")) {
//                checkedIdList.add(Integer.parseInt(workingDayList.get(i).getDay()));
//            }
//        }
        return workingDayList;
    }

    public List<String> getCheckedNamesList() {
        List<String> checkedIdList = new ArrayList<>();
        for (int i = 0; i < workingDayList.size(); i++) {
            if (workingDayList.get(i).getSelected() == 1) {
                checkedIdList.add(workingDayList.get(i).getDay_name());
            }
        }
        return checkedIdList;
    }

    @Override
    public int getItemCount() {
        return workingDayList == null ? 0 : workingDayList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private RvWorkingDayBinding binding;

        MyViewHolder(RvWorkingDayBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface OnItemSelect {
        void onDayChecked(int position, boolean isChecked);
    }
}
