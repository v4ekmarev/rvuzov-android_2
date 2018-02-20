package com.raspisaniyevuzov.app.event;

import com.raspisaniyevuzov.app.api.dto.SuggestDto;

import java.util.List;

/**
 * Created by SAPOZHKOV on 23.10.2015.
 */
public class FacultySuggestResponseReceivedEvent extends BaseSuggestResponseEvent {

    public FacultySuggestResponseReceivedEvent(List<SuggestDto> suggestList, String query) {
        super(suggestList, query);
    }
}

