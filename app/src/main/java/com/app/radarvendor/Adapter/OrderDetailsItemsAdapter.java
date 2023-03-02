package com.app.radarvendor.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.radarvendor.Module.OrderItem;
import com.app.radarvendor.databinding.RvOrderDetailedItemBinding;
import com.bumptech.glide.Glide;

import java.util.List;

public class OrderDetailsItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements CartItemAdditionsAdapter.OnItemSelect {
    private Context context;
    private List<OrderItem> cartItemList;
    private RvOrderDetailedItemBinding binding;
    private OnItemSelect itemClick;
    private CartItemAdditionsAdapter cartItemAdditionsAdapter;

    public OrderDetailsItemsAdapter(Context context, List<OrderItem> cartItemList, OnItemSelect itemClick) {
        this.context = context;
        this.cartItemList = cartItemList;
        this.itemClick = itemClick;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RvOrderDetailedItemBinding.inflate(LayoutInflater.from(context), parent, false);
        return new MyViewHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        OrderItem item = cartItemList.get(position);
        MyViewHolder myViewHolder = (MyViewHolder) holder;

        myViewHolder.binding.tvName.setText(item.getProduct_name());
        myViewHolder.binding.tvPrice.setText(item.getPrice() + "₪");
        myViewHolder.binding.tvAmount.setText("× "+item.getQuantity());

        cartItemAdditionsAdapter = new CartItemAdditionsAdapter(context, item.getAdditions(), position1 -> {

        });
        myViewHolder.binding.rvAdditions.setAdapter(cartItemAdditionsAdapter);
        myViewHolder.binding.rvAdditions.setLayoutManager(new LinearLayoutManager(context));

        Glide.with(context).load(item.getImage_url()).into(myViewHolder.binding.imgMeal);


    }

    @Override
    public int getItemCount() {
        return cartItemList == null ? 0 : cartItemList.size();
    }



    @Override
    public void onAdditionRemoved(int position) {

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private RvOrderDetailedItemBinding binding;

        MyViewHolder(RvOrderDetailedItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface OnItemSelect {
        void onItemIncrease(int position);

    }
}
