package tests;

import models.login.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static specs.login.LoginSpec.*;
import static tests.TestData.*;

public class LoginTest extends BaseTest {

    @Test
    @DisplayName("Успешная авторизация пользователя")
    public void successfulLoginTest() {

        LoginBodyModel loginData = new LoginBodyModel(USERNAME, PASSWORD);

        step("Отправка запроса на авторизацию", () -> {
            SuccessfulLoginBodyResponseModel successfulLoginBodyResponseModel = given(loginRequestSpec)
                    .body(loginData)
                    .when()
                    .post("/auth/token/")
                    .then()
                    .spec(successResponseSpec)
                    .extract().as(SuccessfulLoginBodyResponseModel.class);

            String expectedTokenPath = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";
            String actualAccess = successfulLoginBodyResponseModel.access();
            String actualRefresh = successfulLoginBodyResponseModel.refresh();

            assertThat(actualAccess).startsWith(expectedTokenPath);
            assertThat(actualAccess).startsWith(expectedTokenPath);
            assertThat(actualAccess).isNotEqualTo(actualRefresh);
        });
    }

    @Test
    @DisplayName("Получение ошибки 'Invalid username or password'")
    public void InvalidLoginTest() {

        LoginBodyModel loginData = new LoginBodyModel(USERNAME, WRONG_PASSWORD);

        step("Отправка запроса на авторизацию с неверным паролем", () -> {
            WrongLoginBodyResponseModel loginResponse = given(loginRequestSpec)
                    .body(loginData)
                    .when()
                    .post("/auth/token/")
                    .then()
                    .spec(wrongLoginResponseSpec)
                    .extract().as(WrongLoginBodyResponseModel.class);

            String expectedDetailError = "Invalid username or password.";
            String actualDetailError = loginResponse.detail();

            assertThat(actualDetailError).isEqualTo(expectedDetailError);
        });

    }


    @Test
    @DisplayName("Получение ошибки 'Поле не может быть пустым'")
    public void blankFieldLoginTest() {

        LoginBodyModel loginData = new LoginBodyModel(BLANK_USERNAME, BLANK_PASSWORD);

        step("Отправка запроса на авторизацию с пустыми данными", () -> {
            InvalidLoginBodyResponseModel invalidLoginResponse = given(loginRequestSpec)
                    .body(loginData)
                    .when()
                    .post("/auth/token/")
                    .then()
                    .spec(invalidLoginResponseSpec)
                    .extract().as(InvalidLoginBodyResponseModel.class);

            String expectedError = "This field may not be blank.";
            String actualUsernameError = invalidLoginResponse.username().get(0);
            String actualPasswordError = invalidLoginResponse.password().get(0);

            assertThat(actualUsernameError).isEqualTo(expectedError);
            assertThat(actualPasswordError).isEqualTo(expectedError);
        });

    }

    @Test
    @DisplayName("Получение ошибки '405'")
    public void NotAllowedLoginTest() {

        LoginBodyModel loginData = new LoginBodyModel(BLANK_USERNAME, BLANK_PASSWORD);
        step("Отправка запроса, ошибка 405", () -> {
            given()
                    .body(loginData)
                    .when()
                    .post("/auth/token/")
                    .then()
                    .spec(notAllowedLoginResponseSpec);
        });

    }
}
