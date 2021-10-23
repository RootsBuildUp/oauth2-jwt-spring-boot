package com.RootBuildUp.oauth2jwtspringboot.service;

import com.RootBuildUp.oauth2jwtspringboot.model.User;
import com.RootBuildUp.oauth2jwtspringboot.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
//    private final AuthenticationManager authenticationManager;

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username);

        if (user == null) throw new BadCredentialsException("Bad credentials");

        System.out.println("user information");
        System.out.println(username);
        System.out.println(user.getRoles());
        System.out.println(user.getPassword());

        new AccountStatusUserDetailsChecker().check(user);

        if(user != null && user.isEnabled()) {//here you can check that
            List<GrantedAuthority> authorities = this.getUserAuthority(user.getRoles());
            return buildUserForAuthentication(user, authorities);
        }

        else {
            throw new UsernameNotFoundException("username not found");
        }
//        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken("",""));
//        return new org.springframework.security.core.userdetails.User(String.valueOf(user.getId()), user.getPassword(), getAuthority());
    }

    private List<GrantedAuthority> getUserAuthority(List<String> userRoles) {
        Set<GrantedAuthority> roles = new HashSet<>();
        userRoles.forEach((role) -> {
            roles.add(new SimpleGrantedAuthority(role));
        });

        List<GrantedAuthority> grantedAuthorities = new ArrayList<>(roles);
        return grantedAuthorities;
    }

    private UserDetails buildUserForAuthentication(User user, List<GrantedAuthority> authorities) {
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
    }

//    private List<SimpleGrantedAuthority> getAuthority() {
//        return Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN"));
//    }

    public User create(User user) {
        return userRepository.save(user);

    }

}
