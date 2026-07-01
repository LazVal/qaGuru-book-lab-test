package api;

import models.login.InvalidLoginBodyResponseModel;
import models.login.LoginBodyModel;
import models.login.SuccessfulLoginBodyResponseModel;
import models.login.WrongLoginBodyResponseModel;

import static io.restassured.RestAssured.given;
import static specs.login.LoginSpec.*;

public class AuthApiClient {
    public SuccessfulLoginBodyResponseModel login(LoginBodyModel body) {
        return given(loginRequestSpec)
                .body(body)
                .when()
                .post("/auth/token/")
                .then()
                .spec(successResponseSpec)
                .extract().as(SuccessfulLoginBodyResponseModel.class);
    }

    public WrongLoginBodyResponseModel loginWrong(LoginBodyModel body) {
        return given(loginRequestSpec)
                .body(body)
                .when()
                .post("/auth/token/")
                .then()
                .spec(wrongLoginResponseSpec)
                .extract().as(WrongLoginBodyResponseModel.class);
    }

    public InvalidLoginBodyResponseModel loginInvalid(LoginBodyModel body) {
        return given(loginRequestSpec)
                .body(body)
                .when()
                .post("/auth/token/")
                .then()
                .spec(invalidLoginResponseSpec)
                .extract().as(InvalidLoginBodyResponseModel.class);
    }

    public void loginNotAllowed(LoginBodyModel body) {
        given()
                .body(body)
                .when()
                .post("/auth/token/")
                .then()
                .spec(notAllowedLoginResponseSpec);
    }
}
