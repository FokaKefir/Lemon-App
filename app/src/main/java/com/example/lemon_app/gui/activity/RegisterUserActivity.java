package com.example.lemon_app.gui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.lemon_app.BuildConfig;
import com.example.lemon_app.R;
import com.example.lemon_app.logic.database.DataRequest;
import com.google.android.material.textfield.TextInputLayout;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

public class RegisterUserActivity extends AppCompatActivity implements View.OnClickListener, Response.Listener<String>, Response.ErrorListener {

    // region 0. Constants

    // TODO Change the URL
    private static final String REGISTER_REQUEST_URL = "http://192.168.1.4/lemon_app/register.php";
    private static final String UPLOAD_IMAGE_REQUEST_URL = "http://192.168.1.4/lemon_app/upload_image.php";
    private static final String IMAGE_URL = "http://192.168.1.4/lemon_app/images/";

    private static final String SAMPLE_IMAGE = "sample_profile_image";

    private static final int STORAGE_PERMISSION_CODE = 123;
    private static final int PICK_IMAGE_REQUEST = 1;

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
    private Button btnChooseImage;

    private Uri filePath;

    private String strImage;

    // endregion

    // region 3 Lifecycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        UploadService.NAMESPACE = BuildConfig.APPLICATION_ID;

        requestStoragePermission();
        this.filePath = null;
        this.strImage = SAMPLE_IMAGE;

        this.txtInputName = findViewById(R.id.txt_input_username);
        this.txtInputEmail = findViewById(R.id.txt_input_email);
        this.txtInputPassword = findViewById(R.id.txt_input_password);
        this.btnRegister = findViewById(R.id.btn_register);
        this.btnChooseImage = findViewById(R.id.btn_choose_image);

        this.btnRegister.setOnClickListener(this);
        this.btnChooseImage.setOnClickListener(this);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            this.filePath = data.getData();
            this.btnChooseImage.setText("Choosed");
        }
    }

    // endregion

    // region 4. Sending message to php

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_register) {
            uploadUserData();
        } else if (view.getId() == R.id.btn_choose_image) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
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
                if (!this.strImage.equals(SAMPLE_IMAGE)) {
                    uploadImage();
                }
                finish();

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

    // region 7. Request storage permission

    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //Checking the request code of our request
        if (requestCode == STORAGE_PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
                Toast.makeText(this, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    // endregion

    // region 8. Upload data

    private void uploadUserData() {
        String strName = this.txtInputName.getEditText().getText().toString().trim();
        String strEmail = this.txtInputEmail.getEditText().getText().toString().trim();
        String strPassword = this.txtInputPassword.getEditText().getText().toString().trim();
        if (this.filePath != null){
            this.strImage = "profile_" + UUID.randomUUID().toString();
        }

        if (!validateName(strName) | !validateEmail(strEmail) | !validatePassword(strPassword)) {
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put("name", strName);
        params.put("password", strPassword);
        params.put("email", strEmail);
        params.put("image", IMAGE_URL + this.strImage + ".jpg");
        DataRequest dataRequest = new DataRequest(params, REGISTER_REQUEST_URL, this, this);
        Volley.newRequestQueue(RegisterUserActivity.this).add(dataRequest);
    }

    private void uploadImage() {
        String path = getPath(this.filePath);

        try {
            String uploadId = UUID.randomUUID().toString();

            //Creating a multi part request
            new MultipartUploadRequest(this, uploadId, UPLOAD_IMAGE_REQUEST_URL)
                    .addFileToUpload(path, "image")
                    .addParameter("name", this.strImage)
                    .setNotificationConfig(new UploadNotificationConfig())
                    .setMaxRetries(2)
                    .startUpload();

        } catch (Exception exc) {
            Toast.makeText(this, exc.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    public String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }

    // endregion

}