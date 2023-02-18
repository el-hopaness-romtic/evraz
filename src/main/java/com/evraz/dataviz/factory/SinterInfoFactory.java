package com.evraz.dataviz.factory;

import com.evraz.dataviz.dto.SinterInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class SinterInfoFactory {

    private final ObjectNode genTemplate;
    private final ObjectNode exgTemplate;
    private final int exgCount;

    @SneakyThrows
    SinterInfoFactory(@Value("${EXG_COUNT:6}") int exgCount,
                      @Value("${EXG_TEMPLATE_PATH:./src/main/resources/exgTemplate.json}") String exgTemplatePath,
                      @Value("${EXG_TEMPLATE_PATH:./src/main/resources/genTemplate.json}") String genTemplatePath,
                      @Autowired ObjectMapper objectMapper) {
        String content = Files.readString(Path.of(exgTemplatePath));
        this.exgTemplate = (ObjectNode) objectMapper.readTree(content);

        content = Files.readString(Path.of(genTemplatePath));
        this.genTemplate = (ObjectNode) objectMapper.readTree(content);

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
