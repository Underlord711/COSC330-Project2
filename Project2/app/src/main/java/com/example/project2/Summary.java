package com.example.project2;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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

import java.lang.reflect.Array;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.github.mikephil.charting.charts.LineChart;
public class Summary extends AppCompatActivity {
    private LineChart lineChart;
    private List<String> xvalue;
    Button update;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    String email;

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
        update = findViewById(R.id.Update);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        email = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
        retrieveAndOutputData();
        //Testing the linechart



        // Retrieve data from Firestore and output to console


        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch CreateAccountActivity
                Intent intent = new Intent(Summary.this, Update.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void retrieveAndOutputData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Query Firestore to retrieve data for the current user
            db.collection("users")
                    .document(email)
                    .collection("exerciseData")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Map<String, Float> dateValueMap = new HashMap<>();

                            // Iterate through the retrieved documents
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String date = document.getString("date");
                                String exerciseType = document.getString("exerciseType");
                                String valueStr = document.getString("value");

                                // Parse 'valueStr' to float (assuming 'valueStr' represents a numeric value)
                                float value = Float.parseFloat(valueStr);

                                // Accumulate values for each unique date
                                if (dateValueMap.containsKey(date)) {
                                    float currentValue = dateValueMap.get(date);
                                    dateValueMap.put(date, currentValue + value);
                                } else {
                                    dateValueMap.put(date, value);
                                }

                                // Output data to console
                                Log.d("FirestoreData", "Date: " + date + ", Exercise Type: " + exerciseType + ", Value: " + value);
                            }

                            // Prepare chart entries based on aggregated values
                            List<Entry> entries1 = new ArrayList<>();
                            List<String> dates = new ArrayList<>();

                            // Iterate over unique dates and create chart entries
                            int index = 0;
                            for (String date : dateValueMap.keySet()) {
                                float aggregatedValue = dateValueMap.get(date);
                                // Create Entry object using aggregated value and index
                                Entry entry = new Entry(index, aggregatedValue);
                                entries1.add(entry);
                                dates.add(date); // Collect dates for X-axis labels
                                index++;
                            }

                            // After retrieving data, configure and populate the chart
                            configureChart(entries1, dates);
                        } else {
                            Log.e("FirestoreData", "Error getting documents: ", task.getException());
                        }
                    });
        } else {
            // No user signed in
            Log.e("FirestoreData", "No user signed in.");
        }
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
        xAxis.setValueFormatter(new IndexAxisValueFormatter(dates)); // Set X-axis labels
        xAxis.setLabelCount(dates.size()); // Set label count based on number of dates
        xAxis.setGranularity(1f);

        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(50f); // Adjust based on your data range
        yAxis.setAxisLineWidth(2f);
        yAxis.setAxisLineColor(Color.BLACK);
        yAxis.setLabelCount(15);

        LineDataSet dataSet1 = new LineDataSet(entries1, "Work");
        dataSet1.setColor(Color.BLUE);

        LineData lineData = new LineData(dataSet1);
        lineChart.setData(lineData);
        lineChart.invalidate();
    }


}
