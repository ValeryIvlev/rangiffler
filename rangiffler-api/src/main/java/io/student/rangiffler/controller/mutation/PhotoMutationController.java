package io.student.rangiffler.controller.mutation;

import io.student.rangiffler.entity.UserEntity;
import io.student.rangiffler.model.Photo;
import io.student.rangiffler.model.PhotoInput;
import io.student.rangiffler.repository.UserRepository;
import io.student.rangiffler.service.PhotoService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
@PreAuthorize("isAuthenticated()")
public class PhotoMutationController {

    private final PhotoService photoService;
    private final UserRepository userRepository;

    public PhotoMutationController(PhotoService photoService, UserRepository userRepository) {
        this.photoService = photoService;
        this.userRepository = userRepository;
    }

    @MutationMapping
    public Photo photo(@AuthenticationPrincipal Jwt principal,
                       @Argument PhotoInput input) {

        String username = principal.getSubject();

        UserEntity user = userRepository.findByUsername(username)
                .orElseGet(() -> userRepository.save(
                        UserEntity.builder()
                                .id(UUID.randomUUID())
                                .username(username)
                                .build()
                ));

        return photoService.createPhoto(user.getId(), input);
    }

    @MutationMapping
    public void deletePhoto(@AuthenticationPrincipal Jwt principal,
                            @Argument UUID id) {

        String username = principal.getSubject();

        UserEntity user = userRepository.findByUsername(username)
                .orElseGet(() -> userRepository.save(
                        UserEntity.builder()
                                .id(UUID.randomUUID())
                                .username(username)
                                .build()
                ));
        photoService.deletePhoto(user.getId(), id);
    }
}
