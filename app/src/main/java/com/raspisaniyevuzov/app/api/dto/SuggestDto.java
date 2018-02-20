package com.raspisaniyevuzov.app.api.dto;

/**
 * Created by SAPOZHKOV on 23.10.2015.
 */
public class SuggestDto {

    public String id;
    public String abbr;
    public String name;

    public SuggestDto(String id, String name, String abbr) {
        this.id = id;
        this.name = name;
        this.abbr = abbr;
    }

}
