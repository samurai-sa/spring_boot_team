package com.example.eventapp.entities;

import java.time.LocalDateTime;
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

// イベント
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "event")
@Entity
public class Event {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// 名前
	@Column(name = "name", nullable = false)
	private String name;

	// カテゴリー★要相談
//	@Column(name = "category", nullable = false)
//	private String category;

	//イベントの説明
	@Column(name = "description", nullable = true)
	private String description;
	
	// イベントスペースID
	@ManyToOne(fetch = FetchType.EAGER)
	private EventSpace eventspace;

	// 開催日時
	@Column(name = "eventDate")
	private LocalDateTime eventDate;
	
	// 終了日時
	@Column(name = "eventEndDate")
	private LocalDateTime eventEndDate;
	
	// 参加上限（整理券の発行上限）
	@Column(name = "maxTicket", nullable = false)
	private int maxTicket;

	// 発行済み数
	@Column(name = "printedTicket", nullable = false)
	private int printedTicket;
	
	//実際の参加人数
//	@Column(name = "participants", nullable = false)
//	private int participants;

	// 整理券
	@OneToMany(mappedBy = "event", fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE})
	private List<EventTicket> eventTickets;

	//イベント作成者
	@Column(name="createdBy", nullable = false)
	private String createdBy;
	
	// 作成日時
	@Column(name = "createdAt", nullable = false, updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private ZonedDateTime createdAt;

	// 更新日時
	@Column(name = "updatedAt", nullable = false, updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
	private ZonedDateTime updatedAt;
}
