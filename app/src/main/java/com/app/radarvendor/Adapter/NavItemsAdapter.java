package com.app.radarvendor.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.app.radarvendor.Module.NavItems;
import com.app.radarvendor.R;
import com.app.radarvendor.databinding.RvNavItemsBinding;

import java.util.List;

public class NavItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<NavItems> navItemsList;
    private RvNavItemsBinding binding;
    private OnItemSelect itemClick;
    private int select = 0;

    public NavItemsAdapter(Context context, List<NavItems> navItemsList, OnItemSelect itemClick) {
        this.context = context;
        this.navItemsList = navItemsList;
        this.itemClick = itemClick;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RvNavItemsBinding.inflate(LayoutInflater.from(context), parent, false);
        return new MyViewHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        NavItems items = navItemsList.get(position);
        MyViewHolder myViewHolder = (MyViewHolder) holder;

        myViewHolder.binding.tvTitle.setText(items.getTitle());
        myViewHolder.binding.icon.setImageResource(items.getIcon());

        if (select == position) {
//            DrawableCompat.setTint(
//                    DrawableCompat.wrap(context.getDrawable(items.getIcon())),
//                    ContextCompat.getColor(context, R.color.app));
            myViewHolder.binding.icon.setImageTintList(ColorStateList.valueOf(context.getColor(R.color.app)));
            myViewHolder.binding.tvTitle.setTextColor(context.getColor(R.color.app));
        } else {

//            DrawableCompat.setTint(
//                    DrawableCompat.wrap(context.getDrawable(items.getIcon())),
//                    ContextCompat.getColor(context, R.color.nav_item));
            myViewHolder.binding.icon.setImageTintList(ColorStateList.valueOf(context.getColor(R.color.nav_item)));


            myViewHolder.binding.tvTitle.setTextColor(context.getColor(R.color.text));

        }


        myViewHolder.binding.getRoot().setOnClickListener(view -> {
            itemClick.onOrderClicked(position);
            select = position;
            notifyDataSetChanged();
        });

    }

    @Override
    public int getItemCount() {
        return navItemsList == null ? 0 : navItemsList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private RvNavItemsBinding binding;

        MyViewHolder(RvNavItemsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface OnItemSelect {
        void onOrderClicked(int position);
    }
}