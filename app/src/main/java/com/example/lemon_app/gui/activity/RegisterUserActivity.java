package com.example.lemon_app.gui.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.Volley;
import com.example.lemon_app.R;
import com.example.lemon_app.logic.database.RegisterRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class RegisterUserActivity extends AppCompatActivity implements View.OnClickListener, Response.Listener<String>, Response.ErrorListener {

    // region 0. Constants

    // endregion

    // region 1. Decl and Init

    private EditText txtName;
    private EditText txtEmail;
    private EditText txtPassword;
    private EditText txtPasswordAgain;
    
    private Button btnRegister;
    // endregion

    // region 3 Lifecycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_user_activity);
        
        this.txtName = findViewById(R.id.txt_user_name);
        this.txtEmail = findViewById(R.id.txt_user_email);
        this.txtPassword = findViewById(R.id.txt_user_password);
        this.txtPasswordAgain = findViewById(R.id.txt_user_password_again);
        this.btnRegister = findViewById(R.id.btn_register);
        
        this.btnRegister.setOnClickListener(this);

    }

    // endregion

    // region 4. Sending message to php

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_register) {
            String strName = this.txtName.getText().toString();
            String strEmail = this.txtEmail.getText().toString();
            String strPassword = this.txtPassword.getText().toString();
            String strPasswordAgain = this.txtPasswordAgain.getText().toString();

            if (strPassword.equals(strPasswordAgain)) {
                RequestQueue queue = Volley.newRequestQueue(RegisterUserActivity.this);
                RegisterRequest registerRequest = new RegisterRequest(strName, strPassword, strEmail, "", this, this);
                queue.add(registerRequest);

            } else {
                Toast.makeText(this, "The passwords is not the same", Toast.LENGTH_SHORT).show();
            }
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
}