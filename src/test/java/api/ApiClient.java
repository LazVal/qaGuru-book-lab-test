package api;

import models.login.LoginBodyModel;

import static io.restassured.RestAssured.given;
import static specs.login.LoginSpec.loginRequestSpec;
import static specs.login.LoginSpec.successResponseSpec;

/**
 * Общий API-клиент — единая точка доступа к клиентам эндпоинтов.
 */
public class ApiClient {
    public final UsersApiClient users = new UsersApiClient();
    public final AuthApiClient auth = new AuthApiClient();
    public final LogoutApiClient logout = new LogoutApiClient();

    public String register(LoginBodyModel body) {
        return given(loginRequestSpec)
                .body(body)
                .when()
                .post("/auth/token/")
                .then()
                .spec(successResponseSpec)
                .extract().path("access");
    }
}
