package com.pucmasterapps.pucsportmaster;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by jakub on 04.12.2015.
 */
public class ThirdFragment extends Fragment {
    // Store instance variables
    private String title;
    private int page;

    // newInstance constructor for creating fragment with arguments
    public static ThirdFragment newInstance(int page, String title) {
        ThirdFragment fragmentThird = new ThirdFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);



        fragmentThird.setArguments(args);
        return fragmentThird;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("someInt", 0);
        title = getArguments().getString("someTitle");

    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_third, container, false);
    //    TextView tvLabel = (TextView) view.findViewById(R.id.tvLabel3);
    //    tvLabel.setText(page + " -- " + title);
        return view;

    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        insertNestedFragment();
    }
    private void insertNestedFragment() {
        Fragment childFragment = new MapsActivity();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.child_fragment_container, childFragment).commit();
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return super.onContextItemSelected(item);
    }

    @Override
    public void onStop() {
        System.out.println("Stopped");
        super.onStop();

    }

    @Override
    public void onPause() {
        System.out.println("Paused");
        super.onPause();
    }

    @Override
    public void onDestroy() {
        System.out.println("Destroyed");
        super.onDestroy();

    }

    @Override
    public void onDestroyView() {


        super.onDestroyView();

    }

    @Override
    public void onStart() {

        super.onStart();
    }
}

