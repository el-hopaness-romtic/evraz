package com.evraz.dataviz.propertysetter;

import com.evraz.dataviz.dto.SinterInfo;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.DoubleNode;

public class BooleanSetter extends PropertySetter {

    BooleanSetter(int exgausterNumber, String path, String propertyName) {
        super(exgausterNumber, path, propertyName);
    }

    @Override
    public void setProperty(SinterInfo sinterInfo, Object value) {
        getNode(sinterInfo).set(propertyName, BooleanNode.valueOf(((DoubleNode) value).asDouble() > 0.5));
    }
}
