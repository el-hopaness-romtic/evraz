package com.evraz.dataviz.service;

import com.evraz.dataviz.dto.SinterInfo;
import com.evraz.dataviz.factory.SinterInfoFactory;
import com.evraz.dataviz.propertysetter.PropertySetter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Map;
import java.util.logging.Logger;

@Service
public class KafkaListenerService {
    private static final Logger LOGGER = Logger.getLogger(KafkaListenerService.class.getName());
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private SinterInfoFactory sinterInfoFactory;
    @Autowired
    private Map<String, PropertySetter> map;
    @Autowired
    DatabaseService databaseService;

    @KafkaListener(groupId = "deo123h4562", topics = "zsmk-9433-dev-01")
    public void listen(byte[] content, @Header(KafkaHeaders.OFFSET) int offset) throws IOException {
        SinterInfo sinterInfo = sinterInfoFactory.create();

        objectMapper.readTree(content)
                .fields()
                .forEachRemaining(entry -> {
                    PropertySetter propertySetter = map.get(entry.getKey());
                    if (propertySetter != null) {
                        try {
                            propertySetter.setProperty(sinterInfo, entry.getValue());
                        } catch (ClassCastException e) {
                            LOGGER.warning(MessageFormat.format("Could not use {0} on {1}, offset = {2}",
                                    propertySetter.getClass(), entry, offset));
                        }
                    }
                });

        databaseService.saveSinterInfo(sinterInfo);
    }
}
