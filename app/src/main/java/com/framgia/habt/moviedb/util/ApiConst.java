package com.framgia.habt.moviedb.util;

/**
 * Created by habt on 9/15/16.
 */
public class ApiConst {
    public static final String API_KEY = "8fbfeb446d2072b6787bdd9d4cdd42ed";
    public static final String BASE_URL = "https://api.themoviedb.org/3";
    public static final String API_KEY_PARAMS = "?api_key=" + API_KEY;
    public static final String PAGE_PARAMS = "&page=";
    public static final String POSTER_PATH_PREFIX_URL = "https://image.tmdb.org/t/p/w300_and_h450_bestv2";
    public static final String POPULAR_MOVIE_URL = BASE_URL + "/movie/popular" + API_KEY_PARAMS;
    public static final String TOP_RATED_MOVIE_URL = BASE_URL + "/movie/top_rated" + API_KEY_PARAMS;
    public static final String NOW_PLAYING_MOVIE_URL = BASE_URL + "/movie/now_playing" + API_KEY_PARAMS;
    public static final String UPCOMING_MOVIE_URL = BASE_URL + "/movie/upcoming" + API_KEY_PARAMS;
}
