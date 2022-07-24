package com.example.eventapp.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.eventapp.entities.Store;
import com.example.eventapp.repositories.StoreRepository;

@Service
public class StoreService {

	private final StoreRepository storeRepo;
	private final PasswordEncoder passwordEncoder;

	public StoreService(StoreRepository storeRepo, PasswordEncoder passwordEncoder) {
		this.storeRepo = storeRepo;
		this.passwordEncoder = passwordEncoder;
	}

	// StoreRepoに登録
	@Transactional
	public Store register(String name, String password, String[] roles) {

		// パスワードを暗号化
		String encodedPassword = passwordEncode(password);

		// ユーザー権限の配列を文字列にコンバート
		String joinedRoles = joinRoles(roles);

		// Store エンティティの生成
		Store store = new Store(null, null, name, encodedPassword, joinedRoles, Boolean.TRUE, null, null);

		// ユーザー登録
		return storeRepo.saveAndFlush(store);
	}

	// パスワードを暗号化
	private String passwordEncode(String rawPassword) {
		return passwordEncoder.encode(rawPassword);
	}

	// ユーザー権限の配列を文字列にコンバート
	private String joinRoles(String[] roles) {
		if (roles == null || roles.length == 0) {
			return "";
		}
		return Stream.of(roles)
				.map(String::trim)
				.map(String::toUpperCase)
				.collect(Collectors.joining(","));
	}

	@Transactional(readOnly = true)
	public Optional<Store> findById(Long id) {
		return this.storeRepo.findById(id);
	}

	// Store情報の一覧取得
	@Transactional(readOnly = true)
	public List<Store> findAll() {
		return storeRepo.findAll();
	}

}
