package com.basestructure.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.basestructure.R;
import com.basestructure.activity.MainActivity;
import com.basestructure.base.AppIActivity;
import com.basestructure.base.BaseFragment;

public class FragmentThree extends BaseFragment {

    private static final String TAG = FragmentThree.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recycleview, container, false);

        return rootView;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);

    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity)getActivity()).setActionBarTitle(getResources().getString(R.string.menu_three));
        ((AppIActivity)getActivity()).setNavigationView(R.id.nav_three);
    }
}
