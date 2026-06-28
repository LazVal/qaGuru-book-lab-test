package tests;

import models.User.GetUserResponseBodyModel;
import models.User.UnauthorizedGetUserResponseBodyModel;
import models.User.UpdateUserRequestBodyModel;
import models.login.LoginBodyModel;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static specs.BaseSpec.baseRequestSpec;
import static specs.User.GetUserSpec.successGetUserResponseSpec;
import static specs.User.GetUserSpec.unauthorizedGetUserResponseSpec;
import static specs.login.LoginSpec.loginRequestSpec;
import static specs.login.LoginSpec.successResponseSpec;
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
            accessToken = given(loginRequestSpec)
                    .body(loginData)
                    .when()
                    .post("/auth/token/")
                    .then()
                    .spec(successResponseSpec)
                    .extract().path("access");
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
        step("Отправка запроса на получение информации о пользователе ", () -> {
            GetUserResponseBodyModel getUserResponseBodyModel = given(baseRequestSpec)
                    .when()
                    .auth().oauth2(accessToken)
                    .get("/users/me/")
                    .then()
                    .spec(successGetUserResponseSpec)
                    .extract()
                    .as(GetUserResponseBodyModel.class);

            assertThat(getUserResponseBodyModel.username()).isEqualTo(USERNAME);
        });
    }

    @Test
    @DisplayName("Обновление данных пользователя")
    public void successUpdateUserTest() {
        UpdateUserRequestBodyModel updateUserRequestBodyModel = new UpdateUserRequestBodyModel(FIRSTNAME, LASTNAME, EMAIL);

        step("Отправка запроса на обновление данных пользователя", () -> {
            GetUserResponseBodyModel getUserResponseBodyModel = given(baseRequestSpec)
                    .body(updateUserRequestBodyModel)
                    .when()
                    .auth().oauth2(accessToken)
                    .patch("/users/me/")
                    .then()
                    .spec(successGetUserResponseSpec)
                    .extract()
                    .as(GetUserResponseBodyModel.class);

            assertThat(getUserResponseBodyModel.username()).isEqualTo(USERNAME);
            assertThat(getUserResponseBodyModel.firstName()).isEqualTo(FIRSTNAME);
            assertThat(getUserResponseBodyModel.lastName()).isEqualTo(LASTNAME);
            assertThat(getUserResponseBodyModel.email()).isEqualTo(EMAIL);

        });

    }

    @Test
    @DisplayName("Получение ошибки Authorization")
    public void unauthorizedGetUserTest() {
        step("Отправка запроса без хэдера Authorization", () -> {
            UnauthorizedGetUserResponseBodyModel unauthorizedGetUserResponseBodyModel
                    = given(baseRequestSpec)
                    .when()
                    .auth().oauth2("")
                    .get("/users/me/")
                    .then()
                    .spec(unauthorizedGetUserResponseSpec)
                    .extract()
                    .as(UnauthorizedGetUserResponseBodyModel.class);

            String expectedError = "Authorization header must contain two space-delimited values";
            assertThat(unauthorizedGetUserResponseBodyModel.detail()).isEqualTo(expectedError);
        });
    }

}
