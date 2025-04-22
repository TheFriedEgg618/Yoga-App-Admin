package com.example.yogaappadmin.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.yogaappadmin.data.DatabaseHelper;
import com.example.yogaappadmin.databinding.FragmentTeacherFormBinding;
import com.example.yogaappadmin.model.YogaTypeModel;

import java.util.ArrayList;
import java.util.List;

public class TeacherFormFragment extends Fragment {
    private FragmentTeacherFormBinding binding;
    private DatabaseHelper dbHelper;
    private Long teacherId;

    // For multi‑select dialog:
    private final List<String> allClassNames = new ArrayList<>();
    private boolean[]               checkedClasses;
    private final List<String>      selectedClasses = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTeacherFormBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dbHelper   = new DatabaseHelper(requireContext());

        // 1) Load all yoga‑type names from DB
        List<YogaTypeModel> types = dbHelper.getAllYogaTypesModels();
        for (YogaTypeModel t : types) {
            allClassNames.add(t.getTypeName());
        }
        checkedClasses = new boolean[allClassNames.size()];

        // 2) If editing, prefill name/bio and parse CSV into selectedClasses & checkedClasses
        if (getArguments() != null && getArguments().containsKey("teacherId")) {
            teacherId = getArguments().getLong("teacherId");
            binding.etTeacherName.setText(getArguments().getString("teacherName"));
            binding.etTeacherBio .setText(getArguments().getString("teacherBio"));
            String csv = getArguments().getString("teacherClasses","");
            if (!csv.isEmpty()) {
                for (String s : csv.split(",")) {
                    selectedClasses.add(s);
                    int idx = allClassNames.indexOf(s);
                    if (idx >= 0) checkedClasses[idx] = true;
                }
                binding.etTeacherClasses.setText(csv);
            }
        } else {
            teacherId = null;
        }

        // 3) Show multi‑select dialog on click
        binding.etTeacherClasses.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Select Classes")
                    .setMultiChoiceItems(
                            allClassNames.toArray(new String[0]),
                            checkedClasses,
                            (dialog, which, isChecked) -> {
                                checkedClasses[which] = isChecked;
                                String name = allClassNames.get(which);
                                if (isChecked) {
                                    if (!selectedClasses.contains(name))
                                        selectedClasses.add(name);
                                } else {
                                    selectedClasses.remove(name);
                                }
                            }
                    )
                    .setPositiveButton("OK", (d, w) -> {
                        String joined = TextUtils.join(",", selectedClasses);
                        binding.etTeacherClasses.setText(joined);
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        // Back arrow
        binding.buttonBack.setOnClickListener(v ->
                NavHostFragment.findNavController(this).navigateUp()
        );

        // SAVE
        binding.btnSaveTeacher.setOnClickListener(v -> {
            String name       = binding.etTeacherName.getText().toString().trim();
            String bio        = binding.etTeacherBio   .getText().toString().trim();
            String classesCsv = binding.etTeacherClasses.getText().toString().trim();
            if (name.isEmpty() || classesCsv.isEmpty()) {
                Toast.makeText(getContext(),
                        "Name and Classes are required", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean ok;
            if (teacherId == null) {
                ok = dbHelper.insertTeacher(name, bio, classesCsv);
            } else {
                ok = dbHelper.updateTeacher(
                        teacherId, name, bio, classesCsv
                ) > 0;
            }

            if (ok) {
                Toast.makeText(getContext(),
                        "Teacher saved", Toast.LENGTH_SHORT).show();
                NavHostFragment.findNavController(this).navigateUp();
            } else {
                Toast.makeText(getContext(),
                        "Error saving teacher", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

