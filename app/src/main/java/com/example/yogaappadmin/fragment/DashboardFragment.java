package com.example.yogaappadmin.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.yogaappadmin.R;
import com.example.yogaappadmin.adapter.ClassAdapter;
import com.example.yogaappadmin.data.DatabaseHelper;
import com.example.yogaappadmin.databinding.FragmentDashboardBinding;
import com.example.yogaappadmin.model.ClassModel;
import com.example.yogaappadmin.model.TeacherModel;
import com.example.yogaappadmin.model.YogaTypeModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private DatabaseHelper dbHelper;
    private ClassAdapter adapter;
    private List<ClassModel> allClasses = new ArrayList<>();

    // Spinner filters
    private String selectedTeacher = null;
    private String selectedType    = null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        binding  = FragmentDashboardBinding.inflate(inflater, container, false);
        dbHelper = new DatabaseHelper(requireContext());

        // — Select all 7 days by default —
        MaterialButtonToggleGroup dayToggle = binding.toggleGroupDays.getRoot();
        for (int i = 0; i < dayToggle.getChildCount(); i++) {
            View child = dayToggle.getChildAt(i);
            dayToggle.check(child.getId());
        }

        // — Listen for day‐toggle changes —
        dayToggle.addOnButtonCheckedListener((group, checkedId, isChecked) -> applyFilters());

        // Setup RecyclerView & Adapter
        binding.recyclerViewClasses.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );
        adapter = new ClassAdapter(new ClassAdapter.Listener() {
            @Override
            public void onEdit(@NonNull ClassModel item) {
                Bundle args = new Bundle();
                args.putLong("classId",      item.getId());
                args.putString("title",      item.getTitle());
                args.putString("dayCsv",     item.getDay());
                args.putString("time",       item.getTime());
                args.putInt("capacity",      item.getCapacity());
                args.putInt("duration",      item.getDuration());
                args.putFloat("price",       (float)item.getPrice());
                args.putString("teacher",    item.getTeacherName());
                args.putString("type",       item.getType());
                args.putString("description",item.getDescription());

                NavController nav = NavHostFragment.findNavController(DashboardFragment.this);
                nav.navigate(R.id.action_navigation_dashboard_to_ClassFormFragment, args);
            }

            @Override
            public void onDelete(@NonNull ClassModel item) {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Delete Class")
                        .setMessage("Are you sure you want to delete this class?")
                        .setPositiveButton("Delete", (dlg, which) -> {
                            int rows = dbHelper.deleteClass(item.getId());
                            if (rows > 0) {
                                Toast.makeText(getContext(),
                                        "Class deleted", Toast.LENGTH_SHORT).show();
                                reloadAndFilter();
                            } else {
                                Toast.makeText(getContext(),
                                        "Error deleting class", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });
        binding.recyclerViewClasses.setAdapter(adapter);

        // Load data & wire filters
        loadAllClasses();
        setupTeacherFilter();
        setupTypeFilter();

        return binding.getRoot();
    }

    private void loadAllClasses() {
        allClasses = dbHelper.getAllClassesList();
        applyFilters();
    }

    private void reloadAndFilter() {
        loadAllClasses();
    }

    private void setupTeacherFilter() {
        List<String> names = new ArrayList<>();
        names.add("All Teachers");
        for (TeacherModel t : dbHelper.getAllTeachersModels()) {
            names.add(t.getName());
        }

        ArrayAdapter<String> teachAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                names
        );
        teachAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerTeacherFilter.setAdapter(teachAdapter);

        binding.spinnerTeacherFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedTeacher = (position == 0 ? null : names.get(position));
                applyFilters();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupTypeFilter() {
        List<String> types = new ArrayList<>();
        types.add("All Types");
        for (YogaTypeModel y : dbHelper.getAllYogaTypesModels()) {
            types.add(y.getTypeName());
        }

        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                types
        );
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerTypeFilter.setAdapter(typeAdapter);

        binding.spinnerTypeFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedType = (position == 0 ? null : types.get(position));
                applyFilters();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void applyFilters() {
        MaterialButtonToggleGroup dayToggle = binding.toggleGroupDays.getRoot();
        List<String> selectedDays = new ArrayList<>();
        for (int btnId : dayToggle.getCheckedButtonIds()) {
            MaterialButton btn = dayToggle.findViewById(btnId);
            selectedDays.add(btn.getText().toString());
        }

        List<ClassModel> filtered = new ArrayList<>();
        for (ClassModel c : allClasses) {
            // day filter: at least one overlap
            boolean matchesDay = false;
            String[] classDays = c.getDay().split(",");
            for (String selDay : selectedDays) {
                for (String cd : classDays) {
                    if (cd.trim().equals(selDay.trim())) {
                        matchesDay = true;
                        break;
                    }
                }
                if (matchesDay) break;
            }
            if (!matchesDay) continue;

            // teacher filter
            if (selectedTeacher != null
                    && !selectedTeacher.equals(c.getTeacherName())) {
                continue;
            }
            // type filter
            if (selectedType != null
                    && !selectedType.equals(c.getType())) {
                continue;
            }

            filtered.add(c);
        }
        adapter.setData(filtered);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
