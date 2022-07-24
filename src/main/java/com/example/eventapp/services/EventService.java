package com.example.eventapp.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.eventapp.entities.Event;
import com.example.eventapp.entities.EventSpace;
import com.example.eventapp.form.search.SearchByDateForm;
import com.example.eventapp.repositories.EventRepository;
import com.example.eventapp.repositories.EventSpaceRepository;
import com.example.eventapp.repositories.EventTicketRepository;
import com.example.eventapp.utilities.TimeUtility;

@Service
public class EventService {

	private final EventRepository eventRepo;
	private final TimeUtility timeUtility;
	private final EventTicketRepository eventTicketRepo;
	private final EventSpaceRepository eventSpaceRepo;

	public EventService(EventRepository eventRepo, TimeUtility timeUtility, EventTicketRepository eventTicketRepo,
			EventSpaceRepository eventSpaceRepo) {
		this.eventRepo = eventRepo;
		this.timeUtility = timeUtility;
		this.eventTicketRepo = eventTicketRepo;
		this.eventSpaceRepo = eventSpaceRepo;
	}

	private LocalDateTime joinLocalDateAndLocalTime(LocalDate eventDate, LocalTime eventTime) {
		LocalDateTime date = LocalDateTime.parse(eventDate.toString() + "T" + eventTime.toString());
		return date;
	}

	// イベント重複確認
	@Transactional
	public void checkDuplicate(LocalDate eventDate, LocalTime eventTime, LocalTime eventEndTime, Long eventSpaceId,
			Long id, int maxTicket) throws Exception {
		
		// 開催日時、終了日時
		LocalDateTime newEventDate = joinLocalDateAndLocalTime(eventDate, eventTime);
		LocalDateTime newEventEndDate = joinLocalDateAndLocalTime(eventDate, eventEndTime);

		// 営業時間
		LocalDateTime startTime = joinLocalDateAndLocalTime(eventDate, LocalTime.of(10, 00));
		LocalDateTime endTime = joinLocalDateAndLocalTime(eventDate, LocalTime.of(20, 00));

		Optional<EventSpace> eventSpaceOptional = eventSpaceRepo.findById(eventSpaceId);
		EventSpace eventSpace = eventSpaceOptional.orElseThrow();
		int eventSpaceCapacity = eventSpace.getCapacity();

		// イベントスペースの収容人数よりもイベントの参加上限が多い場合
		if (eventSpaceCapacity < maxTicket) {
			throw new IllegalArgumentException();
		}
		
		// イベントの開始時間と終了時間が同じ場合
		if(newEventDate.isEqual(newEventEndDate)) {
			throw new RuntimeException();
		}

		// eventDateの日付の営業時間内のイベントを取得
		List<Event> sameDates = eventRepo.findByEventspaceIdIsAndEventDateBetween(eventSpaceId, startTime, endTime);

		// イベントが重複していた場合、新規作成しない
		for (Event event : sameDates) {
			if (event.getId() != id) {
				if (newEventEndDate.isBefore(event.getEventDate()) || newEventDate.isAfter(event.getEventEndDate())
						|| newEventEndDate.isEqual(event.getEventDate())
						|| newEventDate.isEqual(event.getEventEndDate())) {

				} else {
					throw new Exception();
				}
			}
		}
	}

	public Event register(String storeName ,Long id, String name, String description, LocalDate eventDate, LocalTime eventTime,
			LocalTime eventEndTime, int maxTicket) {
		Event newEvent = new Event();
		newEvent.setCreatedBy(storeName);
		newEvent.setName(name);
		newEvent.setDescription(description);
		// newEvent.setEventSpace(eventSpace);
		newEvent.setEventDate(joinLocalDateAndLocalTime(eventDate, eventTime));
		newEvent.setEventEndDate(joinLocalDateAndLocalTime(eventDate, eventEndTime));
		newEvent.setMaxTicket(maxTicket);
		newEvent.setPrintedTicket(0);
		newEvent.setEventspace(eventSpaceRepo.findById(id).orElseThrow());
		return eventRepo.saveAndFlush(newEvent);
	}

	@Transactional(readOnly = true)
	public List<Event> findAll() {
		return this.eventRepo.findAll();
	}

	@Transactional(readOnly = true)
	public Event findById(long id) {
		return eventRepo.findById(id).orElseThrow();
	}

	// 現在の日時以降のイベントのみ取得
	@Transactional(readOnly = true)
	public List<Event> findEventAfterToday() {
		return eventRepo.findByEventEndDateAfterOrderByEventDate(LocalDateTime.now());
	}

	// 過去のイベントの取得
	@Transactional(readOnly = true)
	public List<Event> findEventBeforeToday() {
		return eventRepo.findByEventEndDateBeforeOrderByEventDateDesc(LocalDateTime.now());
	}
	
