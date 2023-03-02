package com.app.radarvendor.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.app.radarvendor.Module.About;
import com.app.radarvendor.Module.Additions;
import com.app.radarvendor.Module.PizzaIcon;
import com.app.radarvendor.Utils.Constant;
import com.app.radarvendor.databinding.RvAdditionsBinding;
import com.bumptech.glide.Glide;
import com.orhanobut.hawk.Hawk;

import java.util.List;

public class CartItemAdditionsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<Additions> cartItemAdditionsList;
    private RvAdditionsBinding binding;
    private OnItemSelect itemClick;
    private About about;
    private List<PizzaIcon> pizzaIconsList;

    public CartItemAdditionsAdapter(Context context, List<Additions> cartItemAdditionsList, OnItemSelect itemClick) {
        this.context = context;
        this.cartItemAdditionsList = cartItemAdditionsList;
        this.itemClick = itemClick;
        about = Hawk.get(Constant.about);
        pizzaIconsList = about.getPizzaIconList();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RvAdditionsBinding.inflate(LayoutInflater.from(context), parent, false);
        return new MyViewHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Additions additions = cartItemAdditionsList.get(position);
        MyViewHolder myViewHolder = (MyViewHolder) holder;

        myViewHolder.binding.tvName.setText(additions.getName());
        myViewHolder.binding.tvPrice.setText(additions.getPrice());

        if (additions.getPizza_id() != null) {
            for (int i = 0; i < pizzaIconsList.size(); i++) {
                if (Integer.parseInt(additions.getPizza_id()) == pizzaIconsList.get(i).getId())
                    Glide.with(context).load(pizzaIconsList.get(i).getImage_url()).into(myViewHolder.binding.tvPizza);
            }
        }


    }

    @Override
    public int getItemCount() {
        return cartItemAdditionsList == null ? 0 : cartItemAdditionsList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private RvAdditionsBinding binding;

        MyViewHolder(RvAdditionsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }


    public interface OnItemSelect {
        void onAdditionRemoved(int position);
    }
}
