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

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.lemon_app.R;
import com.example.lemon_app.constants.Constants;
import com.example.lemon_app.database.DataRequest;
import com.example.lemon_app.database.DatabaseManager;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.example.lemon_app.constants.Constants.LOGIN_REQUEST_URL;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, DatabaseManager.LoginManager.OnResponseListener {

    // region 0. Constants



    public static final String NAME = "name";
    public static final String PASSWORD = "password";
    public static final String ACCESS = "access";

    // endregion

    // region 1. Decl and Init

    private DatabaseManager.LoginManager databaseManager;

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

        this.databaseManager = new DatabaseManager.LoginManager(this, LoginActivity.this);

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
        this.databaseManager.login(this.strName, this.strPassword);
    }

    // endregion

    // region 5. Database manager listener

    @Override
    public void onSuccessfulLoginResponse(int userId) {
        this.userId = userId;
        if (this.access) {
            openMainActivity();
        } else {
            dialog();
        }
    }

    @Override
    public void onFailedLoginResponse(String errorMessage) {
        if(!this.access) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(errorMessage)
                    .setNegativeButton("Retry", null)
                    .create().show();
        } else {
            this.txtInputName.getEditText().setText(this.strName);
            this.access = false;
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
        intent.putExtra("name", this.strName);
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
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(NAME, this.strName);
        editor.putString(PASSWORD, this.strPassword);
        editor.putBoolean(ACCESS, true);
        editor.apply();
    }

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFS, MODE_PRIVATE);

        this.strName = sharedPreferences.getString(NAME, null);
        this.strPassword = sharedPreferences.getString(PASSWORD, null);
        this.access = sharedPreferences.getBoolean(ACCESS, false);

        if (this.access) {
            login();
        }

    }

    // endregion

}