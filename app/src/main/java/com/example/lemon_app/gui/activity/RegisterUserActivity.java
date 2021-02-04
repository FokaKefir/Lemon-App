package com.example.lemon_app.gui.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.lemon_app.R;
import com.example.lemon_app.logic.database.RegisterRequest;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

public class RegisterUserActivity extends AppCompatActivity implements View.OnClickListener, Response.Listener<String>, Response.ErrorListener {

    // region 0. Constants

    private static final Pattern PASSWORD_PATTERS =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +         //at least 1 digit
                    "(?=.*[a-z])" +         //at least 1 lower case letter
                    "(?=.*[A-Z])" +         //at least 1 upper case letter
                    //"(?=.*[a-zA-Z])" +      //any letter
                    //"(?=.*[@#$%^&+=])" +    //at least 1 special character
                    "(?=\\S+$)" +           //no white spaces
                    ".{6,20}" +             //at least 6 characters
                    "$");

    // endregion

    // region 1. Decl and Init

    private TextInputLayout txtInputName;
    private TextInputLayout txtInputEmail;
    private TextInputLayout txtInputPassword;

    private Button btnRegister;
    // endregion

    // region 3 Lifecycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_user_activity);

        this.txtInputName = findViewById(R.id.txt_input_username);
        this.txtInputEmail = findViewById(R.id.txt_input_email);
        this.txtInputPassword = findViewById(R.id.txt_input_password);
        this.btnRegister = findViewById(R.id.btn_register);

        this.btnRegister.setOnClickListener(this);

    }

    // endregion

    // region 4. Sending message to php

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_register) {
            String strName = this.txtInputName.getEditText().getText().toString().trim();
            String strEmail = this.txtInputEmail.getEditText().getText().toString().trim();
            String strPassword = this.txtInputPassword.getEditText().getText().toString().trim();

            if (!validateName(strName) | !validateEmail(strEmail) | !validatePassword(strPassword)) {
                return;
            }

            RequestQueue queue = Volley.newRequestQueue(RegisterUserActivity.this);
            RegisterRequest registerRequest = new RegisterRequest(strName, strPassword, strEmail, "", this, this);
            queue.add(registerRequest);
        }
    }

    // endregion

    // region 5. Getting response from php

    @Override
    public void onResponse(String response) {
        try {
            JSONObject jsonResponse = new JSONObject(response);
            boolean success = jsonResponse.getBoolean("success");

            if (success) {
                // TODO Go to login page
            } else {
                String errorMessage = jsonResponse.getString("message");

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(errorMessage)
                        .setNegativeButton("Retry", null)
                        .create().show();

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
    }

    // endregion

    // region 6. Validate inputs

    private boolean validateName(String strName) {
        if (strName.isEmpty()) {
            this.txtInputName.setError("Field can't be empty");
            return false;
        } else if (strName.length() > 16) {
            this.txtInputName.setError("Username too long");
            return false;
        } else {
            this.txtInputName.setError(null);
            return true;
        }

    }

    private boolean validateEmail(String strEmail) {
        if (strEmail.isEmpty()) {
            this.txtInputEmail.setError("Field can't be empty");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(strEmail).matches()) {
            this.txtInputEmail.setError("Please enter a valid email address");
            return false;
        } else {
            this.txtInputEmail.setError(null);
            return true;
        }
    }

    private boolean validatePassword(String strPassword) {
        if (strPassword.isEmpty()) {
            this.txtInputPassword.setError("Field can't be empty");
            return false;
        } else if (strPassword.length() > 20) {
            this.txtInputPassword.setError("Password too long");
            return false;
        } else if (!PASSWORD_PATTERS.matcher(strPassword).matches()) {
            this.txtInputPassword.setError("Password too weak");
            return false;
        } else {
            this.txtInputPassword.setError(null);
            return true;
        }
    }

    // endregion
}