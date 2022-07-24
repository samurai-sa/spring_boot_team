package com.example.eventapp.security;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.example.eventapp.entities.Store;

public class SimpleLoginStore extends org.springframework.security.core.userdetails.User {
	
	private Store store;
	 
    public SimpleLoginStore(Store store) {
        super(String.valueOf(store.getId()), store.getPassword(), store.getEnable(), true, true,
            true, convertGrantedAuthorities(store.getRoles()));
        this.store = store;
    }
 
    public Store getStore() {
        return store;
    }
 
    // 権限管理用のメソッド
    static Set<GrantedAuthority> convertGrantedAuthorities(String roles) {
        if (roles == null || roles.isEmpty()) {
            return Collections.emptySet();
        }
        Set<GrantedAuthority> authorities = Stream.of(roles.split(","))
            .map(SimpleGrantedAuthority::new)	// SimpleGrantedAuthorityを生成
            .collect(Collectors.toSet());
        return authorities;
    }
}
