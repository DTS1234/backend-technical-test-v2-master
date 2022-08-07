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
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ClientEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String telephone;
    private String firstName;
    private String lastName;
    @OneToMany(mappedBy = "client")
    private List<OrderEntity> orders;


}
