package com.evraz.dataviz.propertysetter;

import com.evraz.dataviz.dto.SinterInfo;
import com.fasterxml.jackson.databind.node.TextNode;

public class MomentSetter extends PropertySetter {

    MomentSetter(int exgausterNumber, String path, String propertyName) {
        super(exgausterNumber, path, propertyName);
    }

    @Override
    public void setProperty(SinterInfo sinterInfo, Object value) {
        getNode(sinterInfo).set(propertyName, (TextNode) value);
    }
}
