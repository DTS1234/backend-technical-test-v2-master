package com.tui.proof.persistence;

import com.tui.proof.orders.model.Client;
import com.tui.proof.orders.model.Order;
import com.tui.proof.persistence.model.ClientEntity;
import com.tui.proof.persistence.model.OrderEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * @author akazmierczak
 * @created 07.08.2022
 */
@Service
@RequiredArgsConstructor
public class PersistenceAdapter {

    private final OrderRepository orderRepository;
    private final ClientRepository clientRepository;

    @Transactional
    public Order saveOrder(Order order, Client client) {
        OrderEntity orderEntity = OrderMapper.toEntity(order);
        ClientEntity clientEntity = ClientMapper.toEntity(client);

        Optional<ClientEntity> optionalClient = clientRepository.findClientEntityByEmailOrTelephone(clientEntity.getEmail(), client.getTelephone());
        if (optionalClient.isPresent()) {
            orderEntity.setClient(optionalClient.get());
        } else {
            ClientEntity saved = clientRepository.save(clientEntity);
            orderEntity.setClient(saved);
        }

        orderEntity.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));
        OrderEntity savedEntity = orderRepository.save(orderEntity);
        return OrderMapper.toDomain(savedEntity);
    }

}
