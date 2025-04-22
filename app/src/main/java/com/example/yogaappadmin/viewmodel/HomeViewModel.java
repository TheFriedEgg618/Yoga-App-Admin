package com.example.yogaappadmin.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.yogaappadmin.data.DatabaseHelper;
import com.example.yogaappadmin.model.ClassModel;
import java.util.List;

public class HomeViewModel extends AndroidViewModel {
    private final DatabaseHelper db;
    private final MutableLiveData<List<ClassModel>> classes = new MutableLiveData<>();

    public HomeViewModel(@NonNull Application app) {
        super(app);
        db = new DatabaseHelper(app);
        loadClasses();
    }

    private void loadClasses() {
        List<ClassModel> list = db.getAllClassesList();
        classes.setValue(list);
    }

    /** Expose immutable LiveData */
    public LiveData<List<ClassModel>> getClasses() {
        return classes;
    }

    /** Refresh data from DB */
    public void refresh() {
        loadClasses();
    }
}
