package com.example.project2;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Admin extends AppCompatActivity {
    private LineChart lineChart;
    private List<String> xvalue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        lineChart = findViewById(R.id.chart);
        Description description = new Description();
        description.setText("Students Record");
        description.setPosition(150f, 15f);
        lineChart.setDescription(description);
        lineChart.getAxisRight().setDrawLabels(false);
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xvalue = Arrays.asList("Nadon", "Kamal", "John");
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xvalue));
        xAxis.setLabelCount(3);
        xAxis.setGranularity(1f);
        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(100f);
        yAxis.setAxisLineWidth(2f);
        yAxis.setAxisLineColor(Color.BLACK);
        yAxis.setLabelCount(15);

        List<Entry> entries1 = new ArrayList<>();
        entries1.add(new Entry(0, 10f));
        entries1.add(new Entry(1, 10f));
        entries1.add(new Entry(2, 35f));
        entries1.add(new Entry(3, 45f));

        List<Entry> entries2 = new ArrayList<>();
        entries2.add(new Entry(0, 5f));
        entries2.add(new Entry(1, 15f));
        entries2.add(new Entry(2, 20f));
        entries2.add(new Entry(3, 30f));

        LineDataSet dataSet1 = new LineDataSet(entries1, "Maths");
        dataSet1.setColor(Color.BLUE);
        LineDataSet dataSet2 = new LineDataSet(entries2, "Science");
        dataSet2.setColor(Color.RED);

        LineData lineData = new LineData(dataSet1, dataSet2);
        lineChart.setData(lineData);
        lineChart.invalidate();

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

    }
}







