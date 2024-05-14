package com.example.project2;

<<<<<<< HEAD
import static com.example.project2.NotificationHelper.createNotificationChannel;
import static com.example.project2.NotificationHelper.triggerNotification;

import android.content.Intent;
=======
>>>>>>> 7a4ab1d (Notificatinos)
import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

<<<<<<< HEAD
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
=======
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

import com.example.project2.databinding.ActivityScrollingBinding;
>>>>>>> 7a4ab1d (Notificatinos)

public class ScrollingActivity extends AppCompatActivity {

    private ActivityScrollingBinding binding;
<<<<<<< HEAD
    TextView textView;
    String TAG = "FIREBASE";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    boolean notificationTriggered = false;

=======
>>>>>>> 7a4ab1d (Notificatinos)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
<<<<<<< HEAD
        createNotificationChannel(this); // Create notification channel
=======
>>>>>>> 7a4ab1d (Notificatinos)

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
<<<<<<< HEAD
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
=======
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .setAnchorView(R.id.fab).show();
            }
        });
>>>>>>> 7a4ab1d (Notificatinos)
    }
}