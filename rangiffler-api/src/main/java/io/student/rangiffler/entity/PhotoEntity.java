package io.student.rangiffler.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "photo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhotoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "user_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID userId;

    @Column(name = "country_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID countryId;

    @Column(name = "description", length = 255)
    private String description;

    @Lob
    @Column(name = "photo", nullable = false, columnDefinition = "LONGBLOB")
    private byte[] photo;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

}