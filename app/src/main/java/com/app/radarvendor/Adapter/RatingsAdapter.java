package com.app.radarvendor.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.radarvendor.Module.Review;
import com.app.radarvendor.databinding.RvProductsItemBinding;
import com.app.radarvendor.databinding.RvRatingItemBinding;

import java.util.List;

public class RatingsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<Review> ratingList;
    private RvRatingItemBinding binding;
    private OnItemSelect itemClick;

    public RatingsAdapter(Context context,List<Review> ratingList) {
        this.context = context;
        this.ratingList = ratingList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RvRatingItemBinding.inflate(LayoutInflater.from(context), parent, false);
        return new MyViewHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Review item = ratingList.get(position);
        MyViewHolder myViewHolder = (MyViewHolder) holder;
        myViewHolder.binding.tvName.setText(item.getUser_name());
        myViewHolder.binding.tvDetails.setText(item.getRating_text());
        myViewHolder.binding.ratingBar.setRating(item.getRating());




    }

    @Override
    public int getItemCount() {
        return ratingList == null ? 0 : ratingList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private RvRatingItemBinding binding;

        MyViewHolder(RvRatingItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface OnItemSelect {
        void onProductClicked(int position);
    }
}
