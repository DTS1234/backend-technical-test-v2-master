package com.tui.proof.persistence.model;

import lombok.Data;

import javax.persistence.*;

@Entity(name = "authorities")
@Data
public class UserAuthority {

    public UserAuthority() {
    }

    public UserAuthority(User user, String authority) {
        this.user = user;
        this.authority = authority;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "username", referencedColumnName = "username")
    private User user;

    private String authority;

}
