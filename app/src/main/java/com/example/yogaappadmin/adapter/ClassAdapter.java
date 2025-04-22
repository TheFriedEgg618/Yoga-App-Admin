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

public class ClassAdapter
        extends RecyclerView.Adapter<ClassAdapter.VH> {

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
        h.tvClassName.setText(
                it.getType() + " - " + it.getDuration() + " MINS"
        );
        h.tvClassDateTime.setText(
                it.getDay() + ", @ " + it.getTime()
        );
        h.tvClassInstructor.setText(
                "Cap: " + it.getCapacity()
        );
        h.tvClassLocation.setText(
                "Price: £" + String.format("%.2f", it.getPrice())
        );
    }

    @Override public int getItemCount() { return data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvClassName, tvClassDateTime, tvClassInstructor, tvClassLocation;
        public VH(@NonNull View v) {
            super(v);
            tvClassName     = v.findViewById(R.id.tvClassName);
            tvClassDateTime = v.findViewById(R.id.tvClassDateTime);
            tvClassInstructor = v.findViewById(R.id.tvClassCapacity);
            tvClassLocation = v.findViewById(R.id.tvClassPrice);
        }
    }
}