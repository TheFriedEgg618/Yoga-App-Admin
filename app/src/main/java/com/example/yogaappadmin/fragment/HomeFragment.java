package com.example.yogaappadmin.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.yogaappadmin.R;
import com.example.yogaappadmin.adapter.ClassAdapter;
import com.example.yogaappadmin.data.DatabaseHelper;
import com.example.yogaappadmin.databinding.FragmentHomeBinding;
import com.example.yogaappadmin.model.ClassModel;
import com.example.yogaappadmin.viewmodel.HomeViewModel;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;
    private DatabaseHelper dbHelper;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        dbHelper = new DatabaseHelper(requireContext());

        binding.recyclerViewClasses.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );
        ClassAdapter adapter = new ClassAdapter(new ClassAdapter.Listener() {
            @Override
            public void onEdit(ClassModel item) {
                // Build the bundle with all 8 args
                Bundle args = new Bundle();
                args.putLong("classId",      item.getId());
                args.putString("title",    item.getTitle());
                args.putString("dayCsv",     item.getDay());
                args.putString("time",       item.getTime());
                args.putInt("capacity",      item.getCapacity());
                args.putInt("duration",      item.getDuration());
                args.putFloat("price",       (float)item.getPrice());
                args.putString("teacher",  item.getTeacherName());
                args.putString("type",       item.getType());
                args.putString("description",item.getDescription());

                NavController nav = NavHostFragment.findNavController(HomeFragment.this);
                nav.navigate(R.id.action_navigation_home_to_ClassFormFragment, args);
            }

            @Override
            public void onDelete(ClassModel item) {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Delete Class")
                        .setMessage("Are you sure you want to delete this class?")
                        .setPositiveButton("Delete", (dlg, which) -> {
                            int rows = dbHelper.deleteClass(item.getId());
                            if (rows > 0) {
                                Toast.makeText(getContext(),
                                        "Class deleted", Toast.LENGTH_SHORT).show();
                                homeViewModel.refresh();
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

        homeViewModel.getClasses().observe(getViewLifecycleOwner(), list -> {
            adapter.setData(list);
            binding.tvUpcomingTitleCount.setText(String.valueOf(list.size()));
        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.btnAddClass.setOnClickListener(v -> {
            NavController nav = NavHostFragment.findNavController(this);
            // no bundle → defaults kick in → add‑mode
            nav.navigate(R.id.action_navigation_home_to_ClassFormFragment);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        homeViewModel.refresh();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
