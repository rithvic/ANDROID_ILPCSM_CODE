package com.qbay.qbayApps.ilpcsm.Fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.qbay.qbayApps.ilpcsm.R;
import com.qbay.qbayApps.ilpcsm.Services.AppController;
import com.qbay.qbayApps.ilpcsm.Services.ConnectivityReceiver;
import com.qbay.qbayApps.ilpcsm.Services.Constant;
import com.qbay.qbayApps.ilpcsm.Services.CourseFilterDialogue;
import com.qbay.qbayApps.ilpcsm.Services.ExpandableListAdapter;
import com.qbay.qbayApps.ilpcsm.WebServiceCall.VolleyCustomPostRequest;
import com.squareup.picasso.Picasso;
import com.victor.loading.book.BookLoading;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Parameter;
import java.security.Policy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import developer.shivam.crescento.CrescentoContainer;

/**
 * Created by benchmark on 06/03/18.
 */

public class HomeFragment extends Fragment implements View.OnClickListener {

    //Main views
    LinearLayout mainLayout, recylerLoadingLayout, loadingInnerView;
    BookLoading loadingBook;
    RelativeLayout loadingLayoutView;
    CardView careeCarView, courseCardView, collegeCardView, aboutUsCardView;
    Context maincontext;
    RecyclerViewClickListener recycleitemclicklistener;

    CrescentoContainer topcontainer;
    LinearLayout cardview1, cardview2, navigationlayout, navigationbtnlayout;
    ImageView navBackButton;
    TextView navigationBackTitle;
    ImageButton filterButton;
    SearchView simpleSearchView;

    //Position identity for Course, College and Career
    String positionvalue;

    //Course levels to filter
    String courseLevels[] = {"Any","Under Graduate","Diploma","Certificate"};
    String courseLevelid[] = {"99","4","6","7"};

    Boolean exit = false;

    //Career Declaration
    JSONArray careeralldata, nameFilterCareerArray;
    JSONArray careerinfoalldata;
    private RecyclerView mRecyclerView;
    private MyAdapter mAdapter;
    public RecyclerView.LayoutManager mLayoutManager;
    ScrollView detailScroll;
    TextView careerDomainTxtView, careeSubDomainTxtView, careerEducationTextView, careerDescriptionTextView, careerSalaryTextView, careerEnvironmentTxtView;

    //Course Declaration
    JSONArray courseCategoryArray, courseGroupArray, courseProgramList;
    String selectedGroup, selectedCategory;
    int courseCollegeListCount;

    //College Decleration
    JSONArray collegeListArray;
    Boolean loadMoreChange;
    int collegeListCount;
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;

    //About Us Declaration
    WebView guidanceWeView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mainLayout = (LinearLayout) inflater.inflate(R.layout.home_layout, container, false);

        //Loading View Setup
        recylerLoadingLayout = mainLayout.findViewById(R.id.recycler_loading_view);
        loadingInnerView = mainLayout.findViewById(R.id.loading_inner_view);
        loadingLayoutView = mainLayout.findViewById(R.id.loading_relative_layout);
        loadingBook = mainLayout.findViewById(R.id.bookloading);

        //Guidance View Settings
        guidanceWeView = mainLayout.findViewById(R.id.guidance_web_view);
        WebSettings settings = guidanceWeView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        guidanceWeView.getSettings().setBuiltInZoomControls(true);
        guidanceWeView.loadUrl(Constant.GUIDANCE_PAGE);

        //Expandable ListView for Course inside Colleges
        expListView = mainLayout.findViewById(R.id.lvExp);

        //Main view setup
        careeCarView = mainLayout.findViewById(R.id.career_card_view);
        courseCardView = mainLayout.findViewById(R.id.course_card_view);
        collegeCardView = mainLayout.findViewById(R.id.college_card_view);
        aboutUsCardView = mainLayout.findViewById(R.id.about_us_card_view);
        collegeCardView.setOnClickListener(this);
        courseCardView.setOnClickListener(this);
        careeCarView.setOnClickListener(this);
        aboutUsCardView.setOnClickListener(this);

        maincontext = getContext();
        simpleSearchView = mainLayout.findViewById(R.id.simpleSearchView);

        nameFilterCareerArray = new JSONArray();

        //Search view listener for Career, Course and College
        simpleSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                    if (ConnectivityReceiver.isConnected()) {

                    if (getResources().getString(R.string.PositionValueCareerMain).equals(positionvalue)) {
                        if (newText.equals("") || newText.trim().length() == 0) {
                            getCareerData();
                        }else {
                            getCareerByNameData(newText);
                        }

                    } else if (getResources().getString(R.string.PositionValueCollegeMain).equals(positionvalue) || getResources().getString(R.string.PositionValueCollegeNameSearch).equals(positionvalue)) {


                            if (newText.equals("") || newText.trim().length() == 0) {

                                positionvalue = getResources().getString(R.string.PositionValueCollegeMain);
                                String collegelisturl = Constant.SERVER+Constant.COLLEGE_LIST;
                                loadMoreChange = false;
                                collegeListCount = 30;
                                Map<String, String> collegeParams = new HashMap<>();
                                collegeParams.put("count", String.valueOf(collegeListCount));

                                getCollegeListPostMethod(collegeParams, collegelisturl);


                            } else {

                                Log.e("request server", newText);
                                AppController.getInstance().cancelPendingRequests("tag_json_obj");
                                Map<String, String> programparams = new HashMap<>();
                                programparams.put("collegename", newText);
                                StartLoading();

                                String courseProgramUrl = Constant.SERVER+Constant.COLLEGE_SEARCH_BY_NAME;
                                getCollegeListByNamePostMethod(programparams, courseProgramUrl);
                            }
                    } else if (getResources().getString(R.string.PositionValueCourseMain).equals(positionvalue) || getResources().getString(R.string.PositionValueCourseNameSearch).equals(positionvalue)){

                        if (newText.equals("") || newText.trim().length() == 0) {

                            AppController.getInstance().cancelPendingRequests("tag_json_obj");
                            positionvalue = getResources().getString(R.string.PositionValueCourseMain);
                            String coursecategoryurl = Constant.SERVER+Constant.COURSE_LIST;
                            getCourseDataGetMethod(coursecategoryurl);

                        } else {
                            Log.e("request server", newText);
                            AppController.getInstance().cancelPendingRequests("tag_json_obj");
                            Map<String, String> programparams = new HashMap<>();
                            programparams.put("coursename", newText);
                            StartLoading();

                            String courseProgramUrl = Constant.SERVER+Constant.COURSE_SEARCH_BY_NAME;
                            getCourseListByNamePostMethod(programparams, courseProgramUrl);
                        }

                    }

                    } else {
                        Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                    }

