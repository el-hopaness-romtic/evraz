package com.evraz.dataviz.dto;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ExgData {
    private final ObjectNode generalInfo;
    private final ObjectNode[] exgaustersInfo;

    public ObjectNode getExgausterInfo(int index) {
        return exgaustersInfo[index];
    }
}
