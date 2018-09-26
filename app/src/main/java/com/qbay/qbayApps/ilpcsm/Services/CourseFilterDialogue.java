package com.qbay.qbayApps.ilpcsm.Services;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.weiwangcn.betterspinner.library.BetterSpinner;

import com.qbay.qbayApps.ilpcsm.R;

import java.util.Objects;

/**
 * Created by benchmark on 16/03/18.
 */

public class CourseFilterDialogue {

    private String selected_course;
    private filterListener filterListeners;

    private String courseLevels[] = {"Any","Under Graduate","Diploma","Certificate"};
    private String courseLevelid[] = {"99","4","6","7"};

    public void showDialog(Activity activity, filterListener inputListener){

        this.filterListeners = inputListener;
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.course_filter_dialogue);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        BetterSpinner courselevellist = dialog.findViewById(R.id.course_filter_spinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, android.R.id.text1, courseLevels);

        // Assign adapter to ListView
        courselevellist.setAdapter(adapter);

        courselevellist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("","");
                selected_course  = courseLevelid[position];
            }
        });

        // ListView Item Click Listener
        courselevellist.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });

        Button dialogBtn_cancel = dialog.findViewById(R.id.btn_cancel);
        dialogBtn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Button dialogBtn_okay = dialog.findViewById(R.id.btn_filter);
        dialogBtn_okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterListeners.filterSelected(selected_course);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public interface filterListener {

        void filterSelected(String selectedCourse);
    }

}
