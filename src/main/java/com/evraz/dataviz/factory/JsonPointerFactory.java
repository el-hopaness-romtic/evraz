package com.evraz.dataviz.factory;

import com.fasterxml.jackson.core.JsonPointer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.HashMap;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonPointerFactory {
    private static final HashMap<String, JsonPointer> pathToPointerMap = new HashMap<>();

    public static JsonPointer forPath(String path) {
        return pathToPointerMap.computeIfAbsent(path, JsonPointer::compile);
    }
}
