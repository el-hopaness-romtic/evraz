package com.evraz.dataviz.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonRawValue;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SinterInfoDto {
    @JsonIgnore
    private long id;
    private String moment;
    @JsonRawValue
    private String generalProperties;
    @JsonRawValue
    private List<String> exgaustersProperties;
}
