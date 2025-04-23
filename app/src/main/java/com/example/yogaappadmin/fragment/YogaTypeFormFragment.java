package com.example.yogaappadmin.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.yogaappadmin.data.DatabaseHelper;
import com.example.yogaappadmin.databinding.FragmentYogaTypeFormBinding;

public class YogaTypeFormFragment extends Fragment {

    private FragmentYogaTypeFormBinding binding;
    private DatabaseHelper dbHelper;
    // if >= 0, we're editing an existing type
    private long typeId = -1;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentYogaTypeFormBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dbHelper = new DatabaseHelper(requireContext());

        setupBackButton();
        prefillIfEditMode();
        setupSaveButton();
    }

    private void setupBackButton() {
        binding.buttonBack.setOnClickListener(v ->
                NavHostFragment.findNavController(this).navigateUp()
        );
    }

    private void prefillIfEditMode() {
        Bundle args = getArguments();
        if (args != null && args.containsKey("typeId") && args.getLong("typeId", -1L) >= 0) {
            typeId = args.getLong("typeId");

            binding.etYogaTypeName.setText(args.getString("typeName", ""));
            binding.etYogaTypeDescription.setText(args.getString("description", ""));

            binding.tvYogaTypeFormTitle.setText("Edit Class Type");
            binding.btnSaveType.setText("Update Type");
        } else {
            binding.tvYogaTypeFormTitle.setText("Add Class Type");
            binding.btnSaveType.setText("Save Type");
        }
    }

    private void setupSaveButton() {
        binding.btnSaveType.setOnClickListener(v -> {
            String name = binding.etYogaTypeName.getText().toString().trim();
            if (TextUtils.isEmpty(name)) {
                binding.etYogaTypeName.setError("Name required");
                Toast.makeText(requireContext(),
                        "Name is required",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            String desc = binding.etYogaTypeDescription.getText().toString().trim();

            boolean ok;
            if (typeId < 0) {
                ok = dbHelper.insertYogaType(name, desc);
            } else {
                ok = dbHelper.updateYogaType(typeId, name, desc) > 0;
            }

            Toast.makeText(
                    requireContext(),
                    ok
                            ? (typeId < 0 ? "Class type added" : "Class type updated")
                            : "Database error",
                    Toast.LENGTH_SHORT
            ).show();

            if (ok) {
                NavHostFragment.findNavController(this).navigateUp();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
