package com.example.project2;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class CreateAccount extends AppCompatActivity {
    Button account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_createaccount);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        account=findViewById(R.id.button);
        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText user=(EditText)findViewById(R.id.editText2);
                String username=user.getText().toString();
                EditText password=(EditText)findViewById(R.id.editText1);
                String pass=password.getText().toString();
                // Launch CreateAccountActivity
                Intent intent = new Intent(CreateAccount.this, Summary.class);
                startActivity(intent);
            }
        });
    }
}