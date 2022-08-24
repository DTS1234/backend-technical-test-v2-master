package com.tui.proof;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tui.proof.orders.model.Address;
import com.tui.proof.orders.model.Client;
import com.tui.proof.orders.model.Order;
import com.tui.proof.persistence.PersistenceAdapter;
import com.tui.proof.persistence.model.ClientEntity;
import com.tui.proof.persistence.model.OrderEntity;
import com.tui.proof.persistence.repositories.AddressRepository;
import com.tui.proof.persistence.repositories.ClientRepository;
import com.tui.proof.persistence.repositories.OrderRepository;
import com.tui.proof.persistence.repositories.UserRepository;
import com.tui.proof.web.RequestHelper;
import com.tui.proof.web.model.CustomerOrderSearchFilter;
import com.tui.proof.web.model.OrderRequest;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.tui.proof.orders.PilotesConstants.BIG;
import static com.tui.proof.orders.PilotesConstants.PRICE;
import static io.restassured.RestAssured.given;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = {MainApplication.class, PersistenceAdapter.class, OrderRepository.class}
)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
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
        RestAssured.defaultParser = Parser.JSON;
        Order actual = given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(String.format(BASE_PATH + "/orders", port))
                .then()
                .statusCode(200)
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
                .number(actual.getNumber())
                .pilotes(value)
                .build();

        Assertions.assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);

        OrderEntity savedOrder = orderRepository.findById(Long.valueOf(actual.getNumber())).orElse(null);

        assert savedOrder != null;
        Assertions.assertThat(savedOrder.getTimestamp())
                .isBeforeOrEqualTo(Timestamp.valueOf(LocalDateTime.now()));

        ClientEntity expectedClientEntity = new ClientEntity(1L, client.getEmail(), client.getTelephone(),
                client.getFirstName(), client.getLastName(), Collections.singletonList(savedOrder));

        Assertions.assertThat(savedOrder.getClient())
                .usingRecursiveComparison()
                .ignoringFields("orders")
                .ignoringFields("id")
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
                .statusCode(200)
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

        OrderEntity orderEntity = orderRepository.findById(Long.valueOf(created.getNumber())).orElse(null);
        ClientEntity clientEntity = clientRepository.findById(1L).orElse(null);

        assert orderEntity != null;
        Assertions.assertThat(orderEntity.getClient()).isEqualTo(clientEntity);

        ClientEntity expectedClientEntity = new ClientEntity(1L, client.getEmail(), client.getTelephone(),
                client.getFirstName(), client.getLastName(), Collections.singletonList(orderEntity));

        org.assertj.core.api.Assertions.assertThat(clientEntity)
                .usingRecursiveComparison()
                .isEqualTo(expectedClientEntity);

        Assertions.assertThat(addressRepository.findAll()).hasSize(1);
    }

    @Test
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

        // modify timestamp for test
        OrderEntity orderEntity = orderRepository.findById(Long.valueOf(created.getNumber())).orElseThrow();
        orderEntity.setTimestamp(Timestamp.valueOf(LocalDateTime.now().minus(5, ChronoUnit.MINUTES)));
        OrderEntity saved = orderRepository.save(orderEntity);
        orderRepository.flush();

        OrderRequest updateOrderRequest = new OrderRequest(order, client);

        // update
        given()
                .contentType(ContentType.JSON)
                .body(updateOrderRequest)
                .when()
                .put(String.format(BASE_PATH + "/orders/" + saved.getId(), port))
                .then()
                .statusCode(400);
    }

    @Test
    @Sql(value = "customSearchData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void shouldLookForOrderDataByCustomerCustomSearch() {

        val search = CustomerOrderSearchFilter.requestBuilder()
                .emailEndsWith(".com")
                .firstNameContains("Adam")
                .lastNameStartsWith("K")
                .telephoneEndsWith("33")
                .pageNumber(0)
                .pageSize(10)
                .sort("firstName")
                .order(com.tui.proof.web.Order.DESC)
                .build();


        Map<String, Object> params = new RequestHelper(new ObjectMapper()).convertRequestToParams(search);

        List<Client> actual = given()
                .contentType(ContentType.JSON)
                .params(params)
                .auth().basic("user", "pass123")
                .when()
                .get(String.format(BASE_PATH + "/orders/", port))
                .then()
                .extract()
                .as(new TypeRef<>() {
                });

        Assertions.assertThat(actual).hasSize(2)
                .containsExactly(
                        Client.builder()
                                .email("adam@gmail.com")
                                .telephone("733 777 133")
                                .firstName("AdamZ")
                                .lastName("Ksecond")
                                .orders(Collections.emptyList())
                                .build(),
                        Client.builder()
                                .email("adam@mail.com")
                                .telephone("733 666 133")
                                .firstName("Adam")
                                .lastName("Klast")
                                .orders(Collections.emptyList())
                                .build()
                );
    }


}
