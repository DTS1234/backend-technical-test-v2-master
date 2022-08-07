package com.tui.proof.persistence;

import com.tui.proof.orders.model.Address;
import com.tui.proof.orders.model.Order;
import com.tui.proof.persistence.model.AddressEntity;
import com.tui.proof.persistence.model.OrderEntity;

/**
 * @author akazmierczak
 * @created 07.08.2022
 */
public class OrderMapper {

    public static Order toDomain(OrderEntity orderEntity) {
        return Order.builder()
                .orderTotal(orderEntity.getOrderTotal())
                .pilotes(orderEntity.getPilotes())
                .number(String.valueOf(orderEntity.getId()))
                .deliveryAddress(toDomain(orderEntity.getDeliveryAddress()))
                .build();
    }

    private static Address toDomain(AddressEntity addressEntity) {
        return Address.builder()
                .street(addressEntity.getStreet())
                .postcode(addressEntity.getPostcode())
                .country(addressEntity.getCountry())
                .city(addressEntity.getCity())
                .build();
    }

    public static OrderEntity toEntity(Order order) {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderTotal(order.getOrderTotal());
        orderEntity.setPilotes(order.getPilotes());
        orderEntity.setDeliveryAddress(toEntity(order.getDeliveryAddress()));
        return orderEntity;
    }

    public static AddressEntity toEntity(Address address) {
        AddressEntity addressEntity = new AddressEntity();
        addressEntity.setCity(address.getCity());
        addressEntity.setCountry(address.getCountry());
        addressEntity.setStreet(address.getStreet());
        addressEntity.setPostcode(address.getPostcode());
        return addressEntity;
    }

}
