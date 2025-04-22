package com.example.yogaappadmin.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.yogaappadmin.R;
import com.example.yogaappadmin.model.ClassModel;
import java.util.ArrayList;
import java.util.List;

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.VH> {

    private final List<ClassModel> data = new ArrayList<>();

    public void setData(List<ClassModel> list) {
        data.clear();
        data.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_class_card, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        ClassModel it = data.get(pos);
        // Type & Duration
        h.tvClassTypeDuration.setText(
                it.getType() + " - " + it.getDuration() + " MINS"
        );
        // Date & Time
        h.tvClassDate.setText("Date: " + it.getDay());
        h.tvClassTime.setText("Time: " + it.getTime());
        // Capacity & Price
        h.tvClassCapacity.setText("Capacity: " + it.getCapacity());
        h.tvClassPrice.setText(String.format("Price: £%.2f", it.getPrice()));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvClassTypeDuration;
        TextView tvClassDate;
        TextView tvClassTime;
        TextView tvClassCapacity;
        TextView tvClassPrice;

        public VH(@NonNull View v) {
            super(v);
            tvClassTypeDuration = v.findViewById(R.id.tvClassTypeDuration);
            tvClassDate         = v.findViewById(R.id.tvClassDate);
            tvClassTime         = v.findViewById(R.id.tvClassTime);
            tvClassCapacity     = v.findViewById(R.id.tvClassCapacity);
            tvClassPrice        = v.findViewById(R.id.tvClassPrice);
        }
    }
}