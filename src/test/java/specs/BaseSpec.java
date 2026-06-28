package specs;

import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.with;
import static io.restassured.http.ContentType.JSON;

public class BaseSpec { // спецификация котороая повторяется во всех тестах

    public static RequestSpecification baseRequestSpec = with()
            .log().all()
            .contentType(JSON)
            .basePath("/api/v1");
}