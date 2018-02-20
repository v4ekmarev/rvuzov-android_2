package com.raspisaniyevuzov.app.event;

import com.raspisaniyevuzov.app.api.dto.SuggestDto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SAPOZHKOV on 11.11.2015.
 */
public class BaseSuggestResponseEvent {

    public List<SuggestDto> suggestList = new ArrayList<>();
    public String query;

    public BaseSuggestResponseEvent(List<SuggestDto> suggestList, String query) {
        this.suggestList = suggestList;
        this.query = query;
    }

}
