package com.app.radarvendor.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.radarvendor.Module.Additions;
import com.app.radarvendor.databinding.RvAddProductAdditionsBinding;

import java.util.List;

public class AddProductUniqueAdditionsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<Additions> productList;
    private RvAddProductAdditionsBinding binding;
    private OnItemSelect itemClick;

    public AddProductUniqueAdditionsAdapter(Context context, List<Additions> productList, OnItemSelect itemClick) {
        this.context = context;
        this.productList = productList;
        this.itemClick = itemClick;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RvAddProductAdditionsBinding.inflate(LayoutInflater.from(context), parent, false);
        return new MyViewHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Additions item = productList.get(position);
        MyViewHolder myViewHolder = (MyViewHolder) holder;
        myViewHolder.binding.edName.setText(item.getName());
        myViewHolder.binding.edPrice.setText(item.getPrice());

        myViewHolder.binding.edName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    item.setName(editable.toString());
                }
            }
        });

        myViewHolder.binding.edPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    item.setPrice(editable.toString());
                }
            }
        });

        myViewHolder.binding.getRoot().setOnClickListener(view -> itemClick.onProductClicked(position));


    }

    @Override
    public int getItemCount() {
        return productList == null ? 0 : productList.size();
    }

    public List<Additions> getAdditions() {
//        notifyDataSetChanged();
        return productList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private RvAddProductAdditionsBinding binding;

        MyViewHolder(RvAddProductAdditionsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface OnItemSelect {
        void onProductClicked(int position);
    }
}
