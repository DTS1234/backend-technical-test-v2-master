package com.tui.proof.web.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.tui.proof.orders.OrderService;
import com.tui.proof.orders.model.Address;
import com.tui.proof.orders.model.Client;
import com.tui.proof.orders.model.Order;
import com.tui.proof.web.model.OrderRequest;
import com.tui.proof.web.model.ValidationErrorResponse;
import com.tui.proof.web.model.Violation;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Collections;

import static com.tui.proof.orders.PilotesConstants.SMALL;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * @author akazmierczak
 * @created 07.08.2022
 */
class OrdersControllerTest {

    private MockMvc mockMvc;

    @Mock
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(new OrdersController(orderService)).setControllerAdvice(new ErrorHandlingController()).build();
    }

    @Test
    void shouldThrowWhenAddressIsNull() throws Exception {
        // given
        Order order = Order.builder()
                .deliveryAddress(null)
                .number("123")
                .pilotes(SMALL)
                .orderTotal(BigDecimal.TEN)
                .build();

        Client client = Client.builder()
                .firstName("firstName")
                .lastName("lastName")
                .email("email@mail.com")
                .telephone("123456789")
                .build();

        OrderRequest request = new OrderRequest(order, client);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/orders")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapToJson(request)))
                .andExpect(status().isBadRequest())
                .andReturn();

        // then
        ValidationErrorResponse actual = mapToError(mvcResult);
        Assertions.assertThat(actual.violations)
                .contains(new Violation("order.deliveryAddress", "Delivery address is required!"));
    }

    @Test
    void shouldThrowWhenPilotesValueIsZero() throws Exception {
        // given
        Order order = Order.builder()
                .deliveryAddress(Address.builder()
                        .city("city")
                        .country("country")
                        .postcode("12345")
                        .street("street")
                        .build())
                .number("123")
                .orderTotal(BigDecimal.TEN)
                .build();

        Client client = Client.builder()
                .firstName("firstName")
                .lastName("lastName")
                .email("email@mail.com")
                .telephone("123456789")
                .build();

        OrderRequest request = new OrderRequest(order, client);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/orders")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapToJson(request)))
                .andExpect(status().isBadRequest())
                .andReturn();

        // then
        ValidationErrorResponse actual = mapToError(mvcResult);
        Assertions.assertThat(actual.violations)
                .hasSize(1)
                .isEqualTo(Collections.singletonList(new Violation("order.pilotes", "Number of pilotes have to be equal 5, 10 or 15")));
    }

    @Test
    void shouldThrowWhenOrderTotalIsNegative() throws Exception {
        // given
        Order order = Order.builder()
                .deliveryAddress(Address.builder()
                        .city("city")
                        .country("country")
                        .postcode("12345")
                        .street("street")
                        .build())
                .number("123")
                .orderTotal(BigDecimal.valueOf(-10L))
                .pilotes(5)
                .build();

        Client client = Client.builder()
                .firstName("firstName")
                .lastName("lastName")
                .email("email@mail.com")
                .telephone("123456789")
                .build();

        OrderRequest request = new OrderRequest(order, client);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/orders")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapToJson(request)))
                .andExpect(status().isBadRequest())
                .andReturn();

        // then
        ValidationErrorResponse actual = mapToError(mvcResult);
        Assertions.assertThat(actual.violations)
                .hasSize(1)
                .isEqualTo(Collections.singletonList(new Violation("order.orderTotal", "Order total cannot be a negative value.")));
    }

    @ParameterizedTest
    @ValueSource(strings = {"1s345", "postC", "=-!@^"})
    void shouldThrowWhenAddressPostCodeIsNotOnlyConsistingOfNumbers(String wrongPostCode) throws Exception {
        // given
        Order order = Order.builder()
                .deliveryAddress(Address.builder()
                        .city("city")
                        .country("country")
                        .postcode(wrongPostCode)
                        .street("street")
                        .build())
                .number("123")
                .orderTotal(BigDecimal.valueOf(10L))
                .pilotes(5)
                .build();

        Client client = Client.builder()
                .firstName("firstName")
                .lastName("lastName")
                .email("email@mail.com")
                .telephone("123456789")
                .build();

        OrderRequest request = new OrderRequest(order, client);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/orders")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapToJson(request)))
                .andExpect(status().isBadRequest())
                .andReturn();

        // then
        ValidationErrorResponse actual = mapToError(mvcResult);
        Assertions.assertThat(actual.violations)
                .hasSize(1)
                .isEqualTo(Collections.singletonList(new Violation("order.deliveryAddress.postcode", "Postcode should consist of 5 numbers.")));
    }

    @ParameterizedTest
    @ValueSource(strings = {"123456", "1234", "321", "1", "999999"})
    void shouldThrowWhenAddressPostCodeLengthIsNot5(String wrongPostCode) throws Exception {
        // given
        Order order = Order.builder()
                .deliveryAddress(Address.builder()
                        .city("city")
                        .country("country")
                        .postcode(wrongPostCode)
                        .street("street")
                        .build())
                .number("123")
                .orderTotal(BigDecimal.valueOf(10L))
                .pilotes(5)
                .build();

        Client client = Client.builder()
                .firstName("firstName")
                .lastName("lastName")
                .email("email@mail.com")
                .telephone("123456789")
                .build();

        OrderRequest request = new OrderRequest(order, client);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/orders")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapToJson(request)))
                .andExpect(status().isBadRequest())
                .andReturn();

        // then
        ValidationErrorResponse actual = mapToError(mvcResult);
        Assertions.assertThat(actual.violations)
                .hasSize(1)
                .isEqualTo(Collections.singletonList(new Violation("order.deliveryAddress.postcode", "Postcode should consist of 5 numbers.")));
    }


    @ParameterizedTest
    @ValueSource(strings = {"", "  "})
    void shouldThrowWhenAddressCityIsInvalid(String wrongCityValue) throws Exception {
        // given
        Order order = Order.builder()
                .deliveryAddress(Address.builder()
                        .city(wrongCityValue)
                        .country("country")
                        .postcode("12345")
                        .street("street")
                        .build())
                .number("123")
                .orderTotal(BigDecimal.valueOf(10L))
                .pilotes(5)
                .build();

        Client client = Client.builder()
                .firstName("firstName")
                .lastName("lastName")
                .email("email@mail.com")
                .telephone("123456789")
                .build();

        OrderRequest request = new OrderRequest(order, client);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/orders")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapToJson(request)))
                .andExpect(status().isBadRequest())
                .andReturn();

        // then
        ValidationErrorResponse actual = mapToError(mvcResult);
        Assertions.assertThat(actual.violations)
                .hasSize(2)
                .containsExactlyInAnyOrder(new Violation("order.deliveryAddress.city", "City value is required!"),
                        new Violation("order.deliveryAddress.city", "City value is invalid!"));
    }

    @Test
    void shouldThrowWhenAddressStreetIsEmpty() throws Exception {
        // given
        Order order = Order.builder()
                .deliveryAddress(Address.builder()
                        .city("city")
                        .country("country")
                        .postcode("12345")
                        .street("")
                        .build())
                .number("123")
                .orderTotal(BigDecimal.valueOf(10L))
                .pilotes(5)
                .build();

        Client client = Client.builder()
                .firstName("firstName")
                .lastName("lastName")
                .email("email@mail.com")
                .telephone("123456789")
                .build();

        OrderRequest request = new OrderRequest(order, client);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/orders")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapToJson(request)))
                .andExpect(status().isBadRequest())
                .andReturn();

        // then
        ValidationErrorResponse actual = mapToError(mvcResult);
        Assertions.assertThat(actual.violations)
                .hasSize(1)
                .containsExactlyInAnyOrder(new Violation("order.deliveryAddress.street", "Street value is required!"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "  "})
    void shouldThrowWhenAddressCountryIsInvalid(String wrongCountryValue) throws Exception {
        // given
        Order order = Order.builder()
                .deliveryAddress(Address.builder()
                        .city("city")
                        .country(wrongCountryValue)
                        .postcode("12345")
                        .street("street")
                        .build())
                .number("123")
                .orderTotal(BigDecimal.valueOf(10L))
                .pilotes(5)
                .build();

        Client client = Client.builder()
                .firstName("firstName")
                .lastName("lastName")
                .email("email@mail.com")
                .telephone("123456789")
                .build();

        OrderRequest request = new OrderRequest(order, client);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/orders")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapToJson(request)))
                .andExpect(status().isBadRequest())
                .andReturn();

        // then
        ValidationErrorResponse actual = mapToError(mvcResult);
        Assertions.assertThat(actual.violations)
                .hasSize(2)
                .containsExactlyInAnyOrder(new Violation("order.deliveryAddress.country", "Country value is required!"),
                        new Violation("order.deliveryAddress.country", "Country value is invalid!"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"  ", ""})
    void shouldThrowWhenClientNameIsLacking(String emptyName) throws Exception {
        // given
        Order order = Order.builder()
                .deliveryAddress(Address.builder()
                        .city("city")
                        .country("Country")
                        .postcode("12345")
                        .street("street")
                        .build())
                .number("123")
                .orderTotal(BigDecimal.valueOf(10L))
                .pilotes(5)
                .build();

        Client client = Client.builder()
                .firstName(emptyName)
                .lastName(emptyName)
                .email("email@mail.com")
                .telephone("123456789")
                .build();

        OrderRequest request = new OrderRequest(order, client);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/orders")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapToJson(request)))
                .andExpect(status().isBadRequest())
                .andReturn();

        // then
        ValidationErrorResponse actual = mapToError(mvcResult);
        Assertions.assertThat(actual.violations)
                .hasSize(2)
                .containsExactlyInAnyOrder(new Violation("client.firstName", "First name is required!"),
                        new Violation("client.lastName", "Last name is required!"));
    }

    @ParameterizedTest
    @ValueSource(strings = {" asdfa ", "some@", "value.com", "111@.com"})
    void shouldThrowWhenEmailHasWrongFormat(String wrongEmail) throws Exception {
        // given
        Order order = Order.builder()
                .deliveryAddress(Address.builder()
                        .city("city")
                        .country("Country")
                        .postcode("12345")
                        .street("street")
                        .build())
                .number("123")
                .orderTotal(BigDecimal.valueOf(10L))
                .pilotes(5)
                .build();

        Client client = Client.builder()
                .firstName("first")
                .lastName("Last")
                .email(wrongEmail)
                .telephone("123456789")
                .build();

        OrderRequest request = new OrderRequest(order, client);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/orders")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapToJson(request)))
                .andExpect(status().isBadRequest())
                .andReturn();

        // then
        ValidationErrorResponse actual = mapToError(mvcResult);
        Assertions.assertThat(actual.violations)
                .hasSize(1)
                .containsExactlyInAnyOrder(new Violation("client.email", "Email is invalid!"));
    }


    @ParameterizedTest
    @ValueSource(strings = {" ", "  "})
    void shouldThrowWhenEmailIsBlank(String wrongEmail) throws Exception {
        // given
        Order order = Order.builder()
                .deliveryAddress(Address.builder()
                        .city("city")
                        .country("Country")
                        .postcode("12345")
                        .street("street")
                        .build())
                .number("123")
                .orderTotal(BigDecimal.valueOf(10L))
                .pilotes(5)
                .build();

        Client client = Client.builder()
                .firstName("first")
                .lastName("Last")
                .email(wrongEmail)
                .telephone("123456789")
                .build();

        OrderRequest request = new OrderRequest(order, client);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/orders")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapToJson(request)))
                .andExpect(status().isBadRequest())
                .andReturn();

        // then
        ValidationErrorResponse actual = mapToError(mvcResult);
        Assertions.assertThat(actual.violations)
                .hasSize(2)
                .containsExactlyInAnyOrder(new Violation("client.email", "Email is invalid!"),
                        new Violation("client.email", "Email is required!"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"!!! !!! !!!", "telephone", "0-0-1111111", "++++123541"})
    void shouldThrowWhenTelephoneHasWrongFormat(String wrongPhoneNumber) throws Exception {
        // given
        Order order = Order.builder()
                .deliveryAddress(Address.builder()
                        .city("city")
                        .country("Country")
                        .postcode("12345")
                        .street("street")
                        .build())
                .number("123")
                .orderTotal(BigDecimal.valueOf(10L))
                .pilotes(5)
                .build();

        Client client = Client.builder()
                .firstName("first")
                .lastName("Last")
                .email("mail@mail.com")
                .telephone(wrongPhoneNumber)
                .build();

        OrderRequest request = new OrderRequest(order, client);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/orders")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapToJson(request)))
                .andExpect(status().isBadRequest())
                .andReturn();

        // then
        ValidationErrorResponse actual = mapToError(mvcResult);
        Assertions.assertThat(actual.violations)
                .hasSize(1)
                .containsExactlyInAnyOrder(new Violation("client.telephone", "Phone number is invalid!"));
    }

    private String mapToJson(OrderRequest request) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        return ow.writeValueAsString(request);
    }

    private ValidationErrorResponse mapToError(MvcResult mvcResult) throws UnsupportedEncodingException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(mvcResult.getResponse().getContentAsString(), ValidationErrorResponse.class);
    }
}
