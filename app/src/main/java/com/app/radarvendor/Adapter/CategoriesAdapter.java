package com.app.radarvendor.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.radarvendor.Module.Category;
import com.app.radarvendor.R;
import com.app.radarvendor.databinding.RvAdditionsBinding;
import com.app.radarvendor.databinding.RvCategoriesBinding;

import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<Category> categoriesList;
    private RvCategoriesBinding binding;
    private OnItemSelect itemClick;
    private int select;

    public CategoriesAdapter(Context context, List<Category> categoriesList, OnItemSelect itemClick) {
        this.context = context;
        this.categoriesList = categoriesList;
        this.itemClick = itemClick;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RvCategoriesBinding.inflate(LayoutInflater.from(context), parent, false);
        return new MyViewHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Category category = categoriesList.get(position);
        MyViewHolder myViewHolder = (MyViewHolder) holder;

        if (select == position) {
            myViewHolder.binding.linear.setBackgroundResource(R.drawable.bg_button_bordered_app2);
            myViewHolder.binding.tvName.setTextColor(context.getColor(R.color.app));
        } else {
            myViewHolder.binding.linear.setBackgroundResource(R.drawable.bg_button_gray_big_raduis);
            myViewHolder.binding.tvName.setTextColor(context.getColor(R.color.text));
        }

        myViewHolder.binding.tvName.setText(category.getName());

        myViewHolder.binding.linear.setOnClickListener(view -> {
            itemClick.onCategorySelect(position);
            select = position;
            notifyDataSetChanged();
        });

    }

    @Override
    public int getItemCount() {
        return categoriesList == null ? 0 : categoriesList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private RvCategoriesBinding binding;

        MyViewHolder(RvCategoriesBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface OnItemSelect {
        void onCategorySelect(int position);
    }

}

