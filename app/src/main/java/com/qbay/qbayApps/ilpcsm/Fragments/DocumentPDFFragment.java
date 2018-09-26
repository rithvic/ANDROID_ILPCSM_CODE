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

public class DocumentPDFFragment extends Fragment {

    LinearLayout mainLayout;
    WebView docWeView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mainLayout = (LinearLayout) inflater.inflate(R.layout.document_layout, container, false);



        docWeView = mainLayout.findViewById(R.id.doc_web_view);
        docWeView.setWebViewClient(new WebViewClient());
        WebSettings settings = docWeView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);


        docWeView.loadUrl("http://docs.google.com/gview?embedded=true&url=" + Constant.DOCUMENT_PDF_URL);


        return mainLayout;
    }

}








