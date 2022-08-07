package com.tui.proof.orders;

import com.tui.proof.orders.model.Client;
import com.tui.proof.orders.model.Order;
import com.tui.proof.persistence.PersistenceAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.BindingResultUtils;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.lang.reflect.Parameter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.HashMap;

import static com.tui.proof.orders.Constants.*;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final PersistenceAdapter persistenceAdapter;

    public Order createOrder(Order orderRequest, Client client) {

        int pilotes = orderRequest.getPilotes();

        if(!Arrays.asList(SMALL, MEDIUM, BIG).contains(pilotes)) {
            throw new IllegalArgumentException("Pilotes value can be only equal to 5, 10 or 15.");
        }

        BigDecimal price = BigDecimal.valueOf(PRICE * pilotes).setScale(2, RoundingMode.HALF_UP);
        orderRequest.setOrderTotal(price);
        return persistenceAdapter.saveOrder(orderRequest, client);
    }

}
