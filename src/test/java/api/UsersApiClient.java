package api;

import models.login.LoginBodyModel;
import models.user.GetUserResponseBodyModel;
import models.user.UnauthorizedGetUserResponseBodyModel;
import models.user.UpdateUserRequestBodyModel;
import models.registration.lombok.RegistrationBodyLombokModel;
import models.registration.lombok.RegistrationResponseLombokModel;
import models.registration.lombok.WrongRegistrationResponseLombokModel;
import models.registration.records.ExistingUserResponseRecordsModel;
import models.registration.records.RegistrationBodyRecordsModel;
import models.registration.records.RegistrationResponseRecordsModel;

import static io.restassured.RestAssured.given;
import static specs.BaseSpec.baseRequestSpec;
import static specs.User.GetUserSpec.successGetUserResponseSpec;
import static specs.User.GetUserSpec.unauthorizedGetUserResponseSpec;
import static specs.login.LoginSpec.loginRequestSpec;
import static specs.login.LoginSpec.successResponseSpec;
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

    public void registerNotAllowed (RegistrationBodyLombokModel body) {
        given(registrationRequestSpec)
                .body(body)
                .when()
                .post("/users/registers")
                .then()
                .spec(negativeRegistrationResponseSpec);
    }


    public GetUserResponseBodyModel getUser (String token) {
         return given(baseRequestSpec)
                .when()
                .auth().oauth2(token)
                .get("/users/me/")
                .then()
                .spec(successGetUserResponseSpec)
                .extract()
                .as(GetUserResponseBodyModel.class);
    }

    public GetUserResponseBodyModel updateUser (UpdateUserRequestBodyModel body, String token) {
        return given(baseRequestSpec)
                .body(body)
                .when()
                .auth().oauth2(token)
                .patch("/users/me/")
                .then()
                .spec(successGetUserResponseSpec)
                .extract()
                .as(GetUserResponseBodyModel.class);
    }

    public UnauthorizedGetUserResponseBodyModel anauthUser() {
        return given(baseRequestSpec)
                .when()
                .auth().oauth2("")
                .get("/users/me/")
                .then()
                .spec(unauthorizedGetUserResponseSpec)
                .extract()
                .as(UnauthorizedGetUserResponseBodyModel.class);
    }

    public String registerAccess(LoginBodyModel body) {
        return given(loginRequestSpec)
                .body(body)
                .when()
                .post("/auth/token/")
                .then()
                .spec(successResponseSpec)
                .extract().path("access");
    }
}
