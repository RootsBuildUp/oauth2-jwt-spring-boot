package com.RootBuildUp.oauth2jwtspringboot.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Getter
@Setter
@Entity
public class User extends BaseIdEntity implements UserDetails{

    @Column(nullable = false, unique = true)
    private String username;
    private String password;

    private String first_name;
    private String last_name;
    private String email;
    private String emp_id;
    private int is_active;

    private Date start_date;
    private Date end_date;

    private String created_by;
    private Timestamp created_at;
    private String updated_by;
    private Timestamp updated_at;

    @OneToOne(mappedBy = "user")
    private TokenStore token;

    @ElementCollection(fetch = FetchType.EAGER)
    @Builder.Default
    private List<String> roles = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream().map(SimpleGrantedAuthority::new).collect(toList());
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}

