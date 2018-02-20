package com.raspisaniyevuzov.app.ui.auth.search;


import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.raspisaniyevuzov.app.R;
import com.raspisaniyevuzov.app.api.dto.SuggestDto;

public class SearchRow extends LinearLayout {
    Context mContext;
    String mSearchType;
    SuggestDto mDto;

    public SearchRow(Context context) {
        super(context);
        mContext = context;
        setup();
    }

    public SearchRow(Context context, String searchType) {
        super(context);
        mSearchType = searchType;
        mContext = context;
        setup();
    }

    private void setup() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (BaseSearchFragment.SearchType.UNIVERSITY.type.equals(mSearchType))
            inflater.inflate(R.layout.search_university_list_item, this);
        else if (BaseSearchFragment.SearchType.FACULTY.type.equals(mSearchType))
            inflater.inflate(R.layout.search_faculty_list_item, this);
        else if (BaseSearchFragment.SearchType.GROUP.type.equals(mSearchType))
            inflater.inflate(R.layout.search_group_list_item, this);
    }

    public void setLog(SuggestDto dto) {
        mDto = dto;
        ((TextView) findViewById(R.id.tvName)).setText(dto.name);
        if (BaseSearchFragment.SearchType.UNIVERSITY.type.equals(mSearchType))
            ((TextView) findViewById(R.id.tvAbbr)).setText(dto.abbr);
    }
}