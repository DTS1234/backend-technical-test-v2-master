package com.tui.proof;

import com.tui.proof.orders.model.Address;
import com.tui.proof.orders.model.Client;
import com.tui.proof.orders.model.Order;
import com.tui.proof.persistence.ClientRepository;
import com.tui.proof.persistence.OrderRepository;
import com.tui.proof.persistence.PersistenceAdapter;
import com.tui.proof.persistence.model.ClientEntity;
import com.tui.proof.persistence.model.OrderEntity;
import com.tui.proof.web.model.CreateOrderRequest;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;

import static com.tui.proof.orders.Constants.PRICE;
import static io.restassured.RestAssured.given;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = {MainApplication.class, PersistenceAdapter.class, OrderRepository.class}
)
class IntegrationTest {

    public static final String BASE_PATH = "http://localhost:%s";

    @LocalServerPort
    private int port;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ClientRepository clientRepository;

    @BeforeEach
    public void setUp() {
        orderRepository.deleteAll();
        clientRepository.deleteAll();
    }

    @Transactional
    @ParameterizedTest
    @ValueSource(ints = {5, 10, 15})
    void shouldCreatePilotesWithEveryValuePossible(int value) {

        Order order = Order.builder()
                .deliveryAddress(Address.builder()
                        .city("Milan")
                        .country("Italy")
                        .postcode("54123")
                        .street("Street")
                        .build())
                .pilotes(value)
                .build();

        Client client = Client.builder()
                .firstName("firstName")
                .lastName("lastName")
                .telephone("111222333")
                .email("mail@gmail.com")
                .build();

        CreateOrderRequest request = new CreateOrderRequest(order, client);

        Order actual = given()
                .contentType(ContentType.JSON)
                .body(request)
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
                .number(String.valueOf(value/5))
                .pilotes(value)
                .build();

        Assertions.assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);

        OrderEntity savedOrder = orderRepository.findById((long) (value / 5)).get();

        Assertions.assertThat(savedOrder.getTimestamp())
                .isBeforeOrEqualTo(Timestamp.valueOf(LocalDateTime.now()));

        ClientEntity expectedClientEntity = new ClientEntity(1L, client.getEmail(), client.getTelephone(),
                client.getFirstName(), client.getLastName(), Collections.singletonList(savedOrder));

        Assertions.assertThat(savedOrder.getClient())
                .usingRecursiveComparison()
                .ignoringFields("orders")
                .isEqualTo(expectedClientEntity);
        Assertions.assertThat(savedOrder.getClient().getOrders())
                .contains(savedOrder);
    }
}
