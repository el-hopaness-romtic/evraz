package com.evraz.dataviz.service;

import com.evraz.dataviz.dto.ExgData;
import com.evraz.dataviz.factory.ExgDataFactory;
import com.evraz.dataviz.propertysetter.PropertySetter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.logging.Logger;

@Component
public class KafkaListenerService {
    private static final Logger LOGGER = Logger.getLogger(KafkaListenerService.class.getName());
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ExgDataFactory exgDataFactory;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private Map<String, PropertySetter> map;


    @KafkaListener(groupId = "deo123h4562", topics = "zsmk-9433-dev-01")
    public void listen(byte[] content, @Header(KafkaHeaders.OFFSET) int offset) throws IOException {
        ExgData exgData = exgDataFactory.create();

        objectMapper.readTree(content)
                .fields()
                .forEachRemaining(
                        entry -> {
                            PropertySetter propertySetter = map.get(entry.getKey());
                            if (propertySetter != null) {
                                try {
                                    propertySetter.setProperty(exgData, entry.getValue());
                                } catch (ClassCastException e) {
                                    LOGGER.warning(MessageFormat.format("Could not use {0} on {1}, offset = {2}",
                                            propertySetter.getClass(), entry, offset));
                                }
                            }
                        }
                );

        batchUpdateUsingJdbcTemplate(exgData);
    }

    public void batchUpdateUsingJdbcTemplate(ExgData objectNodes) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        // в кафке данные прилетают в UTC
        LocalDateTime mscDateTime = LocalDateTime.parse(objectNodes.getGeneralInfo().remove("moment").textValue()).plusHours(3);
        Timestamp timestamp = Timestamp.valueOf(mscDateTime);

        PreparedStatementCreatorFactory preparedStatementCreatorFactory = new PreparedStatementCreatorFactory(
                "INSERT INTO general_info(moment, properties) VALUES (?, ?)", Types.TIMESTAMP, Types.VARCHAR
        );

        preparedStatementCreatorFactory.setReturnGeneratedKeys(true);
        preparedStatementCreatorFactory.setGeneratedKeysColumnNames("id");

        jdbcTemplate.update(
                preparedStatementCreatorFactory.newPreparedStatementCreator(new Object[]{timestamp, objectNodes.getGeneralInfo().toString()}),
                keyHolder
        );


        jdbcTemplate.batchUpdate("INSERT INTO exgauster_info(id, exgauster_number, properties) VALUES (?, ?, ?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ObjectNode objectNode = objectNodes.getExgaustersInfo()[i];

                        ps.setLong(1, keyHolder.getKeyAs(Long.class));
                        ps.setInt(2, i + 1);
                        ps.setString(3, objectNode.toString());
                    }

                    @Override
                    public int getBatchSize() {
                        return objectNodes.getExgaustersInfo().length;
                    }
                });
    }

}
