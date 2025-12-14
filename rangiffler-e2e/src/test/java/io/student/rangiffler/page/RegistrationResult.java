package io.student.rangiffler.page;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RegistrationResult {
    SUCCESS("Congratulations! You've registered!"),
    NOT_SUCCESS("not");

    private final String text;
}
