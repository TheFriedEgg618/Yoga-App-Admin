package com.example.yogaappadmin.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView; // Keep if you use ViewModel text later

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
// REMOVE FragmentManager and FragmentTransaction imports if they exist
// import androidx.fragment.app.FragmentManager;
// import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController; // Import NavController
import androidx.navigation.fragment.NavHostFragment; // Import NavHostFragment

import com.example.yogaappadmin.R; // Make sure R is imported
import com.example.yogaappadmin.adapter.ClassAdapter;
import com.example.yogaappadmin.databinding.FragmentHomeBinding;
import com.example.yogaappadmin.viewmodel.HomeViewModel;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding; // View Binding instance
    private HomeViewModel homeViewModel; // ViewModel instance

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Initialize ViewModel
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // Inflate the layout using View Binding
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ClassAdapter adapter = new ClassAdapter();
        binding.recyclerViewClasses.setAdapter(adapter);

        // Observe the LiveData and submit to adapter:
        homeViewModel.getClasses().observe(getViewLifecycleOwner(), list -> {
            adapter.setData(list);
            // optionally update the “Upcoming count” label:
            binding.tvUpcomingTitleCount.setText(String.valueOf(list.size()));
        });
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setup the click listener for the Add Class button
        setupAddButtonListener();
    }

    private void setupAddButtonListener() {
        // Access the button via the binding object
        binding.btnAddClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Find the NavController associated with this Fragment
                // Requires the Fragment to be hosted within a NavHostFragment
                NavController navController = NavHostFragment.findNavController(HomeFragment.this);

                // Navigate using the action ID defined in the navigation graph
                // IMPORTANT: Replace 'R.id.action_navigation_home_to_addClassFragment'
                // with the actual ID of the <action> you defined in your nav graph XML.
                navController.navigate(R.id.action_navigation_home_to_addClassFragment); // <<<--- REPLACE THIS ACTION ID

                // --- Remove the old manual transaction code ---
                /*
                AddClassFragment addClassFragment = AddClassFragment.newInstance();
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.nav_host_fragment_container, addClassFragment); // <<<--- OLD CODE
                transaction.addToBackStack("HomeToAddClass"); // <<<--- OLD CODE
                transaction.commit(); // <<<--- OLD CODE
                */
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Force reload of data whenever this fragment resumes
        homeViewModel.refresh();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Release the binding instance when the view is destroyed to prevent memory leaks
        binding = null;
    }
}