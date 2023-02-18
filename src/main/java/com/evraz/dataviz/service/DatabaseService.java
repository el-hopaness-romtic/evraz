package com.evraz.dataviz.service;

import com.evraz.dataviz.dto.ExgData;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;

@Service
public class DatabaseService {

    private final KeyHolder keyHolder = new GeneratedKeyHolder();
    private final PreparedStatementCreatorFactory preparedStatementCreatorFactory;
    private final JdbcTemplate jdbcTemplate;

    DatabaseService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;

        preparedStatementCreatorFactory = new PreparedStatementCreatorFactory(
                "INSERT INTO general_info(moment, properties) VALUES (?, ?)", Types.TIMESTAMP, Types.VARCHAR
        );
        preparedStatementCreatorFactory.setReturnGeneratedKeys(true);
        preparedStatementCreatorFactory.setGeneratedKeysColumnNames("id");
    }

    @Transactional
    public void saveExgData(ExgData exgData) {
        // в кафке данные прилетают в UTC
        LocalDateTime mscDateTime = LocalDateTime.parse(exgData.getGeneralInfo().remove("moment").textValue()).plusHours(3);
        Timestamp timestamp = Timestamp.valueOf(mscDateTime);

        PreparedStatementCreator psc = preparedStatementCreatorFactory.newPreparedStatementCreator(
                new Object[]{timestamp, exgData.getGeneralInfo().toString()}
        );
        jdbcTemplate.update(psc, keyHolder);

        jdbcTemplate.batchUpdate(
                "INSERT INTO exgauster_info(id, exgauster_number, properties) VALUES (?, ?, ?)",
                createBatchPreparedStatementSetter(exgData)
        );
    }

    private BatchPreparedStatementSetter createBatchPreparedStatementSetter(ExgData exgData) {
        return new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ObjectNode objectNode = exgData.getExgausterInfo(i);

                ps.setLong(1, keyHolder.getKeyAs(Long.class));
                ps.setInt(2, i + 1);
                ps.setString(3, objectNode.toString());
            }

            @Override
            public int getBatchSize() {
                return exgData.getExgaustersInfo().length;
            }
        };
    }
}
