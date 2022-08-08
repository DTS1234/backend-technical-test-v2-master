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
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.NoSuchElementException;

import static com.tui.proof.orders.Constants.*;
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
    void shouldThrowWhenPilotesValueIsNot5or10or15ForCreate(int wrongPilotesValue) {
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

    @ParameterizedTest
    @ValueSource(ints = {6, 11, 14, 16})
    void shouldThrowWhenPilotesValueIsNot5or10or15ForUpdate(int wrongPilotesValue) {
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

        Assertions.assertThatThrownBy(() -> subject.updateOrder(orderRequest, client))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Pilotes value can be only equal to 5, 10 or 15.");
    }

    @Test
    void shouldUpdatePriceValueWhenUpdating() {
        Order orderRequest = Order.builder()
                .number("1")
                .deliveryAddress(Address.builder()
                        .city("city")
                        .country("country")
                        .postcode("12345")
                        .street("street")
                        .build())
                .pilotes(MEDIUM)
                .build();

        Client client = Client.builder()
                .firstName("first")
                .lastName("Last")
                .email("mail@mail.com")
                .telephone("123456789")
                .build();

        when(persistenceAdapter.updateOrder(Order.builder()
                .deliveryAddress(Address.builder()
                        .city("city")
                        .country("country")
                        .postcode("12345")
                        .street("street")
                        .build())
                .pilotes(MEDIUM)
                .orderTotal(BigDecimal.valueOf(13.30).setScale(2, RoundingMode.HALF_UP))
                .number("1")
                .build(), client))
                .thenReturn(Order.builder()
                        .deliveryAddress(Address.builder()
                                .city("city")
                                .country("country")
                                .postcode("12345")
                                .street("street")
                                .build())
                        .pilotes(MEDIUM)
                        .orderTotal(BigDecimal.valueOf(13.30).setScale(2, RoundingMode.HALF_UP))
                        .number("1")
                        .build());


        Order actual1 = subject.updateOrder(orderRequest, client);
        actual1.setPilotes(BIG);

        when(persistenceAdapter.updateOrder(actual1, client))
                .thenReturn(Order.builder()
                        .deliveryAddress(Address.builder()
                                .city("city")
                                .country("country")
                                .postcode("12345")
                                .street("street")
                                .build())
                        .pilotes(BIG)
                        .orderTotal(BigDecimal.valueOf(19.95).setScale(2, RoundingMode.HALF_UP))
                        .number("1")
                        .build());

        Order actual2 = subject.updateOrder(actual1, client);

        Assertions.assertThat(actual2)
                .hasFieldOrPropertyWithValue("number", "1")
                .hasFieldOrPropertyWithValue("orderTotal", BigDecimal.valueOf(19.95).setScale(2, RoundingMode.HALF_UP))
                .hasFieldOrPropertyWithValue("deliveryAddress", Address.builder()
                        .city("city")
                        .country("country")
                        .postcode("12345")
                        .street("street")
                        .build())
                .hasFieldOrPropertyWithValue("pilotes", BIG);
    }

    @Test
    void shouldThrowIfTryingToUpdate5MinutesAfterOrderCreation() {
        when(persistenceAdapter.getOrderTimestamp(any())).thenReturn(
                Timestamp.valueOf(LocalDateTime.now().minus(5, ChronoUnit.MINUTES))
        );

        Order orderRequest = Order.builder()
                .number("1")
                .deliveryAddress(Address.builder()
                        .city("city")
                        .country("country")
                        .postcode("12345")
                        .street("street")
                        .build())
                .pilotes(MEDIUM)
                .build();

        Client client = Client.builder()
                .firstName("first")
                .lastName("Last")
                .email("mail@mail.com")
                .telephone("123456789")
                .build();

        Assertions.assertThatThrownBy(() -> subject.updateOrder(orderRequest, client))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("You cannot update order after 5 minutes since it's creation!");
    }

}
