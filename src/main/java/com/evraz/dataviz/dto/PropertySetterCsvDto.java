package com.evraz.dataviz.dto;

import com.opencsv.bean.CsvBindByName;
import lombok.Getter;

@Getter
public class PropertySetterCsvDto {

    @CsvBindByName(column = "sensor_name", required = true)
    private String sensorName;

    @CsvBindByName(column = "exgauster_number", required = true)
    private int exgausterNumber;

    @CsvBindByName(column = "path")
    private String path;

    @CsvBindByName(column = "prop_name", required = true)
    private String propName;

    @CsvBindByName(column = "type", required = true)
    private String type;

}
