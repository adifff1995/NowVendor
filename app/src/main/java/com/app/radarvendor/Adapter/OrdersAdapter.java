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
import com.app.radarvendor.R;
import com.app.radarvendor.databinding.RvNotificationItemBinding;
import com.app.radarvendor.databinding.RvOrderItemBinding;

import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<OrderResponse> orderItemList;
    private RvOrderItemBinding binding;
    private OnItemSelect itemClick;

    public OrdersAdapter(Context context, List<OrderResponse> orderItemList, OnItemSelect itemClick) {
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

//        OffsetDateTime inst = OffsetDateTime.ofInstant(Instant.parse(item.getOrder_date()),
//                ZoneId.systemDefault());

        if (item.getDelivery_status().equals("complete")){
            myViewHolder.binding.btnAccept.setVisibility(View.GONE);
            myViewHolder.binding.btnReject.setVisibility(View.GONE);
            myViewHolder.binding.tvOrderStatus.setBackgroundResource(R.drawable.bg_order_state_green);
        }
        if (item.getStore_status().equals("acceptance")){
            myViewHolder.binding.btnAccept.setText(context.getString(R.string.ready));
        }else {
            myViewHolder.binding.btnAccept.setText(context.getString(R.string.accept));
        }

        myViewHolder.binding.tvOrderNum.setText(String.valueOf(item.getId()));
        myViewHolder.binding.tvOrderStatus.setText(item.getStore_status_name());
//        myViewHolder.binding.tvDate.setText(DateTimeFormatter.ofPattern("MMM dd, yyyy").format(inst));
        myViewHolder.binding.tvPrice.setText(item.getTotal()  + " ₪");
        myViewHolder.binding.tvName.setText(item.getName());

        myViewHolder.binding.getRoot().setOnClickListener(view -> itemClick.onNotificationClicked(position));
        myViewHolder.binding.btnAccept.setOnClickListener(view ->{
         if (myViewHolder.binding.btnAccept.getText().toString().equals(context.getString(R.string.ready)))
             itemClick.onOrderReadyClicked(position);
         else
             itemClick.onOrderAcceptedClicked(position);
        });
        myViewHolder.binding.btnReject.setOnClickListener(view -> itemClick.onOrderRejectedClicked(position));



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
        void onOrderAcceptedClicked(int position);
        void onOrderReadyClicked(int position);
        void onOrderRejectedClicked(int position);
    }
}
