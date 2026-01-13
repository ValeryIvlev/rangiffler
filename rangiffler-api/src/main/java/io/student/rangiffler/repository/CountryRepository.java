package io.student.rangiffler.repository;

import io.student.rangiffler.entity.CountryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CountryRepository extends JpaRepository<CountryEntity, UUID> {

    Optional<CountryEntity> findByCode(String code);
}