	// 更新処理
	@Transactional
	public void eventUpdate(String storeName ,long id, Long eventSpaceId, String name, String description, LocalDate eventDate,
			LocalTime eventTime, LocalTime eventEndTime, int maxTicket) {
		// 指定Eventの情報を持ってくる
		Event event = eventRepo.findById(id).orElseThrow();
		// 指定Eventの情報を編集する
		event.setCreatedBy(storeName);
		event.setEventspace(eventSpaceRepo.findById(eventSpaceId).orElseThrow());
		event.setName(name);
		event.setDescription(description);
		event.setEventDate(joinLocalDateAndLocalTime(eventDate, eventTime));
		event.setEventEndDate(joinLocalDateAndLocalTime(eventDate, eventEndTime));
		event.setMaxTicket(maxTicket);
		event.setEventspace(eventSpaceRepo.findById(eventSpaceId).orElseThrow());
		eventRepo.saveAndFlush(event);
	}

	// 削除処理
	@Transactional
	public void delete(long id) {
		// イベントを削除
		eventRepo.deleteById(id);

	}

	// 検索
	@Transactional(readOnly = true)
	public List<Event> findByEventRange(SearchByDateForm searchByDateForm, LocalDate startDate, LocalDate endDate) {
		List<Event> eventInformation = new ArrayList<Event>();
		// 日付のみ入力されていたら
		if (startDate != null && endDate != null && searchByDateForm.getWords() == "") {
			eventInformation = eventRepo.findByEventDateBetweenOrderByEventDate(startDate.atStartOfDay(),
					LocalDateTime.of(endDate, LocalTime.MAX));
			return eventInformation;
		}
		// ワードのみ入力されてたら
		else if (searchByDateForm.getWords() != "" && startDate == null && endDate == null) {
			eventInformation = eventRepo.findByNameContainingOrDescriptionContainingOrderByEventDate(
					searchByDateForm.getWords(), searchByDateForm.getWords());

			return eventInformation;
		}
		// ワードと日付両方が入力されていたら
		else if (startDate != null && endDate != null && searchByDateForm.getWords() != "") {
			List<Event> eventKeyWord = eventRepo.findByNameContainingAndEventDateBetweenOrderByEventDate(
					searchByDateForm.getWords(), startDate.atStartOfDay(), LocalDateTime.of(endDate, LocalTime.MAX));

			List<Event> eventKeyWordAndDate = eventRepo.findByDescriptionContainingAndEventDateBetweenOrderByEventDate(
					searchByDateForm.getWords(), startDate.atStartOfDay(), LocalDateTime.of(endDate, LocalTime.MAX));

			eventKeyWord.addAll(eventKeyWordAndDate);

			// 重複要素を削除（上記のままだとname, description, dateそれぞれの結果表示）
			eventInformation = eventKeyWord.stream().distinct().collect(Collectors.toList());

			return eventInformation;
		}

		// 何も入力されていなかったら
		else {
			eventInformation = eventRepo.findAllByOrderByEventDate();
			return eventInformation;
		}
	}

	// イベントの時間選択のリスト
	public List<String> timeList() {
		List<LocalTime> timeList = new ArrayList<>();
		LocalTime start = LocalTime.of(10, 00);
		LocalTime end = LocalTime.of(20, 00);
		Long step = (long) 30;
		timeList = timeUtility.timeList(start, end, step);
		List<String> timeListToString = new ArrayList<>();
		for (LocalTime time : timeList) {
			DateTimeFormatter hourMinutes = DateTimeFormatter.ofPattern("HH:mm");
			String formatTime = time.format(hourMinutes);
			timeListToString.add(formatTime);
		}
		return timeListToString;
	}

	// StoreのIDから現在時刻以降のイベントを店舗ごとに取得する
	public List<Event> findEventsByStoreId(Long id) {
		return eventRepo.findEventsByStoreId(id, LocalDateTime.now());
	}
	
	

	// StoreのIDから現在時刻以前のイベントを店舗ごとに取得する
	public List<Event> findEventsByStoreIdOrderByDesc(Long id) {
		return eventRepo.findEventsByStoreIdOrderByDesc(id, LocalDateTime.now());
	}

	public List<Event> findByStoreIdBetWeen(Long id, LocalDate date) {
		return eventRepo.findEventsByStoreIdBetween(id, date.atStartOfDay(), date.atTime(LocalTime.MAX));
	}

	public List<Event> findByStoreIdBetWeen(Long id, LocalDate date, LocalDate dateEnd) {
		return eventRepo.findEventsByStoreIdBetween(id, date.atStartOfDay(), dateEnd.atTime(LocalTime.MAX));
	}
	
	// スペースIDから現在時刻以降のイベント取得
	public List<Event> findEventsByEventSpaceId(Long eventSpaceId, LocalDateTime now){
		return eventRepo.findEventsByEventSpaceId(eventSpaceId, now);
	}

}
