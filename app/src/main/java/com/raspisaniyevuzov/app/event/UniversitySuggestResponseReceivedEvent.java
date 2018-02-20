package com.raspisaniyevuzov.app.event;

import com.raspisaniyevuzov.app.api.dto.SuggestDto;

import java.util.List;

/**
 * Created by SAPOZHKOV on 23.10.2015.
 */
public class UniversitySuggestResponseReceivedEvent extends BaseSuggestResponseEvent {

    public UniversitySuggestResponseReceivedEvent(List<SuggestDto> suggestList, String query) {
        super(suggestList, query);
    }

}

