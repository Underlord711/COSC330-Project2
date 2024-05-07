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
    Button notifybtn;
    Button push;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        notifybtn = findViewById(R.id.notification);
        editText = findViewById(R.id.writeText);
        push = findViewById(R.id.push);
        createNotificationChannel();
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

        notifybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setVisibility(View.VISIBLE);
                editText.setEnabled(true);
                push.setVisibility(View.VISIBLE);
                push.setEnabled(true);
            }
        });

        push.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = "Wellness";
                String body = editText.getText().toString().trim();
                editText.setVisibility(View.INVISIBLE);
                editText.setEnabled(false);
                sendNotification("Wellness", body);
                FirebaseMessaging.getInstance().getToken()
                        .addOnCompleteListener(new OnCompleteListener<String>() {
                            @Override
                            public void onComplete(@NonNull Task<String> task) {
                                if (!task.isSuccessful()) {
                                    Log.w("Firebase-token", "Fetching FCM registration token failed", task.getException());
                                    return;
                                }

                                // Get new FCM registration token
                                String token = task.getResult();

                                // Log and toast
//                                String msg = getString(R.string.msg_token_fmt, token);
                                Log.d("Firebase-token", token);

                                Toast.makeText(Admin.this, token, Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("channel_id", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    private void sendNotification(String title, String body) {
        push.setVisibility(View.INVISIBLE);
        push.setEnabled(false);
        Log.i("Notification_", "Call to notification sender");

        // Get the device token from Firebase
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        Log.i("Notification_", "Task success");
                        String deviceToken = task.getResult();
                        // Now you have the device token, you can send the notification
                        // Construct the notification payload
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "channel_id")
                                .setSmallIcon(R.drawable.ic_monitor_heart)
                                .setContentTitle(title)
                                .setContentText(body)
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                        // Show the notification
                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                            Log.i("Notification_", "No permissions!");
                            return;
                        }
                        notificationManager.notify(1, builder.build());
                        Log.i("Notification_", "Notification sent!");
                    } else {
                        Log.i("Notification_", "Task fail");
                    }
                });
    }
}
