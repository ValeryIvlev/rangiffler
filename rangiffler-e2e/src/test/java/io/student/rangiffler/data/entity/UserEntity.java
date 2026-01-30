package io.student.rangiffler.data.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {

    @Id
    @Column(name = "id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "username", length = 50)
    private String username;

    @Column(name = "firstname", length = 255)
    private String firstname;

    @Column(name = "lastName", length = 255)
    private String lastName;

    @Lob
    @Column(name = "avatar", nullable = false, columnDefinition = "LONGBLOB")
    private byte[] avatar;

    @Column(name = "country_id", columnDefinition = "BINARY(16)")
    private UUID countryId;
}