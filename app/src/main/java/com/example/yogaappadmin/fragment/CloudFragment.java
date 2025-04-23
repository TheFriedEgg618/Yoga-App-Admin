package com.example.yogaappadmin.fragment;

import android.app.AlertDialog;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.yogaappadmin.data.DatabaseHelper;
import com.example.yogaappadmin.databinding.FragmentCloudBinding;
import com.example.yogaappadmin.model.ClassModel;
import com.example.yogaappadmin.model.TeacherModel;
import com.example.yogaappadmin.model.YogaTypeModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class CloudFragment extends Fragment {
    private static final String TAG = "CloudFragment";

    private FragmentCloudBinding binding;
    private DatabaseHelper dbHelper;
    private DatabaseReference classesRef;
    private DatabaseReference teachersRef;
    private DatabaseReference typesRef;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCloudBinding.inflate(inflater, container, false);

        // initialize local DB and Firebase refs
        dbHelper     = new DatabaseHelper(requireContext());
        FirebaseDatabase fb = FirebaseDatabase.getInstance();
        classesRef   = fb.getReference("classes");
        teachersRef  = fb.getReference("teachers");
        typesRef     = fb.getReference("class_types");

        // confirm before push/pull
        binding.btnPushCloud.setOnClickListener(v -> showPushConfirmation());
        binding.btnPullCloud.setOnClickListener(v -> showPullConfirmation());

        return binding.getRoot();
    }

    private void showPushConfirmation() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Confirm Push")
                .setMessage(
                        "This will overwrite all data in Firebase with your local database.\n"
                                + "This action cannot be undone.\n\nProceed?"
                )
                .setPositiveButton("Yes", (dlg, which) -> actuallyPush())
                .setNegativeButton("No", null)
                .show();
    }

    private void showPullConfirmation() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Confirm Pull")
                .setMessage(
                        "This will overwrite your local database with data from Firebase.\n"
                                + "This action cannot be undone.\n\nProceed?"
                )
                .setPositiveButton("Yes", (dlg, which) -> actuallyPull())
                .setNegativeButton("No", null)
                .show();
    }

    private void actuallyPush() {
        Log.d(TAG, "actuallyPush(): START");
        AlertDialog progress = new AlertDialog.Builder(requireContext())
                .setMessage("Pushing local data to Firebase…")
                .setCancelable(false)
                .show();

        // 1) remove all remote nodes in sequence
        classesRef.removeValue((e1, r1) -> {
            if (e1 != null) Log.e(TAG, "Failed to clear remote classes", e1.toException());
            else Log.d(TAG, "Remote classes cleared");

            teachersRef.removeValue((e2, r2) -> {
                if (e2 != null) Log.e(TAG, "Failed to clear remote teachers", e2.toException());
                else Log.d(TAG, "Remote teachers cleared");

                typesRef.removeValue((e3, r3) -> {
                    if (e3 != null) Log.e(TAG, "Failed to clear remote types", e3.toException());
                    else Log.d(TAG, "Remote types cleared");

                    // 2) push local classes
                    List<ClassModel> classes = dbHelper.getAllClassesList();
                    for (ClassModel c : classes) {
                        classesRef.child(String.valueOf(c.getId()))
                                .setValue(c);
                    }
                    // 3) push local teachers
                    List<TeacherModel> teachers = dbHelper.getAllTeachersModels();
                    for (TeacherModel t : teachers) {
                        teachersRef.child(String.valueOf(t.getId()))
                                .setValue(t);
                    }
                    // 4) push local types
                    List<YogaTypeModel> types = dbHelper.getAllYogaTypesModels();
                    for (YogaTypeModel y : types) {
                        typesRef.child(String.valueOf(y.getId()))
                                .setValue(y);
                    }

                    progress.dismiss();
                    Toast.makeText(requireContext(),
                            "Push complete – Firebase now mirrors your local DB.",
                            Toast.LENGTH_LONG).show();
                    Log.d(TAG, "actuallyPush(): DONE");
                });
            });
        });
    }

    private void actuallyPull() {
        Log.d(TAG, "actuallyPull(): START");
        AlertDialog progress = new AlertDialog.Builder(requireContext())
                .setMessage("Pulling data from Firebase…")
                .setCancelable(false)
                .show();

        // 1) clear local DB
        SQLiteDatabase writable = dbHelper.getWritableDatabase();
        writable.delete(DatabaseHelper.TABLE_CLASSES,     null, null);
        writable.delete(DatabaseHelper.TABLE_TEACHERS,    null, null);
        writable.delete(DatabaseHelper.TABLE_CLASS_TYPES, null, null);
        Log.d(TAG, "Local DB cleared");

        // 2) pull classes
        classesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snap1) {
                for (DataSnapshot cSnap : snap1.getChildren()) {
                    ClassModel c = cSnap.getValue(ClassModel.class);
                    if (c != null) {
                        dbHelper.insertClass(
                                c.getTitle(), c.getTeacherName(),
                                c.getDay(), c.getTime(),
                                c.getCapacity(), c.getDuration(),
                                c.getPrice(), c.getType(),
                                c.getDescription()
                        );
                    }
                }
                Log.d(TAG, "Pulled classes=" + snap1.getChildrenCount());

                // 3) pull teachers
                teachersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override public void onDataChange(@NonNull DataSnapshot snap2) {
                        for (DataSnapshot tSnap : snap2.getChildren()) {
                            TeacherModel t = tSnap.getValue(TeacherModel.class);
                            if (t != null) {
                                dbHelper.insertTeacher(
                                        t.getName(), t.getBio(), t.getClassesCsv()
                                );
                            }
                        }
                        Log.d(TAG, "Pulled teachers=" + snap2.getChildrenCount());

                        // 4) pull types
                        typesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override public void onDataChange(@NonNull DataSnapshot snap3) {
                                for (DataSnapshot ySnap : snap3.getChildren()) {
                                    YogaTypeModel y = ySnap.getValue(YogaTypeModel.class);
                                    if (y != null) {
                                        dbHelper.insertYogaType(
                                                y.getTypeName(), y.getDescription()
                                        );
                                    }
                                }
                                Log.d(TAG, "Pulled types=" + snap3.getChildrenCount());

                                progress.dismiss();
                                Toast.makeText(requireContext(),
                                        "Pull complete – local DB now mirrors Firebase.",
                                        Toast.LENGTH_LONG).show();
                                Log.d(TAG, "actuallyPull(): DONE");
                            }
                            @Override public void onCancelled(@NonNull DatabaseError err) {
                                progress.dismiss();
                                Log.e(TAG, "Failed to pull types", err.toException());
                                Toast.makeText(requireContext(),
                                        "Error pulling types: " + err.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                    @Override public void onCancelled(@NonNull DatabaseError err) {
                        progress.dismiss();
                        Log.e(TAG, "Failed to pull teachers", err.toException());
                        Toast.makeText(requireContext(),
                                "Error pulling teachers: " + err.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
            @Override public void onCancelled(@NonNull DatabaseError err) {
                progress.dismiss();
                Log.e(TAG, "Failed to pull classes", err.toException());
                Toast.makeText(requireContext(),
                        "Error pulling classes: " + err.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
