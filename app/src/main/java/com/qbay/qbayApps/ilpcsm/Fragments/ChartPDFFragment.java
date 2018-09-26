package com.qbay.qbayApps.ilpcsm.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.qbay.qbayApps.ilpcsm.R;
import com.qbay.qbayApps.ilpcsm.Services.Constant;

public class ChartPDFFragment extends Fragment {

    LinearLayout mainLayout;
    WebView chartWeView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mainLayout = (LinearLayout) inflater.inflate(R.layout.chart_pdf_layout, container, false);


        chartWeView = mainLayout.findViewById(R.id.chart_web_view);
        chartWeView.setWebViewClient(new WebViewClient());

        WebSettings settings = chartWeView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);

        chartWeView.loadUrl("http://docs.google.com/gview?embedded=true&url="+Constant.CHART_PDF_URL);


        return mainLayout;
    }

}
