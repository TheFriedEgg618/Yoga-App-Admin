package com.example.yogaappadmin.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.yogaappadmin.data.DatabaseHelper;
import com.example.yogaappadmin.model.ClassModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class HomeViewModel extends AndroidViewModel {
    private final DatabaseHelper dbHelper;
    private final MutableLiveData<List<ClassModel>> classes = new MutableLiveData<>();

    // Map day abbreviations to Calendar.DAY_OF_WEEK
    private static final Map<String, Integer> DOW = new HashMap<>();
    static {
        DOW.put("Sun", Calendar.SUNDAY);
        DOW.put("Mon", Calendar.MONDAY);
        DOW.put("Tue", Calendar.TUESDAY);
        DOW.put("Wed", Calendar.WEDNESDAY);
        DOW.put("Thu", Calendar.THURSDAY);
        DOW.put("Fri", Calendar.FRIDAY);
        DOW.put("Sat", Calendar.SATURDAY);
    }

    private final SimpleDateFormat timeFmt = new SimpleDateFormat("HH:mm", Locale.getDefault());

    public HomeViewModel(@NonNull Application app) {
        super(app);
        dbHelper = new DatabaseHelper(app);
        loadUpcomingThisWeek();
    }

    /** Call this to reload (e.g. in onResume) */
    public void refresh() {
        loadUpcomingThisWeek();
    }

    private void loadUpcomingThisWeek() {
        List<ClassModel> all = dbHelper.getAllClassesList();
        List<ClassModel> upcoming = new ArrayList<>();

        Calendar now = Calendar.getInstance();
        int todayDow = now.get(Calendar.DAY_OF_WEEK);
        Date nowTime = now.getTime();

        for (ClassModel item : all) {
            // item.getDay() is CSV of abbreviations, e.g. "Mon,Wed,Fri"
            String[] parts = item.getDay().split(",");
            for (String d : parts) {
                Integer dayCode = DOW.get(d);
                if (dayCode == null) continue;

                // if the class day is later in the week, keep it
                if (dayCode > todayDow) {
                    upcoming.add(item);
                    break;
                }

                // if it's today, only keep if time is in the future
                if (dayCode == todayDow) {
                    try {
                        Date classTime = timeFmt.parse(item.getTime());
                        // build a Date todayClassDate with today's date and classTime
                        Calendar classCal = (Calendar) now.clone();
                        classCal.set(Calendar.HOUR_OF_DAY, classTime.getHours());
                        classCal.set(Calendar.MINUTE, classTime.getMinutes());
                        classCal.set(Calendar.SECOND, 0);
                        if (classCal.getTime().after(nowTime)) {
                            upcoming.add(item);
                        }
                    } catch (ParseException e) {
                        // if parsing fails, skip this entry
                    }
                    break;
                }
                // if dayCode < todayDow, that day has passed; skip
            }
        }

        // Optional: sort by day-of-week then time
        Collections.sort(upcoming, (a, b) -> {
            // compare first upcoming day in each CSV
            int da = firstDowIndex(a.getDay());
            int dbHelper = firstDowIndex(b.getDay());
            if (da != dbHelper) return da - dbHelper;
            // then compare times
            try {
                Date ta = timeFmt.parse(a.getTime());
                Date tb = timeFmt.parse(b.getTime());
                return ta.compareTo(tb);
            } catch (ParseException e) {
                return 0;
            }
        });

        classes.setValue(upcoming);
    }

    private int firstDowIndex(String csv) {
        String[] parts = csv.split(",");
        for (String d : parts) {
            Integer code = DOW.get(d);
            if (code != null) return code;
        }
        return Integer.MAX_VALUE;
    }

    /** Expose immutable LiveData */
    public LiveData<List<ClassModel>> getClasses() {
        return classes;
    }
}
