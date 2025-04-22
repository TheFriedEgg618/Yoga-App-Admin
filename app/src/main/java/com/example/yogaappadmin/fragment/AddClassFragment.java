package com.example.yogaappadmin.fragment;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import com.example.yogaappadmin.R;
import com.example.yogaappadmin.databinding.FragmentAddClassBinding;
import com.example.yogaappadmin.data.DatabaseHelper;
import com.example.yogaappadmin.viewmodel.AddClassViewModel;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class AddClassFragment extends Fragment {

    private AddClassViewModel mViewModel;
    private FragmentAddClassBinding binding;
    private DatabaseHelper dbHelper;

    // Hold pending values after validation
    private String pendingDay, pendingTime, pendingCapacityStr,
            pendingDurationStr, pendingPriceStr,
            pendingType, pendingDescription;

    public static AddClassFragment newInstance() {
        return new AddClassFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentAddClassBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel = new ViewModelProvider(this)
                .get(AddClassViewModel.class);

        dbHelper = new DatabaseHelper(requireContext());

        setupSpinners();
        setupListeners();
        observeViewModel();
    }

    private void setupSpinners() {
        List<String> days = Arrays.asList(
                "Select Day", "Monday", "Tuesday", "Wednesday",
                "Thursday", "Friday", "Saturday", "Sunday"
        );
        ArrayAdapter<String> dayAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                days
        );
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerDayOfWeek.setAdapter(dayAdapter);

        List<String> types = Arrays.asList(
                "Select Type", "Flow Yoga", "Aerial Yoga", "Family Yoga"
        );
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                types
        );
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerClassType.setAdapter(typeAdapter);
    }

    private void setupListeners() {
        // Time field → TimePickerDialog
        binding.editTextTime.setInputType(InputType.TYPE_NULL);
        binding.editTextTime.setOnClickListener(v -> showTimePicker());
        binding.editTextTime.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                showTimePicker();
                v.clearFocus();
            }
        });

        // Back navigation
        binding.buttonBack.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(this);
            navController.navigateUp();
        });

        // Save button listener
        binding.buttonSaveCourse.setOnClickListener(v -> {
            // Gather inputs
            pendingDay         = binding.spinnerDayOfWeek.getSelectedItem().toString();
            pendingTime        = binding.editTextTime.getText().toString().trim();
            pendingCapacityStr = binding.editTextCapacity.getText().toString().trim();
            pendingDurationStr = binding.editTextDuration.getText().toString().trim();
            pendingPriceStr    = binding.editTextPrice.getText().toString().trim();
            pendingType        = binding.spinnerClassType.getSelectedItem().toString();
            pendingDescription = binding.editTextDescription.getText().toString().trim();

            // Validate via ViewModel
            mViewModel.validateAndSave(
                    pendingDay,
                    pendingTime,
                    pendingCapacityStr,
                    pendingDurationStr,
                    pendingPriceStr,
                    pendingType,
                    pendingDescription
            );
        });
    }

    private void showTimePicker() {
        Calendar now = Calendar.getInstance();
        int hour   = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);

        new TimePickerDialog(
                requireContext(),
                (dialog, selHour, selMin) ->
                        binding.editTextTime.setText(
                                String.format("%02d:%02d", selHour, selMin)
                        ),
                hour, minute,
                true // 24h mode; false for AM/PM
        ).show();
    }

    private void observeViewModel() {
        mViewModel.getDayError().observe(getViewLifecycleOwner(), err -> {
            if (err != null) Toast.makeText(getContext(), err, Toast.LENGTH_SHORT).show();
        });

        mViewModel.getTimeError()
                .observe(getViewLifecycleOwner(), binding.editTextTime::setError);
        mViewModel.getCapacityError()
                .observe(getViewLifecycleOwner(), binding.editTextCapacity::setError);
        mViewModel.getDurationError()
                .observe(getViewLifecycleOwner(), binding.editTextDuration::setError);
        mViewModel.getPriceError()
                .observe(getViewLifecycleOwner(), binding.editTextPrice::setError);
        mViewModel.getTypeError().observe(getViewLifecycleOwner(), err -> {
            if (err != null) Toast.makeText(getContext(), err, Toast.LENGTH_SHORT).show();
        });

        mViewModel.getSaveResultSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success != null) {
                if (success) {
                    // Insert into SQLite
                    boolean inserted = dbHelper.insertClass(
                            pendingDay,
                            pendingTime,
                            Integer.parseInt(pendingCapacityStr),
                            Integer.parseInt(pendingDurationStr),
                            Double.parseDouble(pendingPriceStr),
                            pendingType,
                            pendingDescription
                    );
                    if (inserted) {
                        Toast.makeText(getContext(),
                                        "Class Saved Successfully!", Toast.LENGTH_SHORT)
                                .show();
                        NavHostFragment.findNavController(this)
                                .navigateUp();
                    } else {
                        Toast.makeText(getContext(),
                                        "Error saving to database.", Toast.LENGTH_SHORT)
                                .show();
                    }
                } else {
                    Toast.makeText(getContext(),
                                    "Please correct the errors.", Toast.LENGTH_SHORT)
                            .show();
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
