package com.framgia.habt.moviedb.ui.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.framgia.habt.moviedb.AppController;
import com.framgia.habt.moviedb.R;
import com.framgia.habt.moviedb.data.model.Genre;
import com.framgia.habt.moviedb.data.model.Movie;
import com.framgia.habt.moviedb.data.model.ProductionCompany;
import com.framgia.habt.moviedb.util.ApiConst;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class MovieInfoFragment extends Fragment {
    public static final String ARG_MOVIE_ID = "ARG_MOVIE_ID";
    private NetworkImageView mImvPoster;
    private TextView mTvTitle;
    private TextView mTvStatus;
    private TextView mTvReleaseDate;
    private TextView mTvTagline;
    private TextView mTvOverview;
    private TextView mTvGenres;
    private TextView mTvCompanies;
    private String mMovieId;
    private Activity mActivity;

    public MovieInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMovieId = getArguments().getString(ARG_MOVIE_ID);
        mActivity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_info, container, false);
        mImvPoster = (NetworkImageView) view.findViewById(R.id.fragment_info_imv_poster);
        mTvTitle = (TextView) view.findViewById(R.id.fragment_info_tv_title);
        mTvStatus = (TextView) view.findViewById(R.id.fragment_info_tv_status);
        mTvReleaseDate = (TextView) view.findViewById(R.id.fragment_info_tv_release_date);
        mTvTagline = (TextView) view.findViewById(R.id.fragment_info_tv_tagline);
        mTvOverview = (TextView) view.findViewById(R.id.fragment_info_tv_overview);
        mTvGenres = (TextView) view.findViewById(R.id.fragment_info_tv_genres);
        mTvCompanies = (TextView) view.findViewById(R.id.fragment_info_tv_companies);
        getMovieDetail();
        return view;
    }

    public static MovieInfoFragment newInstance(String movieId) {
        Bundle args = new Bundle();
        args.putString(ARG_MOVIE_ID, movieId);
        MovieInfoFragment fragment = new MovieInfoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void getMovieDetail() {
        String url = String.format(ApiConst.MOVIE_DETAIL_URL, mMovieId);
        StringRequest strReq = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Gson gson = new GsonBuilder().create();
                initContents(gson.fromJson(response, Movie.class));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mActivity,
                        R.string.json_load_error, Toast.LENGTH_SHORT).show();
            }
        });
        AppController.getInstance().addToRequestQueue(strReq);
    }

    private void initContents(Movie movie) {
        ImageLoader imgLoader = AppController.getInstance().getImageLoader();
        mImvPoster.setImageUrl(ApiConst.POSTER_PATH_PREFIX_URL + movie.getPosterPath(), imgLoader);
        mTvTitle.setText(movie.getTitle());
        mTvStatus.setText(movie.getStatus());
        mTvReleaseDate.setText(movie.getReleaseDate());
        mTvTagline.setText(movie.getTagline());
        mTvOverview.setText(movie.getOverview());

        Genre[] listGenres = movie.getGenres();
        StringBuilder builder = new StringBuilder();
        if (listGenres != null && listGenres.length > 0) {
            builder.append(listGenres[0].getName());
            for (int i = 1; i < listGenres.length; i++) {
                builder.append(" / ");
                builder.append(listGenres[i].getName());
            }
            mTvGenres.setText(builder.toString());
        }

        ProductionCompany[] listCompany = movie.getProductionCompanies();
        if (listCompany != null && listCompany.length > 0) {
            builder = new StringBuilder();
            builder.append(listCompany[0].getName());
            for (int i = 1; i < listCompany.length; i++) {
                builder.append(", ");
                builder.append(listCompany[i].getName());
            }
            mTvCompanies.setText(builder.toString());
        }
    }
}
