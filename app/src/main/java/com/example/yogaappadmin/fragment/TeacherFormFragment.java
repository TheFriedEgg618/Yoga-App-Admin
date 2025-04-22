package com.example.yogaappadmin.fragment;

import android.app.AlertDialog;
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
import com.example.yogaappadmin.R;
import com.example.yogaappadmin.data.DatabaseHelper;
import com.example.yogaappadmin.databinding.FragmentTeacherFormBinding;
import com.example.yogaappadmin.model.YogaTypeModel;
import java.util.ArrayList;
import java.util.List;

public class TeacherFormFragment extends Fragment {

    private FragmentTeacherFormBinding binding;
    private DatabaseHelper dbHelper;
    private long teacherId = -1;

    // back up of all yoga‐type names
    private List<String> allTypes;
    // which ones are currently selected
    private boolean[] checkedTypes;
    // to rebuild CSV
    private List<String> selectedTypeNames = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTeacherFormBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dbHelper = new DatabaseHelper(requireContext());

        setupBackButton();
        loadYogaTypes();
        prefillIfEdit();
        setupClassesField();
        setupSaveButton();
    }

    private void setupBackButton() {
        binding.buttonBack.setOnClickListener(v ->
                NavHostFragment.findNavController(this).navigateUp()
        );
    }

    private void loadYogaTypes() {
        // fetch all yoga‐types
        List<YogaTypeModel> types = dbHelper.getAllYogaTypesModels();
        allTypes = new ArrayList<>(types.size());
        for (YogaTypeModel t : types) {
            allTypes.add(t.getTypeName());
        }
        checkedTypes = new boolean[allTypes.size()];
    }

    private void prefillIfEdit() {
        Bundle args = getArguments();
        if (args != null && args.containsKey("teacherId") && args.getLong("teacherId", -1L) >= 0) {
            teacherId = args.getLong("teacherId");

            binding.etTeacherName.setText(args.getString("name",""));
            binding.etTeacherBio.setText(args.getString("bio",""));

            String csv = args.getString("classesCsv","");
            if (!TextUtils.isEmpty(csv)) {
                String[] parts = csv.split(",");
                // mark which types to check
                for (int i = 0; i < allTypes.size(); i++) {
                    if (contains(parts, allTypes.get(i))) {
                        checkedTypes[i] = true;
                        selectedTypeNames.add(allTypes.get(i));
                    }
                }
                // show CSV in the field
                binding.etTeacherClasses.setText(TextUtils.join(", ", selectedTypeNames));
            }
            binding.tvTeacherFormTitle.setText("Edit Teacher");
            binding.btnSaveTeacher.setText("Update Teacher");
        } else {
            binding.tvTeacherFormTitle.setText("Add Teacher");
            binding.btnSaveTeacher.setText("Save Teacher");
        }
    }

    private boolean contains(String[] arr, String s) {
        for (String x : arr) if (x.equals(s)) return true;
        return false;
    }

    private void setupClassesField() {
        binding.etTeacherClasses.setFocusable(false);
        binding.etTeacherClasses.setClickable(true);
        binding.etTeacherClasses.setOnClickListener(v -> {
            // show multi‐choice dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Select Classes They Teach");
            builder.setMultiChoiceItems(
                    allTypes.toArray(new CharSequence[0]),
                    checkedTypes,
                    (dialog, which, isChecked) -> {
                        checkedTypes[which] = isChecked;
                    }
            );
            builder.setPositiveButton("OK", (dialog, which) -> {
                selectedTypeNames.clear();
                for (int i = 0; i < allTypes.size(); i++) {
                    if (checkedTypes[i]) {
                        selectedTypeNames.add(allTypes.get(i));
                    }
                }
                binding.etTeacherClasses.setText(
                        TextUtils.join(", ", selectedTypeNames)
                );
            });
            builder.setNegativeButton("Cancel", null);
            builder.show();
        });
    }

    private void setupSaveButton() {
        binding.btnSaveTeacher.setOnClickListener(v -> {
            String name = binding.etTeacherName.getText().toString().trim();
            if (TextUtils.isEmpty(name)) {
                binding.etTeacherName.setError("Name required");
                return;
            }
            if (selectedTypeNames.isEmpty()) {
                Toast.makeText(requireContext(),
                        "Please select at least one class",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            String bio = binding.etTeacherBio.getText().toString().trim();
            String classesCsv = TextUtils.join(",", selectedTypeNames);

            boolean ok;
            if (teacherId < 0) {
                ok = dbHelper.insertTeacher(name, bio, classesCsv);
            } else {
                ok = dbHelper.updateTeacher(teacherId, name, bio, classesCsv) > 0;
            }

            Toast.makeText(
                    requireContext(),
                    ok
                            ? (teacherId<0 ? "Teacher added" : "Teacher updated")
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
