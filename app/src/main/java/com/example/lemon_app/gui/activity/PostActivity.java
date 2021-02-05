package com.example.lemon_app.gui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.lemon_app.R;

public class PostActivity extends AppCompatActivity {

    // region 0. Constants

    // endregion

    // region 1. Delc and Init

    // endregion

    // region 2. Lifecycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_post);

        Intent intent = getIntent();
        int id = intent.getIntExtra("id", 0);

        TextView textView = findViewById(R.id.txt_post_pos);
        textView.setText(String.valueOf(id));

    }

    // endregion
}