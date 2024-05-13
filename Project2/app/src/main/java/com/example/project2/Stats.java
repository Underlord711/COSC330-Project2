package com.example.project2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.data.Entry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Stats extends AppCompatActivity {
    Spinner statType;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    String email;
    List<Entry> entries;
    List<String> dates;
    Map<String, Float> dateValueMap;
    String maxDate;
    float maxValue;
    String minDate;
    float minValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        // Retrieve exercise type from intent extras
        String exerciseType = getIntent().getStringExtra("exerciseType");

        // Set the title text to display the exercise type
        TextView titleTextView = findViewById(R.id.title);
        titleTextView.setText("Stats for " + exerciseType);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Get user ID (email) from intent extras, if available
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("userId")) {
            email = intent.getStringExtra("userId");
        } else {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                email = currentUser.getEmail(); // Use currently signed-in user's email
            }
        }

        // Initialize the statType spinner
        statType = findViewById(R.id.statTypes);

        // Set up the spinner with options
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.stats_view, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statType.setAdapter(adapter);

        // Set listener for item selection in the statType spinner
        statType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Call different functions based on the selected item
                switch (position) {
                    case 0:
                        // Call function for the first item
                        handleFirstStatType();
                        break;
                    case 1:
                        // Call function for the second item
                        handleSecondStatType();
                        break;
                    case 2:
                        // Call function for the third item
                        handleThirdStatType();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle nothing selected if needed
            }
        });
    }

    // Function to handle the first stat type selection
    private void handleFirstStatType() {
        // Find the TextView by its ID
        TextView body = findViewById(R.id.textView6);

        // Clear existing text in the TextView
        body.setText("");

        // Check if dates list and dateValueMap are not null and not empty
        if (dates != null && !dates.isEmpty() && dateValueMap != null && !dateValueMap.isEmpty()) {
            StringBuilder stringBuilder = new StringBuilder();

            // Iterate through each date in the dates list
            for (String date : dates) {
                // Get the aggregated value for the date from the dateValueMap
                float aggregatedValue = dateValueMap.get(date);

                // Append the date and its aggregated value to the StringBuilder with proper formatting
                stringBuilder.append("Date: ").append(date).append(", Aggregated Value: ").append(aggregatedValue).append("\n");
            }

            // Set the text of the TextView to the accumulated string
            body.setText(stringBuilder.toString());
        }
    }

    // Function to handle the second stat type selection
    private void handleSecondStatType() {
        // Implement your logic for the second stat type
    }

    // Function to handle the third stat type selection
    private void handleThirdStatType() {
        // Implement your logic for the third stat type
    }

    private void retrieveAndOutputData(String exercise) {
        if (email == null) {
            Log.e("FirestoreData", "User ID (email) is null.");
            return;
        }

        db.collection("users")
                .document(email)
                .collection("exerciseData")
                .whereEqualTo("exerciseType", exercise)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        dateValueMap = new HashMap<>();
                        entries = new ArrayList<>();
                        dates = new ArrayList<>();
                        maxValue = Float.MIN_VALUE;
                        minValue = Float.MAX_VALUE;

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String date = document.getString("date");
                            String valueStr = document.getString("value");
                            float value = Float.parseFloat(valueStr);

                            if (dateValueMap.containsKey(date)) {
                                float currentValue = dateValueMap.get(date);
                                dateValueMap.put(date, currentValue + value);
                            } else {
                                dateValueMap.put(date, value);
                            }

                            // Update max value and corresponding date
                            if (value > maxValue) {
                                maxValue = value;
                                maxDate = date;
                            }

                            // Update min value and corresponding date
                            if (value < minValue) {
                                minValue = value;
                                minDate = date;
                            }

                            Log.d("FirestoreData", "Date: " + date + ", Exercise Type: " + exercise + ", Value: " + value);
                        }

                        // Process dates to use only the day component for display
                        for (String date : dateValueMap.keySet()) {
                            float aggregatedValue = dateValueMap.get(date);
                            entries.add(new Entry(entries.size(), aggregatedValue));

                            // Extract day part from the date (assuming date is in "yyyy-MM-dd" format)
                            String day = date.substring(8, 10); // Extracts characters at index 8 and 9 (day part)
                            dates.add(day); // Add day to list of x-axis labels
                        }

                        // Now you can use maxDate, maxValue, minDate, minValue as needed
                        if (maxDate != null && minDate != null) {
                            Log.d("FirestoreData", "Date with highest value: " + maxDate + ", Value: " + maxValue);
                            Log.d("FirestoreData", "Date with lowest value: " + minDate + ", Value: " + minValue);
                        }

                    } else {
                        Log.e("FirestoreData", "Error getting documents: ", task.getException());
                    }
                });
    }
}
