package com.example.portalc;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText;
    private Button loginButton;
    private TextView registerLink;
    private SQLiteHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize UI components
        usernameEditText = findViewById(R.id.username); // Ensure this matches XML ID
        passwordEditText = findViewById(R.id.password); // Ensure this matches XML ID
        loginButton = findViewById(R.id.loginButton);
        registerLink = findViewById(R.id.registerLink);

        // Initialize SQLiteHelper for checking credentials
        dbHelper = new SQLiteHelper(this);

        // Set up login button click listener
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                // Check credentials in the database
                if (dbHelper.checkUser(username, password)) {
                    // If credentials are correct, navigate to MainActivity
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish(); // Close login activity
                } else {
                    // Show an error message if login fails
                    usernameEditText.setError("Invalid credentials");
                }
            }
        });

        // Set up register link click listener
        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}
