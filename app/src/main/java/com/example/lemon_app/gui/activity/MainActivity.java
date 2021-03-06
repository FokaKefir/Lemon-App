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
import com.example.lemon_app.gui.fragment.FollowersFragment;
import com.example.lemon_app.gui.fragment.PostsFragment;
import com.example.lemon_app.gui.fragment.NotificationsFragment;
import com.example.lemon_app.R;
import com.example.lemon_app.gui.fragment.SearchFragment;
import com.example.lemon_app.gui.fragment.UserFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    // region 0. Constants

    // endregion

    // region 1. Decl and Init

    private BottomNavigationView bottomNav;

    private int userId;
    private String strUser;

    private List<Fragment> homeFragments;
    private List<Fragment> notificationFragments;
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
        this.notificationFragments = new ArrayList<>();
        this.userFragments = new ArrayList<>();

        this.homeFragments.add(new PostsFragment(this));
        this.notificationFragments.add(new NotificationsFragment(this));
        this.userFragments.add(new UserFragment(this));

        this.getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, this.homeFragments.get(0)).commit();

        this.getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, this.notificationFragments.get(0)).hide(this.notificationFragments.get(0)).commit();

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
        if (this.activeFragments.size() > 1) {
            removeFromFragments();
        } else {
            finish();
            System.exit(0);
        }
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
                selectedFragments = this.notificationFragments;
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
                    this.getSupportFragmentManager().beginTransaction().remove(fragment).commit();

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

    // region 5. Add and remove fragment

    public void addToFragments(Fragment fragment) {
        this.getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                .add(R.id.fragment_container, fragment).addToBackStack(null).commit();
        this.activeFragments.add(fragment);
    }

    public void removeFromFragments() {
        Fragment fragment = this.activeFragments.get(this.activeFragments.size() - 1);
        this.getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.slide_out, R.anim.slide_in, R.anim.fade_out)
                .remove(fragment).commit();
        this.activeFragments.remove(fragment);
    }

    // endregion

    // region 6. Refresh fragments

    public void refreshFollow(Fragment fromFragment, int userId, int type) {
        for (Fragment fragment : this.homeFragments) {
            if (fragment == fromFragment)
                continue;

            if (fragment.getClass() == UserFragment.class) {
                UserFragment userFragment = (UserFragment) fragment;
                userFragment.refreshFollow(userId, type);
            } else if (fragment.getClass() == FollowersFragment.class) {
                FollowersFragment followersFragment = (FollowersFragment) fragment;
                followersFragment.refreshFollow(userId, type);
            } else if (fragment.getClass() == SearchFragment.class) {
                SearchFragment searchFragment = (SearchFragment) fragment;
                searchFragment.refreshFollow(userId, type);
            }
        }

        for (Fragment fragment : this.notificationFragments) {
            if (fragment == fromFragment)
                continue;

            if (fragment.getClass() == UserFragment.class) {
                UserFragment userFragment = (UserFragment) fragment;
                userFragment.refreshFollow(userId, type);
            } else if (fragment.getClass() == FollowersFragment.class) {
                FollowersFragment followersFragment = (FollowersFragment) fragment;
                followersFragment.refreshFollow(userId, type);
            } else if (fragment.getClass() == SearchFragment.class) {
                SearchFragment searchFragment = (SearchFragment) fragment;
                searchFragment.refreshFollow(userId, type);
            }
        }

        for (Fragment fragment : this.userFragments) {
            if (fragment == fromFragment)
                continue;

            if (fragment.getClass() == UserFragment.class) {
                UserFragment userFragment = (UserFragment) fragment;
                userFragment.refreshFollow(userId, type);
            } else if (fragment.getClass() == FollowersFragment.class) {
                FollowersFragment followersFragment = (FollowersFragment) fragment;
                followersFragment.refreshFollow(userId, type);
            } else if (fragment.getClass() == SearchFragment.class) {
                SearchFragment searchFragment = (SearchFragment) fragment;
                searchFragment.refreshFollow(userId, type);
            }
        }

    }

    public void refreshLike(Fragment fromFragment, int postId, int type) {
        for (Fragment fragment : this.homeFragments) {
            if (fragment == fromFragment)
                continue;

            if (fragment.getClass() == UserFragment.class) {
                UserFragment userFragment = (UserFragment) fragment;
                userFragment.refreshLike(postId, type);
            } else if (fragment.getClass() == PostsFragment.class) {
                PostsFragment postsFragment = (PostsFragment) fragment;
                postsFragment.refreshLike(postId, type);
            }
        }

        for (Fragment fragment : this.notificationFragments) {
            if (fragment == fromFragment)
                continue;

            if (fragment.getClass() == UserFragment.class) {
                UserFragment userFragment = (UserFragment) fragment;
                userFragment.refreshLike(postId, type);
            } else if (fragment.getClass() == PostsFragment.class) {
                PostsFragment postsFragment = (PostsFragment) fragment;
                postsFragment.refreshLike(postId, type);
            }
        }

        for (Fragment fragment : this.userFragments) {
            if (fragment == fromFragment)
                continue;

            if (fragment.getClass() == UserFragment.class) {
                UserFragment userFragment = (UserFragment) fragment;
                userFragment.refreshLike(postId, type);
            } else if (fragment.getClass() == PostsFragment.class) {
                PostsFragment postsFragment = (PostsFragment) fragment;
                postsFragment.refreshLike(postId, type);
            }
        }
    }

    public void refreshComment(Fragment fromFragment, int postId, int type) {
        for (Fragment fragment : this.homeFragments) {
            if (fragment == fromFragment)
                continue;

            if (fragment.getClass() == UserFragment.class) {
                UserFragment userFragment = (UserFragment) fragment;
                userFragment.refreshComment(postId, type);
            } else if (fragment.getClass() == PostsFragment.class) {
                PostsFragment postsFragment = (PostsFragment) fragment;
                postsFragment.refreshComment(postId, type);
            }
        }

        for (Fragment fragment : this.notificationFragments) {
            if (fragment == fromFragment)
                continue;

            if (fragment.getClass() == UserFragment.class) {
                UserFragment userFragment = (UserFragment) fragment;
                userFragment.refreshComment(postId, type);
            } else if (fragment.getClass() == PostsFragment.class) {
                PostsFragment postsFragment = (PostsFragment) fragment;
                postsFragment.refreshComment(postId, type);
            }
        }

        for (Fragment fragment : this.userFragments) {
            if (fragment == fromFragment)
                continue;

            if (fragment.getClass() == UserFragment.class) {
                UserFragment userFragment = (UserFragment) fragment;
                userFragment.refreshComment(postId, type);
            } else if (fragment.getClass() == PostsFragment.class) {
                PostsFragment postsFragment = (PostsFragment) fragment;
                postsFragment.refreshComment(postId, type);
            }
        }
    }

    // endregion

    // region 7. Getters and Setters

    public int getLoggedUserId() {
        return this.userId;
    }

    public String getStrUser() {
        return this.strUser;
    }

    // endregion

}