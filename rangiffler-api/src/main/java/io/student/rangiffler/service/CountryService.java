package io.student.rangiffler.service;

import io.student.rangiffler.entity.CountryEntity;
import io.student.rangiffler.mapper.CountryMapper;
import io.student.rangiffler.model.Country;
import io.student.rangiffler.repository.CountryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CountryService {

    private final CountryRepository countryRepository;
    private final CountryMapper mapper;

    public CountryService(CountryRepository countryRepository, CountryMapper mapper) {
        this.countryRepository = countryRepository;
        this.mapper = mapper;
    }

    public List<Country> findAll() {
        return countryRepository.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    public CountryEntity findByCode(String code) {
        return countryRepository.findByCode(code)
                .orElseThrow(() ->
                        new IllegalArgumentException("Country not found by code: " + code)
                );
    }
}
