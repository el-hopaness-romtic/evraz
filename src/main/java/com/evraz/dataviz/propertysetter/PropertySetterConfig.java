package com.evraz.dataviz.propertysetter;

import com.evraz.dataviz.dto.PropertySetterCsvDto;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class PropertySetterConfig {

    @Bean
    @SneakyThrows
    Map<String, PropertySetter> propertySetterMap() {
        String fileName = "./src/main/resources/mapping.csv";

        List<PropertySetterCsvDto> beans = new CsvToBeanBuilder<PropertySetterCsvDto>(new FileReader(fileName))
                .withType(PropertySetterCsvDto.class)
                .build()
                .parse();

        Map<String, PropertySetter> map = new HashMap<>();
        beans.forEach(b ->
                map.put(
                        b.getSensorName(),
                        PropertySetter.of(b.getExgausterNumber(), b.getPath(), b.getPropName(), b.getType())
                )
        );

        return map;
    }
}
