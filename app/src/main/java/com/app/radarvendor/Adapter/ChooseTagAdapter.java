package com.app.radarvendor.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.radarvendor.Module.Tag;
import com.app.radarvendor.databinding.RvChooseCityItemBinding;
import com.app.radarvendor.databinding.RvChooseTagBinding;

import java.util.ArrayList;
import java.util.List;

public class ChooseTagAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<Tag> tagList;
    private RvChooseTagBinding binding;

    public ChooseTagAdapter(Context context, List<Tag> tagList) {
        this.context = context;
        this.tagList = tagList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RvChooseTagBinding.inflate(LayoutInflater.from(context), parent, false);
        return new MyViewHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Tag item = tagList.get(position);
        MyViewHolder myViewHolder = (MyViewHolder) holder;
        myViewHolder.binding.cb.setText(item.getName());
        myViewHolder.binding.cb.setChecked(item.isChecked());
        myViewHolder.binding.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                item.setChecked(b);
            }
        });


    }

    public List<Integer> getSelectedTags() {
        List<Integer> selectedTagsList = new ArrayList<>();
        for (int i = 0; i < tagList.size(); i++) {
            if (tagList.get(i).isChecked())
                selectedTagsList.add(tagList.get(i).getId());
        }
        return selectedTagsList;
    }

    public StringBuffer getSelectedNamesTags() {
       StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < tagList.size(); i++) {
            if (tagList.get(i).isChecked())
                stringBuffer.append(tagList.get(i).getName()).append(" ,");
        }
//        stringBuffer.deleteCharAt(stringBuffer.length()-1);
        return stringBuffer;
    }
    @Override
    public int getItemCount() {
        return tagList == null ? 0 : tagList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private RvChooseTagBinding binding;

        MyViewHolder(RvChooseTagBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }


}
