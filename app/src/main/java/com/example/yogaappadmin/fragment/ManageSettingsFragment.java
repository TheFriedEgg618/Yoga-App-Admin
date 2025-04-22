package com.example.yogaappadmin.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.yogaappadmin.R;
import com.example.yogaappadmin.adapter.TeacherAdapter;
import com.example.yogaappadmin.adapter.YogaTypeAdapter;
import com.example.yogaappadmin.data.DatabaseHelper;
import com.example.yogaappadmin.databinding.FragmentManageSettingsBinding;
import com.example.yogaappadmin.model.TeacherModel;
import com.example.yogaappadmin.model.YogaTypeModel;
import com.google.android.material.button.MaterialButtonToggleGroup;

import java.util.List;

public class ManageSettingsFragment extends Fragment {

    private FragmentManageSettingsBinding binding;
    private DatabaseHelper dbHelper;

    private TeacherAdapter    teacherAdapter;
    private YogaTypeAdapter   yogaTypeAdapter;
    private boolean           showingTeachers = true;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentManageSettingsBinding.inflate(inflater, container, false);
        dbHelper = new DatabaseHelper(requireContext());

        setupAdapters();
        setupToggleGroup();
        setupAddButton();

        return binding.getRoot();
    }

    private void setupAdapters() {
        // Teacher adapter w/ edit & delete
        teacherAdapter = new TeacherAdapter(new TeacherAdapter.Listener() {
            @Override
            public void onEdit(@NonNull TeacherModel teacher) {
                Bundle args = new Bundle();
                args.putLong  ("teacherId",   teacher.getId());
                args.putString("name",        teacher.getName());
                args.putString("bio",         teacher.getBio());
                args.putString("classesCsv",  teacher.getClassesCsv());
                NavHostFragment.findNavController(ManageSettingsFragment.this)
                        .navigate(R.id.action_manageSettings_to_teacherFormFragment, args);
            }
            @Override
            public void onDelete(@NonNull TeacherModel teacher) {
                new AlertDialog.Builder(requireContext())
                        .setTitle  ("Delete Teacher")
                        .setMessage("Are you sure?")
                        .setPositiveButton("Delete", (d, w) -> {
                            int rows = dbHelper.deleteTeacher(teacher.getId());
                            if (rows>0) {
                                Toast.makeText(getContext(),
                                        "Teacher deleted", Toast.LENGTH_SHORT).show();
                                loadTeachers();
                            } else {
                                Toast.makeText(getContext(),
                                        "Error deleting", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });

        // Yoga‑type adapter w/ edit & delete
        yogaTypeAdapter = new YogaTypeAdapter(new YogaTypeAdapter.Listener() {
            @Override
            public void onEdit(@NonNull YogaTypeModel type) {
                Bundle args = new Bundle();
                args.putLong  ("typeId",    type.getId());
                args.putString("typeName",  type.getTypeName());
                args.putString("description", type.getDescription());
                NavHostFragment.findNavController(ManageSettingsFragment.this)
                        .navigate(R.id.action_manageSettings_to_yogaTypeFormFragment, args);
            }
            @Override
            public void onDelete(@NonNull YogaTypeModel type) {
                new AlertDialog.Builder(requireContext())
                        .setTitle  ("Delete Class Type")
                        .setMessage("Are you sure?")
                        .setPositiveButton("Delete", (d, w) -> {
                            int rows = dbHelper.deleteYogaType(type.getId());
                            if (rows>0) {
                                Toast.makeText(getContext(),
                                        "Class type deleted", Toast.LENGTH_SHORT).show();
                                loadYogaTypes();
                            } else {
                                Toast.makeText(getContext(),
                                        "Error deleting", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });

        binding.rvManageList.setLayoutManager(new LinearLayoutManager(requireContext()));
        // start showing teachers by default:
        loadTeachers();
    }

    private void setupToggleGroup() {
        binding.toggleGroupMode.addOnButtonCheckedListener(
                new MaterialButtonToggleGroup.OnButtonCheckedListener() {
                    @Override
                    public void onButtonChecked(MaterialButtonToggleGroup group,
                                                int checkedId,
                                                boolean isChecked) {
                        if (!isChecked) return;
                        if (checkedId == R.id.btnTeachers) {
                            showingTeachers = true;
                            loadTeachers();
                            binding.btnAdd.setText("Add A Teacher");
                        } else { // Class Types
                            showingTeachers = false;
                            loadYogaTypes();
                            binding.btnAdd.setText("Add A Class Type");
                        }
                    }
                }
        );
        // initial selection
        binding.toggleGroupMode.check(R.id.btnTeachers);
    }

    private void loadTeachers() {
        List<TeacherModel> list = dbHelper.getAllTeachersModels();
        teacherAdapter.setData(list);
        binding.rvManageList.setAdapter(teacherAdapter);
    }

    private void loadYogaTypes() {
        List<YogaTypeModel> list = dbHelper.getAllYogaTypesModels();
        yogaTypeAdapter.setData(list);
        binding.rvManageList.setAdapter(yogaTypeAdapter);
    }

    private void setupAddButton() {
        binding.btnAdd.setOnClickListener(v -> {
            NavController nav = NavHostFragment.findNavController(this);
            if (showingTeachers) {
                nav.navigate(R.id.action_manageSettings_to_teacherFormFragment);
            } else {
                nav.navigate(R.id.action_manageSettings_to_yogaTypeFormFragment);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
