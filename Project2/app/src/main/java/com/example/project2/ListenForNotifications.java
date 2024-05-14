package com.example.project2;

import static com.example.project2.NotificationHelper.triggerNotification;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Objects;

public class ListenForNotifications {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String TAG = "ListenForNotifications";
    boolean notificationTriggered = false;
    public void listenForNotifications(Context context) {
        final DocumentReference docRef = db.collection("notifications").document("notice");
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {

                if (e != null) {
//                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    String source = snapshot.getMetadata().hasPendingWrites() ? "Local" : "Server";
                    String data = formatString(Objects.requireNonNull(snapshot.getData()).toString());

                    // Output the change to the console
                    Log.d(TAG, source + " data: " + snapshot.getData());

                    // Update the TextView with the latest data
//                    textView = findViewById(R.id.textInScrollView);
//                    textView.setText(data);
                    if (notificationTriggered)
                        triggerNotification(context, "Title", data);
                    else
                        notificationTriggered = true;
                } else {
                    Log.d(TAG, "Document snapshot is null or doesn't exist");
                }
            }

            public String formatString(String input) {
                // Remove curly braces
                String parsedString = input.replaceAll("[{}]", "");

                // Replace '=' and '~' with new line characters
                parsedString = parsedString.replaceAll("[=~]", "\n");
                parsedString = parsedString.replaceAll(",", "\n\n");

                return parsedString;
            }
        });


    }

}
