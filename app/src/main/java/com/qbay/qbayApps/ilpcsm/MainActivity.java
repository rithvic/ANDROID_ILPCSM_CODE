package com.qbay.qbayApps.ilpcsm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;
import com.qbay.qbayApps.ilpcsm.Fragments.ChartPDFFragment;
import com.qbay.qbayApps.ilpcsm.Fragments.DocumentPDFFragment;
import com.qbay.qbayApps.ilpcsm.Fragments.GuidaneFragment;
import com.qbay.qbayApps.ilpcsm.Fragments.HomeFragment;
import com.qbay.qbayApps.ilpcsm.Fragments.SearchFragment;
import com.qbay.qbayApps.ilpcsm.Services.AppController;
import com.qbay.qbayApps.ilpcsm.Services.ConnectivityReceiver;
import com.qbay.qbayApps.ilpcsm.Services.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    //Shared Preference Declarations
    SharedPreferences prefs = null;

    //Side Menu Declaration
    SpaceNavigationView spaceNavigationView;
    HomeFragment frag1 = null;

    //Common Declarations
    Context context;
    int navigationPosition;
    String mainNavigationPosition;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide();
        prefs = getSharedPreferences("FirstCheck", MODE_PRIVATE);
        mainNavigationPosition = "Home";
        navigationPosition = 0;
        context = this;

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        //Declare Number Of Bottom Menu Items
        spaceNavigationView = findViewById(R.id.space);
        spaceNavigationView.initWithSaveInstanceState(savedInstanceState);
        spaceNavigationView.addSpaceItem(new SpaceItem("ABOUT US", R.drawable.applogo));
        spaceNavigationView.addSpaceItem(new SpaceItem("SEARCH", R.drawable.search));
        spaceNavigationView.addSpaceItem(new SpaceItem("CAREER CHART", R.drawable.chartpdf));
        spaceNavigationView.addSpaceItem(new SpaceItem("DOCUMENT", R.drawable.docpdf));

        //Bottom Menu Configuration
        spaceNavigationView.showIconOnly();

        //Starting Fragment Initialization
        frag1 = new HomeFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.frame_layout, frag1);
        transaction.replace(R.id.frame_layout, frag1);
        transaction.commit();

        //Menu Selection Listener For Home(Center Button) and Other Menus
        spaceNavigationView.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {

                //If Center Button Clicked Load Home Fragment
                mainNavigationPosition = "Home";
                navigationPosition = 0;
                HomeFragment frag = new HomeFragment();
                frag1 = frag;
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.add(R.id.frame_layout, frag);
                transaction.replace(R.id.frame_layout, frag);
                transaction.commit();

            }

            @Override
            public void onItemClick(int itemIndex, String itemName) {
                switch (itemIndex) {

                    case 0:

                        // Index 0: Load Guidance Fragment
                        mainNavigationPosition = "Guidance";
                        GuidaneFragment frag4 = new GuidaneFragment();
                        FragmentManager fragmentManager4 = getSupportFragmentManager();
                        FragmentTransaction transaction4 = fragmentManager4.beginTransaction();
                        transaction4.add(R.id.frame_layout, frag4);
                        transaction4.replace(R.id.frame_layout, frag4);
                        transaction4.commit();
                        break;

                    case 1:

                        // Index 0: Load Search Fragment
                        mainNavigationPosition = "Search";
                        SearchFragment frag3 = new SearchFragment();
                        FragmentManager fragmentManager3 = getSupportFragmentManager();
                        FragmentTransaction transaction3 = fragmentManager3.beginTransaction();
                        transaction3.add(R.id.frame_layout, frag3);
                        transaction3.replace(R.id.frame_layout, frag3);
                        transaction3.commit();
                        break;
                    case 2:

                        // Index 0: Load Chart Fragment
                        mainNavigationPosition = "Chart";
                        ChartPDFFragment frag5 = new ChartPDFFragment();
                        FragmentManager fragmentManager5 = getSupportFragmentManager();
                        FragmentTransaction transaction5 = fragmentManager5.beginTransaction();
                        transaction5.add(R.id.frame_layout, frag5);
                        transaction5.replace(R.id.frame_layout, frag5);
                        transaction5.commit();
                        break;

                    case 3:

                        // Index 0: Load Document Fragment
                        mainNavigationPosition = "Document";
                        navigationPosition = 0;
                        DocumentPDFFragment frag6 = new DocumentPDFFragment();
                        FragmentManager fragmentManager6 = getSupportFragmentManager();
                        FragmentTransaction transaction6 = fragmentManager6.beginTransaction();
                        transaction6.add(R.id.frame_layout, frag6);
                        transaction6.replace(R.id.frame_layout, frag6);
                        transaction6.commit();
                        break;

                }
            }

            @Override
            public void onItemReselected(int itemIndex, String itemName) {

                //Do Reselection After check Whether User In Same Fragment
                switch (itemIndex) {
                    case 0:
                        if (!"Guidance".equals(mainNavigationPosition)) {
                            mainNavigationPosition = "Guidance";
                            GuidaneFragment frag4 = new GuidaneFragment();
                            FragmentManager fragmentManager4 = getSupportFragmentManager();
                            FragmentTransaction transaction4 = fragmentManager4.beginTransaction();
                            transaction4.add(R.id.frame_layout, frag4);
                            transaction4.replace(R.id.frame_layout, frag4);
                            transaction4.commit();
                        }
                        break;
                    case 1:
                        if (!"Search".equals(mainNavigationPosition)) {
                            mainNavigationPosition = "Search";
                            SearchFragment frag3 = new SearchFragment();
                            FragmentManager fragmentManager3 = getSupportFragmentManager();
                            FragmentTransaction transaction3 = fragmentManager3.beginTransaction();
                            transaction3.add(R.id.frame_layout, frag3);
                            transaction3.replace(R.id.frame_layout, frag3);
                            transaction3.commit();
                        }

                        break;
                    case 2:
                        if (!"Chart".equals(mainNavigationPosition)) {
                            mainNavigationPosition = "Chart";
                            ChartPDFFragment frag5 = new ChartPDFFragment();
                            FragmentManager fragmentManager5 = getSupportFragmentManager();
                            FragmentTransaction transaction5 = fragmentManager5.beginTransaction();
                            transaction5.add(R.id.frame_layout, frag5);
                            transaction5.replace(R.id.frame_layout, frag5);
                            transaction5.commit();
                        }
                        break;
                    case 3:
                        if (!"Document".equals(mainNavigationPosition)) {
                            mainNavigationPosition = "Document";
                            navigationPosition = 0;
                            DocumentPDFFragment frag6 = new DocumentPDFFragment();
                            FragmentManager fragmentManager6 = getSupportFragmentManager();
                            FragmentTransaction transaction6 = fragmentManager6.beginTransaction();
                            transaction6.add(R.id.frame_layout, frag6);
                            transaction6.replace(R.id.frame_layout, frag6);
                            transaction6.commit();
                        }
                        break;

                }

            }
        });

        if (prefs.getBoolean("firstrun", true)) {
            // Do first run stuff here then set 'firstrun' as false
            // using the following line to edit/commit prefs
            if (ConnectivityReceiver.isConnected()) {
                uploadUserData();
            }
        }

    }


    public void uploadUserData() {
        int SocketTimeout = 30000;
        RetryPolicy retry = new DefaultRetryPolicy(SocketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        );
        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateformatstr = dateFormat.format(calendar.getTime());
        String deviceName = Build.MODEL;
        //String deviceMan = Build.MANUFACTURER;
        String productstr = Build.BRAND;
        @SuppressLint("HardwareIds") String id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        JSONObject postparam = new JSONObject();

        try {
            postparam.put("Device_ID", id);
            postparam.put("Date_and_Time", dateformatstr);
            postparam.put("Mobile_Make", productstr);
            postparam.put("Mobile_Model", deviceName);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, Constant.SERVER + Constant.USER_DETAIL_UPDATE_URL, postparam, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.e("Response", response.toString());
                try {

                    if ("Success".equals(response.getString("Response"))) {
                        Log.e("First check", "First installation");
                        prefs.edit().putBoolean("firstrun", false).apply();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error", error.toString());

            }
        });

        jsObjRequest.setRetryPolicy(retry);
        AppController.getInstance().addToRequestQueue(jsObjRequest, "tag_json_obj");
    }

    @Override
    public void onBackPressed() {

        //If User is in Home Call onBackPressed in Home Fragment, Otherwise Navigate To Home Fragment
        if ("Home".equals(mainNavigationPosition)) {

            if (frag1 != null) {
                frag1.onBackPressed();
            }

        } else if ("Login".equals(mainNavigationPosition)) {

            mainNavigationPosition = "Home";
            HomeFragment frag = new HomeFragment();
            frag1 = frag;
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(R.id.frame_layout, frag);
            transaction.replace(R.id.frame_layout, frag);
            transaction.commit();
        } else if ("Search".equals(mainNavigationPosition)) {

            mainNavigationPosition = "Home";
            HomeFragment frag = new HomeFragment();
            frag1 = frag;
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(R.id.frame_layout, frag);
            transaction.replace(R.id.frame_layout, frag);
            transaction.commit();
        } else if ("Guidance".equals(mainNavigationPosition) || "Document".equals(mainNavigationPosition) || "Chart".equals(mainNavigationPosition)) {

            mainNavigationPosition = "Home";
            HomeFragment frag = new HomeFragment();
            frag1 = frag;
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(R.id.frame_layout, frag);
            transaction.replace(R.id.frame_layout, frag);
            transaction.addToBackStack(null);
            transaction.commit();
        }

    }
}
