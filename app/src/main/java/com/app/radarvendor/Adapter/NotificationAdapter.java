package com.app.radarvendor.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.app.radarvendor.Module.NotificationItem;
import com.app.radarvendor.R;
import com.app.radarvendor.databinding.RvNotificationItemBinding;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<NotificationItem> notificationItemList;
    private RvNotificationItemBinding binding;
    private OnItemSelect itemClick;

    public NotificationAdapter(Context context, List<NotificationItem> notificationItemList, OnItemSelect itemClick) {
        this.context = context;
        this.notificationItemList = notificationItemList;
        this.itemClick = itemClick;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RvNotificationItemBinding.inflate(LayoutInflater.from(context), parent, false);
        return new MyViewHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        NotificationItem notificationItem = notificationItemList.get(position);
        MyViewHolder myViewHolder = (MyViewHolder) holder;

        myViewHolder.binding.tvTitle.setText(notificationItem.getTitle());
        myViewHolder.binding.tvTime.setText(notificationItem.getTime());

        if (notificationItem.isIs_read()){
            myViewHolder.binding.container.setBackgroundColor(context.getColor(R.color.white));
            myViewHolder.binding.img.setImageResource(R.drawable.ic_notify);
        }else {
            myViewHolder.binding.container.setBackgroundColor(context.getColor(R.color.notify));
            myViewHolder.binding.img.setImageResource(R.drawable.ic_notify_unread);
        }


        myViewHolder.binding.getRoot().setOnClickListener(view -> itemClick.onNotificationClicked(position));



    }

    @Override
    public int getItemCount() {
        return notificationItemList == null ? 0 : notificationItemList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private RvNotificationItemBinding binding;

        MyViewHolder(RvNotificationItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface OnItemSelect {
        void onNotificationClicked(int position);
    }
}
