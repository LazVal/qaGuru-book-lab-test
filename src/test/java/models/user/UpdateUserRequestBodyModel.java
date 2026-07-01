package models.user;

public record UpdateUserRequestBodyModel(String firstName, String lastName,
                                         String email) {
}
