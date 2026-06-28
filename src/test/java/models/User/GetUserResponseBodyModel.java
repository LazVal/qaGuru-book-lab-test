package models.User;

public record GetUserResponseBodyModel(String id, String username, String firstName, String lastName,
                                       String email, String remoteAddr) {
}
