package tests;

import models.login.LoginBodyModel;
import models.logout.BadRequestLogoutBodyResponseModel;
import models.logout.LogoutBodyModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;

import static org.assertj.core.api.Assertions.assertThat;
import static specs.login.LoginSpec.loginRequestSpec;
import static specs.login.LoginSpec.successResponseSpec;
import static specs.logout.LogoutSpec.*;
import static tests.TestData.PASSWORD;
import static tests.TestData.USERNAME;


public class LogoutTest extends BaseTest {

    @Test
    @DisplayName("Успешный выход пользователя")
    public void successLogoutTest() {
        LoginBodyModel loginData = new LoginBodyModel(USERNAME, PASSWORD);

        String refreshToken = step("Авторизация и получение токена", () ->
                given(loginRequestSpec)
                        .body(loginData)
                        .when()
                        .post("/auth/token/")
                        .then()
                        .spec(successResponseSpec)
                        .extract().path("refresh"));//извлекает из полученного JSON-ответа значение поля "refresh"


        step("Отправка запроса logout и проверка ответа (200) ", () -> {

            LogoutBodyModel logoutData = new LogoutBodyModel(refreshToken);
            given(logoutRequestSpec)
                    .body(logoutData)
                    .when()
                    .post("/auth/logout/")
                    .then()
                    .spec(successLogoutResponseSpec);

        });
    }

    @Test
    @DisplayName("Ошибка 'поле не может быть пустым'")
    public void RefreshTokenIsNullTest() {
        LogoutBodyModel logoutData = new LogoutBodyModel("");

        step("Отправка запроса logout с пустым RefreshToken", () -> {
            BadRequestLogoutBodyResponseModel badRequestLogoutBodyResponseModel = given(logoutRequestSpec)
                    .body(logoutData)
                    .when()
                    .post("/auth/logout/")
                    .then()
                    .spec(isNullLogoutResponseSpec)
                    .extract()
                    .as(BadRequestLogoutBodyResponseModel.class);

            String expectedError = "This field may not be blank.";
            assertThat(badRequestLogoutBodyResponseModel.refresh().get(0)).isEqualTo(expectedError);
        });
    }
}
