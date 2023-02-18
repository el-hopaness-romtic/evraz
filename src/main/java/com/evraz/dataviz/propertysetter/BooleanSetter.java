package com.evraz.dataviz.propertysetter;

import com.evraz.dataviz.dto.ExgData;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.DoubleNode;

public class BooleanSetter extends PropertySetter {

    BooleanSetter(int exgausterNumber, String path, String propertyName) {
        super(exgausterNumber, path, propertyName);
    }

    @Override
    public void setProperty(ExgData exgData, Object value) {
        getNode(exgData).set(propertyName, BooleanNode.valueOf(((DoubleNode) value).asDouble() > 0.5));
    }
}
