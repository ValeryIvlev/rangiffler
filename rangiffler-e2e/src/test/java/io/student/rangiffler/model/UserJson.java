package io.student.rangiffler.model;

public record UserJson(
        Data data
) {
    public record Data(
            User user
    ) {}

    public record User(
            String id,
            String username,
            String firstname,
            String surname,
            String avatar,
            Location location
    ) {}

    public record Location(
            String code,
            String name
    ) {}
}
