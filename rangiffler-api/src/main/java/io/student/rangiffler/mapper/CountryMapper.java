package io.student.rangiffler.mapper;

import io.student.rangiffler.entity.CountryEntity;
import io.student.rangiffler.model.Country;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
public class CountryMapper {

    public Country toDto(CountryEntity entity) {
        return Country.newBuilder()
                .code(entity.getCode())
                .name(entity.getName())
                .flag(
                        "data:image/png;base64," +
                                Base64.getEncoder().encodeToString(entity.getFlag())
                )
                .build();
    }
}
