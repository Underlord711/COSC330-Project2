package com.example.project2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class Summary extends AppCompatActivity {
    Button update;
    FirebaseFirestore db;
    FirebaseAuth mAuth;



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
        Log.d("Hi Mom", "This");

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve data from Firestore and output to console
                retrieveAndOutputData();

                // Launch CreateAccountActivity
                Intent intent = new Intent(Summary.this, Update.class);
                startActivity(intent);
            }
        });
    }

    private void retrieveAndOutputData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Query Firestore to retrieve data for the current user
            db.collection("users")
                    .document(userId)
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
