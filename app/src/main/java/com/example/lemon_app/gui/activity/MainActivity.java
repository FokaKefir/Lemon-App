package com.example.lemon_app.gui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.lemon_app.constants.Constants;
import com.example.lemon_app.gui.fragment.PostsFragment;
import com.example.lemon_app.gui.fragment.NotificationsFragment;
import com.example.lemon_app.R;
import com.example.lemon_app.gui.fragment.UserFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    // region 0. Constants

    // endregion

    // region 1. Decl and Init

    private BottomNavigationView bottomNav;

    private static int userId;
    private static String strUser;

    private PostsFragment postsFragment;
    private NotificationsFragment notificationsFragment;
    private UserFragment userFragment;

    // endregion

    // region 2. Lifecycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            userId = bundle.getInt("id");
            strUser = bundle.getString("name");
        }

        this.bottomNav = findViewById(R.id.bottom_navigation);
        this.bottomNav.setOnNavigationItemSelectedListener(this);

        this.postsFragment = new PostsFragment();
        this.notificationsFragment = new NotificationsFragment();
        this.userFragment = new UserFragment();

        this.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, this.postsFragment).commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logout_menu, menu);
        return true;
    }

    // endregion

    // region 3. Bottom Navigation View

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = null;
        switch (item.getItemId()){
            case R.id.nav_home:
                selectedFragment = this.postsFragment;
                break;
            case R.id.nav_notifications:
                selectedFragment = this.notificationsFragment;
                break;
            case R.id.nav_user:
                selectedFragment = this.userFragment;
                break;
        }

        if (selectedFragment != null) {
            this.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            return true;
        }
        return false;
    }

    // endregion

    // region 4. Toolbar Menu item

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.mnu_logout) {
            SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFS, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    // endregion

    // region 5. Getters and Setters

    public static int getUserId() {
        return userId;
    }
    public static String getStrUser() {
        return strUser;
    }
// endregion

}