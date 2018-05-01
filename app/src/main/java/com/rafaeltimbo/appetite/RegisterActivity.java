package com.rafaeltimbo.appetite;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.rafaeltimbo.appetite.service.UserService;


public class RegisterActivity extends AppCompatActivity {
    private UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize User Service
        userService = new UserService(this);
    }

    public void btnRegister_OnClick(View view) {

        final EditText etEmail = (EditText)findViewById(R.id.etEmail);
        EditText etPassword = (EditText)findViewById(R.id.etPassword);
        EditText etConfirmPassword = (EditText)findViewById(R.id.etConfirmPassword);

        String email = etEmail.getText().toString().toLowerCase().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etEmail.getText().toString().toLowerCase().trim();

        if(etEmail.getText().toString().trim().length() == 0 ||
                etPassword.getText().toString().trim().length() == 0 ||
                etConfirmPassword.getText().toString().trim().length() == 0){

            Toast.makeText(this, "All fields are mandatory", Toast.LENGTH_SHORT).show();
            return;

        } else if (etPassword.getText().toString().trim().length() < 6) {

            Toast.makeText(this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
            return;

        } else if (!etPassword.getText().toString().trim().equals(etConfirmPassword.getText().toString().trim())) {

            Toast.makeText(this, "Password and Confirm Password must match", Toast.LENGTH_SHORT).show();
            return;

        } else if (!Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString().trim().toLowerCase()).matches()) {

            Toast.makeText(this, "Email format is incorrect", Toast.LENGTH_SHORT).show();
            return;

        }

        // Add new user to database if the email is not already in the user database
        if( userService.userEmailExists(email)){
            Toast.makeText(this, "Email already registered", Toast.LENGTH_SHORT).show();
            return;
        }
        userService.saveUser(email, password);
        Toast.makeText(this, "User registered successfully. Please login now.", Toast.LENGTH_SHORT).show();

        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
    }
}