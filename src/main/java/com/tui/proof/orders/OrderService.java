package com.tui.proof.orders;

import com.tui.proof.orders.model.Client;
import com.tui.proof.orders.model.Order;
import com.tui.proof.persistence.PersistenceAdapter;
import com.tui.proof.web.model.CustomerOrderSearchFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static com.tui.proof.orders.PilotesConstants.*;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final PersistenceAdapter persistenceAdapter;

    public List<Client> filterOrders(CustomerOrderSearchFilter filter) {
        return persistenceAdapter.find(filter);
    }

    public Order createOrder(Order orderRequest, Client client) {
        validatedNumberOfPilotes(orderRequest);
        setPriceForOrder(orderRequest);
        return persistenceAdapter.saveOrder(orderRequest, client);
    }

    public Order updateOrder(Order order, Client client) {
        validateIfCanBeUpdated(order);
        validatedNumberOfPilotes(order);
        setPriceForOrder(order);
        return persistenceAdapter.updateOrder(order, client);
    }

    private void validateIfCanBeUpdated(Order order) {
        Timestamp orderTimestamp = persistenceAdapter.getOrderTimestamp(order.getNumber());
        if (is5MinutesAfterCreation(orderTimestamp)) {
            throw new IllegalStateException("You cannot update order after 5 minutes since it's creation!");
        }
    }

    private boolean is5MinutesAfterCreation(Timestamp orderTimestamp) {
        LocalDateTime dateOfOrderPlusFiveMinutes = orderTimestamp.toLocalDateTime().plusMinutes(5);
        return Timestamp.valueOf(LocalDateTime.now()).after(Timestamp.valueOf(dateOfOrderPlusFiveMinutes));
    }

    private void setPriceForOrder(Order orderRequest) {
        BigDecimal price = BigDecimal.valueOf(PRICE * orderRequest.getPilotes()).setScale(2, RoundingMode.HALF_UP);
        orderRequest.setOrderTotal(price);
    }

    private void validatedNumberOfPilotes(Order orderRequest) {
        int pilotes = orderRequest.getPilotes();
        if (!Arrays.asList(SMALL, MEDIUM, BIG).contains(pilotes)) {
            throw new IllegalArgumentException("Pilotes value can be only equal to 5, 10 or 15.");
        }
    }

}
