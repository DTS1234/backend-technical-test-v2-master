package com.tui.proof.orders;

import com.tui.proof.orders.model.Address;
import com.tui.proof.orders.model.Client;
import com.tui.proof.orders.model.Order;
import com.tui.proof.orders.OrderService;
import com.tui.proof.persistence.PersistenceAdapter;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.math.BigDecimal;

import static com.tui.proof.orders.Constants.SMALL;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class OrderServiceTest {

    @Mock
    private PersistenceAdapter persistenceAdapter;

    private OrderService subject;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        subject = new OrderService(persistenceAdapter);
    }

    @Test
    void shouldCreateAnOrderWithTimestampAndTotalOrder() {
        when(persistenceAdapter.saveOrder(any(), any()))
                .thenReturn(Order.builder()
                        .orderTotal(BigDecimal.valueOf(6.65))
                        .deliveryAddress(Address.builder()
                                .city("city")
                                .country("country")
                                .postcode("12345")
                                .street("street")
                                .build())
                        .pilotes(SMALL)
                        .number("1")
                        .build());

        Order orderRequest = Order.builder()
                .deliveryAddress(Address.builder()
                        .city("city")
                        .country("country")
                        .postcode("12345")
                        .street("street")
                        .build())
                .pilotes(SMALL)
                .build();

        Client client = Client.builder()
                .firstName("first")
                .lastName("Last")
                .email("mail@mail.com")
                .telephone("123456789")
                .build();

        Order actual = subject.createOrder(orderRequest, client);

        Assertions.assertThat(actual)
                .hasFieldOrPropertyWithValue("number", "1")
                .hasFieldOrPropertyWithValue("orderTotal", BigDecimal.valueOf(6.65))
                .hasFieldOrPropertyWithValue("deliveryAddress", Address.builder()
                        .city("city")
                        .country("country")
                        .postcode("12345")
                        .street("street")
                        .build())
                .hasFieldOrPropertyWithValue("pilotes", SMALL);
    }

    @ParameterizedTest
    @ValueSource(ints = {6, 11, 14, 16})
    void shouldThrowWhenPilotesValueIsNot5or10or15(int wrongPilotesValue) {
        Order orderRequest = Order.builder()
                .deliveryAddress(Address.builder()
                        .city("city")
                        .country("country")
                        .postcode("12345")
                        .street("street")
                        .build())
                .pilotes(wrongPilotesValue)
                .build();

        Client client = Client.builder()
                .firstName("first")
                .lastName("Last")
                .email("mail@mail.com")
                .telephone("123456789")
                .build();

        Assertions.assertThatThrownBy(() -> subject.createOrder(orderRequest, client))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Pilotes value can be only equal to 5, 10 or 15.");
    }
}
