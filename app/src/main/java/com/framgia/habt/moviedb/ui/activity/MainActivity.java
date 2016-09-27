package com.framgia.habt.moviedb.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
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
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.framgia.habt.moviedb.AppController;
import com.framgia.habt.moviedb.R;
import com.framgia.habt.moviedb.data.model.Account;
import com.framgia.habt.moviedb.ui.fragment.HomeFragment;
import com.framgia.habt.moviedb.util.ApiConst;
import com.framgia.habt.moviedb.util.AuthenticationTask;

public class MainActivity extends AppCompatActivity implements AuthenticationTask.GetSessionIdListener {
    private Toolbar mToolbar;
    private EditText mEdtSearch;
    private ImageButton mImbSearch;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavView;
    private ActionBarDrawerToggle mDrawerToggle;

    private View mNavHeader;
    private NetworkImageView mImvProfilePic;
    private Button mBtnAccount;

    private MainActivity mActivity;
    private ProgressDialog mProgressDialog;

    private View.OnClickListener onClickLogin = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            new AuthenticationTask(mActivity, MainActivity.this).getRequestToken();
            showProgressDialog();
        }
    };

    private View.OnClickListener onClickProfile = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mActivity = this;
        mToolbar = (Toolbar) findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.activity_main_drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mNavView = (NavigationView) findViewById(R.id.activity_main_nav_view);
        mNavHeader = mNavView.inflateHeaderView(R.layout.nav_header);
        mImvProfilePic = (NetworkImageView) mNavHeader.findViewById(R.id.nav_header_imv_profile_pic);
        mBtnAccount = (Button) mNavHeader.findViewById(R.id.nav_header_btn_account);
        setupNavView(checkLoggedIn(), null);
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

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        if (intent != null && intent.getData() != null) {
            Uri uri = intent.getData();
            if (uri.getQueryParameter("action").equalsIgnoreCase(ApiConst.AUTHENTICATION_ACTION)) {
                if (uri.getQueryParameter(AuthenticationTask.APPROVED_RETURN) != null) {
                    showProgressDialog();
                    AuthenticationTask task = new AuthenticationTask(mActivity, MainActivity.this);
                    task.getSessionId(uri.getQueryParameter(AuthenticationTask.REQUEST_TOKEN));
                } else {
                    Toast.makeText(mActivity, getResources().getString(R.string.login_failed), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public void dismissProgressDialog() {
        mProgressDialog.dismiss();
    }

    @Override
    public void onLoginSuccess(Account account) {
        setupNavView(true, account);
    }

    private void selectDrawerItem(MenuItem item) {
        item.setChecked(true);
        mDrawerLayout.closeDrawers();
    }


    private void showHomeFragment() {
        HomeFragment homeFragment = new HomeFragment();
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.activity_main_fl_holder, homeFragment).commit();
    }

    private void setupNavView(boolean isLoggedIn, Account account) {
        MenuItem myListItem = mNavView.getMenu().findItem(R.id.nav_view_my_list_item);
        if (isLoggedIn) {
            if (account != null) {
                String imgUrl = String.format(ApiConst.GRAVATAR_URL, account.getGravatarHash());
                ImageLoader imgLoader = AppController.getInstance().getImageLoader();
                mImvProfilePic.setImageUrl(imgUrl, imgLoader);
                mBtnAccount.setText(account.getUsername());
            }
            mBtnAccount.setOnClickListener(onClickProfile);
            myListItem.setVisible(true);
        } else {
            mBtnAccount.setOnClickListener(onClickLogin);
            mImvProfilePic.setDefaultImageResId(R.drawable.unknown_profile_picture);
            mBtnAccount.setText(getResources().getText(R.string.nav_header_login));
            myListItem.setVisible(false);
        }
    }

    private boolean checkLoggedIn() {
        String sessionId = AuthenticationTask.getSessionId(mActivity);
        if (sessionId != null) {
            new AuthenticationTask(mActivity, this).getAccountInfo(sessionId);
            return true;
        }
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

    private void showProgressDialog() {
        mProgressDialog = new ProgressDialog(mActivity,
                R.style.AppTheme_Dark_Dialog);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.authenticating_message));
        mProgressDialog.show();
    }
}
