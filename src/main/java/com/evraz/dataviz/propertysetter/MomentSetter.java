package com.evraz.dataviz.propertysetter;

import com.evraz.dataviz.dto.ExgData;
import com.fasterxml.jackson.databind.node.TextNode;

public class MomentSetter extends PropertySetter {

    MomentSetter(int exgausterNumber, String path, String propertyName) {
        super(exgausterNumber, path, propertyName);
    }

    @Override
    public void setProperty(ExgData exgData, Object value) {
        getNode(exgData).set(propertyName, (TextNode) value);
    }
}
