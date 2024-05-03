package com.example.project2;
import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Objects;
import java.util.UUID;

public class Admin extends AppCompatActivity {

    Button notifybtn;
    Button push;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        notifybtn = findViewById(R.id.notification);
        editText = findViewById(R.id.writeText);
        push = findViewById(R.id.push);
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
                sendNotification(title, body);
            }
        });
    }
    private void sendNotification(String title, String body){
        push.setVisibility(View.INVISIBLE);
        push.setEnabled(false);
    String campaignid="3704895349349358516";
    }

        /*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(Admin.this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(Admin.this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
        notifybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNotifictaion();
            }
        });
*/

    }



/*    public void createNotifictaion(){
        String channelID="CHANNEL_ID_NOTIFICATIONS";
        NotificationCompat.Builder builder= new NotificationCompat.Builder(getApplicationContext(), channelID);
        builder.setSmallIcon(R.drawable.ic_monitor_heart)
                .setContentTitle("Notification Title")
                .setContentText(" Dont forget to do the thing")
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
            Intent intent=new Intent(getApplicationContext(), NotificationActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("data", "Some stuff to be passed");
        PendingIntent pendingIntent= PendingIntent.getActivity(getApplicationContext(),0,
                intent,PendingIntent.FLAG_MUTABLE);
        builder.setContentIntent(pendingIntent);
        NotificationManager notificationManager=
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel=
                    notificationManager.getNotificationChannel(channelID);
            if(notificationChannel==null){
                int importance = NotificationManager.IMPORTANCE_HIGH;
                notificationChannel= new NotificationChannel(channelID, "Some Description", importance);
                notificationChannel.setLightColor(Color.GREEN);
                notificationChannel.enableVibration(true);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
        notificationManager.notify(0,builder.build());
    }*/
