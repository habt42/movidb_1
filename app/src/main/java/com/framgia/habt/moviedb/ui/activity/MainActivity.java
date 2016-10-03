package com.framgia.habt.moviedb.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.framgia.habt.moviedb.AppController;
import com.framgia.habt.moviedb.R;
import com.framgia.habt.moviedb.data.model.Account;
import com.framgia.habt.moviedb.ui.fragment.DiscoverFragment;
import com.framgia.habt.moviedb.ui.fragment.HomeFragment;
import com.framgia.habt.moviedb.ui.fragment.RecyclerViewFragment;
import com.framgia.habt.moviedb.util.ApiConst;
import com.framgia.habt.moviedb.util.AuthenticationInfo;
import com.framgia.habt.moviedb.util.AuthenticationTask;

public class MainActivity extends AppCompatActivity implements
        AuthenticationTask.GetSessionIdListener,
        View.OnClickListener,
        SearchView.OnQueryTextListener,
        SearchView.OnCloseListener {
    private SearchView mSearchView;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavView;
    private ActionBarDrawerToggle mDrawerToggle;

    private View mNavHeader;
    private NetworkImageView mImvProfilePic;
    private Button mBtnAccount;

    private ProgressDialog mProgressDialog;
    private AuthenticationTask mAuthenTask;

    private RecyclerViewFragment mSearchFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuthenTask = new AuthenticationTask(this);
        setupViews();
        setupNavView(checkLoggedIn(), null);
        setupDrawerContent(mNavView);
        showHomeFragment(false);
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
                    mAuthenTask.getSessionId(uri.getQueryParameter(AuthenticationTask.REQUEST_TOKEN));
                    intent.setData(null);
                    setIntent(intent);
                } else {
                    Toast.makeText(this, getResources().getString(R.string.login_failed), Toast.LENGTH_LONG).show();
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
        AuthenticationInfo.saveAccountId(this, account.getId());
        setupNavView(true, account);
    }

    @Override
    public void onGetRequestTokenResponse(String url) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent intent = builder.build();
        intent.launchUrl(this, Uri.parse(url));
    }

    @Override
    public void onGetSessionIdResponse(String sessionId) {
        AuthenticationInfo.saveSessionId(this, sessionId);
        mAuthenTask.getAccountInfo(sessionId);
    }

    @Override
    public void showLoginFailed() {
        Toast.makeText(this, getResources().getString(R.string.login_failed), Toast.LENGTH_LONG).show();
    }

    @Override
    public void showLoadingFailed() {
        Toast.makeText(this, getResources().getString(R.string.json_load_error), Toast.LENGTH_LONG).show();
    }

    @Override
    public void showGetInfoFailed() {
        Toast.makeText(this, getResources().getString(R.string.get_info_failed), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View view) {
        if (checkLoggedIn()) {
            //more work here
        } else {
            mAuthenTask.getRequestToken();
            showProgressDialog();
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (mSearchFragment == null) {
            showSearchFragment(query);
        } else {
            query = Uri.encode(query);
            String url = String.format(ApiConst.SEARCH_URL, query);
            mSearchFragment.reloadFromUrl(url);
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public boolean onClose() {
        if (mSearchFragment != null) {
            mSearchFragment.getUserVisibleHint();
            getSupportFragmentManager().popBackStackImmediate();
            mSearchFragment = null;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (!mSearchView.isIconified()) {
            closeSearch();
        } else {
            super.onBackPressed();
        }
    }

    private void closeSearch() {
        mSearchView.setQuery("", false);
        mSearchView.setIconified(true);
        onClose();
    }

    private void selectDrawerItem(MenuItem item) {
        Fragment fragment;
        switch (item.getItemId()) {
            case R.id.nav_view_home_item:
                showHomeFragment(true);
                return;
            case R.id.nav_view_discover_item:
                fragment = getDiscoverFragment();
                break;
            case R.id.nav_view_my_watchlist_item:
                fragment = getWatchlistFragment();
                break;
            case R.id.nav_view_my_favorite_item:
                fragment = getFavoriteFragment();
                break;
            case R.id.nav_view_logout_item:
                logout();
                return;
            default:
                fragment = new HomeFragment();
        }
        replaceFragment(fragment, true);
        item.setChecked(true);
        mDrawerLayout.closeDrawers();
    }

    private void replaceFragment(Fragment fragment, boolean addToBackstack) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.activity_main_fl_holder, fragment);
        if (addToBackstack) {
            ft.addToBackStack(null);
        }
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

    private void showSearchFragment(String query) {
        query = Uri.encode(query);
        String url = String.format(ApiConst.SEARCH_URL, query);
        mSearchFragment = RecyclerViewFragment.newInstance(url, RecyclerViewFragment.LIST_MOVIE);
        replaceFragment(mSearchFragment, true);
    }

    private Fragment getDiscoverFragment() {
        closeSearch();
        return DiscoverFragment.newInstance();
    }

    private Fragment getFavoriteFragment() {
        closeSearch();
        String title = getResources().getString(R.string.my_favorite_tb_title);
        String url = String.format(ApiConst.FAVORITE_LIST_URL, AuthenticationInfo.getAccountId(this),
                AuthenticationInfo.getSessionId(this));
        return RecyclerViewFragment.newInstance(url, RecyclerViewFragment.LIST_MOVIE).setTitle(title);
    }

    private Fragment getWatchlistFragment() {
        closeSearch();
        String title = getResources().getString(R.string.my_watchlist_tb_title);
        String url = String.format(ApiConst.WATCHLIST_URL, AuthenticationInfo.getAccountId(this),
                AuthenticationInfo.getSessionId(this));
        return RecyclerViewFragment.newInstance(url, RecyclerViewFragment.LIST_MOVIE).setTitle(title);
    }

    private void logout() {
        AuthenticationInfo.removeAccountId(this);
        AuthenticationInfo.removeSessionId(this);
        setupNavView(checkLoggedIn(), null);
        showHomeFragment(false);
    }

    private void showHomeFragment(boolean addToBackstack) {
        closeSearch();
        replaceFragment(new HomeFragment(), addToBackstack);
        mDrawerLayout.closeDrawers();
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
            myListItem.setVisible(true);
        } else {
            Bitmap img = BitmapFactory.decodeResource(getResources(), R.drawable.unknown_profile_picture);
            mImvProfilePic.setImageBitmap(img);
            mBtnAccount.setText(getResources().getText(R.string.nav_header_login));
            myListItem.setVisible(false);
        }
    }

    private boolean checkLoggedIn() {
        String sessionId = AuthenticationInfo.getSessionId(this);
        if (sessionId != null) {
            new AuthenticationTask(this).getAccountInfo(sessionId);
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

    private void setupViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(toolbar);
        mSearchView = (SearchView) findViewById(R.id.activity_main_search_view);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setOnCloseListener(this);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.activity_main_drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mNavView = (NavigationView) findViewById(R.id.activity_main_nav_view);
        mNavHeader = mNavView.inflateHeaderView(R.layout.nav_header);
        mImvProfilePic = (NetworkImageView) mNavHeader.findViewById(R.id.nav_header_imv_profile_pic);
        mBtnAccount = (Button) mNavHeader.findViewById(R.id.nav_header_btn_account);
        mBtnAccount.setOnClickListener(this);
    }

    private void showProgressDialog() {
        mProgressDialog = new ProgressDialog(this,
                R.style.AppTheme_Dark_Dialog);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.authenticating_message));
        mProgressDialog.show();
    }
}
