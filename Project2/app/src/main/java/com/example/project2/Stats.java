package com.example.project2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Map;

public class Stats extends AppCompatActivity {
    Spinner statType;
    TextView titleText;
    TextView body;
    Map<String, Float> dateValueMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        // Retrieve exercise type and data from intent extras
        Intent intent = getIntent();
        if (intent != null) {
            String exerciseType = intent.getStringExtra("exerciseType");
            dateValueMap = (Map<String, Float>) intent.getSerializableExtra("dateValueMap");

            // Set the title text to display the exercise type
            titleText = findViewById(R.id.title);
            titleText.setText("Exercise Type: " + exerciseType);
        }

        // Initialize views
        body = findViewById(R.id.textView6);

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
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                // Call different functions based on the selected item
                switch (position) {
                    case 0:
                        handleFirstStatType();
                        break;
                    case 1:
                        handleSecondStatType();
                        break;
                    case 2:
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
        // Clear existing text in the TextView
        body.setText("");

        // Check if dateValueMap is not null and not empty
        if (dateValueMap != null && !dateValueMap.isEmpty()) {
            StringBuilder stringBuilder = new StringBuilder();

            // Iterate through each entry in the dateValueMap
            for (Map.Entry<String, Float> entry : dateValueMap.entrySet()) {
                String date = entry.getKey();
                float aggregatedValue = entry.getValue();

                // Append the date and its aggregated value to the StringBuilder with proper formatting
                stringBuilder.append("Date: ").append(date).append(", Hours of exercise: ").append(aggregatedValue).append("\n");
            }

            // Set the text of the TextView to the accumulated string
            body.setText(stringBuilder.toString());
        }
    }

    // Function to handle the second stat type selection
// Function to handle the second stat type selection
    private void handleSecondStatType() {
        // Clear existing text in the TextView
        body.setText("");

        // Check if dateValueMap is not null and not empty
        if (dateValueMap != null && !dateValueMap.isEmpty()) {
            // Initialize variables to track highest, lowest, and sum of aggregated values
            String highestDate = null;
            float highestValue = Float.MIN_VALUE;
            String lowestDate = null;
            float lowestValue = Float.MAX_VALUE;
            float sumValues = 0;
            int count = 0;

            // Iterate through each entry in the dateValueMap
            for (Map.Entry<String, Float> entry : dateValueMap.entrySet()) {
                String date = entry.getKey();
                float aggregatedValue = entry.getValue();

                // Update highest aggregated value
                if (aggregatedValue > highestValue) {
                    highestValue = aggregatedValue;
                    highestDate = date;
                }

                // Update lowest aggregated value
                if (aggregatedValue < lowestValue) {
                    lowestValue = aggregatedValue;
                    lowestDate = date;
                }

                // Accumulate sum of aggregated values
                sumValues += aggregatedValue;
                count++;
            }

            // Calculate average aggregated value
            float averageValue = sumValues / count;

            // Prepare the output text with the results
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Date with Highest Value: ").append(highestDate).append(", Value: ").append(highestValue).append("\n");
            stringBuilder.append("Date with Lowest Value: ").append(lowestDate).append(", Value: ").append(lowestValue).append("\n");
            stringBuilder.append("Average Value: ").append(averageValue);

            // Set the text of the TextView to the accumulated string
            body.setText(stringBuilder.toString());
        }
    }


    // Function to handle the third stat type selection
    private void handleThirdStatType() {
        // Clear existing text in the TextView
        body.setText("Third Stat Type Selected");
        // Implement your logic for the third stat type here
        // You can customize this to display specific information or perform actions
    }
}
