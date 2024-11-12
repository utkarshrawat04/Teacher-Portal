package com.example.portalc;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText;
    private Button registerButton;
    private SQLiteHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize UI components
        usernameEditText = findViewById(R.id.registerUsername);  // Ensure this matches XML ID
        passwordEditText = findViewById(R.id.registerPassword);  // Ensure this matches XML ID
        registerButton = findViewById(R.id.registerButton);

        // Initialize SQLiteHelper
        dbHelper = new SQLiteHelper(this);

        // Register button click listener
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                if (username.isEmpty() || password.isEmpty()) {
                    // Display error if username or password is empty
                    if (username.isEmpty()) {
                        usernameEditText.setError("Username is required");
                    }
                    if (password.isEmpty()) {
                        passwordEditText.setError("Password is required");
                    }
                } else {
                    // Register user in the database
                    long result = dbHelper.addUser(username, password);
                    if (result > 0) {
                        // Registration successful
                        Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                        // Redirect to login screen
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish(); // Close RegisterActivity
                    } else {
                        // Registration failed
                        Toast.makeText(RegisterActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
