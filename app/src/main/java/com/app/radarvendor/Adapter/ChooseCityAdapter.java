package com.app.radarvendor.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.radarvendor.databinding.RvChooseCityItemBinding;
import com.app.radarvendor.databinding.RvServicePeriodBinding;

import java.util.List;

public class ChooseCityAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<String> durationsList;
    private RvChooseCityItemBinding binding;
    private OnItemSelect itemClick;
    private int select = 0;

    public ChooseCityAdapter(Context context, List<String> durationsList, OnItemSelect itemClick) {
        this.context = context;
        this.durationsList = durationsList;
        this.itemClick = itemClick;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RvChooseCityItemBinding.inflate(LayoutInflater.from(context), parent, false);
        return new MyViewHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String item = durationsList.get(position);
        MyViewHolder myViewHolder = (MyViewHolder) holder;
        myViewHolder.binding.tvCity.setText(item);
        if (select == position)
            myViewHolder.binding.imgChecked.setVisibility(View.VISIBLE);
        else
            myViewHolder.binding.imgChecked.setVisibility(View.GONE);


        myViewHolder.binding.getRoot().setOnClickListener(view -> {
            itemClick.onProductClicked(position);
            select = position;
            notifyDataSetChanged();
        });


    }

    @Override
    public int getItemCount() {
        return durationsList == null ? 0 : durationsList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private RvChooseCityItemBinding binding;

        MyViewHolder(RvChooseCityItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface OnItemSelect {
        void onProductClicked(int position);
    }
}
