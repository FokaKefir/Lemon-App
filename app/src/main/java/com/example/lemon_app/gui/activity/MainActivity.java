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

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    // region 0. Constants

    // endregion

    // region 1. Decl and Init

    private BottomNavigationView bottomNav;

    private static int userId;
    private static String strUser;

    private List<Fragment> homeFragments;
    private List<Fragment> notFragments;
    private List<Fragment> userFragments;

    private List<Fragment> activeFragments;

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

        this.homeFragments = new ArrayList<>();
        this.notFragments = new ArrayList<>();
        this.userFragments = new ArrayList<>();

        this.homeFragments.add(new PostsFragment(this));
        this.notFragments.add(new NotificationsFragment(this));
        this.userFragments.add(new UserFragment(this));

        this.getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, this.homeFragments.get(0)).commit();

        this.getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, this.notFragments.get(0)).hide(this.notFragments.get(0)).commit();

        this.getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, this.userFragments.get(0)).hide(this.userFragments.get(0)).commit();

        this.activeFragments = this.homeFragments;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logout_menu, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        System.exit(0);
    }

    // endregion

    // region 3. Bottom Navigation View

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        List<Fragment> selectedFragments = null;
        Fragment selectedFragment = null;
        switch (item.getItemId()){
            case R.id.nav_home:
                selectedFragments = this.homeFragments;
                selectedFragment = new PostsFragment(this);
                break;
            case R.id.nav_notifications:
                selectedFragments = this.notFragments;
                selectedFragment = new NotificationsFragment(this);
                break;
            case R.id.nav_user:
                selectedFragments = this.userFragments;
                selectedFragment = new UserFragment(this);
                break;
        }

        if (selectedFragments != null) {
            if (selectedFragments == this.activeFragments) {
                for (Fragment fragment : this.activeFragments)
                    getSupportFragmentManager().beginTransaction().remove(fragment).commit();

                this.getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, selectedFragment).commit();
                this.activeFragments.clear();
                this.activeFragments.add(selectedFragment);
            } else {
                for (Fragment fragment : this.activeFragments)
                    this.getSupportFragmentManager().beginTransaction().hide(fragment).commit();
                for (Fragment fragment : selectedFragments)
                    this.getSupportFragmentManager().beginTransaction().show(fragment).commit();
                this.activeFragments = selectedFragments;
            }

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

    // region 5. Fragment functions

    public void addToFragments(Fragment fragment) {
        this.activeFragments.add(fragment);
    }

    public void removeFromFragments() {
        if (this.activeFragments.size() > 1) {
            Fragment fragment = this.activeFragments.get(this.activeFragments.size() - 1);
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            this.activeFragments.remove(fragment);
        } else {
            onBackPressed();
        }
    }

    // endregion

    // region 6. Getters and Setters

    public static int getUserId() {
        return userId;
    }
    public static String getStrUser() {
        return strUser;
    }

    // endregion

}