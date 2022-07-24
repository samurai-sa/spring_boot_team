package com.example.eventapp.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.eventapp.entities.EventSpace;
import com.example.eventapp.repositories.EventSpaceRepository;
import com.example.eventapp.repositories.StoreRepository;

@Service
public class EventSpaceService {

	private final EventSpaceRepository eventSpaceRepo;
	private final StoreRepository storeRepo;

	public EventSpaceService(EventSpaceRepository eventSpaceRepo, StoreRepository storeRepo) {
		this.eventSpaceRepo = eventSpaceRepo;
		this.storeRepo = storeRepo;
	}

	@Transactional(readOnly = true)
	public List<EventSpace> findAll() {
		return eventSpaceRepo.findAll();
	}

	@Transactional(readOnly = true)
	public EventSpace findById(long id) {
		return eventSpaceRepo.findById(id).orElseThrow();
	}

	@Transactional(readOnly = true)
	public List<EventSpace> findByStoreId(long storeId) {
		return eventSpaceRepo.findByStoreId(storeId);
	}

	@Transactional(readOnly = true)
	public Long numberOfStore() {
		return eventSpaceRepo.count();
	}
	

	// 登録
	@Transactional
	public EventSpace register(Long id, String name, int capacity) {
		EventSpace eventSpace = new EventSpace();
		eventSpace.setName(name);
		eventSpace.setCapacity(capacity);
		eventSpace.setStore(storeRepo.findById(id).orElseThrow());
		return eventSpaceRepo.saveAndFlush(eventSpace);
	}

	// 編集
	@Transactional
	public EventSpace update(long id, String name, int capacity) {
		EventSpace eventSpace = eventSpaceRepo.findById(id).orElseThrow();
		eventSpace.setName(name);
		// eventSpace.setStore(storeRepo.findById(storeId).orElseThrow());
		eventSpace.setCapacity(capacity);
		eventSpaceRepo.saveAndFlush(eventSpace);
		return eventSpace;
	}

	// 削除
	@Transactional
	public void delete(long id) {
		eventSpaceRepo.deleteById(id);
	}

	@Transactional(readOnly = true)
	public List<EventSpace> findAllByOrderByStoreId() {
		return eventSpaceRepo.findAllByOrderByStoreId();
	}

}
