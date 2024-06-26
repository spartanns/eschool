package com.example.server.user;

import com.example.server.security.Views;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Entity @NoArgsConstructor @AllArgsConstructor @Data @Builder
public class User implements UserDetails {
    @JsonView(Views.Public.class)
    private @Id @GeneratedValue(strategy = GenerationType.AUTO) Long id;
    @JsonView(Views.Private.class)
    private @Column(nullable = false, unique = true) String username;
    @JsonView(Views.Private.class)
    private @Column(nullable = false) String password;
    @JsonView(Views.Admin.class)
    private @Enumerated @Column(nullable = false) Role role;

    @Override @JsonView(Views.Admin.class)
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getAuthorities();
    }

    @Override @JsonView(Views.Admin.class)
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override @JsonView(Views.Admin.class)
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override @JsonView(Views.Admin.class)
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override @JsonView(Views.Admin.class)
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
