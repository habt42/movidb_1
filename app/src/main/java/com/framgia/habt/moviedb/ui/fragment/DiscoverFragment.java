package com.framgia.habt.moviedb.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.framgia.habt.moviedb.AppController;
import com.framgia.habt.moviedb.R;
import com.framgia.habt.moviedb.data.model.Genre;
import com.framgia.habt.moviedb.util.ApiConst;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Calendar;

public class DiscoverFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private static final int START_YEAR = 1900;
    private Spinner mSpnYear;
    private Spinner mSpnSort;
    private Spinner mSpnGenre;

    private RecyclerViewFragment mListMovieFragment;

    public DiscoverFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discover, container, false);
        mSpnYear = (Spinner) view.findViewById(R.id.fragment_discover_spn_year);
        mSpnSort = (Spinner) view.findViewById(R.id.fragment_discover_spn_sort_by);
        mSpnGenre = (Spinner) view.findViewById(R.id.fragment_discover_spn_genre);
        initListMovieFragment();
        initSpnYear();
        initSpnSort();
        initSpnGenre();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.discover_fragment_tb_title);
    }

    public static DiscoverFragment newInstance() {
        return new DiscoverFragment();
    }

    private void initListMovieFragment() {
        mListMovieFragment = RecyclerViewFragment.newInstance(null, RecyclerViewFragment.LIST_MOVIE);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.fragment_discover_fl_holder, mListMovieFragment);
        ft.commit();
    }

    private void initSpnYear() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        String[] listYear = new String[currentYear - START_YEAR + 2];
        listYear[0] = getString(R.string.spinner_select_none);
        int index = 0;
        for (int i = currentYear; i >= START_YEAR; i--) {
            index++;
            listYear[index] = Integer.toString(i);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, listYear);
        mSpnYear.setAdapter(adapter);
        mSpnYear.setOnItemSelectedListener(this);
    }

    private void initSpnSort() {
        String[] listSortName = getResources().getStringArray(R.array.sort_by_array);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, listSortName);
        mSpnSort.setAdapter(adapter);
        mSpnSort.setOnItemSelectedListener(this);
    }

    private void initSpnGenre() {
        String url = ApiConst.LIST_GENRE_URL;
        StringRequest strReq = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Gson gson = new GsonBuilder().create();
                ArrayList<Genre> listGenre = gson.fromJson(response, ParseGson.class).getListGenre();
                Genre itemNone = new Genre();
                itemNone.setName(getString(R.string.spinner_select_none));
                listGenre.add(0, itemNone);
                ArrayAdapter<Genre> adapter = new ArrayAdapter<>(getActivity(),
                        android.R.layout.simple_spinner_dropdown_item, listGenre);
                mSpnGenre.setAdapter(adapter);
                mSpnGenre.setOnItemSelectedListener(DiscoverFragment.this);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), getString(R.string.json_load_error), Toast.LENGTH_LONG).show();
            }
        });
        AppController.getInstance().addToRequestQueue(strReq);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        reloadListMovie();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void reloadListMovie() {
        StringBuilder url = new StringBuilder();
        url.append(ApiConst.DISCOVER_URL);
        String sortParam = getSortParam(mSpnSort.getSelectedItem().toString());
        url.append(String.format(ApiConst.SORT_BY_PARAM, sortParam));
        if (!mSpnYear.getSelectedItem().toString().equalsIgnoreCase(getString(R.string.spinner_select_none))) {
            String year = String.format(ApiConst.YEAR_PARAM, mSpnYear.getSelectedItem().toString());
            url.append(year);
        }
        Genre genre = (Genre) mSpnGenre.getSelectedItem();
        if (genre != null && genre.getId() != null) {
            url.append(String.format(ApiConst.GENRE_PARAM, genre.getId()));
        }
        mListMovieFragment.reloadFromUrl(url.toString());
    }

    private String getSortParam(String itemName) {
        if (itemName.equalsIgnoreCase(getString(R.string.popularity_desc))) {
            return ApiConst.SORT_POPULARITY_DESC;
        } else if (itemName.equalsIgnoreCase(getString(R.string.popularity_asc))) {
            return ApiConst.SORT_POPULARITY_ASC;
        } else if (itemName.equalsIgnoreCase(getString(R.string.release_date_desc))) {
            return ApiConst.SORT_RELEASE_DATE_DESC;
        } else if (itemName.equalsIgnoreCase(getString(R.string.release_date_asc))) {
            return ApiConst.SORT_RELEASE_DATE_ASC;
        } else if (itemName.equalsIgnoreCase(getString(R.string.vote_average_desc))) {
            return ApiConst.SORT_RATING_DESC;
        } else if (itemName.equalsIgnoreCase(getString(R.string.vote_average_asc))) {
            return ApiConst.SORT_RATING_ASC;
        } else if (itemName.equalsIgnoreCase(getString(R.string.original_title_desc))) {
            return ApiConst.SORT_TITLE_DESC;
        } else if (itemName.equalsIgnoreCase(getString(R.string.original_title_asc))) {
            return ApiConst.SORT_TITLE_ASC;
        }
        return ApiConst.SORT_POPULARITY_DESC;
    }

    private static class ParseGson {
        @SerializedName("genres")
        private ArrayList<Genre> listGenre;

        public ArrayList<Genre> getListGenre() {
            return listGenre;
        }
    }
}
