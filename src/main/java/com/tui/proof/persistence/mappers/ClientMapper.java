package com.tui.proof.persistence.mappers;

import com.tui.proof.orders.model.Client;
import com.tui.proof.persistence.model.ClientEntity;

import java.util.stream.Collectors;

/**
 * @author akazmierczak
 * @created 08.08.2022
 */
public class ClientMapper {

    public static ClientEntity toEntity(Client client) {
        ClientEntity clientEntity = new ClientEntity();
        clientEntity.setEmail(client.getEmail());
        clientEntity.setFirstName(client.getFirstName());
        clientEntity.setLastName(client.getLastName());
        clientEntity.setTelephone(client.getTelephone());
        return clientEntity;
    }

    public static Client toDomain(ClientEntity clientEntity) {
        return Client.builder()
                .telephone(clientEntity.getTelephone())
                .email(clientEntity.getEmail())
                .firstName(clientEntity.getFirstName())
                .lastName(clientEntity.getLastName())
                .orders(clientEntity.getOrders().stream().map(OrderMapper::toDomain).collect(Collectors.toList()))
                .build();
    }

}
