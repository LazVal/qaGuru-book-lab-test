package tests;

import models.login.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;
import static tests.TestData.*;

public class LoginTest extends BaseTest {

    @Test
    @DisplayName("Успешная авторизация пользователя")
    public void successfulLoginTest() {

        LoginBodyModel loginData = new LoginBodyModel(USERNAME, PASSWORD);

        SuccessfulLoginBodyResponseModel successfulLoginBodyResponseModel = step("Отправка запроса на авторизацию", () ->
                api.auth.login(loginData)
        );

        step("Проверка токена", () -> {
            assertThat(successfulLoginBodyResponseModel.access()).startsWith(EXPECTED_TOKEN);
            assertThat(successfulLoginBodyResponseModel.access()).startsWith(EXPECTED_TOKEN);
            assertThat(successfulLoginBodyResponseModel.access()).isNotEqualTo(successfulLoginBodyResponseModel.refresh());
        });
    }

    @Test
    @DisplayName("Получение ошибки 'Invalid username or password'")
    public void InvalidLoginTest() {

        LoginBodyModel loginData = new LoginBodyModel(USERNAME, WRONG_PASSWORD);

        WrongLoginBodyResponseModel loginResponse = step("Отправка запроса на авторизацию с неверным паролем", () ->
                api.auth.loginWrong(loginData)
        );

        step("Проверка результата", () -> {
            assertThat(loginResponse.detail()).isEqualTo(INVALID_USERNAME_ERROR);
        });

    }


    @Test
    @DisplayName("Получение ошибки 'Поле не может быть пустым'")
    public void blankFieldLoginTest() {

        LoginBodyModel loginData = new LoginBodyModel(BLANK_USERNAME, BLANK_PASSWORD);

        InvalidLoginBodyResponseModel invalidLoginResponse = step("Отправка запроса на авторизацию с пустыми данными", () ->
                api.auth.loginInvalid(loginData)

        );

        String actualUsernameError = invalidLoginResponse.username().get(0);
        String actualPasswordError = invalidLoginResponse.password().get(0);

        step("Проверка результата", () -> {

            assertThat(actualUsernameError).isEqualTo(NOT_BE_BLANK_ERROR);
            assertThat(actualPasswordError).isEqualTo(NOT_BE_BLANK_ERROR);
        });

    }

    @Test
    @DisplayName("Получение ошибки '405'")
    public void NotAllowedLoginTest() {

        LoginBodyModel loginData = new LoginBodyModel(BLANK_USERNAME, BLANK_PASSWORD);
        step("Отправка запроса, ошибка 405", () ->
            api.auth.loginNotAllowed(loginData)
        );

    }
}
