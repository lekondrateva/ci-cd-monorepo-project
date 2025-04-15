package com.myapp;

import io.qameta.allure.*;
import io.restassured.RestAssured;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.equalTo;

@Epic("Health check")
@Feature("Basic availability")
public class ApplicationTest {

    @Test
    @DisplayName("Check that application is running")
    @Description("Calls /actuator/health and expects 200 OK")
    @Severity(SeverityLevel.CRITICAL)
    public void healthEndpointTest() {
        RestAssured.get("http://my-app-container:8080/actuator/health")
                .then()
                .statusCode(200)
                .body(equalTo("Application is running!"));
    }

}