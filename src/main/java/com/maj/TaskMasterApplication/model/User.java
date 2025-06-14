package com.maj.TaskMasterApplication.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "task_user")
public class User implements UserDetails { // Implement UserDetails

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String username; // This will be used by UserDetails getUsername()

    @Column(nullable = false)
    private String password; // This will be used by UserDetails getPassword()

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Roles role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks;

    // UserDetails methods implementation
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Roles in Spring Security are typically prefixed with "ROLE_"
        // If your Roles enum doesn't have this, you might add it here or ensure your
        // security expressions account for it (e.g. .hasAuthority("ADMIN") vs .hasRole("ADMIN"))
        // For simplicity, using the enum name directly.
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username; // Or email, if you prefer to log in with email
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Add logic if accounts can expire
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Add logic if accounts can be locked
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Add logic if credentials can expire
    }

    @Override
    public boolean isEnabled() {
        return true; // Add logic if accounts can be disabled
    }
}