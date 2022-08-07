package com.tui.proof.ws.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.tui.proof.OrderService;
import com.tui.proof.ValidationErrorResponse;
import com.tui.proof.Violation;
import com.tui.proof.model.Address;
import com.tui.proof.model.Order;
import com.tui.proof.ws.ErrorHandlingController;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Collections;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


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
                .pilotes(5)
                .orderTotal(BigDecimal.TEN)
                .build();

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/orders")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapToJson(order)))
                .andExpect(status().isBadRequest())
                .andReturn();

        // then
        ValidationErrorResponse actual = mapToError(mvcResult);
        Assertions.assertThat(actual.violations)
                .hasSize(1)
                .isEqualTo(Collections.singletonList(new Violation("deliveryAddress", "must not be null")));
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

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/orders")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapToJson(order)))
                .andExpect(status().isBadRequest())
                .andReturn();

        // then
        ValidationErrorResponse actual = mapToError(mvcResult);
        Assertions.assertThat(actual.violations)
                .hasSize(1)
                .isEqualTo(Collections.singletonList(new Violation("pilotes", "must be greater than or equal to 5")));
    }

    private String mapToJson(Order order) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        return ow.writeValueAsString(order);
    }

    private ValidationErrorResponse mapToError(MvcResult mvcResult) throws UnsupportedEncodingException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(mvcResult.getResponse().getContentAsString(), ValidationErrorResponse.class);
    }
}
