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
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.yogaappadmin.data.DatabaseHelper;
import com.example.yogaappadmin.databinding.FragmentYogatypeFormBinding;

public class YogaTypeFormFragment extends Fragment {

    private FragmentYogatypeFormBinding binding;
    private DatabaseHelper dbHelper;
    private Long typeId; // null for add, otherwise edit/delete

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentYogatypeFormBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dbHelper = new DatabaseHelper(requireContext());

        // Pre‑fill if editing
        if (getArguments() != null && getArguments().containsKey("typeId")) {
            typeId = getArguments().getLong("typeId");
            binding.etYogaTypeName.setText(getArguments().getString("typeName"));
            binding.etYogaTypeDescription.setText(getArguments().getString("typeDescription"));
        } else {
            typeId = null;
        }

        // Back arrow
        binding.buttonBack.setOnClickListener(v ->
                NavHostFragment.findNavController(this).navigateUp()
        );

        // Save
        binding.btnSaveType.setOnClickListener(v -> {
            String name = binding.etYogaTypeName.getText().toString().trim();
            String desc = binding.etYogaTypeDescription.getText().toString().trim();

            if (TextUtils.isEmpty(name)) {
                Toast.makeText(getContext(),
                        "Type name is required", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean ok;
            if (typeId == null) {
                ok = dbHelper.insertYogaType(name, desc);
            } else {
                ok = dbHelper.updateYogaType(typeId, name, desc) > 0;
            }

            if (ok) {
                Toast.makeText(getContext(),
                        "Class type saved", Toast.LENGTH_SHORT).show();
                NavHostFragment.findNavController(this).navigateUp();
            } else {
                Toast.makeText(getContext(),
                        "Error saving class type", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
