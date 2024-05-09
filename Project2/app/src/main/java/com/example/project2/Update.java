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
        date = findViewById(R.id.calendarView4);
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

                // Get current user's email
                String userEmail = mAuth.getCurrentUser().getEmail();

                // Write data to Firestore
                writeDataToFirestore(userEmail, selectedDate, exerciseType, value);
                summary.setVisibility(View.VISIBLE);
                summary.setEnabled(true);


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
        Map<String, Object> data = new HashMap<>();
        data.put("date", selectedDate);
        data.put("exerciseType", exerciseType);
        data.put("value", value);

        db.collection("users").document(userEmail).collection("exerciseData")
                .add(data)
                .addOnSuccessListener(documentReference -> {
                    Log.d("Firestore", "DocumentSnapshot added with ID: " + documentReference.getId());
                    // Show toast indicating success
                    Toast.makeText(Update.this, "Data saved successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error adding document", e);
                    // Show toast indicating failure
                    Toast.makeText(Update.this, "Failed to save data.", Toast.LENGTH_SHORT).show();
                });

    }
}
