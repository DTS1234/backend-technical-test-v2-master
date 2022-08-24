package com.tui.proof.persistence.model;

import lombok.*;

import javax.persistence.*;
import java.util.List;

/**
 * @author akazmierczak
 * @created 08.08.2022
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "CLIENT")
public class ClientEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;
    private String telephone;
    @Column(name = "FIRSTNAME")
    private String firstName;
    @Column(name = "LASTNAME")
    private String lastName;
    @OneToMany(mappedBy = "client")
    private List<OrderEntity> orders;


}
