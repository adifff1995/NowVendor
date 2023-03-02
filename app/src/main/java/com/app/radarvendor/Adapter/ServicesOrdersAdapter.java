package com.app.radarvendor.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.radarvendor.Module.Reservations;
import com.app.radarvendor.databinding.RvOrderItemBinding;
import com.app.radarvendor.databinding.RvServiceOrderItemBinding;

import java.util.List;

public class ServicesOrdersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<Reservations> orderItemList;
    private RvServiceOrderItemBinding binding;
    private OnItemSelect itemClick;

    public ServicesOrdersAdapter(Context context, List<Reservations> orderItemList, OnItemSelect itemClick) {
        this.context = context;
        this.orderItemList = orderItemList;
        this.itemClick = itemClick;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RvServiceOrderItemBinding.inflate(LayoutInflater.from(context), parent, false);
        return new MyViewHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Reservations item = orderItemList.get(position);
        MyViewHolder myViewHolder = (MyViewHolder) holder;

        myViewHolder.binding.tvName.setText(item.getService_name());
        myViewHolder.binding.tvOrderNum.setText(String.valueOf(item.getId()));
        myViewHolder.binding.tvTime.setText(item.getBooking_time());
        myViewHolder.binding.tvDate.setText(item.getBooking_date());
        myViewHolder.binding.tvOrderStatus.setText(item.getStore_status_name());
        myViewHolder.binding.getRoot().setOnClickListener(view -> itemClick.onNotificationClicked(position));
        myViewHolder.binding.btnAccept.setOnClickListener(view -> itemClick.onAcceptClicked(position));

        switch (item.getStore_status()) {
            case "pending":
                myViewHolder.binding.btnAccept.setVisibility(View.VISIBLE);
                myViewHolder.binding.btnReject.setVisibility(View.VISIBLE);
                myViewHolder.binding.tvState.setVisibility(View.GONE);
                break;
            case "acceptance":
                myViewHolder.binding.btnAccept.setVisibility(View.GONE);
                myViewHolder.binding.btnReject.setVisibility(View.GONE);
                myViewHolder.binding.tvState.setVisibility(View.GONE);
                break;

            case "reject":
                myViewHolder.binding.btnAccept.setVisibility(View.GONE);
                myViewHolder.binding.btnReject.setVisibility(View.GONE);
                myViewHolder.binding.tvState.setText("تم الغاء الحجز من قبل الزبون");
                myViewHolder.binding.tvState.setVisibility(View.VISIBLE);
                break;
        }
        myViewHolder.binding.btnReject.setOnClickListener(view -> itemClick.onRejectClicked(position));
    }

    @Override
    public int getItemCount() {
        return orderItemList == null ? 0 : orderItemList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private RvServiceOrderItemBinding binding;

        MyViewHolder(RvServiceOrderItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface OnItemSelect {
        void onNotificationClicked(int position);

        void onAcceptClicked(int position);

        void onRejectClicked(int position);
    }
}
