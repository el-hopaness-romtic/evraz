package com.evraz.dataviz.propertysetter;

import com.evraz.dataviz.dto.PropertySetterCsvDto;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class PropertySetterConfig {

    private static final String MAPPING_CSV = "mapping.csv";

    @Bean
    @SneakyThrows
    Map<String, PropertySetter> propertySetterMap() {
        List<PropertySetterCsvDto> beans;
        try (Reader reader = new InputStreamReader(new ClassPathResource(MAPPING_CSV).getInputStream())) {
            beans = new CsvToBeanBuilder<PropertySetterCsvDto>(reader)
                    .withType(PropertySetterCsvDto.class)
                    .build()
                    .parse();
        }

        Map<String, PropertySetter> map = new HashMap<>();
        beans.forEach(b -> map.put(
                b.getSensorName(),
                PropertySetter.of(b.getExgausterNumber(), b.getPath().intern(), b.getPropName().intern(), b.getType().intern())
        ));

        return map;
    }
}
