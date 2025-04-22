package com.example.yogaappadmin.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yogaappadmin.R;
import com.example.yogaappadmin.model.TeacherModel;

import java.util.ArrayList;
import java.util.List;

public class TeacherAdapter extends RecyclerView.Adapter<TeacherAdapter.VH> {

    public interface Listener {
        void onEdit(@NonNull TeacherModel teacher);
        void onDelete(@NonNull TeacherModel teacher);
    }

    private final List<TeacherModel> data = new ArrayList<>();
    private final Listener listener;

    public TeacherAdapter(@NonNull Listener listener) {
        this.listener = listener;
    }

    public void setData(@NonNull List<TeacherModel> list) {
        data.clear();
        data.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_teacher_card, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        TeacherModel teacher = data.get(position);
        holder.tvName.setText(teacher.getName());
        holder.tvBio.setText(teacher.getBio());
        holder.tvClasses.setText("Teaches: " + teacher.getClassesCsv());

        holder.btnEdit.setOnClickListener(v -> listener.onEdit(teacher));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(teacher));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvName, tvBio, tvClasses;
        ImageButton btnEdit, btnDelete;

        VH(@NonNull View itemView) {
            super(itemView);
            tvName    = itemView.findViewById(R.id.tvTeacherName);
            tvBio     = itemView.findViewById(R.id.tvTeacherBio);
            tvClasses = itemView.findViewById(R.id.tvTeacherClasses);
            btnEdit   = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
