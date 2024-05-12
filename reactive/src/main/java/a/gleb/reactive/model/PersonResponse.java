package a.gleb.reactive.model;

import a.gleb.reactive.db.entity.PersonEntity;

import java.util.UUID;

public record PersonResponse(
        UUID id,
        String login,
        String firstName,
        String lastName,
        String phone
) {
    public static PersonResponse fromEntity(PersonEntity entity) {
        return new PersonResponse(
                entity.getId(),
                entity.getLogin(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getPhone()
        );
    }
}
