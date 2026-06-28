package tests;

import models.login.LoginBodyModel;
import models.logout.BadRequestLogoutBodyResponseModel;
import models.logout.LogoutBodyModel;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

import static org.assertj.core.api.Assertions.assertThat;
import static specs.login.LoginSpec.loginRequestSpec;
import static specs.login.LoginSpec.successResponseSpec;
import static specs.logout.LogoutSpec.*;
import static tests.TestData.PASSWORD;
import static tests.TestData.USERNAME;


public class LogoutTest extends BaseTest{

    @Test
    public void successLogoutTest() {
        LoginBodyModel loginData = new LoginBodyModel(USERNAME, PASSWORD);

        String refreshToken = given(loginRequestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(successResponseSpec)
                .extract().path("refresh");//извлекает из полученного JSON-ответа значение поля "refresh"


        LogoutBodyModel logoutData = new LogoutBodyModel(refreshToken);

        given(logoutRequestSpec)
                .body(logoutData)
                .when()
                .post("/auth/logout/")
                .then()
                .spec(successLogoutResponseSpec);


    }

    @Test
    public void RefreshTokenIsNullTest() {
        LogoutBodyModel logoutData = new LogoutBodyModel("");

        BadRequestLogoutBodyResponseModel badRequestLogoutBodyResponseModel = given(logoutRequestSpec)
                .body(logoutData)
                .when()
                .post("/auth/logout/")
                .then()
                .spec(isNullLogoutResponseSpec)
                .extract()
                .as(BadRequestLogoutBodyResponseModel.class);

        String expectedError = "This field may not be blank.";
        assertThat(badRequestLogoutBodyResponseModel.value().get(0)).isEqualTo(expectedError);
    }
}
