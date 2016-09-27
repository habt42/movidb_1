package com.framgia.habt.moviedb.ui.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.android.volley.toolbox.NetworkImageView;
import com.framgia.habt.moviedb.R;
import com.framgia.habt.moviedb.ui.fragment.HomeFragment;

public class MainActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private EditText mEdtSearch;
    private ImageButton mImbSearch;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavView;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.activity_main_drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mNavView = (NavigationView) findViewById(R.id.activity_main_nav_view);
        setupNavView(checkLoggedIn());
        setupDrawerContent(mNavView);
        showHomeFragment();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mDrawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean checkLoggedIn() {
        //check here
        return false;
    }

    private void setupDrawerContent(NavigationView navView) {
        navView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        selectDrawerItem(item);
                        return true;
                    }
                });
    }

    private void selectDrawerItem(MenuItem item) {
        item.setChecked(true);
        mDrawerLayout.closeDrawers();
    }

    private void setupNavView(boolean isLoggedIn) {
        View navHeader = mNavView.inflateHeaderView(R.layout.nav_header);
        NetworkImageView imvProfilePic = (NetworkImageView) navHeader.findViewById(R.id.nav_header_imv_profile_pic);
        Button btnAccount = (Button) navHeader.findViewById(R.id.nav_header_btn_account);
        MenuItem myListItem = mNavView.getMenu().findItem(R.id.nav_view_my_list_item);
        if (isLoggedIn) {
            //more work here
            myListItem.setVisible(true);
        } else {
            imvProfilePic.setDefaultImageResId(R.drawable.unknown_profile_picture);
            btnAccount.setText(getResources().getText(R.string.nav_header_login));
            myListItem.setVisible(false);
        }
    }

    private void showHomeFragment() {
        HomeFragment homeFragment = new HomeFragment();
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.activity_main_fl_holder, homeFragment).commit();
    }
}
