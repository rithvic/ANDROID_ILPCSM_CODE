package com.qbay.qbayApps.ilpcsm.Services;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import com.qbay.qbayApps.ilpcsm.R;

/**
 * Created by benchmark on 21/03/18.
 */

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<String>> _listDataChild;

    private String courseLevels[] = {"Any","Ph.D.","M.Phil.","Post Graduate","Under Graduate","PG Diploma","Diploma","Certificate","Integrated","10","10+2"};
    private String courseLevelid[] = {"99","1","2","3","4","5","6","7","8","10","11"};

    public ExpandableListAdapter(Context context, List<String> listDataHeader,
                                 HashMap<String, List<String>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }

    @Override
    public String getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = getChild(groupPosition, childPosition);
        String[]  itms=childText.split("\n");

        String courseLevelStr = "Not Available";

        for (int i = 0; i < courseLevelid.length; i++) {
            if (courseLevelid[i].equals(itms[3])){
                courseLevelStr = courseLevels[i];
            }
        }

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = Objects.requireNonNull(infalInflater).inflate(R.layout.course_list_item, null);
        }

        TextView courlevelChild = convertView.findViewById(R.id.course_level_txt_view);
        TextView programtxtview = convertView.findViewById(R.id.program_name_txt_view);
        TextView programdurationtxtview = convertView.findViewById(R.id.program_duration_txt_view);
        TextView coursenameTxtView = convertView.findViewById(R.id.course_name_txt_view);

        courlevelChild.setText("Course Level : "+courseLevelStr);
        programtxtview.setText("Program Name : "+itms[2]);
        programdurationtxtview.setText("Duration : "+itms[1]);
        coursenameTxtView.setText(itms[0]);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = Objects.requireNonNull(infalInflater).inflate(R.layout.course_list_group, null);
        }

        TextView lblListHeader = convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }



}
