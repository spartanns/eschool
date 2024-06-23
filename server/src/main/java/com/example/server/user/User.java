package com.example.server.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Entity @NoArgsConstructor @AllArgsConstructor @Data @Builder
public class User implements UserDetails {
    private @Id @GeneratedValue(strategy = GenerationType.AUTO) Long id;

    @NotNull(message = "Username must be provided.")
    @Size(min=5, max=25, message = "Username must be between {min} and {max} characters long.")
    private @Column(nullable = false, unique = true) String username;

    @NotNull(message = "Password must be provided.")
    @Size(min=5, message = "Password must be at least {min} characters long.")
    private @Column(nullable = false) String password;

    @NotNull(message = "Role must be provided.")
    private @Enumerated @Column(nullable = false) Role role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getAuthorities();
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
