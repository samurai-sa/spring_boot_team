package com.example.eventapp.entities;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 整理券
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "eventTicket")
@Entity
public class EventTicket {
	
	// id
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	// イベント
	@ManyToOne(fetch = FetchType.EAGER)
	private Event event;
	
	// 整理番号
	@Column(name = "ticketNum", nullable = false, unique = true)
	private String ticketNum;
	
	// 認証確認
	@Column(name = "authenticated", nullable = false)
	private Boolean authenticated = false;
	
	// 作成日時
	@Column(name = "createdAt", nullable = false, updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private ZonedDateTime createdAt;
	
	// 更新日時
	@Column(name = "updatedAt", nullable = false, updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
	private ZonedDateTime updatedAt;
	
	// 認証日時
	@Column(name = "authenticatedTime")
	private LocalDateTime authenticatedTime;

}
