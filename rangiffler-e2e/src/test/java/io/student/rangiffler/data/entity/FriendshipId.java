package io.student.rangiffler.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendshipId implements Serializable {

    @Column(name = "requester_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID requesterId;

    @Column(name = "addressee_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID addresseeId;
}
