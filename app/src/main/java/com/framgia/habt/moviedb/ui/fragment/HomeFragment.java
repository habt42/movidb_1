package com.framgia.habt.moviedb.ui.fragment;

import android.content.Context;
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

public class HomeFragment extends Fragment {
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mTabLayout = (TabLayout) view.findViewById(R.id.fragment_home_tab_layout);
        mViewPager = (ViewPager) view.findViewById(R.id.fragment_home_view_pager);
        String[] tabTitles = getActivity().getResources().getStringArray(R.array.home_fragment_pager_title);
        mViewPager.setAdapter(new PagerAdapter(getFragmentManager(), tabTitles));
        mTabLayout.setupWithViewPager(mViewPager);
        return view;
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
            return new ListMovieFragment();
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
