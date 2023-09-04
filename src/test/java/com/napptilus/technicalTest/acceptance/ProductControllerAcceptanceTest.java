package com.napptilus.technicalTest.acceptance;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

class ProductControllerAcceptanceTest {

    private static final String HOST = "http://localhost:5000";

    @Test()
    void getRelatedProductsNotFound() {
        when().get(HOST + "/product/33/similar")
                .then()
                .statusCode(404);
    }

    @Test()
    void getRelatedProductsOk() {
        when().get(HOST + "/product/1/similar")
                .then()
                .statusCode(200)
                .body("", hasSize(3))
                .body("[0].id", equalTo(2))
                .body("[1].id", equalTo(4))
                .body("[2].id", equalTo(3));
    }
}
