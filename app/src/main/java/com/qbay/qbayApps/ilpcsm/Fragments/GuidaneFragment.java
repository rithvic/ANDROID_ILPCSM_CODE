package com.qbay.qbayApps.ilpcsm.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;

import com.qbay.qbayApps.ilpcsm.R;

/**
 * Created by benchmark on 31/03/18.
 */

public class GuidaneFragment extends Fragment {

    LinearLayout mainLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mainLayout = (LinearLayout) inflater.inflate(R.layout.guidance_layout, container, false);
        return mainLayout;

    }
}
