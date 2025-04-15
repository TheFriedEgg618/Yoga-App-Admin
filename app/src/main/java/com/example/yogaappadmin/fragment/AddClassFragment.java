package com.example.yogaappadmin.fragment;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController; // Import NavController
import androidx.navigation.fragment.NavHostFragment; // Import NavHostFragment

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button; // Keep Button if still needed
import android.widget.EditText; // Keep EditText if still needed
import android.widget.ImageButton; // Import ImageButton
import android.widget.Spinner; // Keep Spinner if still needed
import android.widget.Toast;

import com.example.yogaappadmin.R;
// Import ViewBinding class
import com.example.yogaappadmin.databinding.FragmentAddClassBinding;
import com.example.yogaappadmin.viewmodel.AddClassViewModel;

import java.util.Arrays;
import java.util.List;

public class AddClassFragment extends Fragment {

    private AddClassViewModel mViewModel;
    // Declare binding variable
    private FragmentAddClassBinding binding;

    // Remove individual view variables if using binding
    // private Spinner spinnerDayOfWeek;
    // ... other views ...
    // private ImageButton buttonBack; // Not needed if using binding

    public static AddClassFragment newInstance() {
        return new AddClassFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout using View Binding
        binding = FragmentAddClassBinding.inflate(inflater, container, false);
        return binding.getRoot(); // Return the root from binding
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize ViewModel
        mViewModel = new ViewModelProvider(this).get(AddClassViewModel.class);

        // No need for findViews() if using binding

        // Setup Spinners (using binding)
        setupSpinners();

        // Setup Listeners (using binding)
        setupListeners();

        // Observe ViewModel LiveData (using binding)
        observeViewModel();
    }

    // Removed findViews() method as binding handles it.

    private void setupSpinners() {
        // Access views via binding object
        // --- Example Spinner Setup ---
        List<String> days = Arrays.asList("Select Day", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");
        ArrayAdapter<String> dayAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, days);
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerDayOfWeek.setAdapter(dayAdapter); // Use binding.spinnerDayOfWeek

        List<String> types = Arrays.asList("Select Type", "Flow Yoga", "Aerial Yoga", "Family Yoga");
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, types);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerClassType.setAdapter(typeAdapter); // Use binding.spinnerClassType
        // --- End Example Spinner Setup ---
    }


    private void setupListeners() {
        // Back Button Listener
        binding.buttonBack.setOnClickListener(v -> {
            // Use NavController to navigate Up or Pop BackStack
            NavController navController = NavHostFragment.findNavController(AddClassFragment.this);
            navController.navigateUp(); // Preferred way to go back respecting navigation hierarchy
            // Alternative: navController.popBackStack(); // Simply goes back one step
        });

        // Save Button Listener
        binding.buttonSaveCourse.setOnClickListener(v -> {
            // Gather input from UI elements using binding
            String day = binding.spinnerDayOfWeek.getSelectedItem() != null ? binding.spinnerDayOfWeek.getSelectedItem().toString() : "";
            String time = binding.editTextTime.getText().toString().trim();
            String capacityStr = binding.editTextCapacity.getText().toString().trim();
            String durationStr = binding.editTextDuration.getText().toString().trim();
            String priceStr = binding.editTextPrice.getText().toString().trim();
            String type = binding.spinnerClassType.getSelectedItem() != null ? binding.spinnerClassType.getSelectedItem().toString() : "";
            String description = binding.editTextDescription.getText().toString().trim();

            // Call ViewModel to validate and process
            mViewModel.validateAndSave(day, time, capacityStr, durationStr, priceStr, type, description);
        });
    }

    private void observeViewModel() {
        // Observe Error LiveData using binding
        mViewModel.getTimeError().observe(getViewLifecycleOwner(), error -> binding.editTextTime.setError(error));
        mViewModel.getCapacityError().observe(getViewLifecycleOwner(), error -> binding.editTextCapacity.setError(error));
        mViewModel.getDurationError().observe(getViewLifecycleOwner(), error -> binding.editTextDuration.setError(error));
        mViewModel.getPriceError().observe(getViewLifecycleOwner(), error -> binding.editTextPrice.setError(error));

        // Observe Spinner Errors (showing Toast as example)
        mViewModel.getDayError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
        mViewModel.getTypeError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        // Observe Save Result
        mViewModel.getSaveResultSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success != null) {
                if (success) {
                    Toast.makeText(getContext(), "Class Saved Successfully!", Toast.LENGTH_SHORT).show();
                    NavController navController = NavHostFragment.findNavController(AddClassFragment.this);
                    navController.navigateUp(); // Go back after successful save
                } else {
                    Toast.makeText(getContext(), "Please correct the errors.", Toast.LENGTH_SHORT).show();
                }
                mViewModel.resetSaveStatus();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Nullify the binding object
        binding = null;
    }
}