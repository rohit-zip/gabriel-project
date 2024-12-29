package com.gn128.authentication;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * Author - rohit
 * Project - java-backend
 * Package - com.gn128.authentication
 * Created_on - November 10 - 2024
 * Created_at - 12:26
 */

public class AuthenticationToken extends UsernamePasswordAuthenticationToken {
    public AuthenticationToken(Object principal, Object credentials) {
        super(principal, credentials);
    }

    public AuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
    }
}
