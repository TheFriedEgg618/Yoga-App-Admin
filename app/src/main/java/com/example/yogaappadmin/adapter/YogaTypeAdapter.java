package com.example.yogaappadmin.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yogaappadmin.R;
import com.example.yogaappadmin.model.YogaTypeModel;

import java.util.ArrayList;
import java.util.List;

public class YogaTypeAdapter extends RecyclerView.Adapter<YogaTypeAdapter.VH> {

    public interface Listener {
        void onEdit(@NonNull YogaTypeModel yogaType);
        void onDelete(@NonNull YogaTypeModel yogaType);
    }

    private final List<YogaTypeModel> data = new ArrayList<>();
    private final Listener listener;

    public YogaTypeAdapter(@NonNull Listener listener) {
        this.listener = listener;
    }

    public void setData(@NonNull List<YogaTypeModel> list) {
        data.clear();
        data.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_yoga_card, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        YogaTypeModel type = data.get(position);
        String des = type.getDescription();
        if (des.isBlank()) { des = "N/A"; }

        holder.tvName.setText(type.getTypeName());
        holder.tvDescription.setText("Description: " + des);

        holder.btnEdit.setOnClickListener(v -> listener.onEdit(type));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(type));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvName, tvDescription;
        ImageButton btnEdit, btnDelete;

        VH(@NonNull View itemView) {
            super(itemView);
            tvName        = itemView.findViewById(R.id.tvYogaTypeName);
            tvDescription = itemView.findViewById(R.id.tvYogaTypeDescription);
            btnEdit       = itemView.findViewById(R.id.btnEdit);
            btnDelete     = itemView.findViewById(R.id.btnDelete);
        }
    }
}
