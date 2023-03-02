package com.app.radarvendor.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.app.radarvendor.Module.Service;
import com.app.radarvendor.Utils.GlobalMethods;
import com.app.radarvendor.databinding.RvSevicesItemBinding;
import com.bumptech.glide.Glide;

import java.util.List;

public class ServicesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<Service> productList;
    private RvSevicesItemBinding binding;
    private OnItemSelect itemClick;

    public ServicesAdapter(Context context, List<Service> productList, OnItemSelect itemClick) {
        this.context = context;
        this.productList = productList;
        this.itemClick = itemClick;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RvSevicesItemBinding.inflate(LayoutInflater.from(context), parent, false);
        return new MyViewHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Service item = productList.get(position);
        MyViewHolder myViewHolder = (MyViewHolder) holder;

        CircularProgressDrawable circularProgressDrawable = GlobalMethods.getPlaceHolder(context);
        circularProgressDrawable.start();

        myViewHolder.binding.tvTitle.setText(item.getName());
        myViewHolder.binding.tvDetails.setText(item.getDescription());
        myViewHolder.binding.tvDuration.setText(item.getService_time());
        if (item.getImage_url() != null)
            Glide.with(context).load(item.getImage_url()).placeholder(circularProgressDrawable).into(myViewHolder.binding.image);
        myViewHolder.binding.getRoot().setOnClickListener(view -> itemClick.onProductClicked(position));

        myViewHolder.binding.getRoot().setOnClickListener(view -> itemClick.onProductClicked(position));


    }

    @Override
    public int getItemCount() {
        return productList == null ? 0 : productList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private RvSevicesItemBinding binding;

        MyViewHolder(RvSevicesItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface OnItemSelect {
        void onProductClicked(int position);
    }
}
