package tests;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import api.ApiClient;
public class BaseTest {

    protected static final ApiClient api = new ApiClient();

    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = "https://book-club.qa.guru";
    }
}

