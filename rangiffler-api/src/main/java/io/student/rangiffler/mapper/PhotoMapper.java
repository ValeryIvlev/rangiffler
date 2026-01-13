package io.student.rangiffler.mapper;

import io.student.rangiffler.entity.PhotoEntity;
import io.student.rangiffler.model.Photo;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.Date;

@Component
public class PhotoMapper {

    public Photo toDto(PhotoEntity entity) {
        return Photo.newBuilder()
                .id(entity.getId())
                .src(
                        "data:image/png;base64," +
                                Base64.getEncoder().encodeToString(entity.getPhoto())
                )
                .description(entity.getDescription())
                .creationDate(Date.from(
                        entity.getCreatedDate()
                                .atZone(ZoneId.systemDefault())
                                .toInstant()
                ))
                .country(null)
                .likes(null)
                .isOwner(false)
                .build();
    }
}