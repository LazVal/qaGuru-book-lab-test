package models.registration.lombok;

import lombok.Data;

@Data
//@AllArgsConstructor - для работы с конструктором
//@NoArgsConstructor - чтобы работал и конструктор и без конструктора
public class RegistrationBodyLombokModel {
    String username;
    String password;


}