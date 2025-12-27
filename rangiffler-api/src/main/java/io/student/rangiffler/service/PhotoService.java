package io.student.rangiffler.service;

import io.student.rangiffler.entity.PhotoEntity;
import io.student.rangiffler.mapper.PhotoMapper;
import io.student.rangiffler.model.Photo;
import io.student.rangiffler.model.PhotoInput;
import io.student.rangiffler.repository.PhotoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@Service
public class PhotoService {

    private final PhotoRepository photoRepository;
    private final CountryService countryService;
    private final PhotoMapper mapper;

    public PhotoService(PhotoRepository photoRepository,
                        CountryService countryService,
                        PhotoMapper mapper) {
        this.photoRepository = photoRepository;
        this.countryService = countryService;
        this.mapper = mapper;
    }

    public Photo createPhoto(UUID userId, PhotoInput input) {
        UUID countryId = countryService
                .findByCode(input.getCountry().getCode())
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

    public void deletePhoto(UUID userId, UUID photoId) {
        photoRepository.deleteById(photoId);
    }
}