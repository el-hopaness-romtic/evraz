package com.evraz.dataviz.controller;

import com.evraz.dataviz.dto.ExgausterHistoricalDto;
import com.evraz.dataviz.dto.SinterInfoDto;
import com.evraz.dataviz.service.SinterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
public class SinterController {
    @Autowired
    SinterService service;

    @Operation(summary = "Получить последние данные о состоянии агломашин")
    @GetMapping("actual")
    public SinterInfoDto getActualData() {
        return service.getActualData();
    }

    @Operation(summary = "Получить исторические данные о состоянии эксгаустера в хронологическом порядке")
    @GetMapping("historical")
    public ExgausterHistoricalDto getHistoricalData(
            @Parameter(description = "Дата и время, с которой нужны данные, формат ISO 8601", example = "2023-02-19T01:02:25.438")
            @DateTimeFormat(pattern = "YYYY-MM-DD'T'HH:mm:ss.SSS")
            @RequestParam("since")
            LocalDateTime since,
            @Parameter(description = "Номер эксгаустера", example = "2")
            @RequestParam("exgausterNumber")
            int exgausterNumber
    ) {
        return service.getHistoricalData(since, exgausterNumber);
    }
}
