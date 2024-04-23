package com.example.project2;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;
import java.util.Date;


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

import java.util.Locale;

 public class Update extends AppCompatActivity {
    CalendarView date;
    Button update;

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

        update = findViewById(R.id.button3);
        date = findViewById(R.id.calendarView4);

        // Set a listener to get the selected date
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
                // Launch CreateAccountActivity
                Intent intent = new Intent(Update.this, Summary.class);
                startActivity(intent);
            }
        });
    }
     private String formatDate(int year, int month, int dayOfMonth) {
         SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
         Calendar calendar = Calendar.getInstance();
         calendar.set(year, month, dayOfMonth);
         return sdf.format(calendar.getTime());
     }
 }

