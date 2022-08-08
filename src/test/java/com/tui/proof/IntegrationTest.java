package com.tui.proof;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tui.proof.orders.model.Address;
import com.tui.proof.orders.model.Client;
import com.tui.proof.orders.model.Order;
import com.tui.proof.persistence.repositories.AddressRepository;
import com.tui.proof.persistence.repositories.ClientRepository;
import com.tui.proof.persistence.repositories.OrderRepository;
import com.tui.proof.persistence.PersistenceAdapter;
import com.tui.proof.persistence.model.ClientEntity;
import com.tui.proof.persistence.model.OrderEntity;
import com.tui.proof.web.RequestHelper;
import com.tui.proof.web.model.CustomerOrderSearchFilter;
import com.tui.proof.web.model.OrderRequest;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.tui.proof.orders.Constants.BIG;
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
    @Autowired
    private AddressRepository addressRepository;

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

        OrderRequest request = new OrderRequest(order, client);

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
                .number(String.valueOf(value / 5))
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

        Assertions.assertThat(addressRepository.findAll()).hasSize(1);
    }

    @Test
    @Transactional
    void shouldUpdatePilotesOrderIfFiveMinutesDidNotPassSinceCreation() {

        Order order = Order.builder()
                .deliveryAddress(Address.builder()
                        .city("Milan")
                        .country("Italy")
                        .postcode("54123")
                        .street("Street")
                        .build())
                .pilotes(5)
                .build();

        Client client = Client.builder()
                .firstName("firstName")
                .lastName("lastName")
                .telephone("111222333")
                .email("mail@gmail.com")
                .build();

        OrderRequest createOrderRequest = new OrderRequest(order, client);

        // create
        Order created = given()
                .contentType(ContentType.JSON)
                .body(createOrderRequest)
                .when()
                .post(String.format(BASE_PATH + "/orders", port))
                .then()
                .extract()
                .as(new TypeRef<>() {
                });

        order.setPilotes(BIG);
        order.setDeliveryAddress(Address.builder()
                .city("Torino")
                .country("Italy")
                .postcode("54310")
                .street("Street2")
                .build());
        order.setNumber(created.getNumber());

        client.setEmail("newmail@gmail.com");
        client.setTelephone("123456000");

        OrderRequest updateOrderRequest = new OrderRequest(order, client);
        // update
        Order actual = given()
                .contentType(ContentType.JSON)
                .body(updateOrderRequest)
                .when()
                .put(String.format(BASE_PATH + "/orders/" + created.getNumber(), port))
                .then()
                .extract()
                .as(new TypeRef<>() {
                });

        Order expectedAfterUpdate = Order.builder()
                .orderTotal(BigDecimal.valueOf(19.95))
                .deliveryAddress(Address.builder()
                        .city("Torino")
                        .country("Italy")
                        .postcode("54310")
                        .street("Street2")
                        .build())
                .number(created.getNumber())
                .pilotes(BIG)
                .build();

        Assertions.assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expectedAfterUpdate);

        OrderEntity orderEntity = orderRepository.findById(Long.valueOf(created.getNumber())).get();
        ClientEntity clientEntity = clientRepository.findById(1L).get();

        Assertions.assertThat(orderEntity.getClient()).isEqualTo(clientEntity);

        ClientEntity expectedClientEntity = new ClientEntity(1L, client.getEmail(), client.getTelephone(),
                client.getFirstName(), client.getLastName(), Collections.singletonList(orderEntity));

        org.assertj.core.api.Assertions.assertThat(clientEntity)
                .usingRecursiveComparison()
                .isEqualTo(expectedClientEntity);

        Assertions.assertThat(addressRepository.findAll()).hasSize(1);
    }

    @Test
    @Transactional
    void shouldThrowWhenTryingToUpdateOrderAfterFiveMinutesSinceCreation() {
        Order order = Order.builder()
                .deliveryAddress(Address.builder()
                        .city("Milan")
                        .country("Italy")
                        .postcode("54123")
                        .street("Street")
                        .build())
                .pilotes(5)
                .build();

        Client client = Client.builder()
                .firstName("firstName")
                .lastName("lastName")
                .telephone("111222333")
                .email("mail@gmail.com")
                .build();

        OrderRequest createOrderRequest = new OrderRequest(order, client);

        // create
        Order created = given()
                .contentType(ContentType.JSON)
                .body(createOrderRequest)
                .when()
                .post(String.format(BASE_PATH + "/orders", port))
                .then()
                .extract()
                .as(new TypeRef<>() {
                });

        order.setPilotes(BIG);
        order.setDeliveryAddress(Address.builder()
                .city("Torino")
                .country("Italy")
                .postcode("54310")
                .street("Street2")
                .build());
        order.setNumber(created.getNumber());

        client.setEmail("newmail@gmail.com");
        client.setTelephone("123456000");

        OrderEntity orderEntity = orderRepository.findById(Long.valueOf(created.getNumber())).get();
        orderEntity.setTimestamp(Timestamp.valueOf(LocalDateTime.now().plus(5, ChronoUnit.MINUTES)));
        orderRepository.save(orderEntity);

        OrderRequest updateOrderRequest = new OrderRequest(order, client);
        // update
        given()
                .contentType(ContentType.JSON)
                .body(updateOrderRequest)
                .when()
                .put(String.format(BASE_PATH + "/orders/" + created.getNumber(), port))
                .then()
                .statusCode(400);
    }

    @Test
    void shouldLookForOrderDataByCustomerCustomSearch() {

        CustomerOrderSearchFilter search = CustomerOrderSearchFilter.requestBuilder()
                .email("adam@mail.com")
                .firstNameContains("Adam")
                .lastNameStartsWith("K")
                .telephoneEndsWith("33")
                .pageNumber(1)
                .pageSize(10)
                .order(com.tui.proof.web.Order.DESC)
                .sort("firstName")
                .build();

        Map<String, Object> params = new RequestHelper(new ObjectMapper()).convertRequestToParams(search);

        List<Order> actual = given()
                .contentType(ContentType.JSON)
                .params(params)
                .when()
                .get(String.format(BASE_PATH + "/orders/", port))
                .then()
                .statusCode(200)
                .extract()
                .as(new TypeRef<>() {
                });

        Assertions.assertThat(actual).isNotEmpty();
    }


}
