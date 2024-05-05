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
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
        //Testing the linechart
        lineChart=findViewById(R.id.chart);
        Description description= new Description();
        description.setText("Students Record");
        description.setPosition(150f,15f);
        lineChart.setDescription(description);
        lineChart.getAxisRight().setDrawLabels(false);
        XAxis xAxis=lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xvalue= Arrays.asList("Nadon","Kamal","John");
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xvalue));
        xAxis.setLabelCount(3);
        xAxis.setGranularity(1f);
        YAxis yAxis=lineChart.getAxisLeft();
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(100f);
        yAxis.setAxisLineWidth(2f);
        yAxis.setAxisLineColor(Color.BLACK);
        yAxis.setLabelCount(15);

        List<Entry> entries1= new ArrayList<>();
        entries1.add(new Entry(0,10f));
        entries1.add(new Entry(1,10f));
        entries1.add(new Entry(2,35f));
        entries1.add(new Entry(3,45f));

        List<Entry> entries2= new ArrayList<>();
        entries2.add(new Entry(0,5f));
        entries2.add(new Entry(1,15f));
        entries2.add(new Entry(2,20f));
        entries2.add(new Entry(3,30f));

        LineDataSet dataSet1 = new LineDataSet(entries1, "Maths");
        dataSet1.setColor(Color.BLUE);
        LineDataSet dataSet2 = new LineDataSet(entries2, "Science");
        dataSet2.setColor(Color.RED);

        LineData lineData=new LineData(dataSet1,dataSet2);
        lineChart.setData(lineData);
        lineChart.invalidate();


        // Retrieve data from Firestore and output to console
        retrieveAndOutputData();

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
                            // Iterate through the retrieved documents and output data to console
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String date = document.getString("date");
                                String exerciseType = document.getString("exerciseType");
                                String value = document.getString("value");

                                // Output data to console
                                Log.d("FirestoreData", "Date: " + date + ", Exercise Type: " + exerciseType + ", Value: " + value);
                            }
                        } else {
                            Log.e("FirestoreData", "Error getting documents: ", task.getException());
                        }
                    });
        } else {
            // No user signed in
            Log.e("FirestoreData", "No user signed in.");
        }
    }
}
