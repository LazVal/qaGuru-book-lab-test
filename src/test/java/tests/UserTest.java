package tests;

import models.user.GetUserResponseBodyModel;
import models.user.UnauthorizedGetUserResponseBodyModel;
import models.user.UpdateUserRequestBodyModel;
import models.login.LoginBodyModel;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static tests.TestData.*;

public class UserTest extends BaseTest {
    private static String accessToken;
    String FIRSTNAME;
    String LASTNAME;
    String EMAIL;

    @BeforeAll
    @DisplayName("Успешная авторизация пользователя")
    public static void loginPrepareTest() {
        LoginBodyModel loginData = new LoginBodyModel(USERNAME, PASSWORD);
        step("Успешная авторизация пользователя", () -> {
            accessToken = api.users.registerAccess(loginData);
        });
    }

    @BeforeEach
    public void prepareTestData() {
        Faker faker = new Faker();
        FIRSTNAME = faker.name().firstName();
        LASTNAME = faker.name().firstName();
        EMAIL = faker.internet().emailAddress();
    }

    @Test
    @DisplayName("Получение ионформации о пользователе")
    public void successGetUserTest() {

        GetUserResponseBodyModel getUserResponseBodyModel = step("Отправка запроса на получение информации о пользователе ", () ->
                api.users.getUser(accessToken)
        );

        step("Проверка результата ", () -> {
            assertThat(getUserResponseBodyModel.username()).isEqualTo(USERNAME);
        });
    }

    @Test
    @DisplayName("Обновление данных пользователя")
    public void successUpdateUserTest() {
        UpdateUserRequestBodyModel updateUserRequestBodyModel = new UpdateUserRequestBodyModel(FIRSTNAME, LASTNAME, EMAIL);

        GetUserResponseBodyModel getUserResponseBodyModel = step("Отправка запроса на обновление данных пользователя", () ->
                api.users.updateUser(updateUserRequestBodyModel, accessToken)
        );

        step("Проверка результата", () -> {
            assertThat(getUserResponseBodyModel.username()).isEqualTo(USERNAME);
            assertThat(getUserResponseBodyModel.firstName()).isEqualTo(FIRSTNAME);
            assertThat(getUserResponseBodyModel.lastName()).isEqualTo(LASTNAME);
            assertThat(getUserResponseBodyModel.email()).isEqualTo(EMAIL);

        });

    }

    @Test
    @DisplayName("Получение ошибки Authorization")
    public void unauthorizedGetUserTest() {

        UnauthorizedGetUserResponseBodyModel unauthorizedGetUserResponseBodyModel
                = step("Отправка запроса без хэдера Authorization", () -> api.users.anauthUser()
        );
        step("Отправка запроса без хэдера Authorization", () -> {
            assertThat(unauthorizedGetUserResponseBodyModel.detail()).isEqualTo(AUTHORIZATION_ERROR);
        });
    }

}
