package com.example.project2;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CreateNotificationActivity extends AppCompatActivity {

    // Initialize the fab button
    private FloatingActionButton fabCancel;
    private FloatingActionButton fabSend;
    private TextInputEditText title;
    private TextInputEditText message;
    private String titleText;
    private String messageText;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_notification);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        fabCancel = findViewById(R.id.fabCancel);
        fabSend = findViewById(R.id.fabSend);
        fabCancel.setOnClickListener(v -> finish());

        title = findViewById(R.id.titleText);
        message = findViewById(R.id.messageText);

        fabSend.setOnClickListener(v -> {
            Log.d("CreateNotificationActivity", "fabSend clicked");
            writeDataToFirestore();
        });
    }

    private void writeDataToFirestore() {
        // Get current date and time
        String dateTime = "null";
        LocalDateTime now = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            now = LocalDateTime.now();
        }

        // Define a formatter
        DateTimeFormatter formatter = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss");
        }

        // Format the current date and time
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            dateTime = now.format(formatter);
        }

        titleText = Objects.requireNonNull(title.getText()).toString();
        messageText = Objects.requireNonNull(message.getText()).toString();

        // Create a map to hold the data
        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put(dateTime, titleText + "~" + messageText);

        // Add the data to the "notice" document in the "notifications" collection
        db.collection("notifications")
                .document("notice") // Assuming "notice" is the document ID you want to write to
                .update(notificationData) // Use update instead of set
                .addOnSuccessListener(aVoid -> {
                    Log.d("CreateNotificationActivity", "DocumentSnapshot added successfully");
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.w("CreateNotificationActivity", "Error adding document", e);
                });
    }

}