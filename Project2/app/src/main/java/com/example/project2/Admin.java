package com.example.project2;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Admin extends AppCompatActivity {
    private LineChart lineChart;
    private List<String> xvalue;
    FirebaseFirestore db;
    Spinner exercise;
    Spinner user;
    Button jump;
    Button notificationsButton;
    ArrayAdapter<String> userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        exercise = findViewById(R.id.activity);
//
        db = FirebaseFirestore.getInstance();

        FirebaseMessaging.getInstance().subscribeToTopic("weather")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Subscribed";
                        if (!task.isSuccessful()) {
                            msg = "Subscribe failed";
                        }
                        Log.d("Firebase", msg);
//                        Toast.makeText(Admin.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });

        exercise.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedExerciseType = parent.getItemAtPosition(position).toString();
                retrieveAndOutputData(selectedExerciseType); // Call function with selected exercise type
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        user = findViewById(R.id.user);
        userAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        userAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        user.setAdapter(userAdapter);

        notificationsButton = findViewById(R.id.goToNotificationsButton);

        // Populate the user Spinner with document IDs from 'users' collection
        populateUserSpinner();
        jump = findViewById(R.id.jump);
        jump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedUserId = (String) user.getSelectedItem();
                Intent intent = new Intent(Admin.this, Summary.class);
                intent.putExtra("userId", selectedUserId); // Pass the selected user ID to Summary activity
                startActivity(intent);
            }
        });

        notificationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Admin.this, ScrollingActivity.class);
                startActivity(intent);
            }
        });
    }

    private void populateUserSpinner() {
        db.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> userIds = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            userIds.add(document.getId()); // Add document ID to the list
                        }
                        userAdapter.addAll(userIds); // Add all IDs to the Spinner adapter
                        userAdapter.notifyDataSetChanged(); // Notify adapter about data change
                    } else {
                        Log.e("Firestore", "Error getting user documents: ", task.getException());
                    }
                });
    }

    private void retrieveAndOutputData(String exercise) {
        db.collectionGroup("exerciseData")
                .whereEqualTo("exerciseType", exercise)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Map<String, Float> dateValueMap = new HashMap<>();
                        int documentCount = 0; // To count the number of documents

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String date = document.getString("date");
                            String valueStr = document.getString("value");
                            float value = Float.parseFloat(valueStr);

                            // Accumulate values for each date
                            if (dateValueMap.containsKey(date)) {
                                float currentValue = dateValueMap.get(date);
                                dateValueMap.put(date, currentValue + value);
                            } else {
                                dateValueMap.put(date, value);
                            }

                            documentCount++; // Increment document count
                            Log.d("FirestoreData", "Date: " + date + ", Exercise Type: " + exercise + ", Value: " + value);
                        }

                        List<Entry> entries = new ArrayList<>();
                        List<String> dates = new ArrayList<>();

                        int index = 0;
                        for (String date : dateValueMap.keySet()) {
                            float aggregatedValue = dateValueMap.get(date);

                            // Calculate average value before adding to entry
                            if (documentCount > 0) {
                                aggregatedValue /= documentCount;
                            }

                            Entry entry = new Entry(index, aggregatedValue);
                            entries.add(entry);
                            dates.add(date);
                            index++;
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
