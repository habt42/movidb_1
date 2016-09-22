package com.framgia.habt.moviedb.ui.activity;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.framgia.habt.moviedb.AppController;
import com.framgia.habt.moviedb.R;
import com.framgia.habt.moviedb.data.model.Movie;
import com.framgia.habt.moviedb.ui.fragment.ListCastFragment;
import com.framgia.habt.moviedb.ui.fragment.ListMovieFragment;
import com.framgia.habt.moviedb.ui.fragment.MovieInfoFragment;
import com.framgia.habt.moviedb.util.ApiConst;

public class MovieDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        Movie movie = getIntent().getParcelableExtra(ListMovieFragment.EXTRA_MOVIE);

        CollapsingToolbarLayout clsToolbar = (CollapsingToolbarLayout) findViewById(R.id.activity_movie_detail_cls_toolbar);
        clsToolbar.setTitle(movie.getTitle());
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_movie_detail_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.activity_movie_detail_tl);
        ViewPager viewPager = (ViewPager) findViewById(R.id.activity_movie_detail_vp);
        String[] tabTitles = getResources().getStringArray(R.array.movie_detail_activity_pager_title);
        viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager(), tabTitles, movie));
        tabLayout.setupWithViewPager(viewPager);

        ImageLoader imgLoader = AppController.getInstance().getImageLoader();
        NetworkImageView imvBackdrop = (NetworkImageView) findViewById(R.id.activity_movie_detail_imv_backdrop);
        imvBackdrop.setImageUrl(ApiConst.BACKDROP_PATH_PREFIX_URL + movie.getBackdropPath(), imgLoader);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private static class PagerAdapter extends FragmentPagerAdapter {
        private static final int PAGE_COUNT = 3;
        private String[] mTitles;
        private Movie mMovie;

        public PagerAdapter(FragmentManager fm, String[] titles, Movie movie) {
            super(fm);
            mTitles = titles;
            mMovie = movie;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return MovieInfoFragment.newInstance(mMovie.getId());
                case 1:
                    return ListCastFragment.newInstance(mMovie.getId());
                case 2:
                    String url = String.format(ApiConst.SIMILAR_MOVIES_URL, mMovie.getId());
                    return ListMovieFragment.newInstance(url);
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }
    }
}
