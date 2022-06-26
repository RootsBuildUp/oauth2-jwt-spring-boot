package com.RootBuildUp.oauth2jwtspringboot.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;


@Getter
@Setter
@Entity
public class TokenStore extends BaseIdEntity {

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
    @Column
    private String role;
    @Column
    private String tokenStore;
    @Column
    private String privilege;
    

}
