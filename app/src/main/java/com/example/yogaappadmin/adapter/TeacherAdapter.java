package com.example.yogaappadmin.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
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

        // — Bio (same as before) —
        String bio = teacher.getBio();
        if (bio.isBlank()) { bio = "N/A"; }
        holder.tvBio.setText("Bio: " + bio);

        // — Classes CSV with error if empty —
        String csv = teacher.getClassesCsv();
        if (csv == null || csv.trim().isEmpty()) {
            holder.tvClasses.setText("Teaches: (none)");
            holder.tvClasses.setError("No classes assigned");
            holder.tvClasses.setTextColor(
                    ContextCompat.getColor(holder.itemView.getContext(),
                            com.google.android.material.R.color.design_default_color_error)
            );
        } else {
            holder.tvClasses.setError(null);
            holder.tvClasses.setTextColor(
                    ContextCompat.getColor(holder.itemView.getContext(),
                            R.color.text_black)
            );
            holder.tvClasses.setText("Teaches: " + csv);
        }

        // — Name (you might also want to flag missing names, but up to you) —
        holder.tvName.setText(teacher.getName());

        // — Button callbacks —
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
