package models.user;

public record GetUserResponseBodyModel(String id, String username, String firstName, String lastName,
                                       String email, String remoteAddr) {
}
