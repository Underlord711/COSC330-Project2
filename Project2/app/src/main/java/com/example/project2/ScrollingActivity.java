package com.example.project2;

import static com.example.project2.NotificationHelper.createNotificationChannel;
import static com.example.project2.NotificationHelper.triggerNotification;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.project2.databinding.ActivityScrollingBinding;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScrollingActivity extends AppCompatActivity {

    private ActivityScrollingBinding binding;
    TextView textView;
    String TAG = "FIREBASE";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    boolean notificationTriggered = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createNotificationChannel(this); // Create notification channel

        binding = ActivityScrollingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = binding.toolbarLayout;
        toolBarLayout.setTitle(getTitle());

        FloatingActionButton fab = binding.fab;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Take user to message composition activity
                Intent intent = new Intent(ScrollingActivity.this, CreateNotificationActivity.class);
                startActivity(intent);
            }
        });

        final DocumentReference docRef = db.collection("notifications").document("notice");
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    String source = snapshot.getMetadata().hasPendingWrites() ? "Local" : "Server";
                    String data = formatString(Objects.requireNonNull(snapshot.getData()).toString());

                    // Output the change to the console
                    Log.d(TAG, source + " data: " + snapshot.getData());

                    // Update the TextView with the latest data
                    textView = findViewById(R.id.textInScrollView);
                    textView.setText(data);
                    if (notificationTriggered)
                        triggerNotification(ScrollingActivity.this,getTitle(data), getMessage(data));
                    else
                        notificationTriggered = true;
                } else {
                    Log.d(TAG, "Document snapshot is null or doesn't exist");
                }
            }
        });
    }

    public String formatString(String input) {
        // Remove curly braces
        String parsedString = input.replaceAll("[{}]", "");

        // Replace '=' and '~' with new line characters
        parsedString = parsedString.replaceAll("[=~]", "\n");
        parsedString = parsedString.replaceAll(",", "\n\n");

        return parsedString;
    }

    public String getTitle(String input) {
        // Remove curly braces

        // The title is between the first and second line
        String[] lines = input.split("\n");
        if (lines.length < 2) {
            return "No title";
        }

        String title = lines[1];
        Log.d("TITLE", title);

        return title;
    }

    public String getMessage(String input) {
        // Remove curly braces

        // The title is between the first and second line
        String[] lines = input.split("\n");
        if (lines.length < 2) {
            return "No title";
        }

        String title = lines[2];
        Log.d("TITLE", title);

        return title;
    }
}