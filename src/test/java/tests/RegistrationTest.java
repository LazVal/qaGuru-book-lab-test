package tests;

import models.registration.lombok.RegistrationBodyLombokModel;
import models.registration.lombok.RegistrationResponseLombokModel;
import models.registration.lombok.WrongRegistrationResponseLombokModel;
import models.registration.pojo.RegistrationBodyPojoModel;
import models.registration.pojo.RegistrationBodyResponsePojoModel;
import models.registration.records.ExistingUserResponseRecordsModel;
import models.registration.records.RegistrationBodyRecordsModel;
import models.registration.records.RegistrationResponseRecordsModel;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static specs.registration.RegistrationSpec.*;

public class RegistrationTest extends BaseTest {

    String USERNAME;
    String PASSWORD;

    @BeforeEach
    public void prepareTestData() {
        Faker faker = new Faker();
        USERNAME = faker.name().firstName();
        PASSWORD = faker.name().firstName();
    }

    @Test
    @Disabled
    public void successfulRegistrationTest_bad_practice() {
        String data = "{\"USERNAME\": \"" + USERNAME + "\",\"password\": \"" + PASSWORD + "\"}";

        given()
                .log().all()
                .contentType(JSON)
//                .header("content-type", ContentType.JSON)
                .body(data)
                .when()
                .post("/users/register/")
                .then()
                .log().all()
                .statusCode(201)
                .body("username", is(USERNAME))
                .body("id", notNullValue());
    }

    @Test
    @Disabled
    public void successfulRegistrationTest_with_pojo() {
        RegistrationBodyPojoModel data = new RegistrationBodyPojoModel();
        data.setUsername(USERNAME);
        data.setPassword(PASSWORD);

//      RegistrationBodyPojoModel data = new RegistrationBodyPojoModel(USERNAME, password);

        RegistrationBodyResponsePojoModel registrationResponse = given(registrationRequestSpec) //проверка ответа через модель
                .body(data)
                .when()
                .post("/users/register/")
                .then()
                .log().all()
                .statusCode(201)
                .extract()//проверка ответа через модель
                .as(RegistrationBodyResponsePojoModel.class);//проверка ответа через модель

        assertEquals(USERNAME, registrationResponse.getUsername());
    }

    @Test
    @DisplayName("Успешная регистрация пользователя")
    public void successfulRegistrationTest_with_lombok() {
        RegistrationBodyLombokModel data = new RegistrationBodyLombokModel();
        data.setUsername(USERNAME);
        data.setPassword(PASSWORD);

//      RegistrationBodyLombokModel data = new RegistrationBodyLombokModel(USERNAME, password);
        step("Отправка запроса Registration и проверка ответа (200) ", () -> {
            RegistrationResponseLombokModel registrationResponse = given(registrationRequestSpec)
                    .body(data)
                    .when()
                    .post("/users/register/")
                    .then()
                    .spec(successRegistrationResponseSpec)
                    .extract()// переключаемся на извлечение
                    .as(RegistrationResponseLombokModel.class);// десериализуем в модель


            assertEquals(USERNAME, registrationResponse.getUsername());
        });
    }

    @Test
    @Disabled
    public void successfulRegistrationTest_with_records() {
        RegistrationBodyRecordsModel data = new RegistrationBodyRecordsModel(USERNAME, PASSWORD);

        RegistrationResponseRecordsModel registrationResponse = given(registrationRequestSpec)
                .body(data)
                .when()
                .post("/users/register/")
                .then()
                .log().all()
                .statusCode(201)
                .body(matchesJsonSchemaInClasspath(
                        "schemas/registration/successful_registration_response_schema.json"))
                .body("username", notNullValue())
                .body("id", notNullValue())
                .body("remoteAddr", notNullValue())
                .extract()
                .as(RegistrationResponseRecordsModel.class);

        String actualUserName = registrationResponse.username();

        assertThat(registrationResponse.id()).isGreaterThan(0);
        assertThat(actualUserName).isEqualTo(USERNAME);
        assertThat(registrationResponse.firstName()).isEqualTo("");
        assertThat(registrationResponse.lastName()).isEqualTo("");
        assertThat(registrationResponse.email()).isEqualTo("");
    }

    @Test
    @DisplayName("Получение ошибки 'Пользователь уже существует'")
    public void existingUserTest() {
        RegistrationBodyRecordsModel data = new RegistrationBodyRecordsModel(USERNAME, PASSWORD);

        step("Отправка запроса Registration и проверка ответа (200) ", () -> {
            RegistrationResponseRecordsModel firstRegistrationResponse = given(registrationRequestSpec)
                    .body(data)
                    .when()
                    .post("/users/register/")
                    .then()
                    .spec(successRegistrationResponseSpec)
                    .extract()
                    .as(RegistrationResponseRecordsModel.class);

            assertThat(firstRegistrationResponse.username()).isEqualTo(USERNAME);
        });

        step("Повторная отправка запроса Registration и проверка получения ошибки", () -> {
            ExistingUserResponseRecordsModel secondRegistrationResponse = given(registrationRequestSpec)
                    .body(data)
                    .when()
                    .post("/users/register/")
                    .then()
                    .log().all()
                    .spec(existingUserRegistrationResponseSpec)
                    .extract()
                    .as(ExistingUserResponseRecordsModel.class);

            String expectedError = "A user with that username already exists.";
            assertThat(secondRegistrationResponse.username().get(0)).isEqualTo(expectedError);
        });
    }

    @Test
    @DisplayName("Получение ошибки 'Поле не может быть пустым'")
    public void invalidUsername400Test() {
        RegistrationBodyLombokModel data = new RegistrationBodyLombokModel();
        data.setUsername("");
        data.setPassword(PASSWORD);

        step("Отправка запроса Registration с пустым Username", () -> {
            WrongRegistrationResponseLombokModel wrongRegistrationResponseLombokModel = given(registrationRequestSpec)
                    .body(data)
                    .when()
                    .post("/users/register/")
                    .then()
                    .spec(existingUserRegistrationResponseSpec)
                    .extract()
                    .as(WrongRegistrationResponseLombokModel.class);


            String expectedError = "This field may not be blank.";
            assertThat(wrongRegistrationResponseLombokModel.getUsername().get(0)).isEqualTo(expectedError);
        });
    }

    @Test
    @DisplayName("Получение ошибки 404 not Found")
    public void negativeRegistration404Test() {
        RegistrationBodyLombokModel data = new RegistrationBodyLombokModel();
        data.setUsername(USERNAME);
        data.setPassword(PASSWORD);

        step("Отправка запроса Registration на неверный endPoint", () -> {
            given(registrationRequestSpec)
                    .body(data)
                    .when()
                    .post("/users/registers")
                    .then()
                    .spec(negativeRegistrationResponseSpec);
        });
    }
}
