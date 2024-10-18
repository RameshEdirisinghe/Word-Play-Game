package com.example.mywordplaygame;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    EditText nameEditText;      // Input field for user name
    Button saveNameButton;      // Button to save name
    TextView welcomeTextView;   // TextView to display welcome message
    SharedPreferences sharedPreferences;  // To store the name
    String userNameKey = "UserName";      // Key to store/retrieve the name

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);  // Set layout for MainActivity

        // Link UI elements to Java code
        nameEditText = findViewById(R.id.nameEditText);
        saveNameButton = findViewById(R.id.saveNameButton);
        welcomeTextView = findViewById(R.id.welcomeTextView);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        // Check if the user's name is already saved
        String savedName = sharedPreferences.getString(userNameKey, null);

        if (savedName != null) {  // If the name is already saved
            // Display welcome message
            welcomeTextView.setText("Welcome back, " + savedName + "!");

            // Hide name input and save button
            nameEditText.setVisibility(View.GONE);
            saveNameButton.setVisibility(View.GONE);

            // Navigate to GameActivity directly
            Intent intent = new Intent(MainActivity.this, GameActivity.class);
            startActivity(intent);
        } else {
            // If no name is saved, allow user to input and save name
            saveNameButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name = nameEditText.getText().toString().trim(); // Get the entered name

                    if (!name.isEmpty()) {  // Check if name is not empty
                        // Save the name in SharedPreferences
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(userNameKey, name);
                        editor.apply();  // Apply changes

                        // Display welcome message with the entered name
                        welcomeTextView.setText("Welcome, " + name + "!");

                        // Hide name input and save button
                        nameEditText.setVisibility(View.GONE);
                        saveNameButton.setVisibility(View.GONE);

                        // Navigate to GameActivity after saving the name
                        Intent intent = new Intent(MainActivity.this, GameActivity.class);
                        startActivity(intent);
                    }
                }
            });
        }
    }
}


