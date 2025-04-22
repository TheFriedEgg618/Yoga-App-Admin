package com.example.yogaappadmin.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yogaappadmin.R;
import com.example.yogaappadmin.model.ClassModel;

import java.util.ArrayList;
import java.util.List;

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.VH> {

    public interface Listener {
        void onEdit(@NonNull ClassModel item);
        void onDelete(@NonNull ClassModel item);
    }

    private final List<ClassModel> data = new ArrayList<>();
    private final Listener listener;

    public ClassAdapter(@NonNull Listener listener) {
        this.listener = listener;
    }

    public void setData(@NonNull List<ClassModel> list) {
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

        // Title + Teacher in one TextView
        String title = it.getTitle();               // e.g. "Morning Flow"
        String teacher = it.getTeacherName();       // e.g. "Jane Smith"
        h.tvClassTitle.setText(title + " by " + teacher);

        // Date & Time
        h.tvClassDate.setText("Date: " + it.getDay());
        h.tvClassTime.setText("Time: " + it.getTime());

        // Capacity & Price
        h.tvClassCapacity.setText("Capacity: " + it.getCapacity());
        h.tvClassPrice.setText(String.format("Price: £%.2f", it.getPrice()));

        // Type & Duration
        h.tvClassType.setText("Type: " + it.getType());
        h.tvClassDuration.setText("Duration: " + it.getDuration() + " min");

        // Button callbacks
        h.btnEditClass.setOnClickListener(v -> listener.onEdit(it));
        h.btnDeleteClass.setOnClickListener(v -> listener.onDelete(it));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvClassTitle;
        TextView tvClassDate, tvClassTime;
        TextView tvClassCapacity, tvClassPrice;
        TextView tvClassType, tvClassDuration;
        ImageButton btnEditClass, btnDeleteClass;

        VH(@NonNull View v) {
            super(v);
            tvClassTitle    = v.findViewById(R.id.tvClassTitle);
            tvClassDate     = v.findViewById(R.id.tvClassDate);
            tvClassTime     = v.findViewById(R.id.tvClassTime);
            tvClassCapacity = v.findViewById(R.id.tvClassCapacity);
            tvClassPrice    = v.findViewById(R.id.tvClassPrice);
            tvClassType     = v.findViewById(R.id.tvClassType);
            tvClassDuration = v.findViewById(R.id.tvClassDuration);
            btnEditClass    = v.findViewById(R.id.btnEdit);
            btnDeleteClass  = v.findViewById(R.id.btnDelete);
        }
    }
}
