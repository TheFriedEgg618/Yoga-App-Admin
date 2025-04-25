package com.example.yogaappadmin.fragment;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
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

import com.example.yogaappadmin.data.DatabaseHelper;
import com.example.yogaappadmin.databinding.FragmentClassFormBinding;
import com.example.yogaappadmin.model.TeacherModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ClassFormFragment extends Fragment {

    private FragmentClassFormBinding binding;
    private DatabaseHelper dbHelper;
    private long classId = -1;

    // store the incoming type so can re-select it after loading the spinner
    private String initialTypeName = null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentClassFormBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper = new DatabaseHelper(requireContext());

        setupTeacherSpinner();
        setupTypeSpinner();      // disabled until teacher is chosen
        setupTimePicker();

        binding.buttonBack.setOnClickListener(v ->
                NavHostFragment.findNavController(this).navigateUp()
        );

        prefillIfEdit();
        setupSaveListener();
    }

    private void setupTeacherSpinner() {
        List<TeacherModel> teachers = dbHelper.getAllTeachersModels();
        List<String> names = new ArrayList<>();
        names.add("Select Teacher");
        for (TeacherModel t : teachers) names.add(t.getName());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                names
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerTeacher.setAdapter(adapter);

        binding.spinnerTeacher.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    binding.spinnerClassType.setEnabled(false);
                    ArrayAdapter<String> empty = new ArrayAdapter<>(
                            requireContext(),
                            android.R.layout.simple_spinner_item,
                            List.of("Select Type")
                    );
                    empty.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.spinnerClassType.setAdapter(empty);

                } else {
                    String teacherName = names.get(position);
                    TeacherModel chosen = null;
                    for (TeacherModel t : teachers) {
                        if (t.getName().equals(teacherName)) {
                            chosen = t;
                            break;
                        }
                    }

                    List<String> typesList = new ArrayList<>();
                    typesList.add("Select Type");
                    if (chosen != null && !TextUtils.isEmpty(chosen.getClassesCsv())) {
                        for (String ty : chosen.getClassesCsv().split(",")) {
                            typesList.add(ty.trim());
                        }
                    }

                    ArrayAdapter<String> ta = new ArrayAdapter<>(
                            requireContext(),
                            android.R.layout.simple_spinner_item,
                            typesList
                    );
                    ta.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.spinnerClassType.setAdapter(ta);
                    binding.spinnerClassType.setEnabled(true);

                    // after loading, if editing, select the initial type
                    if (initialTypeName != null) {
                        int idx = typesList.indexOf(initialTypeName);
                        if (idx >= 0) {
                            binding.spinnerClassType.setSelection(idx);
                        }
                        // only once!
                        initialTypeName = null;
                    }
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupTypeSpinner() {
        binding.spinnerClassType.setEnabled(false);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                List.of("Select Type")
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerClassType.setAdapter(adapter);
    }

    private void setupTimePicker() {
        binding.editTextTime.setInputType(InputType.TYPE_NULL);
        binding.editTextTime.setOnClickListener(v -> showTimePicker());
        binding.editTextTime.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                showTimePicker();
                v.clearFocus();
            }
        });
    }

    private void showTimePicker() {
        Calendar now = Calendar.getInstance();
        int h = now.get(Calendar.HOUR_OF_DAY), m = now.get(Calendar.MINUTE);
        new TimePickerDialog(requireContext(),
                (tp, hour, minute) ->
                        binding.editTextTime.setText(String.format("%02d:%02d", hour, minute)),
                h, m, true
        ).show();
    }

    private void prefillIfEdit() {
        Bundle args = getArguments();
        if (args != null && args.containsKey("classId") && args.getLong("classId", -1L) >= 0) {
            classId = args.getLong("classId");

            binding.tvAddClassTitle.setText("Edit Class");
            binding.buttonSaveCourse.setText("Update Class");

            binding.editTextTitle.setText(args.getString("title", ""));
            binding.editTextTime.setText(args.getString("time", ""));
            binding.editTextCapacity.setText(String.valueOf(args.getInt("capacity", 0)));
            binding.editTextDuration.setText(String.valueOf(args.getInt("duration", 0)));
            binding.editTextPrice.setText(String.valueOf(args.getFloat("price", 0f)));
            binding.editTextDescription.setText(args.getString("description", ""));

            String dayCsv = args.getString("dayCsv", "");
            if (!TextUtils.isEmpty(dayCsv)) {
                MaterialButtonToggleGroup tg = binding.toggleGroupDays.getRoot();
                for (String d : dayCsv.split(",")) {
                    int btnId = getResources().getIdentifier(
                            "btn" + d, "id", requireContext().getPackageName());
                    if (btnId != 0) tg.check(btnId);
                }
            }

            // remember this so can select it after the spinner reloads
            initialTypeName = args.getString("type", "");

            // select teacher (fires onItemSelected -> reload types -> apply initialTypeName)
            ArrayAdapter<String> teachAdapter =
                    (ArrayAdapter<String>) binding.spinnerTeacher.getAdapter();
            int teachPos = teachAdapter.getPosition(args.getString("teacher", ""));
            if (teachPos >= 0) {
                binding.spinnerTeacher.setSelection(teachPos);
            }
        } else {
            binding.tvAddClassTitle.setText("Add New Class");
            binding.buttonSaveCourse.setText("Add Class");
        }
    }

    private void setupSaveListener() {
        binding.buttonSaveCourse.setOnClickListener(v -> {
            // validation & save logic
            String title = binding.editTextTitle.getText().toString().trim();
            if (TextUtils.isEmpty(title)) {
                binding.editTextTitle.setError("Title required");
                Toast.makeText(getContext(), "Title is required", Toast.LENGTH_SHORT).show();
                return;
            }
            // days
            MaterialButtonToggleGroup tg = binding.toggleGroupDays.getRoot();
            StringBuilder sb = new StringBuilder();
            for (int id : tg.getCheckedButtonIds()) {
                MaterialButton btn = tg.findViewById(id);
                if (sb.length() > 0) sb.append(",");
                sb.append(btn.getText().toString());
            }
            String daysCsv = sb.toString();
            if (TextUtils.isEmpty(daysCsv)) {
                Toast.makeText(getContext(), "Please select at least one day", Toast.LENGTH_SHORT).show();
                return;
            }
            // time
            String time = binding.editTextTime.getText().toString().trim();
            if (TextUtils.isEmpty(time)) {
                binding.editTextTime.setError("Time required");
                Toast.makeText(getContext(), "Time is required", Toast.LENGTH_SHORT).show();
                return;
            }
            // capacity
            int capacity;
            try { capacity = Integer.parseInt(binding.editTextCapacity.getText().toString()); }
            catch (Exception e) {
                binding.editTextCapacity.setError("Valid capacity required");
                Toast.makeText(getContext(), "Please enter a valid capacity", Toast.LENGTH_SHORT).show();
                return;
            }
            // duration
            int duration;
            try { duration = Integer.parseInt(binding.editTextDuration.getText().toString()); }
            catch (Exception e) {
                binding.editTextDuration.setError("Valid duration required");
                Toast.makeText(getContext(), "Please enter a valid duration", Toast.LENGTH_SHORT).show();
                return;
            }
            // price
            double price;
            try { price = Double.parseDouble(binding.editTextPrice.getText().toString()); }
            catch (Exception e) {
                binding.editTextPrice.setError("Valid price required");
                Toast.makeText(getContext(), "Please enter a valid price", Toast.LENGTH_SHORT).show();
                return;
            }
            // teacher
            String teacher = binding.spinnerTeacher.getSelectedItem().toString();
            if ("Select Teacher".equals(teacher)) {
                Toast.makeText(getContext(), "Please select a teacher", Toast.LENGTH_SHORT).show();
                return;
            }
            // type
            String type = binding.spinnerClassType.getSelectedItem().toString();
            if ("Select Type".equals(type)) {
                Toast.makeText(getContext(), "Please select a class type", Toast.LENGTH_SHORT).show();
                return;
            }
            String desc = binding.editTextDescription.getText().toString().trim();

            boolean success;
            if (classId < 0) {
                success = dbHelper.insertClass(
                        title, teacher, daysCsv, time,
                        capacity, duration, price, type, desc
                );
            } else {
                success = dbHelper.updateClass(
                        classId, title, teacher, daysCsv, time,
                        capacity, duration, price, type, desc
                ) > 0;
            }

            if (success) {
                Toast.makeText(getContext(),
                        classId < 0 ? "Class added" : "Class updated",
                        Toast.LENGTH_SHORT
                ).show();
                NavController nav = NavHostFragment.findNavController(this);
                nav.navigateUp();
            } else {
                Toast.makeText(getContext(),
                        "Database error",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
