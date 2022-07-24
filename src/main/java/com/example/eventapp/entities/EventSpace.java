package com.example.eventapp.entities;

import java.time.ZonedDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// イベントスペース
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "eventspace")
@Entity
public class EventSpace {

	// id
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	//店舗ID
	@ManyToOne(fetch = FetchType.EAGER)
	private Store store;
	
	//event
	@OneToMany(mappedBy = "eventspace", fetch = FetchType.EAGER, cascade = {CascadeType.REMOVE})
	List<Event> events;
	
	//名前
	@Column(name = "name", nullable=false)
	private String name;

	// 収容数
	@Column(name = "capacity", nullable = false)
	private int capacity;

	// 作成日時
	@Column(name = "createdAt", nullable = false, updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private ZonedDateTime createdAt;

	// 更新日時
	@Column(name = "updatedAt", nullable = false, updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
	private ZonedDateTime updatedAt;

}
