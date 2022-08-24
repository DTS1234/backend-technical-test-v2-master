package com.tui.proof.persistence.model;

import lombok.*;

import javax.persistence.*;

/**
 * @author akazmierczak
 * @created 07.08.2022
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table(name = "ADDRESS")
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

    @Override
    public String toString() {
        return "AddressEntity{" +
                "id=" + id +
                ", street='" + street + '\'' +
                ", postcode='" + postcode + '\'' +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}
