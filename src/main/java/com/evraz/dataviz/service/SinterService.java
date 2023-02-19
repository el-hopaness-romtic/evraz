package com.evraz.dataviz.service;

import com.evraz.dataviz.dto.ExgausterHistoricalDto;
import com.evraz.dataviz.dto.SinterInfo;
import com.evraz.dataviz.dto.SinterInfoDto;
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
import java.util.ArrayList;
import java.util.List;

@Service
public class SinterService {

    public static final String SELECT_GENERAL_QUERY = """
            SELECT id, moment, properties
            FROM general_info
            ORDER BY moment DESC
            LIMIT 1
            """;
    public static final String SELECT_EXGAUSTER_BY_ID_QUERY = """
            SELECT exgauster_number, properties
            FROM exgauster_info
            WHERE general_info_id = ?
            """;
    public static final String SELECT_HISTORICAL_EXGAUSTER_DATA_QUERY = """
            SELECT ei.properties
            FROM general_info gi
            INNER JOIN exgauster_info ei ON gi.id = ei.general_info_id
            WHERE gi.moment > ? AND ei.exgauster_number = ?
            ORDER BY gi.moment ASC
            """;
    public static final String INSERT_GENERAL_INFO_QUERY = "INSERT INTO general_info(moment, properties) VALUES (?, ?)";
    public static final String INSERT_EXGAUSTER_INFO_QUERY = "INSERT INTO exgauster_info VALUES (?, ?, ?)";
    private final KeyHolder keyHolder = new GeneratedKeyHolder();
    private final PreparedStatementCreatorFactory preparedStatementCreatorFactory;
    private final JdbcTemplate jdbcTemplate;
    private SinterInfo actualData;
    private LocalDateTime actualMoment;

    SinterService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;

        preparedStatementCreatorFactory = new PreparedStatementCreatorFactory(
                INSERT_GENERAL_INFO_QUERY, Types.TIMESTAMP, Types.VARCHAR
        );
        preparedStatementCreatorFactory.setReturnGeneratedKeys(true);
        preparedStatementCreatorFactory.setGeneratedKeysColumnNames("id");
    }

    @Transactional
    public void saveSinterInfo(SinterInfo sinterInfo) {
        // в кафке данные прилетают в UTC
        LocalDateTime mscDateTime = LocalDateTime.parse(sinterInfo.getGeneralInfo().remove("moment").textValue()).plusHours(3);
        actualData = sinterInfo;
        actualMoment = mscDateTime;

        PreparedStatementCreator psc = preparedStatementCreatorFactory.newPreparedStatementCreator(new Object[]{
                Timestamp.valueOf(mscDateTime),
                sinterInfo.getGeneralInfo().toString()
        });
        jdbcTemplate.update(psc, keyHolder);

        jdbcTemplate.batchUpdate(
                INSERT_EXGAUSTER_INFO_QUERY,
                createBatchPreparedStatementSetter(sinterInfo)
        );
    }

    private BatchPreparedStatementSetter createBatchPreparedStatementSetter(SinterInfo sinterInfo) {
        return new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ObjectNode objectNode = sinterInfo.getExgausterInfo(i);

                ps.setLong(1, keyHolder.getKeyAs(Long.class));
                ps.setInt(2, i + 1);
                ps.setString(3, objectNode.toString());
            }

            @Override
            public int getBatchSize() {
                return sinterInfo.getExgaustersInfo().length;
            }
        };
    }

    public SinterInfoDto getActualData() {
        SinterInfoDto sinterInfoDto = new SinterInfoDto();

        if (actualData != null) {
            sinterInfoDto.setMoment(actualMoment.toString());
            sinterInfoDto.setGeneralProperties(actualData.getGeneralInfo().toString());

            List<String> exgausterProperties = new ArrayList<>();
            for (ObjectNode jsonNodes : actualData.getExgaustersInfo()) {
                exgausterProperties.add(jsonNodes.toString());
            }
            sinterInfoDto.setExgaustersProperties(exgausterProperties);
            return sinterInfoDto;
        }

        jdbcTemplate.queryForObject(SELECT_GENERAL_QUERY, (rs, rowNum) -> {
            sinterInfoDto.setId(rs.getLong("id"));
            sinterInfoDto.setMoment(rs.getTimestamp("moment").toLocalDateTime().toString());
            sinterInfoDto.setGeneralProperties(rs.getString("properties"));
            return sinterInfoDto;
        });


        ArrayList<String> strings = new ArrayList<>();
        jdbcTemplate.query(
                SELECT_EXGAUSTER_BY_ID_QUERY,
                (rs, rowNum) -> strings.add(rs.getString("properties")),
                sinterInfoDto.getId()
        );
        sinterInfoDto.setExgaustersProperties(strings);
        return sinterInfoDto;
    }

    public ExgausterHistoricalDto getHistoricalData(LocalDateTime since, int exgausterNumber) {

        ArrayList<String> strings = new ArrayList<>();
        jdbcTemplate.query(
                SELECT_HISTORICAL_EXGAUSTER_DATA_QUERY,
                (rs, rowNum) -> strings.add(rs.getString("properties")),
                since, exgausterNumber
        );

        return new ExgausterHistoricalDto(strings);
    }
}
