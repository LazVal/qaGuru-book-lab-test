package models.registration.lombok;

import lombok.Data;

import java.util.List;

@Data
public class WrongRegistrationResponseLombokModel {
    Integer id;
    List<String> username;
    String firstName;
    String lastName;
    String email;
    String remoteAddr;

}