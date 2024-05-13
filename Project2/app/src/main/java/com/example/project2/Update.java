package com.example.project2;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.project2.Summary;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Update extends AppCompatActivity {
    CalendarView date;
    Spinner exerciseSpinner;
    EditText typeInput;
    Button update;
    Button summary;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db;

    private String formatDate(int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(calendar.getTime());
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.update);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        update = findViewById(R.id.updateDataButton);
        summary=findViewById(R.id.gotoSummary);
        date = findViewById(R.id.calendarView);
        exerciseSpinner = findViewById(R.id.exerciseSelectionSpinner);
        db = FirebaseFirestore.getInstance();
        typeInput = findViewById(R.id.timeInputEditText);

        // Set a listener to get the selected date
        date.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                // Log the selected date when it changes
                String selectedDate = formatDate(year, month, dayOfMonth);
                Log.d("SelectedDate", "Date Selected: " + selectedDate);
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get data
                String selectedDate = formatDate(date.getDate());
                String exerciseType = exerciseSpinner.getSelectedItem().toString();
                String value = typeInput.getText().toString();

                // Validate the input value
                try {
                    // Attempt to parse the input value into a float
                    float floatValue = Float.parseFloat(value);

                    // If parsing is successful, proceed to write data to Firestore
                    String userEmail = mAuth.getCurrentUser().getEmail();
                    writeDataToFirestore(userEmail, selectedDate, exerciseType, value);

                    // Clear the input field after successful write
                    typeInput.setText("");

                    // Show/hide UI elements as needed
                    summary.setVisibility(View.VISIBLE);
                    summary.setEnabled(true);
                } catch (NumberFormatException e) {
                    // Display a toast message indicating invalid time format
                    Toast.makeText(getApplicationContext(), "Invalid time format", Toast.LENGTH_SHORT).show();
                }
            }
        });

        summary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Update.this, Summary.class);
                startActivity(intent);
            }
        });

    }

    private String formatDate(long timeInMillis) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);
        return sdf.format(calendar.getTime());
    }

    private void writeDataToFirestore(String userEmail, String selectedDate, String exerciseType, String value) {
        // Map to hold the new field 'hiMom' with value '123'
        Map<String, Object> data = new HashMap<>();
        data.put("hiMom", "123");

        // Get a reference to the user document using the userEmail
        db.collection("users").document(userEmail)
                .update(data) // Update the existing document with the new 'hiMom' field
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Document updated successfully");
                    // Show toast indicating success
                    Toast.makeText(Update.this, "Data saved successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error updating document", e);
                    // Show toast indicating failure
                    Toast.makeText(Update.this, "Failed to save data.", Toast.LENGTH_SHORT).show();
                });

        // Now add the exercise data to the 'exerciseData' subcollection as before
        Map<String, Object> exerciseData = new HashMap<>();
        exerciseData.put("date", selectedDate);
        exerciseData.put("exerciseType", exerciseType);
        exerciseData.put("value", value);

        db.collection("users").document(userEmail).collection("exerciseData")
                .add(exerciseData)
                .addOnSuccessListener(documentReference -> {
                    Log.d("Firestore", "Exercise data added with ID: " + documentReference.getId());
                    // No need to show toast here as it's for exercise data, not for 'hiMom'
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error adding exercise data", e);
                    // No need to show toast here as it's for exercise data, not for 'hiMom'
                });
    }

}
