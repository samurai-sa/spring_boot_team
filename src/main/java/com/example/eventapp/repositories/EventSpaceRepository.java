package com.example.eventapp.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.eventapp.entities.EventSpace;

@Repository
public interface EventSpaceRepository extends JpaRepository<EventSpace, Long> {
    List<EventSpace> findAllByOrderByStoreId();

    public List<EventSpace> findByStoreId(Long storeId);
    // Long numberOfEventSpace count();
}
