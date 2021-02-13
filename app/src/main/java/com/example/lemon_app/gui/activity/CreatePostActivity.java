package com.example.lemon_app.gui.activity;

import androidx.annotation.NonNull;
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
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.lemon_app.BuildConfig;
import com.example.lemon_app.R;
import com.example.lemon_app.constants.Constants;
import com.example.lemon_app.database.DataRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.example.lemon_app.constants.Constants.IMAGE_URL;
import static com.example.lemon_app.constants.Constants.UPLOAD_IMAGE_REQUEST_URL;
import static com.example.lemon_app.constants.Constants.UPLOAD_POST_REQUEST_URL;

public class CreatePostActivity extends AppCompatActivity implements View.OnClickListener, Response.ErrorListener, Response.Listener<String> {

    // region 0. Constants

    // endregion

    // region 1. Delc and Init

    private TextInputLayout txtInputContent;

    private Button btnChooseImage;
    private FloatingActionButton fabFinish;

    private Uri filePath;

    private String image;
    private int userId;

    // endregion

    // region 2. Lifecycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        UploadService.NAMESPACE = BuildConfig.APPLICATION_ID;

        this.filePath = null;
        this.image = "";

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            this.userId = bundle.getInt("id");
        }

        this.txtInputContent = findViewById(R.id.txt_input_content);
        this.btnChooseImage = findViewById(R.id.btn_choose_image_post);
        this.fabFinish = findViewById(R.id.fab_finish_post);

        this.btnChooseImage.setOnClickListener(this);
        this.fabFinish.setOnClickListener(this);

    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            this.filePath = data.getData();
            this.btnChooseImage.setText("Choosed");
        }
    }

    // endregion

    // region 3. Clicking on button

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_choose_image_post) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                chooseImageFromStorage();
            } else {
                requestStoragePermission();
            }

        } else if (view.getId() == R.id.fab_finish_post) {
            uploadPostData();
        }
    }

    // endregion

    // region 4. Getting response from php

    @Override
    public void onResponse(String response) {
        try {
            JSONObject jsonResponse = new JSONObject(response);
            boolean success = jsonResponse.getBoolean("success");

            if (success) {
                if (!this.image.equals("")) {
                    uploadImage();
                }
                finish();
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

    // region 5. Validate inputs

    private boolean validateContent(String strContent) {
        if (strContent.length() > 500) {
            this.txtInputContent.setError("Content is too long");
            return false;
        } else {
            this.txtInputContent.setError(null);
            return true;
        }
    }

    // endregion

    // region 6. Request storage permission

    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Constants.STORAGE_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //Checking the request code of our request
        if (requestCode == Constants.STORAGE_PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
                Toast.makeText(this, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
                chooseImageFromStorage();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    // endregion

    // region 7. Choose image from storage

    private void chooseImageFromStorage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), Constants.PICK_IMAGE_REQUEST);
    }

    // endregion

    // region 8. Upload data

    private void uploadPostData() {
        String strContent = this.txtInputContent.getEditText().getText().toString().trim();
        String strImage = "";
        if (this.filePath != null){
            this.image = "post_" + UUID.randomUUID().toString();
            strImage = IMAGE_URL + this.image + ".jpg";
        }

        if (!validateContent(strContent)) {
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(this.userId));
        params.put("content", strContent);
        params.put("image", strImage);
        DataRequest dataRequest = new DataRequest(params, UPLOAD_POST_REQUEST_URL, this, this);
        Volley.newRequestQueue(CreatePostActivity.this).add(dataRequest);
    }

    private void uploadImage() {
        String path = getPath(this.filePath);

        try {
            String uploadId = UUID.randomUUID().toString();

            //Creating a multi part request
            new MultipartUploadRequest(this, uploadId, UPLOAD_IMAGE_REQUEST_URL)
                    .addFileToUpload(path, "image")
                    .addParameter("name", this.image)
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