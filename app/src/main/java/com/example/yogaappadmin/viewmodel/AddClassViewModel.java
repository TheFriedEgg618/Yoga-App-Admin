package com.example.yogaappadmin.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.text.TextUtils;

public class AddClassViewModel extends ViewModel {

    // --- LiveData for Input Errors ---
    private final MutableLiveData<String> dayError = new MutableLiveData<>();
    private final MutableLiveData<String> timeError = new MutableLiveData<>();
    private final MutableLiveData<String> capacityError = new MutableLiveData<>();
    private final MutableLiveData<String> durationError = new MutableLiveData<>();
    private final MutableLiveData<String> priceError = new MutableLiveData<>();
    private final MutableLiveData<String> typeError = new MutableLiveData<>();
    // No error LiveData for optional fields like description unless specific validation needed

    // --- LiveData for Save Operation Result ---
    // Using Boolean: true for success, null/false for initial/failure state
    // Consider using an Event wrapper for single-fire events like navigation/toasts
    private final MutableLiveData<Boolean> saveResultSuccess = new MutableLiveData<>();

    // --- Public Getters for LiveData (expose as immutable LiveData) ---
    public LiveData<String> getDayError() { return dayError; }
    public LiveData<String> getTimeError() { return timeError; }
    public LiveData<String> getCapacityError() { return capacityError; }
    public LiveData<String> getDurationError() { return durationError; }
    public LiveData<String> getPriceError() { return priceError; }
    public LiveData<String> getTypeError() { return typeError; }
    public LiveData<Boolean> getSaveResultSuccess() { return saveResultSuccess; }


    // --- Business Logic: Validation and Saving (or triggering save) ---
    public void validateAndSave(String day, String time, String capacityStr, String durationStr, String priceStr, String type, String description) {
        // Assume default spinner prompts are passed if nothing selected
        String defaultSpinnerPromptDay = "Select Day"; // Adjust if needed
        String defaultSpinnerPromptType = "Select Type"; // Adjust if needed
        boolean isValid = true;

        // Reset errors before validation
        dayError.setValue(null);
        timeError.setValue(null);
        capacityError.setValue(null);
        durationError.setValue(null);
        priceError.setValue(null);
        typeError.setValue(null);
        saveResultSuccess.setValue(null); // Reset save status

        // --- Validation Logic (moved from Activity/Fragment) ---

        // Day
        if (TextUtils.isEmpty(day) || day.equalsIgnoreCase(defaultSpinnerPromptDay)) {
            dayError.setValue("Day of the week is required.");
            isValid = false;
        }

        // Time
        if (TextUtils.isEmpty(time)) {
            timeError.setValue("Time is required");
            isValid = false;
        }

        // Capacity
        if (TextUtils.isEmpty(capacityStr)) {
            capacityError.setValue("Capacity is required");
            isValid = false;
        } else {
            try {
                int capacity = Integer.parseInt(capacityStr);
                if (capacity <= 0) {
                    capacityError.setValue("Capacity must be a positive number");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                capacityError.setValue("Capacity must be a valid number");
                isValid = false;
            }
        }

        // Duration
        if (TextUtils.isEmpty(durationStr)) {
            durationError.setValue("Duration is required");
            isValid = false;
        } else {
            try {
                int duration = Integer.parseInt(durationStr);
                if (duration <= 0) {
                    durationError.setValue("Duration must be a positive number (minutes)");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                durationError.setValue("Duration must be a valid number");
                isValid = false;
            }
        }

        // Price
        if (TextUtils.isEmpty(priceStr)) {
            priceError.setValue("Price is required");
            isValid = false;
        } else {
            try {
                double price = Double.parseDouble(priceStr);
                if (price < 0) {
                    priceError.setValue("Price cannot be negative");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                priceError.setValue("Price must be a valid number");
                isValid = false;
            }
        }

        // Type
        if (TextUtils.isEmpty(type) || type.equalsIgnoreCase(defaultSpinnerPromptType)) {
            typeError.setValue("Class Type is required.");
            isValid = false;
        }

        // --- If Valid, Proceed (e.g., call Repository) ---
        if (isValid) {
            // TODO:
            // 1. Create a Course object from the validated data
            // int capacity = Integer.parseInt(capacityStr);
            // int duration = Integer.parseInt(durationStr);
            // double price = Double.parseDouble(priceStr);
            // Course course = new Course(day, time, capacity, duration, price, type, description);
            //
            // 2. Call a Repository to save the Course object
            // repository.saveCourse(course); // This would likely involve callbacks or LiveData from the repo

            // 3. For now, simulate successful save
            saveResultSuccess.setValue(true); // Signal success to the Fragment

        } else {
            saveResultSuccess.setValue(false); // Signal validation failure (optional)
        }
    }

    // Method to reset the save status, e.g., after the Fragment has reacted
    public void resetSaveStatus() {
        saveResultSuccess.setValue(null);
    }
}