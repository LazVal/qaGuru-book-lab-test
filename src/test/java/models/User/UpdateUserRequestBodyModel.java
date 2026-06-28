package models.User;

public record UpdateUserRequestBodyModel(String firstName, String lastName,
                                         String email) {
}
