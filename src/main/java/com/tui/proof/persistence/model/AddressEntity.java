package com.tui.proof.persistence.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * @author akazmierczak
 * @created 07.08.2022
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class AddressEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Getter @Setter
    private String street;
    @Getter @Setter
    private String postcode;
    @Getter @Setter
    private String city;
    @Getter @Setter
    private String country;
}
