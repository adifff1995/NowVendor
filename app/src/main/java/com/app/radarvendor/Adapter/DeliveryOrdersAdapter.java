package com.app.radarvendor.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.app.radarvendor.Module.OrderResponse;
import com.app.radarvendor.databinding.RvOrderItemBinding;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DeliveryOrdersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<OrderResponse> orderItemList;
    private RvOrderItemBinding binding;
    private OnItemSelect itemClick;
    String  createdAt = null;

    public DeliveryOrdersAdapter(Context context, List<OrderResponse> orderItemList, OnItemSelect itemClick) {
        this.context = context;
        this.orderItemList = orderItemList;
        this.itemClick = itemClick;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RvOrderItemBinding.inflate(LayoutInflater.from(context), parent, false);
        return new MyViewHolder(binding);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        OrderResponse item = orderItemList.get(position);
        MyViewHolder myViewHolder = (MyViewHolder) holder;
        myViewHolder.binding.btnAccept.setVisibility(View.GONE);
        myViewHolder.binding.btnReject.setVisibility(View.GONE);
        OffsetDateTime inst = OffsetDateTime.ofInstant(Instant.parse(item.getOrder_date()),
                ZoneId.systemDefault());

        myViewHolder.binding.tvOrderNum.setText(String.valueOf(item.getId()));
        myViewHolder.binding.tvOrderStatus.setText(item.getStore_status());
        myViewHolder.binding.tvDate.setText(DateTimeFormatter.ofPattern("MMM dd, yyyy").format(inst));
        myViewHolder.binding.tvPrice.setText(item.getTotal()  + " ₪");
        myViewHolder.binding.tvName.setText(item.getName());

        myViewHolder.binding.getRoot().setOnClickListener(view -> itemClick.onNotificationClicked(position));



    }

    @Override
    public int getItemCount() {
        return orderItemList == null ? 0 : orderItemList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private RvOrderItemBinding binding;

        MyViewHolder(RvOrderItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface OnItemSelect {
        void onNotificationClicked(int position);
    }
}
