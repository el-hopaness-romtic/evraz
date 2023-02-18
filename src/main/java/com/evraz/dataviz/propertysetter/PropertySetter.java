package com.evraz.dataviz.propertysetter;

import com.evraz.dataviz.dto.SinterInfo;
import com.evraz.dataviz.factory.JsonPointerFactory;
import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public abstract class PropertySetter {
    private final int exgausterNumber;
    private final JsonPointer path;
    private final boolean isGeneral;
    protected final String propertyName;

    protected PropertySetter(int exgausterNumber, String path, String propertyName) {
        this.exgausterNumber = exgausterNumber;
        this.path = JsonPointerFactory.forPath(path);
        this.propertyName = propertyName;
        this.isGeneral = exgausterNumber == -1;
    }

    protected ObjectNode getNode(SinterInfo sinterInfo) {
        JsonNode node;
        if (isGeneral) {
            node = sinterInfo.getGeneralInfo();
        } else {
            node = sinterInfo.getExgausterInfo(exgausterNumber);
        }

        return (ObjectNode) node.at(path);
    }

    public abstract void setProperty(SinterInfo sinterInfo, Object value);

    public static PropertySetter of(int exgausterNumber, String path, String propertyName, String type) {
        return switch (type) {
            case "datetime" -> new MomentSetter(exgausterNumber, path, propertyName);
            case "digital" -> new BooleanSetter(exgausterNumber, path, propertyName);
            case "analog" -> new DoubleSetter(exgausterNumber, path, propertyName);
            default -> throw new IllegalArgumentException("Unknown type: " + type);
        };
    }
}
