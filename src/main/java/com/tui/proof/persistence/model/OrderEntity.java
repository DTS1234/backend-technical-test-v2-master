package com.tui.proof.persistence.model;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @author akazmierczak
 * @created 07.08.2022
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int pilotes;
    private BigDecimal orderTotal;
    @OneToOne(cascade = CascadeType.ALL)
    private AddressEntity deliveryAddress;
    private Timestamp timestamp;
    @ManyToOne(cascade = CascadeType.PERSIST)
    private ClientEntity client;
}
