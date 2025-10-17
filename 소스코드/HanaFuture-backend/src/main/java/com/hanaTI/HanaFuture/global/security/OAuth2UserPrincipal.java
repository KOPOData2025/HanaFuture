package com.hanaTI.HanaFuture.global.security;

import com.hanaTI.HanaFuture.domain.user.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class OAuth2UserPrincipal implements OAuth2User {
    private final User user;
    private final Map<String, Object> attributes;
    
    public OAuth2UserPrincipal(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }
    
    public static OAuth2UserPrincipal create(User user, Map<String, Object> attributes) {
        return new OAuth2UserPrincipal(user, attributes);
    }
    
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(user.getRole().getKey()));
    }
    
    @Override
    public String getName() {
        return user.getEmail();
    }
    
    public User getUser() {
        return user;
    }
}
