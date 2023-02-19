package com.evraz.dataviz.dto;

import com.fasterxml.jackson.annotation.JsonRawValue;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ExgausterHistoricalDto {
    @JsonRawValue
    private final List<String> history;
}
