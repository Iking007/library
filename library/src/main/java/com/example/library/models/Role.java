package com.example.library.models;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    USER,
    MODER,
    ADMIN;

    @Override
    public String getAuthority() {
        return name();
    }
}
