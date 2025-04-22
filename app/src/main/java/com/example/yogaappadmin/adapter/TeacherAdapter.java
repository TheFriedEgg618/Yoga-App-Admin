package com.example.yogaappadmin.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yogaappadmin.R;
import com.example.yogaappadmin.model.TeacherModel;

import java.util.ArrayList;
import java.util.List;

public class TeacherAdapter
        extends RecyclerView.Adapter<TeacherAdapter.VH> {

    private final List<TeacherModel> data = new ArrayList<>();

    /** Swap in a new list of teachers */
    public void setData(List<TeacherModel> list) {
        data.clear();
        if (list != null) data.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_teacher_card, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        TeacherModel t = data.get(pos);
        h.tvName   .setText(t.getName());
        h.tvBio    .setText(t.getBio());
        h.tvClasses.setText("Teaches: " + t.getClassesCsv());
    }

    @Override public int getItemCount() {
        return data.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        final TextView tvName, tvBio, tvClasses;
        VH(@NonNull View itemView) {
            super(itemView);
            tvName    = itemView.findViewById(R.id.tvTeacherName);
            tvBio     = itemView.findViewById(R.id.tvTeacherBio);
            tvClasses = itemView.findViewById(R.id.tvTeacherClasses);
        }
    }
}
