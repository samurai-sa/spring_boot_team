package com.example.eventapp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.eventapp.entities.Store;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {

}
