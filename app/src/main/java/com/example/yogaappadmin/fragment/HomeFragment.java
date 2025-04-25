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
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.yogaappadmin.R;
import com.example.yogaappadmin.adapter.ClassAdapter;
import com.example.yogaappadmin.data.DatabaseHelper;
import com.example.yogaappadmin.databinding.FragmentHomeBinding;
import com.example.yogaappadmin.model.ClassModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private DatabaseHelper       dbHelper;
    private ClassAdapter         adapter;

    // Formatter & day‐of‐week map
    private final SimpleDateFormat timeFmt = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private static final Map<String,Integer> DOW = new HashMap<>();
    static {
        DOW.put("Sun", Calendar.SUNDAY);
        DOW.put("Mon", Calendar.MONDAY);
        DOW.put("Tue", Calendar.TUESDAY);
        DOW.put("Wed", Calendar.WEDNESDAY);
        DOW.put("Thu", Calendar.THURSDAY);
        DOW.put("Fri", Calendar.FRIDAY);
        DOW.put("Sat", Calendar.SATURDAY);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding      = FragmentHomeBinding.inflate(inflater, container, false);
        dbHelper     = new DatabaseHelper(requireContext());

        // RecyclerView + adapter setup
        binding.recyclerViewClasses.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );
        adapter = new ClassAdapter(new ClassAdapter.Listener() {
            @Override
            public void onEdit(@NonNull ClassModel item) {
                Bundle args = new Bundle();
                args.putLong  ("classId",      item.getId());
                args.putString("title",      item.getTitle());
                args.putString("dayCsv",     item.getDay());
                args.putString("time",       item.getTime());
                args.putInt   ("capacity",      item.getCapacity());
                args.putInt   ("duration",      item.getDuration());
                args.putFloat ("price",       (float)item.getPrice());
                args.putString("teacher",    item.getTeacherName());
                args.putString("type",       item.getType());
                args.putString("description",item.getDescription());

                NavController nav = NavHostFragment.findNavController(HomeFragment.this);
                nav.navigate(R.id.action_navigation_home_to_ClassFormFragment, args);
            }

            @Override
            public void onDelete(@NonNull ClassModel item) {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Delete Class")
                        .setMessage("Are you sure you want to delete this class?")
                        .setPositiveButton("Delete", (dlg, which) -> {
                            int rows = dbHelper.deleteClass(item.getId());
                            if (rows > 0) {
                                Toast.makeText(getContext(),
                                        "Class deleted", Toast.LENGTH_SHORT).show();
                                loadUpcomingThisWeek();
                            } else {
                                Toast.makeText(getContext(),
                                        "Error deleting class", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
            @Override
            public void onItemClick(@NonNull ClassModel item) {
                Bundle args = new Bundle();
                args.putLong   ("classId",      item.getId());
                args.putString ("title",        item.getTitle());
                args.putString ("dayCsv",       item.getDay());
                args.putString ("time",         item.getTime());
                args.putInt    ("capacity",     item.getCapacity());
                args.putInt    ("duration",     item.getDuration());
                args.putFloat  ("price",        (float)item.getPrice());
                args.putString ("teacher",      item.getTeacherName());
                args.putString ("type",         item.getType());
                args.putString ("description",  item.getDescription());

                NavController nav = NavHostFragment.findNavController(HomeFragment.this);
                nav.navigate(R.id.action_navigation_home_to_navigation_class_details, args);
            }
        });
        binding.recyclerViewClasses.setAdapter(adapter);

        // Add‐button
        binding.btnAddClass.setOnClickListener(v -> {
            NavController nav = NavHostFragment.findNavController(this);
            nav.navigate(R.id.action_navigation_home_to_ClassFormFragment);
        });

        // Initial load
        loadUpcomingThisWeek();

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUpcomingThisWeek();
    }

    /** Load, filter for later this week, sort, and display. */
    private void loadUpcomingThisWeek() {
        List<ClassModel> all = dbHelper.getAllClassesList();
        List<ClassModel> upcoming = new ArrayList<>();

        Calendar now     = Calendar.getInstance();
        int      todayDow = now.get(Calendar.DAY_OF_WEEK);
        Date     nowTime  = now.getTime();

        for (ClassModel item : all) {
            String[] parts = item.getDay().split(",");
            for (String d : parts) {
                Integer dayCode = DOW.get(d);
                if (dayCode == null) continue;

                // Future weekday
                if (dayCode > todayDow) {
                    upcoming.add(item);
                    break;
                }

                // Today but later time
                if (dayCode == todayDow) {
                    try {
                        Date classTime = timeFmt.parse(item.getTime());
                        Calendar classCal = (Calendar) now.clone();
                        classCal.set(Calendar.HOUR_OF_DAY, classTime.getHours());
                        classCal.set(Calendar.MINUTE,     classTime.getMinutes());
                        classCal.set(Calendar.SECOND,     0);
                        if (classCal.getTime().after(nowTime)) {
                            upcoming.add(item);
                        }
                    } catch (ParseException ignored) { }
                    break;
                }
                // Past day → skip
            }
        }

        // Sort by day‐of‐week, then by time
        Collections.sort(upcoming, (a, b) -> {
            int da = firstDowIndex(a.getDay());
            int db = firstDowIndex(b.getDay());
            if (da != db) return da - db;
            try {
                Date ta = timeFmt.parse(a.getTime());
                Date tb = timeFmt.parse(b.getTime());
                return ta.compareTo(tb);
            } catch (ParseException e) {
                return 0;
            }
        });

        // Display & count
        adapter.setData(upcoming);
        binding.tvUpcomingTitleCount.setText(String.valueOf(upcoming.size()));
    }

    /** Returns the first valid Calendar.DAY_OF_WEEK from the CSV */
    private int firstDowIndex(String csv) {
        for (String d : csv.split(",")) {
            Integer code = DOW.get(d.trim());
            if (code != null) return code;
        }
        return Integer.MAX_VALUE;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
