package tests;

import models.login.LoginBodyModel;
import models.logout.BadRequestLogoutBodyResponseModel;
import models.logout.LogoutBodyModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;

import static org.assertj.core.api.Assertions.assertThat;
import static tests.TestData.*;


public class LogoutTest extends BaseTest {

    @Test
    @DisplayName("Успешный выход пользователя")
    public void successLogoutTest() {
        LoginBodyModel loginData = new LoginBodyModel(USERNAME, PASSWORD);

        String refreshToken = step("Авторизация и получение токена", () ->
                api.logout.registerRefresh(loginData));


        step("Отправка запроса logout и проверка ответа (200) ", () -> {

            LogoutBodyModel logoutData = new LogoutBodyModel(refreshToken);
            api.logout.logout(logoutData);

        });
    }

    @Test
    @DisplayName("Ошибка 'поле не может быть пустым'")
    public void RefreshTokenIsNullTest() {
        LogoutBodyModel logoutData = new LogoutBodyModel("");

        BadRequestLogoutBodyResponseModel badRequestLogoutBodyResponseModel = step("Отправка запроса logout с пустым RefreshToken", () ->
                api.logout.logoutBad(logoutData)
        );

        step("Проверка результата", () ->
                assertThat(badRequestLogoutBodyResponseModel.refresh().get(0)).isEqualTo(NOT_BE_BLANK_ERROR)
        );
    }
}
