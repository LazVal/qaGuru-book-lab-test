package api;

import models.registration.lombok.RegistrationBodyLombokModel;
import models.registration.lombok.RegistrationResponseLombokModel;
import models.registration.lombok.WrongRegistrationResponseLombokModel;
import models.registration.records.ExistingUserResponseRecordsModel;
import models.registration.records.RegistrationBodyRecordsModel;
import models.registration.records.RegistrationResponseRecordsModel;

import static io.restassured.RestAssured.given;
import static specs.registration.RegistrationSpec.*;

public class UsersApiClient {

    public RegistrationResponseLombokModel register(RegistrationBodyLombokModel body) {
        return given(registrationRequestSpec)
                .body(body)
                .when()
                .post("/users/register/")
                .then()
                .spec(successRegistrationResponseSpec)
                .extract()// переключаемся на извлечение
                .as(RegistrationResponseLombokModel.class);// десериализуем в модель
    }

    public RegistrationResponseRecordsModel registerRecord(RegistrationBodyRecordsModel body) {
        return given(registrationRequestSpec)
                .body(body)
                .when()
                .post("/users/register/")
                .then()
                .spec(successRegistrationResponseSpec)
                .extract()
                .as(RegistrationResponseRecordsModel.class);
    }

    public ExistingUserResponseRecordsModel registerExisting(RegistrationBodyRecordsModel body) {
        return given(registrationRequestSpec)
                .body(body)
                .when()
                .post("/users/register/")
                .then()
                .log().all()
                .spec(existingUserRegistrationResponseSpec)
                .extract()
                .as(ExistingUserResponseRecordsModel.class);
    }

    public WrongRegistrationResponseLombokModel registerWrong(RegistrationBodyLombokModel body) {
        return given(registrationRequestSpec)
                .body(body)
                .when()
                .post("/users/register/")
                .then()
                .spec(existingUserRegistrationResponseSpec)
                .extract()
                .as(WrongRegistrationResponseLombokModel.class);
    }
}
