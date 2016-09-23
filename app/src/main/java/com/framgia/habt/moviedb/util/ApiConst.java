package com.framgia.habt.moviedb.util;

/**
 * Created by habt on 9/15/16.
 */
public class ApiConst {
    public static final String API_KEY = "8fbfeb446d2072b6787bdd9d4cdd42ed";
    public static final String BASE_MOVIE_URL = "http://api.themoviedb.org/3/movie/";
    public static final String BASE_IMAGE_URL = "https://image.tmdb.org/t/p/";
    public static final String API_KEY_PARAMS = "?api_key=" + API_KEY;
    public static final String PAGE_PARAMS = "&page=";
    public static final String SIMILAR_MOVIES_URL = BASE_MOVIE_URL + "%s" + "/similar" + API_KEY_PARAMS;
    public static final String MOVIE_CREDITS_URL = BASE_MOVIE_URL + "%s" + "/credits" + API_KEY_PARAMS;
    public static final String MOVIE_DETAIL_URL = BASE_MOVIE_URL + "%s" + API_KEY_PARAMS;
    public static final String POSTER_PATH_PREFIX_URL = BASE_IMAGE_URL + "w300_and_h450_bestv2";
    public static final String BACKDROP_PATH_PREFIX_URL = BASE_IMAGE_URL + "w500";
    public static final String POPULAR_MOVIE_URL = BASE_MOVIE_URL + "popular" + API_KEY_PARAMS;
    public static final String TOP_RATED_MOVIE_URL = BASE_MOVIE_URL + "top_rated" + API_KEY_PARAMS;
    public static final String NOW_PLAYING_MOVIE_URL = BASE_MOVIE_URL + "now_playing" + API_KEY_PARAMS;
    public static final String UPCOMING_MOVIE_URL = BASE_MOVIE_URL + "upcoming" + API_KEY_PARAMS;
    public static final String MOVIE_LIST_URL_WITH_PAGE_PARAMS = "%s" + PAGE_PARAMS + "%d";
}
