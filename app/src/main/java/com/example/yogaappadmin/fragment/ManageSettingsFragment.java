package com.example.yogaappadmin.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.google.android.material.button.MaterialButtonToggleGroup;

public class ManageSettingsFragment extends Fragment {

    private FragmentManageSettingsBinding binding;
    private DatabaseHelper dbHelper;

    private TeacherAdapter teacherAdapter;
    private YogaTypeAdapter yogaAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentManageSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dbHelper = new DatabaseHelper(requireContext());

        // 1) Instantiate both adapters
        teacherAdapter = new TeacherAdapter();
        yogaAdapter    = new YogaTypeAdapter();

        // 2) RecyclerView setup
        binding.rvManageList.setLayoutManager(new LinearLayoutManager(requireContext()));
        // default to Teachers
        binding.rvManageList.setAdapter(teacherAdapter);
        teacherAdapter.setData(dbHelper.getAllTeachersModels());

        // 3) Toggle listener: swap adapter & reload data
        MaterialButtonToggleGroup toggle = binding.toggleGroupMode;
        toggle.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (!isChecked) return;
            if (checkedId == R.id.btnTeachers) {
                // show teachers
                binding.rvManageList.setAdapter(teacherAdapter);
                teacherAdapter.setData(dbHelper.getAllTeachersModels());

                // update Add button text
                binding.btnAdd.setText("Add A Teacher");
            } else { // R.id.btnTypes
                // show class types
                binding.rvManageList.setAdapter(yogaAdapter);
                yogaAdapter.setData(dbHelper.getAllYogaTypesModels());

                // update Add button text
                binding.btnAdd.setText("Add A Class Type");
            }
        });
        // ensure initial button state
        binding.btnTeachers.setChecked(true);
        binding.btnAdd.setText("Add A Teacher");

        // 4) Add button → navigate to the appropriate “FormFragment” in add mode
        binding.btnAdd.setOnClickListener(v -> {
            NavController nav = NavHostFragment.findNavController(this);
            if (binding.btnTeachers.isChecked()) {
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
