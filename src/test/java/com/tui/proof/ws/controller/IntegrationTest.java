package com.tui.proof.ws.controller;

import com.tui.proof.MainApplication;
import com.tui.proof.model.Address;
import com.tui.proof.model.Order;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

import static com.tui.proof.Constants.PRICE;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = {MainApplication.class}
)
class IntegrationTest {

    public static final String BASE_PATH = "http://localhost:%s";

    @LocalServerPort
    private int port;

    @ParameterizedTest
    @ValueSource(ints = {5, 10, 15})
    void shouldCreatePilotesWithEveryValuePossible(int value) {

        String number = "1";

        Order order = Order.builder()
                .deliveryAddress(Address.builder()
                        .city("")
                        .country("")
                        .postcode("")
                        .street("")
                        .build())
                .number(number)
                .pilotes(value)
                .build();

        Order actual = given()
                .contentType(ContentType.JSON)
                .body(order)
                .when()
                .post(String.format(BASE_PATH + "/orders", port))
                .then()
                .extract()
                .as(new TypeRef<>() {
                });

        BigDecimal expectedPrice = new BigDecimal(value * PRICE).setScale(2, RoundingMode.HALF_UP);

        Order expected = Order.builder()
                .deliveryAddress(Address.builder()
                        .city("Milan")
                        .country("Italy")
                        .postcode("54123")
                        .street("Street")
                        .build())
                .orderTotal(expectedPrice)
                .number(number)
                .pilotes(value)
                .build();

        Assertions.assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }
}