                return false;
            }
        });

        //RecyclerView Click Listener For Career, Course and College
        recycleitemclicklistener = new RecyclerViewClickListener() {
            @Override
            public void recyclerViewListClicked(View v, int position) {

                try {
                    if (getResources().getString(R.string.PositionValueCareerMain).equals(positionvalue)) {
                        if (ConnectivityReceiver.isConnected()) {

                            if (careeralldata.getJSONObject(position).getString("listType").equals("mainList")) {
                                StartLoading();
                                simpleSearchView.setVisibility(View.GONE);
                                getSubCareerAllData(careeralldata.getJSONObject(position).getString("CAREER_GROUP_NUMBER_C"));
                            }else {
                                navigationBackTitle.setText(getResources().getString(R.string.CareerDetails));
                                positionvalue = getResources().getString(R.string.PositionValueCareerDetails);
                                simpleSearchView.setVisibility(View.GONE);
                                mRecyclerView.setVisibility(View.GONE);
                                detailScroll.setVisibility(View.VISIBLE);
                                careerDomainTxtView.setText(Html.fromHtml("<u>"+careeralldata.getJSONObject(position).getString("NAME") +"</u>"));
                                careeSubDomainTxtView.setText(Html.fromHtml("<u>"+careeralldata.getJSONObject(position).getString("SUB_NAME")+"</u>"));

                                careerEducationTextView.setText(careeralldata.getJSONObject(position).getString("EDUCATIONAL_QUALIFICATIONS_AND_TRAINING__C(What Should I Study?)"));
                                careerDescriptionTextView.setText(careeralldata.getJSONObject(position).getString("JOB_DESCRIPTION_C_What_Will_I_Do"));
                                careerSalaryTextView.setText(careeralldata.getJSONObject(position).getString("EXCEPTED_SALARY_What_Will_I_Earn"));
                                careerEnvironmentTxtView.setText(careeralldata.getJSONObject(position).getString("Work_Environment_My_Work_Environment"));
                            }
                        }else {
                            Toast.makeText(getActivity(),"No Internet Connection", Toast.LENGTH_SHORT).show();
                        }

                    }else if (getResources().getString(R.string.PositionValueSubCareer).equals(positionvalue)){

                        navigationBackTitle.setText(getResources().getString(R.string.CareerDetails));
                        positionvalue = getResources().getString(R.string.PositionValueCareerDetailsFromMain);
                        simpleSearchView.setVisibility(View.GONE);
                        mRecyclerView.setVisibility(View.GONE);
                        detailScroll.setVisibility(View.VISIBLE);
                        careerDomainTxtView.setText(Html.fromHtml("<u>"+careerinfoalldata.getJSONObject(position).getString("NAME") +"</u>"));
                        careeSubDomainTxtView.setText(Html.fromHtml("<u>"+careerinfoalldata.getJSONObject(position).getString("SUB_NAME")+"</u>"));

                        careerEducationTextView.setText(careerinfoalldata.getJSONObject(position).getString("EDUCATIONAL_QUALIFICATIONS_AND_TRAINING__C(What Should I Study?)"));
                        careerDescriptionTextView.setText(careerinfoalldata.getJSONObject(position).getString("JOB_DESCRIPTION_C_What_Will_I_Do"));
                        careerSalaryTextView.setText(careerinfoalldata.getJSONObject(position).getString("EXCEPTED_SALARY_What_Will_I_Earn"));
                        careerEnvironmentTxtView.setText(careerinfoalldata.getJSONObject(position).getString("Work_Environment_My_Work_Environment"));

                    }else if (getResources().getString(R.string.PositionValueCourseMain).equals(positionvalue)){

                        if (ConnectivityReceiver.isConnected()) {
                            Map<String, String> programparams = new HashMap<>();
                            programparams.put("category_id", courseCategoryArray.getJSONObject(position).getString("id"));

                            String courseProgramUrl = Constant.SERVER+Constant.COURSE_PROGRAM_LIST;
                            getCourseDataPostMethod(programparams, courseProgramUrl);
                        }else {
                            Toast.makeText(getActivity(),"No Internet Connection", Toast.LENGTH_SHORT).show();
                        }

                    } else if (getResources().getString(R.string.PositionValueCourseNameSearch).equals(positionvalue)){
                        if (ConnectivityReceiver.isConnected()) {

                            if ("mainList".equals(courseCategoryArray.getJSONObject(position).getString("listType"))){

                                Map<String, String> programparams = new HashMap<>();
                                programparams.put("category_id", courseCategoryArray.getJSONObject(position).getString("id"));

                                String courseProgramUrl = Constant.SERVER+Constant.COURSE_PROGRAM_LIST;
                                getCourseDataPostMethod(programparams, courseProgramUrl);


                            }else if ("categoryList".equals(courseCategoryArray.getJSONObject(position).getString("listType"))){

                                if (ConnectivityReceiver.isConnected()) {
                                    StartLoading();
                                    filterButton.setVisibility(View.GONE);
                                    selectedCategory = courseCategoryArray.getJSONObject(position).getString("ref_Course_Broad_Discipline_Gro_Category_id");
                                    selectedGroup = courseCategoryArray.getJSONObject(position).getString("ref_Course_Broad_Discipline_Gro_ID");
                                    String coursecollegelisturl = Constant.SERVER+Constant.COURSE_BASED_COLLEGES;
                                    courseCollegeListCount = 10;
                                    Map<String, String> coursecollegeParams = new HashMap<>();
                                    coursecollegeParams.put("count", String.valueOf(courseCollegeListCount));
                                    coursecollegeParams.put("groupid", selectedGroup);
                                    coursecollegeParams.put("categoryid", selectedCategory);
                                    getCollegeListPostMethod(coursecollegeParams, coursecollegelisturl);

                                }else {
                                    Toast.makeText(getActivity(),"No Internet Connection", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }else {
                            Toast.makeText(getActivity(),"No Internet Connection", Toast.LENGTH_SHORT).show();
                        }
                    } else if ("course_group".equals(positionvalue)){
                        if (ConnectivityReceiver.isConnected()) {
                            navigationBackTitle.setText(getResources().getString(R.string.CourseDetails));
                            filterButton.setVisibility(View.VISIBLE);
                            positionvalue = getResources().getString(R.string.PositionValueProgramInsideCourse);
                            Map<String, String> programparams = new HashMap<>();
                            programparams.put("category_id", courseGroupArray.getJSONObject(position).getString("category_id"));
                            programparams.put("group_id", courseGroupArray.getJSONObject(position).getString("id"));
                            selectedGroup = courseGroupArray.getJSONObject(position).getString("id");
                            selectedCategory = courseGroupArray.getJSONObject(position).getString("category_id");
                            String courseProgramUrl = "http://quadrobay.co.in/eduapps_api/eduapps/public/api/eduapps/get-category?token=24462a501a5aa6ca239d1ade0fc90c9c";
                            getCourseDataPostMethod(programparams, courseProgramUrl);
                        }else {
                            Toast.makeText(getActivity(),"No Internet Connection", Toast.LENGTH_SHORT).show();
                        }
                    }else if (getResources().getString(R.string.PositionValueProgramInsideCourse).equals(positionvalue) || getResources().getString(R.string.PositionValueCourseNameSearchProgram).equals(positionvalue)){
                        if (ConnectivityReceiver.isConnected()) {
                            StartLoading();
                            filterButton.setVisibility(View.GONE);
                            selectedCategory = courseProgramList.getJSONObject(position).getString("ref_Course_Broad_Discipline_Gro_Category_id");
                            selectedGroup = courseProgramList.getJSONObject(position).getString("ref_Course_Broad_Discipline_Gro_ID");
                            String coursecollegelisturl = Constant.SERVER+Constant.COURSE_BASED_COLLEGES;
                            courseCollegeListCount = 10;
                            Map<String, String> coursecollegeParams = new HashMap<>();
                            coursecollegeParams.put("count", String.valueOf(courseCollegeListCount));
                            coursecollegeParams.put("groupid", selectedGroup);
                            coursecollegeParams.put("categoryid", selectedCategory);
                            getCollegeListPostMethod(coursecollegeParams, coursecollegelisturl);
                        }else {
                            Toast.makeText(getActivity(),"No Internet Connection", Toast.LENGTH_SHORT).show();
                        }
                    }else if (getResources().getString(R.string.PositionValueCollegeInsideCourseProgram).equals(positionvalue) || getResources().getString(R.string.PositionValueCourseNameSearchCollegeCourse).equals(positionvalue) || getResources().getString(R.string.PositionValueCourseNameSearchCollege).equals(positionvalue)){
                        if (ConnectivityReceiver.isConnected()) {
                            StartLoading();
                            String collegecourselisturl = Constant.SERVER+Constant.COLLEGE_OFFERING_COURSES;
                            Map<String, String> coursecollegeParams = new HashMap<>();
                            coursecollegeParams.put("collegeid", collegeListArray.getJSONObject(position).getString("College_ID"));
                            getCourseDataPostMethod(coursecollegeParams, collegecourselisturl);
                        }else {
                            Toast.makeText(getActivity(),"No Internet Connection", Toast.LENGTH_SHORT).show();
                        }
                    }else if (getResources().getString(R.string.PositionValueCollegeMain).equals(positionvalue) || getResources().getString(R.string.PositionValueCollegeNameSearch).equals(positionvalue)){
                        if (ConnectivityReceiver.isConnected()) {
                            StartLoading();
                            simpleSearchView.setVisibility(View.GONE);
                            String collegecourselisturl = Constant.SERVER+Constant.COLLEGE_OFFERING_COURSES;
                            Map<String, String> coursecollegeParams = new HashMap<>();
                            coursecollegeParams.put("collegeid", collegeListArray.getJSONObject(position).getString("College_ID"));
                            getCourseDataPostMethod(coursecollegeParams, collegecourselisturl);
                        }else {
                            Toast.makeText(getActivity(),"No Internet Connection", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        topcontainer = mainLayout.findViewById(R.id.cresento_top_view);
        cardview1 = mainLayout.findViewById(R.id.menu_cardview1);
        cardview2 = mainLayout.findViewById(R.id.menu_cardview2);
        navigationlayout = mainLayout.findViewById(R.id.top_navigation_layout);
        navigationbtnlayout = mainLayout.findViewById(R.id.navigation_btn_layout);
        navigationbtnlayout.setOnClickListener(this);
        navigationBackTitle = mainLayout.findViewById(R.id.navigation_back_title);
        navBackButton = mainLayout.findViewById(R.id.back_button);
        filterButton = mainLayout.findViewById(R.id.filter_button);
        filterButton.setOnClickListener(this);
        positionvalue = "main";


        mRecyclerView = mainLayout.findViewById(R.id.career_recycler_view);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        detailScroll = mainLayout.findViewById(R.id.detail_scroll);
        careerDomainTxtView = mainLayout.findViewById(R.id.career_domain_txt_view);
        careeSubDomainTxtView = mainLayout.findViewById(R.id.career_sub_domain_txt_view);
        careerEducationTextView = mainLayout.findViewById(R.id.career_education_txt_view);
        careerDescriptionTextView = mainLayout.findViewById(R.id.career_description_txt_view);
        careerSalaryTextView = mainLayout.findViewById(R.id.career_salary_txt_view);
        careerEnvironmentTxtView = mainLayout.findViewById(R.id.career_enviroment_txt_view);
        careerDomainTxtView.setOnClickListener(this);
        careeSubDomainTxtView.setOnClickListener(this);
        loadMoreChange = false;
        return mainLayout;
    }

    public void StartLoading(){
        loadingLayoutView.setVisibility(View.VISIBLE);
        loadingInnerView.setVisibility(View.VISIBLE);
        loadingBook.start();
    }

    public void StopLoading(){
        loadingLayoutView.setVisibility(View.GONE);
        loadingInnerView.setVisibility(View.GONE);
        if (loadingBook.isStart()){
            loadingBook.stop();
        }
    }

    public void onBackPressed(){

        if ("main".equals(positionvalue)){
            if (exit) {
                Objects.requireNonNull(getActivity()).finish(); // finish activity
            } else {
                Toast.makeText(getActivity(), "Press Back again to Exit.", Toast.LENGTH_SHORT).show();
                exit = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        exit = false;

                    }
                }, 3 * 1000);
            }
        }else {
            backNavigation();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.career_card_view:
                if (ConnectivityReceiver.isConnected()){
                    simpleSearchView.setQueryHint("Search Career");
                    simpleSearchView.setQuery("",false);
                    StartLoading();
                    getCareerData();
                }else {
                    Toast.makeText(getActivity(),"No Internet Connection", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.career_domain_txt_view:
                StopLoading();
                showViews(simpleSearchView.getId(), mRecyclerView.getId());
                hideViews(detailScroll.getId());
                navigationBackTitle.setText(getResources().getString(R.string.ListOfCareers));
                positionvalue = getResources().getString(R.string.PositionValueCareerMain);
                mAdapter = new MyAdapter(maincontext, careeralldata, positionvalue, recycleitemclicklistener, null, null, false);
                mRecyclerView.setAdapter(mAdapter);
                break;
            case R.id.career_sub_domain_txt_view:
                StopLoading();
                navigationBackTitle.setText(getResources().getString(R.string.CareerCategory));
                positionvalue = getResources().getString(R.string.PositionValueSubCareer);
                showViews(mRecyclerView.getId());
                hideViews(detailScroll.getId());
                mAdapter = new MyAdapter(maincontext, careerinfoalldata, positionvalue, recycleitemclicklistener, null, null, false);
                mRecyclerView.setAdapter(mAdapter);
                break;
            case R.id.course_card_view:
                if (ConnectivityReceiver.isConnected()) {
                    simpleSearchView.setQueryHint("Search Course");
                    simpleSearchView.setQuery("",false);
                    StartLoading();
                    String coursecategoryurl = Constant.SERVER+Constant.COURSE_LIST;
                    getCourseDataGetMethod(coursecategoryurl);
                }else {
                    Toast.makeText(getActivity(),"No Internet Connection", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.college_card_view:
                if (ConnectivityReceiver.isConnected()) {
                    StartLoading();
                    simpleSearchView.setQueryHint("Search College");
                    simpleSearchView.setQuery("",false);
                    String collegelisturl = Constant.SERVER+Constant.COLLEGE_LIST;
                    loadMoreChange = false;
                    collegeListCount = 30;
                    Map<String, String> collegeParams = new HashMap<>();
                    collegeParams.put("count", String.valueOf(collegeListCount));
                    getCollegeListPostMethod(collegeParams, collegelisturl);
                }else {
                    Toast.makeText(getActivity(),"No Internet Connection", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.about_us_card_view:
                positionvalue = getResources().getString(R.string.PositionValueGuidance);
                navigationBackTitle.setText(getResources().getString(R.string.GuidanceNavigation));
                showViews(guidanceWeView.getId(), navigationlayout.getId());
                hideViews(topcontainer.getId(), cardview1.getId(), cardview2.getId());
                break;
            case R.id.navigation_btn_layout:
                backNavigation();
                break;
            case R.id.filter_button:
                if (getResources().getString(R.string.PositionValueProgramInsideCourse).equals(positionvalue) || getResources().getString(R.string.PositionValueCourseNameSearchProgram).equals(positionvalue)){
                    CourseFilterDialogue alert = new CourseFilterDialogue();
                    alert.showDialog(getActivity(), new CourseFilterDialogue.filterListener() {

                        @Override
                        public void filterSelected(String selectedCourse) {
                        if (selectedCourse != null) {
                            if ("99".equals(selectedCourse)) {
                                mAdapter = new MyAdapter(maincontext, courseProgramList, positionvalue, recycleitemclicklistener, null, null, false);
                                mRecyclerView.setAdapter(mAdapter);
                                } else {
                                    JSONArray filterArray = new JSONArray();
                                    for (int i = 0; i < courseProgramList.length() - 1; i++) {
                                        try {
                                            if (courseProgramList.getJSONObject(i).getString("ref_Course_Level_ID").equals(selectedCourse)) {
                                                filterArray.put(courseProgramList.getJSONObject(i));

                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    if (filterArray.length() == 0) {
                                        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                                        dialog.setTitle( "No Records Found" )
                                                .setMessage("No records found for selected course level")
                                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialoginterface, int i) {
                                                        }
                                                    }).show();
                                    } else {
                                        mAdapter = new MyAdapter(maincontext, filterArray, positionvalue, recycleitemclicklistener, null, null, false);
                                        mRecyclerView.setAdapter(mAdapter);
                                    }
                                }
                            }
                        }
                    });
                }
                break;
        }
    }

    public void backNavigation(){

        if (getResources().getString(R.string.PositionValueCareerMain).equals(positionvalue) || getResources().getString(R.string.PositionValueCourseMain).equals(positionvalue) || getResources().getString(R.string.PositionValueCollegeMain).equals(positionvalue) || getResources().getString(R.string.PositionValueCollegeNameSearch).equals(positionvalue) || getResources().getString(R.string.PositionValueCourseNameSearch).equals(positionvalue)) {
            StopLoading();
            positionvalue = "main";
            showViews(topcontainer.getId(),cardview1.getId(),cardview2.getId());
            hideViews(mRecyclerView.getId(),simpleSearchView.getId(),recylerLoadingLayout.getId(),navigationlayout.getId());
        }else if (getResources().getString(R.string.PositionValueSubCareer).equals(positionvalue)){
            StopLoading();
            showViews(simpleSearchView.getId());
            navigationBackTitle.setText(getResources().getString(R.string.ListOfCareers));
            positionvalue = getResources().getString(R.string.PositionValueCareerMain);
            mAdapter = new MyAdapter(maincontext, careeralldata, positionvalue, recycleitemclicklistener, null, null, false);
            mRecyclerView.setAdapter(mAdapter);
        }else if (getResources().getString(R.string.PositionValueCareerDetailsFromMain).equals(positionvalue)){
            StopLoading();
            showViews(mRecyclerView.getId());
            hideViews(detailScroll.getId());
            navigationBackTitle.setText(getResources().getString(R.string.CareerCategory));
            positionvalue = getResources().getString(R.string.PositionValueSubCareer);
            mAdapter = new MyAdapter(maincontext, careerinfoalldata, positionvalue, recycleitemclicklistener, null, null, false);
            mRecyclerView.setAdapter(mAdapter);
        }else if (getResources().getString(R.string.PositionValueCareerDetails).equals(positionvalue)){
            showViews(mRecyclerView.getId());
            hideViews(detailScroll.getId());
            navigationBackTitle.setText(getResources().getString(R.string.ListOfCareers));
            positionvalue = getResources().getString(R.string.PositionValueCareerMain);
        } else if ("course_group".equals(positionvalue)){
            StopLoading();
            navigationBackTitle.setText(getResources().getString(R.string.CourseList));
            positionvalue = getResources().getString(R.string.PositionValueCourseMain);
            mAdapter = new MyAdapter(maincontext, courseCategoryArray, positionvalue, recycleitemclicklistener, null, null, false);
            mRecyclerView.setAdapter(mAdapter);

        }else if (getResources().getString(R.string.PositionValueCourseNameSearchProgram).equals(positionvalue) || getResources().getString(R.string.PositionValueCourseNameSearchCollege).equals(positionvalue)) {
            StopLoading();
            showViews(simpleSearchView.getId());
            hideViews(filterButton.getId());
            positionvalue = getResources().getString(R.string.PositionValueCourseNameSearch);
            navigationBackTitle.setText(getResources().getString(R.string.CourseList));
            mAdapter = new MyAdapter(maincontext, courseCategoryArray, positionvalue, recycleitemclicklistener, null, null, false);
            mRecyclerView.setAdapter(mAdapter);
        } else if (getResources().getString(R.string.PositionValueCourseNameSearchCollegeCourse).equals(positionvalue)){
            StopLoading();
            navigationBackTitle.setText(getResources().getString(R.string.CourseDetails));
            showViews(filterButton.getId());
            hideViews(loadingLayoutView.getId(), recylerLoadingLayout.getId());
            loadMoreChange = false;
            positionvalue = getResources().getString(R.string.PositionValueCourseNameSearchProgram);
            mAdapter = new MyAdapter(maincontext, courseProgramList, positionvalue, recycleitemclicklistener, null, null, false);
            mRecyclerView.setAdapter(mAdapter);
        }else if (getResources().getString(R.string.PositionValueProgramInsideCourse).equals(positionvalue)){
            StopLoading();
            navigationBackTitle.setText(getResources().getString(R.string.CourseList));
            showViews(simpleSearchView.getId());
            hideViews(filterButton.getId());
            positionvalue = getResources().getString(R.string.PositionValueCourseMain);
            mAdapter = new MyAdapter(maincontext, courseCategoryArray, positionvalue, recycleitemclicklistener, null, null, false);
            mRecyclerView.setAdapter(mAdapter);
        }else if (getResources().getString(R.string.PositionValueCollegeInsideCourseProgram).equals(positionvalue)){
            StopLoading();
            showViews(filterButton.getId());
            hideViews(recylerLoadingLayout.getId());
            navigationBackTitle.setText(getResources().getString(R.string.CourseDetails));
            loadMoreChange = false;
            positionvalue = getResources().getString(R.string.PositionValueProgramInsideCourse);
            mAdapter = new MyAdapter(maincontext, courseProgramList, positionvalue, recycleitemclicklistener, null, null, false);
            mRecyclerView.setAdapter(mAdapter);
        }else if ("name_course_college_course".equals(positionvalue)){
            StopLoading();
            showViews(mRecyclerView.getId());
            hideViews(expListView.getId());
            navigationBackTitle.setText(getResources().getString(R.string.OfferingColleges));
            positionvalue = getResources().getString(R.string.PositionValueCourseNameSearchCollegeCourse);
        } else if ("name_detail_college_course".equals(positionvalue)){
            StopLoading();
            showViews(mRecyclerView.getId());
            hideViews(expListView.getId());
            navigationBackTitle.setText(getResources().getString(R.string.OfferingColleges));
            positionvalue = getResources().getString(R.string.PositionValueCourseNameSearchCollege);
        }else if (getResources().getString(R.string.PositionValueCoursesOfferedByProgramCollege).equals(positionvalue)){
            StopLoading();
            showViews(mRecyclerView.getId());
            hideViews(expListView.getId());
            navigationBackTitle.setText(getResources().getString(R.string.OfferingColleges));
            positionvalue = getResources().getString(R.string.PositionValueCollegeInsideCourseProgram);
        } else if (getResources().getString(R.string.PositionValueGuidance).equals(positionvalue)){
            showViews(topcontainer.getId(), cardview1.getId(), cardview2.getId());
            hideViews(navigationlayout.getId(), guidanceWeView.getId());
        } else if (getResources().getString(R.string.PositionValueCollegeCourse).equals(positionvalue)){
            StopLoading();
            showViews(mRecyclerView.getId());
            hideViews(expListView.getId());
            navigationBackTitle.setText(getResources().getString(R.string.CollegeList));
            positionvalue = getResources().getString(R.string.PositionValueCollegeMain);
        }

    }

    public void showErrorToast(VolleyError error){
        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
            StopLoading();
            Toast.makeText(getActivity(),"Timed out. please try again.", Toast.LENGTH_SHORT).show();
        } else if (error instanceof AuthFailureError) {
            StopLoading();
            Toast.makeText(getActivity(),"Authentication Failure. please try again.", Toast.LENGTH_SHORT).show();
        } else if (error instanceof ServerError) {
            StopLoading();
            Toast.makeText(getActivity(),"No data found. please try again.", Toast.LENGTH_SHORT).show();
        } else if (error instanceof NetworkError) {
            StopLoading();
            Toast.makeText(getActivity(),"Network error. please try again.", Toast.LENGTH_SHORT).show();
        } else if (error instanceof ParseError) {
            StopLoading();
            Toast.makeText(getActivity(),"Parsing error. please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    public void hideViews(int... parameters){

        for (int parameter : parameters) {
            mainLayout.findViewById(parameter).setVisibility(View.GONE);
        }
    }

    public void showViews(int... viewParameters){
        for (int parameter : viewParameters) {
            mainLayout.findViewById(parameter).setVisibility(View.VISIBLE);
        }
    }

    //Career Methods
    //Get Career List
    public void getCareerData(){
        int SocketTimeout = 30000;
        RetryPolicy retry= new DefaultRetryPolicy(SocketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        );
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, Constant.SERVER+Constant.CAREER_LIST, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("Server Res", response.toString());
                        try {
                            if ("OK".equals(response.getString("Response"))){
                                careeralldata = new JSONArray();
                                navigationBackTitle.setText(getResources().getString(R.string.ListOfCareers));
                                positionvalue = getResources().getString(R.string.PositionValueCareerMain);
                                showViews(navigationlayout.getId(), mRecyclerView.getId(), simpleSearchView.getId());
                                hideViews(topcontainer.getId(), cardview1.getId(), cardview2.getId());
                                JSONArray careeralld = response.getJSONArray("Career_List");
                                if (careeralld.length() !=0) {
                                    for (int i = 0; i < careeralld.length();i++){
                                        JSONObject insd = careeralld.getJSONObject(i);
                                        insd.put("listType","mainList");
                                        careeralldata.put(insd);
                                    }
                                }
                                mAdapter = new MyAdapter(maincontext, careeralldata, positionvalue, recycleitemclicklistener, null, null, false);
                                mRecyclerView.setAdapter(mAdapter);
                                StopLoading();
                            }else if ("No data found".equals(response.getString("Response"))){
                                StopLoading();
                                Toast.makeText(getActivity(),"No data found. please try again.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Server err", error.toString());
                        showErrorToast(error);
                    }
                }
        );
        request.setRetryPolicy(retry);
        AppController.getInstance().addToRequestQueue(request,"json_req_method");
    }

    public void getCareerByNameData(final String searchQuery) {
        Map<String, String> params = new HashMap<>();
        params.put("careername", searchQuery);
        int SocketTimeout = 30000;
        RetryPolicy retry = new DefaultRetryPolicy(SocketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        );
        VolleyCustomPostRequest jsObjRequest = new VolleyCustomPostRequest(Request.Method.POST, Constant.SERVER + Constant.SEARCH_CAREER, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.e("Response", response.toString());
                try {
                    if ("OK".equals(response.getString("Response"))) {
                        careeralldata = new JSONArray();
                        JSONArray mainlist = response.getJSONArray("Search_list");
                        JSONArray categorylist1 = response.getJSONArray("Search_data");
                        if (mainlist.length() != 0) {
                            for (int i = 0; i < mainlist.length(); i++) {
                                JSONObject insd = mainlist.getJSONObject(i);
                                insd.put("listType", "mainList");
                                careeralldata.put(insd);
                            }
                        }
                        if (categorylist1.length() != 0) {
                            for (int i = 0; i < categorylist1.length(); i++) {
                                JSONObject insd1 = categorylist1.getJSONObject(i);
                                insd1.put("listType", "categoryList");
                                careeralldata.put(insd1);
                            }
                        }
                        mAdapter = new MyAdapter(maincontext, careeralldata, positionvalue, recycleitemclicklistener, null, null, false);
                        mRecyclerView.setAdapter(mAdapter);
                        StopLoading();
                    } else if ("No data found".equals(response.getString("Response"))) {
                        StopLoading();
                        Toast.makeText(getActivity(), "No data found. please try again.", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error", error.toString());
                showErrorToast(error);
            }
        });
        jsObjRequest.setRetryPolicy(retry);
        AppController.getInstance().addToRequestQueue(jsObjRequest, "tag_json_obj");
    }

    // Get Sub Career Method
    public void getSubCareerAllData(final String careerid){
        Map<String, String> params = new HashMap<>();
        params.put("careerid", careerid);
        int SocketTimeout = 30000;
        RetryPolicy retry= new DefaultRetryPolicy(SocketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        );
        VolleyCustomPostRequest jsObjRequest = new VolleyCustomPostRequest(Request.Method.POST, Constant.SERVER+Constant.CAREER_DETAILS, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.e("Response", response.toString());
                try {
                    if ("OK".equals(response.getString("Response"))) {
                        navigationBackTitle.setText(getResources().getString(R.string.CareerCategory));
                        positionvalue = getResources().getString(R.string.PositionValueSubCareer);
                        careerinfoalldata = response.getJSONArray("Career_Inside_Data");
                        mAdapter = new MyAdapter(maincontext, careerinfoalldata, positionvalue, recycleitemclicklistener, null, null, false);
                        mRecyclerView.setAdapter(mAdapter);
                        StopLoading();
                    }else if ("No data found".equals(response.getString("Response"))){
                        StopLoading();
                        Toast.makeText(getActivity(),"No data found. please try again.", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error", error.toString());
                showErrorToast(error);
            }
        });
        jsObjRequest.setRetryPolicy(retry);
        AppController.getInstance().addToRequestQueue(jsObjRequest, "tag_json_obj");
    }

    //Course Methods
    //Get Main Course Data
    public void getCourseDataGetMethod(String url){
        int SocketTimeout = 30000;
        RetryPolicy retry= new DefaultRetryPolicy(SocketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        );
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("Server Res", response.toString());
                        try {
                            if ("OK".equals(response.getString("Response"))) {
                                courseCategoryArray = response.getJSONArray("Course_Main_List");
                                // specify an adapter (see also next example)
                                navigationBackTitle.setText(getResources().getString(R.string.CourseList));
                                positionvalue = getResources().getString(R.string.PositionValueCourseMain);
                                showViews(navigationlayout.getId(), mRecyclerView.getId(), simpleSearchView.getId());
                                hideViews(topcontainer.getId(), cardview1.getId(), cardview2.getId());
                                mAdapter = new MyAdapter(maincontext, courseCategoryArray, positionvalue, recycleitemclicklistener, null, null, false);
                                mRecyclerView.setAdapter(mAdapter);
                                StopLoading();
                            }else if ("No data found".equals(response.getString("Response"))){
                                StopLoading();
                                Toast.makeText(getActivity(),"No data found. please try again.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },

                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Server err", error.toString());
                        error.printStackTrace();
                        showErrorToast(error);
                    }


                }
        );
        request.setRetryPolicy(retry);
        AppController.getInstance().addToRequestQueue(request,"json_req_method");
    }

    public void coursesOfferedByCollegeSorting(JSONArray courseFilterArray,JSONArray sortedArray, List<String> listDataHeader1, HashMap<String, List<String>> listDataChild1){
        try {
        for (int i=0;i<courseFilterArray.length();i++) {
            for (String aCourseLevelid : courseLevelid) {

                    if (aCourseLevelid.equals(courseFilterArray.getJSONObject(i).getString("course_level_id"))) {
                        sortedArray.put(courseFilterArray.getJSONObject(i));
                    }

            }
        }

            for (int i = 0; i < courseFilterArray.length(); i++) {
                if (listDataHeader1.isEmpty() || !listDataHeader1.contains(courseFilterArray.getJSONObject(i).getString("programme_name"))) {
                    listDataHeader1.add(courseFilterArray.getJSONObject(i).getString("programme_name"));
                    List<String> dchild = new ArrayList<>();
                    if ("".equals(courseFilterArray.getJSONObject(i).getString("Duration_Year"))) {
                        String totalval = courseFilterArray.getJSONObject(i).getString("Discipline") + "\n" + "Not Available" + "\n" + courseFilterArray.getJSONObject(i).getString("programme_name") + "\n" + courseFilterArray.getJSONObject(i).getString("course_level_id");
                        dchild.add(totalval);
                    } else {
                        String totalval = courseFilterArray.getJSONObject(i).getString("Discipline") + "\n" + courseFilterArray.getJSONObject(i).getString("Duration_Year") + "\n" + courseFilterArray.getJSONObject(i).getString("programme_name") + "\n" + courseFilterArray.getJSONObject(i).getString("course_level_id");
                        dchild.add(totalval);
                    }
                    listDataChild1.put(courseFilterArray.getJSONObject(i).getString("programme_name"), dchild);
                } else {
                    List<String> d1child = listDataChild1.get(courseFilterArray.getJSONObject(i).getString("programme_name"));
                    if ("".equals(courseFilterArray.getJSONObject(i).getString("Duration_Year"))) {
                        String totalval = courseFilterArray.getJSONObject(i).getString("Discipline") + "\n" + "Not Available" + "\n" + courseFilterArray.getJSONObject(i).getString("programme_name") + "\n" + courseFilterArray.getJSONObject(i).getString("course_level_id");
                        d1child.add(totalval);
                    } else {
                        String totalval = courseFilterArray.getJSONObject(i).getString("Discipline") + "\n" + courseFilterArray.getJSONObject(i).getString("Duration_Year") + "\n" + courseFilterArray.getJSONObject(i).getString("programme_name") + "\n" + courseFilterArray.getJSONObject(i).getString("course_level_id");
                        d1child.add(totalval);
                    }
                    listDataChild1.put(courseFilterArray.getJSONObject(i).getString("programme_name"), d1child);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void getCourseDataPostMethod(Map<String, String> params, String url){
        int SocketTimeout = 30000;
        RetryPolicy retry= new DefaultRetryPolicy(SocketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        );
        VolleyCustomPostRequest jsObjRequest = new VolleyCustomPostRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.e("Response", response.toString());
                try {
                    if (getResources().getString(R.string.PositionValueCourseMain).equals(positionvalue) || getResources().getString(R.string.PositionValueCourseNameSearch).equals(positionvalue)) {
                        if ("OK".equals(response.getString("Response"))) {
                            navigationBackTitle.setText(getResources().getString(R.string.CourseDetails));
                            showViews(filterButton.getId());
                            hideViews(simpleSearchView.getId());
                            if (getResources().getString(R.string.PositionValueCourseNameSearch).equals(positionvalue)){
                                positionvalue = getResources().getString(R.string.PositionValueCourseNameSearchProgram);
                            }else {
                                positionvalue = getResources().getString(R.string.PositionValueProgramInsideCourse);
                            }
                            courseProgramList = new JSONArray();
                            JSONArray submaincoursetemp = response.getJSONArray("Sub_Main_Course_List");
                            for (int i=0;i<submaincoursetemp.length();i++) {
                                for (String aCourseLevelid : courseLevelid) {
                                    if (aCourseLevelid.equals(submaincoursetemp.getJSONObject(i).getString("ref_Course_Level_ID"))) {
                                        courseProgramList.put(submaincoursetemp.getJSONObject(i));
                                    }
                                }
                            }
                            mAdapter = new MyAdapter(maincontext, courseProgramList, positionvalue, recycleitemclicklistener, null, null, false);
                            mRecyclerView.setAdapter(mAdapter);
                        }else if ("No data found".equals(response.getString("Response"))){
                            StopLoading();
                            Toast.makeText(getActivity(),"No data found. please try again.", Toast.LENGTH_SHORT).show();
                        }
                    }else if (getResources().getString(R.string.PositionValueCollegeInsideCourseProgram).equals(positionvalue) || getResources().getString(R.string.PositionValueCourseNameSearchCollegeCourse).equals(positionvalue) || getResources().getString(R.string.PositionValueCourseNameSearchCollege).equals(positionvalue)){
                        if ("OK".equals(response.getString("Response"))) {
                            navigationBackTitle.setText(getResources().getString(R.string.CoursesOffered));
                            showViews(expListView.getId());
                            hideViews(recylerLoadingLayout.getId(), filterButton.getId(), mRecyclerView.getId());
                            if (getResources().getString(R.string.PositionValueCourseNameSearchCollegeCourse).equals(positionvalue)){
                                positionvalue = "name_course_college_course";
                            } else if (getResources().getString(R.string.PositionValueCourseNameSearchCollege).equals(positionvalue)){
                                positionvalue = "name_detail_college_course";
                            } else {
                                positionvalue = getResources().getString(R.string.PositionValueCoursesOfferedByProgramCollege);
                            }
                            JSONArray maincourselist = new JSONArray();
                            JSONArray maincourseListTemp = response.getJSONArray("College_Inside_Courses");

                            List<String> listDataHeader1 = new ArrayList<>();
                            HashMap<String, List<String>> listDataChild1 = new HashMap<>();

                            coursesOfferedByCollegeSorting(maincourseListTemp, maincourselist, listDataHeader1, listDataChild1);
                            listAdapter = new ExpandableListAdapter(getActivity(), listDataHeader1, listDataChild1);
                            expListView.setAdapter(listAdapter);
                            StopLoading();
                        }else if ("No data found".equals(response.getString("Response"))){
                            StopLoading();
                            Toast.makeText(getActivity(),"No data found. please try again.", Toast.LENGTH_SHORT).show();
                        }
                    }else if (getResources().getString(R.string.PositionValueCollegeMain).equals(positionvalue) || getResources().getString(R.string.PositionValueCollegeNameSearch).equals(positionvalue) ){
                        if ("OK".equals(response.getString("Response"))) {
                            navigationBackTitle.setText(getResources().getString(R.string.CoursesOffered));
                            positionvalue = getResources().getString(R.string.PositionValueCollegeCourse);
                            showViews(expListView.getId());
                            hideViews(recylerLoadingLayout.getId(), mRecyclerView.getId());
                            JSONArray maincourselist = new JSONArray();
                            List<String> listDataHeader1 = new ArrayList<>();
                            HashMap<String, List<String>> listDataChild1 = new HashMap<>();

                            JSONArray maincourseListTemp = response.getJSONArray("College_Inside_Courses");

                            coursesOfferedByCollegeSorting(maincourseListTemp, maincourselist, listDataHeader1, listDataChild1);
                            listAdapter = new ExpandableListAdapter(getActivity(), listDataHeader1, listDataChild1);
                            expListView.setAdapter(listAdapter);
                            StopLoading();
                        }else if ("No data found".equals(response.getString("Response"))){
                            if (getResources().getString(R.string.PositionValueCollegeMain).equals(positionvalue) || getResources().getString(R.string.PositionValueCollegeNameSearch).equals(positionvalue) ){
                                simpleSearchView.setVisibility(View.VISIBLE);
                            }
                            StopLoading();
                            Toast.makeText(getActivity(),"Information on courses offered by college coming soon.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        courseGroupArray = response.getJSONArray("Sub_Main_Course_List");
                        mAdapter = new MyAdapter(maincontext, courseGroupArray, positionvalue, recycleitemclicklistener, null, null, false);
                        mRecyclerView.setAdapter(mAdapter);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error", error.toString());

                if (getResources().getString(R.string.PositionValueCollegeCourse).equals(positionvalue)){
                    navigationBackTitle.setText(getResources().getString(R.string.CollegeList));
                    positionvalue=getResources().getString(R.string.PositionValueCollegeMain);
                    showViews(mRecyclerView.getId());
                    hideViews(expListView.getId());
                    StopLoading();
                    Toast.makeText(getActivity(),"Information on courses offered by college coming soon", Toast.LENGTH_SHORT).show();
                }else {
                    showErrorToast(error);
                }

            }
        });
        jsObjRequest.setRetryPolicy(retry);
        AppController.getInstance().addToRequestQueue(jsObjRequest, "tag_json_obj");
    }

    // Get Course by name for course tab.
    public void getCourseListByNamePostMethod(Map<String, String> params, String url){

        int SocketTimeout = 30000;
        RetryPolicy retry= new DefaultRetryPolicy(SocketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        );
        VolleyCustomPostRequest jsObjRequest = new VolleyCustomPostRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    if ("main".equals(positionvalue) || getResources().getString(R.string.PositionValueCourseMain).equals(positionvalue) || getResources().getString(R.string.PositionValueCourseNameSearch).equals(positionvalue)) {
                        if (loadMoreChange){
                            if ("OK".equals(response.getString("Response"))) {
                                final JSONArray newLoadedArray = response.getJSONArray("College_List");
                                collegeListArray.put(null);
                                mAdapter.notifyItemInserted(collegeListArray.length() - 1);
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        collegeListArray.remove(collegeListArray.length() - 1);
                                        mAdapter.notifyItemRemoved(collegeListArray.length());
                                        for (int i = 0; i < newLoadedArray.length() - 1; i++) {
                                            // adapterData.add("Item" + (adapterData.size() + 1));
                                            try {
                                                collegeListArray.put(newLoadedArray.getJSONObject(i));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        mAdapter.notifyItemInserted(collegeListArray.length());
                                        mAdapter.setLoaded();
                                        recylerLoadingLayout.setVisibility(View.GONE);
                                    }
                                }, 2000);
                            }else if ("No data found".equals(response.getString("Response"))){
                                recylerLoadingLayout.setVisibility(View.GONE);
                                Toast.makeText(getActivity(),"Information on courses offered by college coming soon.", Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            if ("OK".equals(response.getString("Response"))) {
                                loadMoreChange = false;
                                positionvalue = getResources().getString(R.string.PositionValueCourseNameSearch);
                                courseCategoryArray = new JSONArray();
                                JSONArray searchmcourse = response.getJSONArray("Search_main_course");
                                JSONArray searchsubcourse = response.getJSONArray("Search_sub_course");
                                if (searchmcourse.length() !=0) {
                                    for (int i = 0; i < searchmcourse.length();i++){
                                        JSONObject insd = searchmcourse.getJSONObject(i);
                                        insd.put("listType","mainList");
                                        courseCategoryArray.put(insd);
                                    }
                                }
                                if (searchsubcourse.length() !=0) {
                                    for (int i = 0; i < searchsubcourse.length(); i++) {
                                        JSONObject insd1 = searchsubcourse.getJSONObject(i);
                                         for (int j = 0; j < courseLevelid.length; j++) {
                                           if (courseLevelid[j].equals(insd1.getString("ref_Course_Level_ID"))){
                                               insd1.put("listType","categoryList");
                                               insd1.put("ref_Course_Level_ID",courseLevels[j]);
                                               courseCategoryArray.put(insd1);
                                    }
                                }
                                    }
                                }
                                mAdapter = new MyAdapter(maincontext, courseCategoryArray, positionvalue, recycleitemclicklistener, null, null, false);
                                mRecyclerView.setAdapter(mAdapter);
                                StopLoading();
                            }else if ("No data found".equals(response.getString("Response"))){
                                StopLoading();
                                Toast.makeText(getActivity(),"Information on courses offered by college coming soon.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error", error.toString());
                recylerLoadingLayout.setVisibility(View.GONE);
                showErrorToast(error);
            }
        });
        jsObjRequest.setRetryPolicy(retry);
        AppController.getInstance().addToRequestQueue(jsObjRequest, "tag_json_obj");
    }

    //College Methods
    // Get college for college tab and colleges for course.
    public void getCollegeListByNamePostMethod(Map<String, String> params, String url){
        int SocketTimeout = 30000;
        RetryPolicy retry= new DefaultRetryPolicy(SocketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        );
        VolleyCustomPostRequest jsObjRequest = new VolleyCustomPostRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    if ("main".equals(positionvalue) || getResources().getString(R.string.PositionValueCollegeMain).equals(positionvalue) || getResources().getString(R.string.PositionValueCollegeNameSearch).equals(positionvalue)) {
                        if (loadMoreChange){
                            if ("OK".equals(response.getString("Response"))) {
                                final JSONArray newLoadedArray = response.getJSONArray("College_List");
                                collegeListArray.put(null);
                                mAdapter.notifyItemInserted(collegeListArray.length() - 1);
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        collegeListArray.remove(collegeListArray.length() - 1);
                                        mAdapter.notifyItemRemoved(collegeListArray.length());
                                        for (int i = 0; i < newLoadedArray.length() - 1; i++) {
                                            // adapterData.add("Item" + (adapterData.size() + 1));
                                            try {
                                                collegeListArray.put(newLoadedArray.getJSONObject(i));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        mAdapter.notifyItemInserted(collegeListArray.length());
                                        mAdapter.setLoaded();
                                        hideViews(recylerLoadingLayout.getId());
                                    }
                                }, 2000);
                            }else if ("No data found".equals(response.getString("Response"))){
                                hideViews(recylerLoadingLayout.getId());
                                Toast.makeText(getActivity(),"No data found. please try again.", Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            if ("OK".equals(response.getString("Response"))) {
                                loadMoreChange = false;
                                positionvalue = getResources().getString(R.string.PositionValueCollegeNameSearch);
                                collegeListArray = response.getJSONArray("Search_colleges");
                                mAdapter = new MyAdapter(maincontext, collegeListArray, positionvalue, recycleitemclicklistener, null, null, false);

                                mRecyclerView.setAdapter(mAdapter);
                                StopLoading();
                            }else if ("No data found".equals(response.getString("Response"))){
                                StopLoading();
                                Toast.makeText(getActivity(),"No data found. please try again.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error", error.toString());
                hideViews(recylerLoadingLayout.getId());
                showErrorToast(error);
            }
        });
        jsObjRequest.setRetryPolicy(retry);
        AppController.getInstance().addToRequestQueue(jsObjRequest, "tag_json_obj");
    }

    //Search college by name
    public void getCollegeListPostMethod(Map<String, String> params, String url){
        int SocketTimeout = 30000;
        RetryPolicy retry= new DefaultRetryPolicy(SocketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        );
        VolleyCustomPostRequest jsObjRequest = new VolleyCustomPostRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    if ("main".equals(positionvalue) || getResources().getString(R.string.PositionValueCollegeMain).equals(positionvalue)) {
                        OnLoadMoreListener loadMoreList = new OnLoadMoreListener() {
                            @Override
                            public void onLoadMore() {
                                if (getResources().getString(R.string.PositionValueCollegeMain).equals(positionvalue)) {
                                    loadMoreChange = true;
                                    Log.e("", "");
                                    String collegelisturl1 = Constant.SERVER+Constant.COLLEGE_LIST;
                                    collegeListCount += 30;
                                    Map<String, String> collegeParams = new HashMap<>();
                                    collegeParams.put("count", String.valueOf(collegeListCount));
                                    showViews(recylerLoadingLayout.getId());
                                    getCollegeListPostMethod(collegeParams, collegelisturl1);
                                }
                            }
                        };
                        if (loadMoreChange){
                            if ("OK".equals(response.getString("Response"))) {
                                final JSONArray newLoadedArray = response.getJSONArray("College_List");
                                collegeListArray.put(null);
                                mAdapter.notifyItemInserted(collegeListArray.length() - 1);
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        collegeListArray.remove(collegeListArray.length() - 1);
                                        mAdapter.notifyItemRemoved(collegeListArray.length());
                                        for (int i = 0; i < newLoadedArray.length() - 1; i++) {
                                            // adapterData.add("Item" + (adapterData.size() + 1));
                                            try {
                                                collegeListArray.put(newLoadedArray.getJSONObject(i));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        mAdapter.notifyItemInserted(collegeListArray.length());
                                        mAdapter.setLoaded();
                                        hideViews(recylerLoadingLayout.getId());
                                    }
                                }, 2000);
                            }else if ("No data found".equals(response.getString("Response"))){
                                hideViews(recylerLoadingLayout.getId());
                                Toast.makeText(getActivity(),"No data found. please try again.", Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            if ("OK".equals(response.getString("Response"))) {
                                navigationBackTitle.setText(getResources().getString(R.string.CollegeList));
                                positionvalue = getResources().getString(R.string.PositionValueCollegeMain);
                                showViews(navigationlayout.getId(), mRecyclerView.getId(), simpleSearchView.getId());
                                hideViews(topcontainer.getId(), cardview1.getId(), cardview2.getId());
                                collegeListArray = response.getJSONArray("College_List");
                                mAdapter = new MyAdapter(maincontext, collegeListArray, positionvalue, recycleitemclicklistener, mRecyclerView, loadMoreList, true);
                                mRecyclerView.setAdapter(mAdapter);
                                StopLoading();
                            }else if ("No data found".equals(response.getString("Response"))){
                                StopLoading();
                                Toast.makeText(getActivity(),"No data found. please try again.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }else if (getResources().getString(R.string.PositionValueProgramInsideCourse).equals(positionvalue) || getResources().getString(R.string.PositionValueCollegeInsideCourseProgram).equals(positionvalue) || getResources().getString(R.string.PositionValueCourseNameSearch).equals(positionvalue) || getResources().getString(R.string.PositionValueCourseNameSearchProgram).equals(positionvalue) || getResources().getString(R.string.PositionValueCourseNameSearchCollegeCourse).equals(positionvalue) || getResources().getString(R.string.PositionValueCourseNameSearchCollege).equals(positionvalue)){
                        OnLoadMoreListener loadMoreList1 = new OnLoadMoreListener() {
                            @Override
                            public void onLoadMore() {
                                if (getResources().getString(R.string.PositionValueCollegeInsideCourseProgram).equals(positionvalue) || getResources().getString(R.string.PositionValueCourseNameSearchCollegeCourse).equals(positionvalue) || getResources().getString(R.string.PositionValueCourseNameSearchCollege).equals(positionvalue)) {
                                    showViews(recylerLoadingLayout.getId());
                                    loadMoreChange = true;
                                    Log.e("", "");
                                    courseCollegeListCount += 30;
                                    String coursecollegelisturl = Constant.SERVER+Constant.COURSE_BASED_COLLEGES;
                                    Map<String, String> coursecollegeParams = new HashMap<>();
                                    coursecollegeParams.put("count", String.valueOf(courseCollegeListCount));
                                    coursecollegeParams.put("groupid", selectedGroup);
                                    coursecollegeParams.put("categoryid", selectedCategory);
                                    getCollegeListPostMethod(coursecollegeParams, coursecollegelisturl);
                                }
                            }
                        };
                        if (loadMoreChange){
                            if ("OK".equals(response.getString("Response"))) {
                                final JSONArray newLoadedArray = response.getJSONArray("Courses_Based_College");
                                collegeListArray.put(null);
                                mAdapter.notifyItemInserted(collegeListArray.length() - 1);
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        collegeListArray.remove(collegeListArray.length() - 1);
                                        mAdapter.notifyItemRemoved(collegeListArray.length());
                                        for (int i = 0; i < newLoadedArray.length() - 1; i++) {
                                            try {
                                                collegeListArray.put(newLoadedArray.getJSONObject(i));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        mAdapter.notifyItemInserted(collegeListArray.length());
                                        mAdapter.setLoaded();
                                        hideViews(recylerLoadingLayout.getId());
                                    }
                                }, 2000);
                            }else if ("No data found".equals(response.getString("Response"))){
                                hideViews(recylerLoadingLayout.getId());
                            }
                        }else {
                            if ("OK".equals(response.getString("Response"))) {
                                navigationBackTitle.setText(getResources().getString(R.string.OfferingColleges));
                                courseCollegeListCount = 30;
                                hideViews(simpleSearchView.getId(), filterButton.getId());
                                if (getResources().getString(R.string.PositionValueCourseNameSearch).equals(positionvalue)){
                                    positionvalue = getResources().getString(R.string.PositionValueCourseNameSearchCollege);
                                }else if (getResources().getString(R.string.PositionValueCourseNameSearchProgram).equals(positionvalue)){
                                    positionvalue = getResources().getString(R.string.PositionValueCourseNameSearchCollegeCourse);
                                } else {
                                    positionvalue = getResources().getString(R.string.PositionValueCollegeInsideCourseProgram);
                                }
                                collegeListArray = response.getJSONArray("Courses_Based_College");
                                mAdapter = new MyAdapter(maincontext, collegeListArray, positionvalue, recycleitemclicklistener, mRecyclerView, loadMoreList1, true);
                                mRecyclerView.setAdapter(mAdapter);
                                StopLoading();
                            }else if ("No data found".equals(response.getString("Response"))){
                                hideViews(filterButton.getId());
                                StopLoading();
                                Toast.makeText(getActivity(),"No data found. please try again.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error", error.toString());
                showErrorToast(error);
                hideViews(recylerLoadingLayout.getId());
                if ("course_college".equals(positionvalue) || "name_course_detail_college".equals(positionvalue) || "name_course_college".equals(positionvalue)){
                    showViews(filterButton.getId());
                }

            }
        });

        jsObjRequest.setRetryPolicy(retry);
        AppController.getInstance().addToRequestQueue(jsObjRequest, "tag_json_obj");
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        JSONArray mCareerData= new JSONArray();
        String positionstr;
        Context context;
        RecyclerViewClickListener itemclicklistener;
        OnLoadMoreListener mOnLoadMoreListener;
        RecyclerView recycleview;
        private final int VIEW_TYPE_ITEM = 0;
        private final int VIEW_TYPE_LOADING = 1;
        private int visibleThreshold = 5;
        private int lastVisibleItem, totalItemCount;
        private boolean loading;

        @Override
        public int getItemViewType(int position) {
            int vType = 0;
            try {
                vType =  mCareerData.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
            } catch (JSONException e) {
                e.printStackTrace();
            }
           return   vType;
        }
        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            // each data item is just a string in this case
            //Career Views
            TextView careerTitle;
            //Course Views
            TextView courseLevelTxtView, courseNameTxtView, programNameTxtView, durationTxtView, lineTxtView;
            //College Views
            TextView collegeNameTxtView, CollegeAddressTxtView, CollegePinCodeTxtView, CollegeAffilitaedTxtView, CollegeWebSiteTxtView;
            Button offeringButton;
            ImageView careerImageView;

            ViewHolder(View v) {
                super(v);
                if (getResources().getString(R.string.PositionValueProgramInsideCourse).equals(positionstr) || getResources().getString(R.string.PositionValueCourseNameSearchProgram).equals(positionstr)){
                    courseLevelTxtView = v.findViewById(R.id.course_level_txt_view);
                    courseNameTxtView = v.findViewById(R.id.course_name_txt_view);
                    programNameTxtView = v.findViewById(R.id.program_name_txt_view);
                    durationTxtView = v.findViewById(R.id.program_duration_txt_view);
                    offeringButton = v.findViewById(R.id.offering_college_button);
                    offeringButton.setOnClickListener(this);
                }else if (getResources().getString(R.string.PositionValueCollegeMain).equals(positionstr) || getResources().getString(R.string.PositionValueCollegeInsideCourseProgram).equals(positionstr) || getResources().getString(R.string.PositionValueCollegeNameSearch).equals(positionstr) || getResources().getString(R.string.PositionValueCourseNameSearchCollege).equals(positionvalue) || getResources().getString(R.string.PositionValueCourseNameSearchCollegeCourse).equals(positionstr)){
                    collegeNameTxtView = v.findViewById(R.id.college_name_txt_view);
                    CollegeAddressTxtView = v.findViewById(R.id.address_text_view);
                    CollegePinCodeTxtView = v.findViewById(R.id.pincode_text_view);
                    CollegeAffilitaedTxtView = v.findViewById(R.id.affiliated_text_view);
                    CollegeWebSiteTxtView = v.findViewById(R.id.website_txt_view);
                    v.setOnClickListener(this);
                } else if (getResources().getString(R.string.PositionValueCareerMain).equals(positionvalue)){
                    careerTitle = v.findViewById(R.id.career_title);
                    careerImageView = v.findViewById(R.id.career_imag_view);
                    v.setOnClickListener(this);
                }else if (getResources().getString(R.string.PositionValueCourseNameSearch).equals(positionstr)){
                    courseLevelTxtView = v.findViewById(R.id.course_level_txt_view);
                    courseNameTxtView = v.findViewById(R.id.course_name_txt_view);
                    programNameTxtView = v.findViewById(R.id.program_name_txt_view);
                    durationTxtView = v.findViewById(R.id.program_duration_txt_view);
                    offeringButton = v.findViewById(R.id.offering_college_button);
                    lineTxtView = v.findViewById(R.id.line_textview);
                    offeringButton.setOnClickListener(this);
                    v.setOnClickListener(this);
                } else {
                    careerTitle = v.findViewById(R.id.career_title);
                    v.setOnClickListener(this);
                }
            }

            @Override
            public void onClick(View v) {
                itemclicklistener.recyclerViewListClicked(v, this.getLayoutPosition());
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        MyAdapter(Context inputcontext, JSONArray careerdata, String position, RecyclerViewClickListener inputrecyclerViewClickListener, RecyclerView recyclerView, OnLoadMoreListener inputloadmorelistener, Boolean loadMore) {

          this.mCareerData = careerdata;
            positionstr = position;
            itemclicklistener = inputrecyclerViewClickListener;
            context = inputcontext;
            if (loadMore){
                recycleview = recyclerView;
                mOnLoadMoreListener = inputloadmorelistener;
                final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView
                        .getLayoutManager();

                recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);

                        totalItemCount = linearLayoutManager.getItemCount();
                        lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                        if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                            // End has been reached
                            // Do something
                            if (mOnLoadMoreListener != null) {
                                mOnLoadMoreListener.onLoadMore();
                            }
                            loading = true;
                        }
                    }

                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                    }
                });
            }
        }

        // Create new views (invoked by the layout manager)
        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v;
            // create a new view
            if (getResources().getString(R.string.PositionValueProgramInsideCourse).equals(positionstr) || getResources().getString(R.string.PositionValueCourseNameSearchProgram).equals(positionstr)){
                v= LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.course_list_layout, parent, false);
            }else if (getResources().getString(R.string.PositionValueCollegeMain).equals(positionstr) || getResources().getString(R.string.PositionValueCollegeInsideCourseProgram).equals(positionstr) || getResources().getString(R.string.PositionValueCollegeNameSearch).equals(positionstr) || getResources().getString(R.string.PositionValueCourseNameSearchCollege).equals(positionvalue) || getResources().getString(R.string.PositionValueCourseNameSearchCollegeCourse).equals(positionstr)){
                    v = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.college_recycler_view, parent, false);
            }else if ("course_prog_list".equals(positionstr)){
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.course_list_layout, parent, false);
            }else if (getResources().getString(R.string.PositionValueCareerMain).equals(positionvalue)){
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.career_image_recycler_view, parent, false);
            }else if (getResources().getString(R.string.PositionValueCourseNameSearch).equals(positionstr)){
                v= LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.course_list_layout, parent, false);
            } else {
                 v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.career_recyclerview, parent, false);
            }
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            // holder.mTextView.setText(mDataset[position]);
            String courseLevelStr;
            try {
                if (getResources().getString(R.string.PositionValueCareerMain).equals(positionstr)) {
                    JSONObject newobj = mCareerData.getJSONObject(position);
                    if (newobj.getString("listType").equals("mainList")){
                        holder.careerImageView.setVisibility(View.VISIBLE);
                        Picasso.get().load(Constant.CAREER_IMAGES_URL+newobj.getString("CAREER_GROUP_NUMBER_C")+".jpg").into(holder.careerImageView);
                        holder.careerTitle.setText(newobj.getString("NAME"));
                    }else {
                        holder.careerImageView.setVisibility(View.GONE);
                        holder.careerTitle.setText(newobj.getString("SUB_NAME"));
                    }
                }else if (getResources().getString(R.string.PositionValueSubCareer).equals(positionstr)){
                    JSONObject newobj = mCareerData.getJSONObject(position);
                    holder.careerTitle.setText(newobj.getString("SUB_NAME"));
                }else if (getResources().getString(R.string.PositionValueCourseMain).equals(positionstr)){
                    JSONObject newobj = mCareerData.getJSONObject(position);
                    holder.careerTitle.setText(newobj.getString("discipline_group_category"));
                }else if ("course_group".equals(positionstr)){
                    JSONObject newobj = mCareerData.getJSONObject(position);
                    holder.careerTitle.setText(newobj.getString("discipline_group"));
                }else if (getResources().getString(R.string.PositionValueProgramInsideCourse).equals(positionstr) || getResources().getString(R.string.PositionValueCourseNameSearchProgram).equals(positionstr)){
                    JSONObject newobj = mCareerData.getJSONObject(position);
                    for (int i = 0; i < courseLevelid.length; i++) {
                        if (courseLevelid[i].equals(newobj.getString("ref_Course_Level_ID"))){
                            courseLevelStr = courseLevels[i];
                            holder.courseLevelTxtView.setText(getResources().getString(R.string.CourseListCourseLevel, courseLevelStr));
                            holder.courseNameTxtView.setText(newobj.getString("NAME"));
                            holder.programNameTxtView.setText(getResources().getString(R.string.CourseListProgram, newobj.getString("programme_name")));
                            holder.durationTxtView.setText(getResources().getString(R.string.CourseListDuration, newobj.getString("DURATION_YEARS")));
                        }
                    }
                }else if (getResources().getString(R.string.PositionValueCollegeMain).equals(positionstr) || getResources().getString(R.string.PositionValueCollegeInsideCourseProgram).equals(positionstr) || getResources().getString(R.string.PositionValueCollegeNameSearch).equals(positionstr) || getResources().getString(R.string.PositionValueCourseNameSearchCollege).equals(positionstr) || getResources().getString(R.string.PositionValueCourseNameSearchCollegeCourse).equals(positionstr)){
                    JSONObject newobj = mCareerData.getJSONObject(position);
                    holder.collegeNameTxtView.setText(newobj.getString("Name"));
                    holder.CollegeAddressTxtView.setText(getResources().getString(R.string.CollegeListAddress, newobj.getString("Address_Line_1"), newobj.getString("Address_line_2")));
                    holder.CollegePinCodeTxtView.setText(newobj.getString("Pin_Code"));
                    if (newobj.getString("WEBSITE").equals("")){
                        holder.CollegeWebSiteTxtView.setText(getResources().getString(R.string.NotAvailable));
                    }
                    else {
                        holder.CollegeWebSiteTxtView.setText(newobj.getString("WEBSITE"));
                    }
                }else if (getResources().getString(R.string.PositionValueCollegeCourse).equals(positionstr)){
                    holder.careerTitle.setText(mCareerData.get(position).toString());
                }else if (getResources().getString(R.string.PositionValueCourseNameSearch).equals(positionstr)){
                    JSONObject newobj = mCareerData.getJSONObject(position);
                    if ("mainList".equals(newobj.getString("listType"))){
                        holder.courseLevelTxtView.setVisibility(View.GONE);
                        holder.offeringButton.setVisibility(View.GONE);
                        holder.lineTxtView.setVisibility(View.GONE);
                        holder.programNameTxtView.setVisibility(View.GONE);
                        holder.durationTxtView.setText(newobj.getString("discipline_group_category"));
                        holder.durationTxtView.setTextColor(getResources().getColor(R.color.BlackColor));
                        holder.durationTxtView.setTextSize(18);
                        holder.courseNameTxtView.setVisibility(View.GONE);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        params.setMargins(50, 20, 5, 20);
                        holder.durationTxtView.setLayoutParams(params);
                    }else if ("categoryList".equals(newobj.getString("listType"))){
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        params.setMargins(2, 2, 2, 2);
                        holder.durationTxtView.setLayoutParams(params);
                        holder.durationTxtView.setTextColor(getResources().getColor(R.color.TextColor));
                        holder.durationTxtView.setTextSize(18);
                        holder.courseNameTxtView.setVisibility(View.VISIBLE);
                        holder.courseLevelTxtView.setVisibility(View.VISIBLE);
                        holder.offeringButton.setVisibility(View.VISIBLE);
                        holder.lineTxtView.setVisibility(View.VISIBLE);
                        holder.programNameTxtView.setVisibility(View.VISIBLE);
                        holder.durationTxtView.setVisibility(View.VISIBLE);
                        holder.courseLevelTxtView.setText(getResources().getString(R.string.CourseListCourseLevel, newobj.getString("ref_Course_Level_ID")));
                        holder.courseNameTxtView.setText(newobj.getString("NAME"));
                        holder.programNameTxtView.setText(getResources().getString(R.string.CourseListProgram, newobj.getString("programme_name")));
                        holder.durationTxtView.setText(getResources().getString(R.string.CourseListDuration, newobj.getString("DURATION_YEARS")));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        public void setLoaded() {
            loading = false;
        }

            // Return the size of your dataset (invoked by the layout manager)
            @Override
            public int getItemCount() {
                return mCareerData.length();
            }
        }

        public interface RecyclerViewClickListener
        {

        void recyclerViewListClicked(View v, int position);

        }

        public interface OnLoadMoreListener {
            void onLoadMore();
        }
}
