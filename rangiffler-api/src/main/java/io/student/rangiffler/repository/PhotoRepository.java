package io.student.rangiffler.repository;

import io.student.rangiffler.entity.PhotoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PhotoRepository extends JpaRepository<PhotoEntity, UUID> {
}