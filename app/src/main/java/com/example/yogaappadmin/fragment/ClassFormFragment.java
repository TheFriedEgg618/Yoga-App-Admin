package com.example.yogaappadmin.fragment;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.yogaappadmin.data.DatabaseHelper;
import com.example.yogaappadmin.databinding.FragmentClassFormBinding;
import com.example.yogaappadmin.model.TeacherModel;
import com.example.yogaappadmin.model.YogaTypeModel;
import com.example.yogaappadmin.viewmodel.AddClassViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ClassFormFragment extends Fragment {

    private FragmentClassFormBinding binding;
    private AddClassViewModel       mViewModel;
    private DatabaseHelper          dbHelper;

    private long classId = -1;

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

        // ViewModel + DB
        mViewModel = new ViewModelProvider(this).get(AddClassViewModel.class);
        dbHelper   = new DatabaseHelper(requireContext());

        // Spinners & pickers
        setupTeacherSpinner();
        setupTypeSpinner();
        setupTimePicker();

        // Back nav
        binding.buttonBack.setOnClickListener(v ->
                NavHostFragment.findNavController(this).navigateUp()
        );

        prefillIfEdit();
        setupSaveListener();
        observeViewModel();
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
    }

    private void setupTypeSpinner() {
        List<YogaTypeModel> types = dbHelper.getAllYogaTypesModels();
        List<String> names = new ArrayList<>();
        names.add("Select Type");
        for (YogaTypeModel y : types) names.add(y.getTypeName());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                names
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
            // --- EDIT MODE ---
            classId = args.getLong("classId");

            // Header & button
            binding.tvAddClassTitle.setText("Edit Class");
            binding.buttonSaveCourse.setText("Update Class");

            // Title
            binding.editTextTitle.setText(args.getString("title", ""));

            // Days
            String dayCsv = args.getString("dayCsv", "");
            if (!TextUtils.isEmpty(dayCsv)) {
                MaterialButtonToggleGroup tg = binding.toggleGroupDays.getRoot();
                for (String d : dayCsv.split(",")) {
                    int btnId = getResources().getIdentifier(
                            "btn" + d, "id", requireContext().getPackageName()
                    );
                    if (btnId != 0) tg.check(btnId);
                }
            }

            // Time / capacity / duration / price
            binding.editTextTime.setText(args.getString("time", ""));
            binding.editTextCapacity.setText(String.valueOf(args.getInt("capacity", 0)));
            binding.editTextDuration.setText(String.valueOf(args.getInt("duration", 0)));
            binding.editTextPrice.setText(String.valueOf(args.getDouble("price", 0.0)));

            // Teacher spinner
            String teacher = args.getString("teacher", "");
            @SuppressWarnings("unchecked")
            ArrayAdapter<String> teachAdapter =
                    (ArrayAdapter<String>) binding.spinnerTeacher.getAdapter();
            int posT = teachAdapter.getPosition(teacher);
            if (posT >= 0) binding.spinnerTeacher.setSelection(posT);

            // Class-type spinner
            String type = args.getString("type", "");
            @SuppressWarnings("unchecked")
            ArrayAdapter<String> typeAdapter =
                    (ArrayAdapter<String>) binding.spinnerClassType.getAdapter();
            int posC = typeAdapter.getPosition(type);
            if (posC >= 0) binding.spinnerClassType.setSelection(posC);

            // Description
            binding.editTextDescription.setText(args.getString("description", ""));

        } else {
            // --- ADD MODE ---
            binding.tvAddClassTitle.setText("Add New Class");
            binding.buttonSaveCourse.setText("Add Class");
        }
    }

    private void setupSaveListener() {
        binding.buttonSaveCourse.setOnClickListener(v -> {
            // Title
            String title = binding.editTextTitle.getText().toString().trim();
            if (TextUtils.isEmpty(title)) {
                binding.editTextTitle.setError("Title required");
                return;
            }

            // Days CSV
            MaterialButtonToggleGroup tg = binding.toggleGroupDays.getRoot();
            StringBuilder sb = new StringBuilder();
            for (int id : tg.getCheckedButtonIds()) {
                MaterialButton btn = tg.findViewById(id);
                if (sb.length() > 0) sb.append(",");
                sb.append(btn.getText().toString());
            }
            String daysCsv = sb.toString();

            // Other fields
            String time    = binding.editTextTime.getText().toString().trim();
            String cap     = binding.editTextCapacity.getText().toString().trim();
            String dur     = binding.editTextDuration.getText().toString().trim();
            String pr      = binding.editTextPrice.getText().toString().trim();
            String teacher = binding.spinnerTeacher.getSelectedItem().toString();
            String type    = binding.spinnerClassType.getSelectedItem().toString();
            String desc    = binding.editTextDescription.getText().toString().trim();

            // Quick checks
            if ("Select Teacher".equals(teacher)) {
                Toast.makeText(getContext(), "Teacher required", Toast.LENGTH_SHORT).show();
                return;
            }
            if ("Select Type".equals(type)) {
                Toast.makeText(getContext(), "Class type required", Toast.LENGTH_SHORT).show();
                return;
            }

            // Trigger ViewModel validation
            mViewModel.validateAndSave(daysCsv, time, cap, dur, pr, type, desc);

            // Save/update into DB on success
            mViewModel.getSaveResultSuccess().observe(getViewLifecycleOwner(), ok -> {
                if (ok == null) return;
                if (ok) {
                    boolean result;
                    if (classId < 0) {
                        result = dbHelper.insertClass(
                                title, teacher, daysCsv, time,
                                Integer.parseInt(cap),
                                Integer.parseInt(dur),
                                Double.parseDouble(pr),
                                type, desc
                        );
                    } else {
                        result = dbHelper.updateClass(
                                classId, title, teacher, daysCsv, time,
                                Integer.parseInt(cap),
                                Integer.parseInt(dur),
                                Double.parseDouble(pr),
                                type, desc
                        ) > 0;
                    }

                    Toast.makeText(
                            getContext(),
                            result ? (classId<0 ? "Class added" : "Class updated")
                                    : "Database error",
                            Toast.LENGTH_SHORT
                    ).show();

                    if (result) {
                        NavHostFragment.findNavController(this).navigateUp();
                    }

                } else {
                    Toast.makeText(getContext(),
                            "Please correct the errors", Toast.LENGTH_SHORT).show();
                }
                mViewModel.resetSaveStatus();
            });
        });
    }

    private void observeViewModel() {
        mViewModel.getDayError().observe(getViewLifecycleOwner(),
                err -> { if (err!=null) Toast.makeText(getContext(), err, Toast.LENGTH_SHORT).show(); });
        mViewModel.getTimeError().observe(getViewLifecycleOwner(),
                binding.editTextTime::setError);
        mViewModel.getCapacityError().observe(getViewLifecycleOwner(),
                binding.editTextCapacity::setError);
        mViewModel.getDurationError().observe(getViewLifecycleOwner(),
                binding.editTextDuration::setError);
        mViewModel.getPriceError().observe(getViewLifecycleOwner(),
                binding.editTextPrice::setError);
        mViewModel.getTypeError().observe(getViewLifecycleOwner(),
                err -> { if (err!=null) Toast.makeText(getContext(), err, Toast.LENGTH_SHORT).show(); });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
