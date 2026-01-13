package io.student.rangiffler.service;

import io.student.rangiffler.entity.PhotoEntity;
import io.student.rangiffler.mapper.PhotoMapper;
import io.student.rangiffler.model.Photo;
import io.student.rangiffler.model.PhotoInput;
import io.student.rangiffler.repository.CountryRepository;
import io.student.rangiffler.repository.PhotoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@Service
public class PhotoService {

    private final PhotoRepository photoRepository;
    private final CountryRepository countryRepository;
    private final PhotoMapper mapper;

    public PhotoService(PhotoRepository photoRepository, CountryRepository countryRepository,
                        PhotoMapper mapper) {
        this.photoRepository = photoRepository;
        this.countryRepository = countryRepository;
        this.mapper = mapper;
    }

    public Photo createPhoto(UUID userId, PhotoInput input) {
        UUID countryId = countryRepository
                .findByCode(input.getCountry().getCode())
                .orElseThrow(() ->
                        new IllegalArgumentException("Country not found by code: " + input.getCountry().getCode())
                )
                .getId();

        PhotoEntity entity = PhotoEntity.builder()
                .userId(userId)
                .countryId(countryId)
                .description(input.getDescription())
                .photo(Base64.getDecoder().decode(input.getSrc()))
                .createdDate(LocalDateTime.now())
                .build();

        PhotoEntity saved = photoRepository.save(entity);
        return mapper.toDto(saved);
    }

    public void deletePhoto(UUID photoId) {
        photoRepository.deleteById(photoId);
    }
}