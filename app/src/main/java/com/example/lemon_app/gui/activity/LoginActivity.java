package com.example.lemon_app.gui.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.lemon_app.R;
import com.example.lemon_app.logic.database.DataRequest;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, Response.Listener<String>, Response.ErrorListener {

    // region 0. Constants

    // TODO change the URL
    private static final String LOGIN_REQUEST_URL = "http://192.168.1.3/lemon_app/login.php";

    // endregion

    // region 1. Decl and Init

    private TextInputLayout txtInputName;
    private TextInputLayout txtInputPassword;

    private TextView txtRegister;

    private Button btnLogin;

    // endregion

    // region 2. Lifecycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.txtInputName = findViewById(R.id.txt_input_login_username);
        this.txtInputPassword = findViewById(R.id.txt_input_login_password);
        this.txtRegister = findViewById(R.id.txt_register);
        this.btnLogin = findViewById(R.id.btn_login);

        this.txtRegister.setOnClickListener(this);
        this.btnLogin.setOnClickListener(this);

    }

    // endregion

    // region 3. Button listener

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_login) {
            login();
        } else if (view.getId() == R.id.txt_register) {
            Intent intent = new Intent(this, RegisterUserActivity.class);
            startActivity(intent);
        }
    }

    // endregion

    // region 4. Login

    private void login() {
        String strName = this.txtInputName.getEditText().getText().toString().trim();
        String strPassword = this.txtInputPassword.getEditText().getText().toString().trim();

        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
        Map<String, String> params = new HashMap<>();
        params.put("name", strName);
        params.put("password", strPassword);
        DataRequest dataRequest = new DataRequest(params, LOGIN_REQUEST_URL, this, this);
        queue.add(dataRequest);
    }

    // endregion

    // region 5. Response from php

    @Override
    public void onResponse(String response) {
        try {
            JSONObject jsonResponse = new JSONObject(response);
            boolean success = jsonResponse.getBoolean("success");

            if (success){
                int userId = jsonResponse.getInt("id");
                openMainActivity(userId);

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

    // region 6. Open MainActivity

    private void openMainActivity(int userId) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("id", userId);
        startActivity(intent);
    }

    // endregion

}