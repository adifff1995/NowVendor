package com.app.radarvendor.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.app.radarvendor.Module.ProductImages;
import com.app.radarvendor.Utils.GlobalMethods;
import com.app.radarvendor.databinding.RvOrderItemBinding;
import com.app.radarvendor.databinding.RvProductsImagesItemBinding;
import com.bumptech.glide.Glide;

import java.util.List;

public class ProductImagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<ProductImages> imagesList;
    private RvProductsImagesItemBinding binding;
    private OnItemSelect itemClick;
    private int flag;

    public ProductImagesAdapter(Context context, List<ProductImages> imagesList, int flag, OnItemSelect itemClick) {
        this.context = context;
        this.imagesList = imagesList;
        this.itemClick = itemClick;
        this.flag = flag;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RvProductsImagesItemBinding.inflate(LayoutInflater.from(context), parent, false);
        return new MyViewHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ProductImages item = imagesList.get(position);
        MyViewHolder myViewHolder = (MyViewHolder) holder;
//        myViewHolder.binding.img.setImageURI(item);
        CircularProgressDrawable circularProgressDrawable = GlobalMethods.getPlaceHolder(context);
        circularProgressDrawable.start();
        Glide.with(context).load(item.getImage_url()).placeholder(circularProgressDrawable).into(binding.img);
        if (flag == 0)
            myViewHolder.binding.imgRemove.setVisibility(View.GONE);
        else
            myViewHolder.binding.imgRemove.setVisibility(View.VISIBLE);

        myViewHolder.binding.imgRemove.setOnClickListener(view -> itemClick.onDeleteImage(position));


    }

    public void removeImage(int position) {
        imagesList.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return imagesList == null ? 0 : imagesList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private RvProductsImagesItemBinding binding;

        MyViewHolder(RvProductsImagesItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface OnItemSelect {
        void onDeleteImage(int position);
    }


}
