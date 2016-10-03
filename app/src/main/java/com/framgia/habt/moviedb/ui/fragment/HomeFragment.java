package com.framgia.habt.moviedb.ui.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.framgia.habt.moviedb.R;
import com.framgia.habt.moviedb.util.ApiConst;

public class HomeFragment extends Fragment {
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mTabLayout = (TabLayout) view.findViewById(R.id.fragment_home_tab_layout);
        mViewPager = (ViewPager) view.findViewById(R.id.fragment_home_view_pager);
        String[] tabTitles = getActivity().getResources().getStringArray(R.array.home_fragment_pager_title);
        mViewPager.setAdapter(new PagerAdapter(getChildFragmentManager(), tabTitles));
        mViewPager.setOffscreenPageLimit(3);
        mTabLayout.setupWithViewPager(mViewPager);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.app_name);
    }

    private static class PagerAdapter extends FragmentPagerAdapter {
        private static final int PAGE_COUNT = 4;
        private String mTabTitles[];

        public PagerAdapter(FragmentManager fm, String[] tabTitles) {
            super(fm);
            mTabTitles = tabTitles;
        }

        @Override
        public Fragment getItem(int position) {
            String type;
            switch (position) {
                case 0:
                    type = ApiConst.POPULAR_MOVIE_URL;
                    break;
                case 1:
                    type = ApiConst.TOP_RATED_MOVIE_URL;
                    break;
                case 2:
                    type = ApiConst.NOW_PLAYING_MOVIE_URL;
                    break;
                case 3:
                    type = ApiConst.UPCOMING_MOVIE_URL;
                    break;
                default:
                    return null;
            }
            return RecyclerViewFragment.newInstance(type, RecyclerViewFragment.LIST_MOVIE);
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabTitles[position];
        }
    }
}
