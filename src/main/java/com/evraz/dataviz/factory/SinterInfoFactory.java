package com.evraz.dataviz.factory;

import com.evraz.dataviz.dto.SinterInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class SinterInfoFactory {

    private static final String EXG_TEMPLATE_JSON = "exgTemplate.json";
    private static final String GEN_TEMPLATE_JSON = "genTemplate.json";

    private final ObjectNode genTemplate;
    private final ObjectNode exgTemplate;
    private final int exgCount;

    @SneakyThrows
    SinterInfoFactory(@Value("${EXG_COUNT:6}") int exgCount,
                      @Autowired ObjectMapper objectMapper) {
        try (Scanner scan = new Scanner(new ClassPathResource(EXG_TEMPLATE_JSON).getInputStream())) {
            scan.useDelimiter("\\Z");
            this.exgTemplate = (ObjectNode) objectMapper.readTree(scan.next());
        }

        try (Scanner scan = new Scanner(new ClassPathResource(GEN_TEMPLATE_JSON).getInputStream())) {
            scan.useDelimiter("\\Z");
            this.genTemplate = (ObjectNode) objectMapper.readTree(scan.next());
        }

        this.exgCount = exgCount;
    }

    public SinterInfo create() {
        ObjectNode generalInfo = genTemplate.deepCopy();
        ObjectNode[] exgaustersInfo = new ObjectNode[exgCount];
        for (int i = 0; i < exgaustersInfo.length; i++) {
            exgaustersInfo[i] = exgTemplate.deepCopy();
        }

        return new SinterInfo(generalInfo, exgaustersInfo);
    }
}
