package com.example.lemon_app.gui.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
    private static final String LOGIN_REQUEST_URL = "http://192.168.1.4/lemon_app/login.php";

    public static final String SHARED_PREFS = "login";
    public static final String NAME = "name";
    public static final String PASSWORD = "password";
    public static final String ACCESS = "access";

    // endregion

    // region 1. Decl and Init

    private TextInputLayout txtInputName;
    private TextInputLayout txtInputPassword;

    private TextView txtRegister;

    private Button btnLogin;

    private String strName;
    private String strPassword;

    private int userId;
    private Boolean access;

    // endregion

    // region 2. Lifecycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.access = false;
        loadData();

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
            this.strName = this.txtInputName.getEditText().getText().toString().trim();
            this.strPassword = this.txtInputPassword.getEditText().getText().toString().trim();
            login();
        } else if (view.getId() == R.id.txt_register) {
            Intent intent = new Intent(this, RegisterUserActivity.class);
            startActivity(intent);
        }
    }

    // endregion

    // region 4. Login

    private void login() {
        Map<String, String> params = new HashMap<>();
        params.put("name", this.strName);
        params.put("password", this.strPassword);
        DataRequest dataRequest = new DataRequest(params, LOGIN_REQUEST_URL, this, this);
        Volley.newRequestQueue(LoginActivity.this).add(dataRequest);
    }

    // endregion

    // region 5. Response from php

    @Override
    public void onResponse(String response) {
        try {
            JSONObject jsonResponse = new JSONObject(response);
            boolean success = jsonResponse.getBoolean("success");

            if (success){
                this.userId = jsonResponse.getInt("id");
                if (this.access) {
                    openMainActivity();
                } else {
                    dialog();
                }


            } else if(!this.access) {
                String errorMessage = jsonResponse.getString("message");

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(errorMessage)
                        .setNegativeButton("Retry", null)
                        .create().show();
            } else {
                this.txtInputName.getEditText().setText(this.strName);
                this.access = false;
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

    private void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("id", this.userId);
        startActivity(intent);
        finish();

    }

    // endregion

    // region 7. Save and load data

    private void dialog() {
        new AlertDialog.Builder(this)
                .setTitle("Save login data")
                .setMessage("Do you want to save login data?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        saveData();
                        openMainActivity();

                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        openMainActivity();
                    }
                })
                .setIcon(R.drawable.ic_dialog_save)
                .show();
    }

    private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(NAME, this.strName);
        editor.putString(PASSWORD, this.strPassword);
        editor.putBoolean(ACCESS, true);
        editor.apply();
    }

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        this.strName = sharedPreferences.getString(NAME, null);
        this.strPassword = sharedPreferences.getString(PASSWORD, null);
        this.access = sharedPreferences.getBoolean(ACCESS, false);

        if (this.access) {
            login();
        }

    }

    // endregion

}