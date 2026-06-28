package tests;

import models.login.*;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static specs.login.LoginSpec.*;
import static tests.TestData.*;

public class LoginTest extends BaseTest{

    @Test
    public void successfulLoginTest(){

        LoginBodyModel loginData = new LoginBodyModel(USERNAME, PASSWORD);

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
    }

    @Test
    public void InvalidLoginTest(){

        LoginBodyModel loginData = new LoginBodyModel(USERNAME, WRONG_PASSWORD);

        WrongLoginBodyResponseModel loginResponse  = given(loginRequestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(wrongLoginResponseSpec)
                .extract().as(WrongLoginBodyResponseModel.class);

        String expectedDetailError = "Invalid username or password.";
        String actualDetailError = loginResponse.detail();

        assertThat(actualDetailError).isEqualTo(expectedDetailError);

    }


    @Test
    public void blankFieldLoginTest() {

        LoginBodyModel loginData = new LoginBodyModel(BLANK_USERNAME,BLANK_PASSWORD);

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

    }
    @Test
    public void NotAllowedLoginTest() {

        LoginBodyModel loginData = new LoginBodyModel(BLANK_USERNAME,BLANK_PASSWORD);
            given()
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(notAllowedLoginResponseSpec);

    }
}
