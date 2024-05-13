package com.example.project2;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.github.mikephil.charting.charts.LineChart;

public class Summary extends AppCompatActivity {
    private LineChart lineChart;
    private List<String> xvalue;
    private Map<String, Float> dateValueMap = new HashMap<>();

    Spinner exercise;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    String email;
    Button summary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.summary);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        exercise = findViewById(R.id.exerciseSelectionSpinner);

        // Initialize Firebase instances
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

        // Set spinner item selection listener
        exercise.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedExerciseType = parent.getItemAtPosition(position).toString();
                retrieveAndOutputData(selectedExerciseType); // Call function with selected exercise type
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle nothing selected if needed
            }
        });
        summary=findViewById(R.id.stats);
        summary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedExerciseType = exercise.getSelectedItem().toString(); // Get the selected exercise type
                Intent statsIntent = new Intent(Summary.this, Stats.class);

                // Pass exercise type and dateValueMap to StatsActivity
                statsIntent.putExtra("exerciseType", selectedExerciseType);
                statsIntent.putExtra("dateValueMap", new HashMap<>(dateValueMap)); // Pass a copy to avoid modifications

                startActivity(statsIntent);
            }
        });


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
                        // Clear existing entries from the global dateValueMap
                        dateValueMap.clear();

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

                            Log.d("FirestoreData", "Date: " + date + ", Exercise Type: " + exercise + ", Value: " + value);
                        }

                        List<Entry> entries = new ArrayList<>();
                        List<String> dates = new ArrayList<>();

                        // Process dates to use only the day component for display
                        for (String date : dateValueMap.keySet()) {
                            float aggregatedValue = dateValueMap.get(date);
                            entries.add(new Entry(entries.size(), aggregatedValue));

                            // Extract day part from the date (assuming date is in "yyyy-MM-dd" format)
                            String day = date.substring(8, 10); // Extracts characters at index 8 and 9 (day part)
                            dates.add(day); // Add day to list of x-axis labels
                        }

                        configureChart(entries, dates); // Update chart with new data
                    } else {
                        Log.e("FirestoreData", "Error getting documents: ", task.getException());
                    }
                });
    }



    private void configureChart(List<Entry> entries1, List<String> dates) {
        lineChart = findViewById(R.id.chart);
        Description description = new Description();
        description.setText("Students Record");
        description.setPosition(150f, 15f);
        lineChart.setDescription(description);
        lineChart.getAxisRight().setDrawLabels(false);
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(dates));
        xAxis.setLabelCount(dates.size());
        xAxis.setGranularity(1f);

        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(12f);
        yAxis.setAxisLineWidth(2f);
        yAxis.setAxisLineColor(Color.BLACK);
        yAxis.setLabelCount(15);

        LineDataSet dataSet1 = new LineDataSet(entries1, "Work");
        dataSet1.setColor(Color.RED);

        LineData lineData = new LineData(dataSet1);
        lineChart.setData(lineData);
        lineChart.invalidate();
    }
}