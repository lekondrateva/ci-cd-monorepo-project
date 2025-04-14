package com.myapp;

import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.get;
import static org.hamcrest.Matchers.equalTo;

public class ApplicationTest {

    @Test
    void healthEndpointTest() {
        get("http://localhost:8080/health")
                .then()
                .statusCode(200)
                .body(equalTo("OK"));
    }
}