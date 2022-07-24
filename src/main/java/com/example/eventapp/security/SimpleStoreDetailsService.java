package com.example.eventapp.security;

import javax.transaction.Transactional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.eventapp.repositories.StoreRepository;

@Service
public class SimpleStoreDetailsService implements UserDetailsService {
	
	private final StoreRepository storeRepository;
	
	public SimpleStoreDetailsService(StoreRepository storeRepository) {
		this.storeRepository = storeRepository;
	}
	
	// nameを指定して、ユーザー情報を取得
	@Transactional//(readOnly = true)
	@Override
	public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
		assert id != null;
		Long longId = Long.parseLong(id);
		return storeRepository
			.findById(longId)
			.map(SimpleLoginStore::new)
			.orElseThrow();
	}

}
