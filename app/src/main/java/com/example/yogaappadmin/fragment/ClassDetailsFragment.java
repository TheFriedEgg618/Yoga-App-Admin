package com.example.yogaappadmin.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.yogaappadmin.databinding.FragmentClassDetailsBinding;

public class ClassDetailsFragment extends Fragment {

    private FragmentClassDetailsBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentClassDetailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Back button
        binding.buttonBack.setOnClickListener(v ->
                NavHostFragment.findNavController(this).navigateUp()
        );

        // Read nav arguments
        Bundle args = getArguments();
        if (args != null) {
            String title       = args.getString("title", "");
            String dayCsv      = args.getString("dayCsv", "");
            String time        = args.getString("time", "");
            int    capacity    = args.getInt   ("capacity", 0);
            int    duration    = args.getInt   ("duration", 0);
            float  price       = args.getFloat ("price", 0f);
            String teacher     = args.getString("teacher", "");
            String type        = args.getString("type", "");
            String description = args.getString("description", "");

            // Populate UI
            binding.tvClassTitle.setText(title);
            binding.tvDay.setText(dayCsv);
            binding.tvTime.setText(time);
            binding.tvCapacity.setText(capacity + " students");
            binding.tvDuration.setText(duration + " minutes");
            binding.tvPrice.setText(String.format("£%.2f", price));
            binding.tvTeacher.setText(teacher);
            binding.tvClassType.setText(type);

            // Description may be empty
            if (description.isEmpty()) {
                binding.tvDescription.setText("No description provided.");
            } else {
                binding.tvDescription.setText(description);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
