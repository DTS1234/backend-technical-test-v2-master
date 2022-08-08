package com.tui.proof.persistence;

import com.tui.proof.orders.model.Client;
import com.tui.proof.orders.model.Order;
import com.tui.proof.persistence.mappers.ClientMapper;
import com.tui.proof.persistence.mappers.OrderMapper;
import com.tui.proof.persistence.model.AddressEntity;
import com.tui.proof.persistence.model.ClientEntity;
import com.tui.proof.persistence.model.OrderEntity;
import com.tui.proof.persistence.repositories.AddressRepository;
import com.tui.proof.persistence.repositories.ClientRepository;
import com.tui.proof.persistence.repositories.OrderRepository;
import com.tui.proof.persistence.specification.ClientSpecification;
import com.tui.proof.web.model.CustomerOrderSearchFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author akazmierczak
 * @created 07.08.2022
 */
@Service
@RequiredArgsConstructor
public class PersistenceAdapter {

    private final OrderRepository orderRepository;
    private final ClientRepository clientRepository;
    private final AddressRepository addressRepository;

    @Transactional
    public Order saveOrder(Order order, Client client) {
        OrderEntity orderEntity = OrderMapper.toEntity(order);
        ClientEntity clientEntity = ClientMapper.toEntity(client);

        AddressEntity addressEntity = orderEntity.getDeliveryAddress();
        Optional<AddressEntity> optionalAddressEntity = addressRepository.findAddressEntityByCityAndAndCountryAndPostcodeAndStreet(addressEntity.getCity(), addressEntity.getCountry(), addressEntity.getPostcode(), addressEntity.getStreet());
        if (optionalAddressEntity.isPresent()) {
            orderEntity.setDeliveryAddress(optionalAddressEntity.get());
        } else {
            orderEntity.setDeliveryAddress(addressRepository.save(addressEntity));
        }

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

    public Timestamp getOrderTimestamp(String number) {
        OrderEntity orderEntity = orderRepository.findById(Long.valueOf(number))
                .orElseThrow(() -> new NoSuchElementException("Order with number equal to " + number + " does not exist!"));
        return orderEntity.getTimestamp();
    }

    public Order updateOrder(Order order, Client client) {

        OrderEntity existingOrder = orderRepository.findById(Long.valueOf(order.getNumber())).orElseThrow(() ->
                new NoSuchElementException("Order with the number equal to " + order.getNumber() + " does not exist!"));

        OrderEntity entityToUpdate = OrderMapper.toEntity(order);
        AddressEntity addressEntity = updateAddressEnitity(order, existingOrder);
        AddressEntity updatedAddress = addressRepository.save(addressEntity);
        entityToUpdate.setDeliveryAddress(updatedAddress);

        ClientEntity clientEntity = updateClientEntity(client, existingOrder);
        ClientEntity updatedClient = clientRepository.save(clientEntity);

        entityToUpdate.setClient(updatedClient);
        OrderEntity updatedOrder = orderRepository.save(entityToUpdate);

        return OrderMapper.toDomain(updatedOrder);
    }

    private AddressEntity updateAddressEnitity(Order order, OrderEntity existingOrder) {
        AddressEntity addressEntity = existingOrder.getDeliveryAddress();
        addressEntity.setCity(order.getDeliveryAddress().getCity());
        addressEntity.setCountry(order.getDeliveryAddress().getCountry());
        addressEntity.setPostcode(order.getDeliveryAddress().getPostcode());
        addressEntity.setStreet(order.getDeliveryAddress().getStreet());
        return addressEntity;
    }

    private ClientEntity updateClientEntity(Client client, OrderEntity existingOrder) {
        ClientEntity clientToUpdate = existingOrder.getClient();
        clientToUpdate.setEmail(client.getEmail());
        clientToUpdate.setFirstName(client.getFirstName());
        clientToUpdate.setLastName(client.getLastName());
        clientToUpdate.setTelephone(client.getTelephone());
        return clientToUpdate;
    }

    public List<Order> find(CustomerOrderSearchFilter filter) {
        ClientSpecification spec = new ClientSpecification(filter);
        Pageable of = PageRequest.of(filter.getPageNumber(), filter.getPageSize());

        Page<ClientEntity> all = clientRepository.findAll(spec, of);
        List<Order> result = all.getContent().stream()
                .map(ClientEntity::getOrders)
                .map(orders -> orders.stream()
                        .map(OrderMapper::toDomain)
                        .collect(Collectors.toList()))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());


        return result;
    }
}
