package com.example.yogaappadmin.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yogaappadmin.R;
import com.example.yogaappadmin.model.YogaTypeModel;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView adapter for displaying a list of yoga class types.
 */
public class YogaTypeAdapter
        extends RecyclerView.Adapter<YogaTypeAdapter.VH> {

    private final List<YogaTypeModel> data = new ArrayList<>();

    /**
     * Swap in a new list of yoga types.
     */
    public void setData(List<YogaTypeModel> list) {
        data.clear();
        if (list != null) data.addAll(list);
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
        YogaTypeModel item = data.get(position);
        holder.tvName       .setText(item.getName());
        holder.tvDescription.setText(item.getDescription());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        final TextView tvName;
        final TextView tvDescription;

        VH(@NonNull View itemView) {
            super(itemView);
            tvName        = itemView.findViewById(R.id.tvYogaTypeName);
            tvDescription = itemView.findViewById(R.id.tvYogaTypeDescription);
        }
    }
}
