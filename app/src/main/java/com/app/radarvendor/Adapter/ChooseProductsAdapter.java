package com.app.radarvendor.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.app.radarvendor.Module.Product;
import com.app.radarvendor.Utils.GlobalMethods;
import com.app.radarvendor.databinding.RvProductsForOffersListBinding;
import com.app.radarvendor.databinding.RvProductsItemBinding;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class ChooseProductsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<Product> productList;
    private RvProductsForOffersListBinding binding;
    private OnItemSelect itemClick;
    private int flag;

    public ChooseProductsAdapter(Context context, List<Product> productList, int flag, OnItemSelect itemClick) {
        this.context = context;
        this.productList = productList;
        this.flag = flag;
        this.itemClick = itemClick;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RvProductsForOffersListBinding.inflate(LayoutInflater.from(context), parent, false);
        return new MyViewHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Product item = productList.get(position);
        MyViewHolder myViewHolder = (MyViewHolder) holder;
        CircularProgressDrawable circularProgressDrawable = GlobalMethods.getPlaceHolder(context);
        circularProgressDrawable.start();
        if (flag == 0)
            myViewHolder.binding.cb.setVisibility(View.VISIBLE);
        else {
            myViewHolder.binding.cb.setVisibility(View.GONE);
            myViewHolder.binding.btnDecrease.setVisibility(View.GONE);
            myViewHolder.binding.btnIncrease.setVisibility(View.GONE);
            myViewHolder.binding.tvAmount.setText(String.valueOf(item.getQuantity()));
        }

        myViewHolder.binding.tvName.setText(item.getName());
        myViewHolder.binding.tvAmount.setText(String.valueOf(item.getQuantity()));
        myViewHolder.binding.tvDescp.setText(item.getCategory_name());
        myViewHolder.binding.tvPrice.setText(item.getPrice() + " ₪");
        item.setUnitPrice(item.getPrice());
        if (item.getImages() != null)
            if (item.getImages().size() != 0)
                Glide.with(context).load(item.getImages().get(0).getImage_url()).placeholder(circularProgressDrawable).into(myViewHolder.binding.imgMeal);

        myViewHolder.binding.cb.setOnCheckedChangeListener((compoundButton, b) -> {
            item.setChecked(b);
            itemClick.onProductClicked(position, myViewHolder.binding.cb.isChecked());
        });

        myViewHolder.binding.btnIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentAmount = Integer.parseInt(myViewHolder.binding.tvAmount.getText().toString());
                int newAmount = currentAmount + 1;
                double unitPrice = item.getUnitPrice();
                double newPrice = unitPrice * newAmount;
                item.setPrice(newPrice);
                item.setQuantity(newAmount);
                myViewHolder.binding.tvAmount.setText(String.valueOf(newAmount));
                myViewHolder.binding.tvPrice.setText(String.valueOf(newPrice));
                itemClick.onItemIncrease(position);
            }
        });

        myViewHolder.binding.btnDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentAmount = Integer.parseInt(myViewHolder.binding.tvAmount.getText().toString());
                if (currentAmount > 1) {
                    int newAmount = currentAmount - 1;
                    double currentPrice = item.getPrice();
                    double unitPrice = item.getUnitPrice();
                    double newPrice = currentPrice - unitPrice;
                    item.setPrice(newPrice);
                    item.setQuantity(newAmount);
                    myViewHolder.binding.tvAmount.setText(String.valueOf(newAmount));
                    myViewHolder.binding.tvPrice.setText(String.valueOf(newPrice));
                    itemClick.onItemDecrease(position);
                } else {
//                    itemClick.onItemDelete(position);
                }
            }
        });


    }

    public List<Integer> getCheckedProductsIDs() {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < productList.size(); i++) {
            if (productList.get(i).isChecked())
                list.add(productList.get(i).getId());

        }
        return list;
    }

    public List<Product> getCheckedProducts() {
        List<Product> list = new ArrayList<>();
        for (int i = 0; i < productList.size(); i++) {
            if (productList.get(i).isChecked())
                list.add(productList.get(i));

        }

        return list;
    }

    public double getTotalCheckedProducts() {
        double total = 0;
        List<Product> list = new ArrayList<>();
        for (int i = 0; i < productList.size(); i++) {
            if (productList.get(i).isChecked()) {
                list.add(productList.get(i));
                total += (productList.get(i).getPrice());
            }
        }

        return total;
    }

    @Override
    public int getItemCount() {
        return productList == null ? 0 : productList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private RvProductsForOffersListBinding binding;

        MyViewHolder(RvProductsForOffersListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface OnItemSelect {
        void onProductClicked(int position, boolean isChecked);

        void onItemIncrease(int position);

        void onItemDecrease(int position);
    }
}
