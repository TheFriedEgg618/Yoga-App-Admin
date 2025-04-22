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
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.yogaappadmin.data.DatabaseHelper;
import com.example.yogaappadmin.databinding.FragmentClassFormBinding;
import com.example.yogaappadmin.viewmodel.AddClassViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class AddClassFragment extends Fragment {

    private FragmentClassFormBinding binding;
    private AddClassViewModel mViewModel;
    private DatabaseHelper dbHelper;

    // toggle‑group for days
    private MaterialButtonToggleGroup toggleDays;

    // pending values after validation
    private String pendingDaysCsv,
            pendingTime,
            pendingCapacityStr,
            pendingDurationStr,
            pendingPriceStr,
            pendingType,
            pendingDescription;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentClassFormBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // ViewModel & DB
        mViewModel = new ViewModelProvider(this).get(AddClassViewModel.class);
        dbHelper   = new DatabaseHelper(requireContext());

        setupDayToggle();
        setupSpinners();
        setupTimePicker();
        setupNavigation();
        setupSaveListener();
        observeViewModel();
    }

    private void setupDayToggle() {
        toggleDays = binding.toggleGroupDays.getRoot();
    }

    private void setupSpinners() {
        // Class‑type spinner
        List<String> types = Arrays.asList(
                "Select Type", "Flow Yoga", "Aerial Yoga", "Family Yoga"
        );
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                types
        );
        typeAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );
        binding.spinnerClassType.setAdapter(typeAdapter);
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
        int hour   = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);

        new TimePickerDialog(
                requireContext(),
                (tp, selHour, selMin) ->
                        binding.editTextTime.setText(
                                String.format("%02d:%02d", selHour, selMin)
                        ),
                hour, minute,
                true
        ).show();
    }

    private void setupNavigation() {
        // Back arrow
        binding.buttonBack.setOnClickListener(v -> {
            NavController nav = NavHostFragment.findNavController(this);
            nav.navigateUp();
        });
    }

    private void setupSaveListener() {
        binding.buttonSaveCourse.setOnClickListener(v -> {
            // 1) Gather multi‑day selection
            List<Integer> checkedIds = toggleDays.getCheckedButtonIds();
            List<String>  days       = new ArrayList<>();
            for (int id : checkedIds) {
                MaterialButton btn = binding.getRoot().findViewById(id);
                days.add(btn.getText().toString());
            }
            pendingDaysCsv = TextUtils.join(",", days);

            // 2) Gather other inputs
            pendingTime         = binding.editTextTime.getText().toString().trim();
            pendingCapacityStr  = binding.editTextCapacity.getText().toString().trim();
            pendingDurationStr  = binding.editTextDuration.getText().toString().trim();
            pendingPriceStr     = binding.editTextPrice.getText().toString().trim();
            pendingType         = binding.spinnerClassType.getSelectedItem().toString();
            pendingDescription  = binding.editTextDescription.getText().toString().trim();

            // 3) Validate via ViewModel
            mViewModel.validateAndSave(
                    pendingDaysCsv,
                    pendingTime,
                    pendingCapacityStr,
                    pendingDurationStr,
                    pendingPriceStr,
                    pendingType,
                    pendingDescription
            );
        });
    }

    private void observeViewModel() {
        // Day error → toast (toggleGroup doesn't support setError)
        mViewModel.getDayError().observe(getViewLifecycleOwner(), err -> {
            if (err != null) {
                Toast.makeText(getContext(), err, Toast.LENGTH_SHORT).show();
            }
        });

        // Field errors
        mViewModel.getTimeError()
                .observe(getViewLifecycleOwner(), binding.editTextTime::setError);
        mViewModel.getCapacityError()
                .observe(getViewLifecycleOwner(), binding.editTextCapacity::setError);
        mViewModel.getDurationError()
                .observe(getViewLifecycleOwner(), binding.editTextDuration::setError);
        mViewModel.getPriceError()
                .observe(getViewLifecycleOwner(), binding.editTextPrice::setError);
        mViewModel.getTypeError().observe(getViewLifecycleOwner(), err -> {
            if (err != null) {
                Toast.makeText(getContext(), err, Toast.LENGTH_SHORT).show();
            }
        });

        // Save result: on success, insert into SQLite
        mViewModel.getSaveResultSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success != null) {
                if (success) {
                    boolean ok = dbHelper.insertClass(
                            pendingDaysCsv,
                            pendingTime,
                            Integer.parseInt(pendingCapacityStr),
                            Integer.parseInt(pendingDurationStr),
                            Double.parseDouble(pendingPriceStr),
                            pendingType,
                            pendingDescription
                    );
                    if (ok) {
                        Toast.makeText(
                                getContext(),
                                "Class saved successfully!",
                                Toast.LENGTH_SHORT
                        ).show();
                        NavHostFragment.findNavController(this).navigateUp();
                    } else {
                        Toast.makeText(
                                getContext(),
                                "Error saving to database.",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                } else {
                    Toast.makeText(
                            getContext(),
                            "Please correct the errors.",
                            Toast.LENGTH_SHORT
                    ).show();
                }
                mViewModel.resetSaveStatus();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
