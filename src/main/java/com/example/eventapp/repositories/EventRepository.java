package com.example.eventapp.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.eventapp.entities.Event;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

	// 全イベントの日付降順
	public List<Event> findAllByOrderByEventDate();
	
	// 全イベントの日付降順
	public List<Event> findAllByOrderByEventDateDesc();

	// ある日付の営業時間内のイベントを取得
	public List<Event> findByEventspaceIdIsAndEventDateBetween(Long id, LocalDateTime startTime, LocalDateTime endTime);

	// 指定の日時以降のイベントのみ取得
	public List<Event> findByEventEndDateAfterOrderByEventDate(LocalDateTime time);

	// 指定の日時より過去のイベントのみ取得
	public List<Event> findByEventEndDateBeforeOrderByEventDateDesc(LocalDateTime time);

	// 当イベント以外のイベントを取得
	public List<Event> findByIdIsNot(Long id);

	// 全イベントの名前、説明を取得
	public List<Event> findByNameContainingOrDescriptionContainingOrderByEventDate(String name, String description);

	// 全イベントの日付を取得
	public List<Event> findByEventDateBetweenOrderByEventDate(LocalDateTime startTime, LocalDateTime endTime);

	// 全イベントの名前、日にちを取得
	public List<Event> findByNameContainingAndEventDateBetweenOrderByEventDate(String name, LocalDateTime startTime,
			LocalDateTime endTime);

	// 全イベントの説明、日にちを取得
	public List<Event> findByDescriptionContainingAndEventDateBetweenOrderByEventDate(String description,
			LocalDateTime startTime, LocalDateTime endTime);
		
	// スペースIDから現在時刻以降のイベント取得
	@Query(value = "select e from Event e join EventSpace sp on e.eventspace = sp.id where sp.id = :eventspaceId and e.eventEndDate >= :now order by e.eventDate")
	public List<Event> findEventsByEventSpaceId(@Param("eventspaceId") Long id, @Param("now") LocalDateTime now);

	// StoreのIDから現在時刻以降のイベントを店舗ごとに取得する
	@Query(value = "select e from Event e join EventSpace sp on e.eventspace = sp.id join Store st on sp.store = st.id where st.id = :storeId and e.eventEndDate >= :now order by e.eventDate")
	public List<Event> findEventsByStoreId(@Param("storeId") Long id, @Param("now") LocalDateTime now);

	// StoreのIDから現在時刻以前のイベントを店舗ごとに取得する
	@Query(value = "select e from Event e join EventSpace sp on e.eventspace = sp.id join Store st on sp.store = st.id where st.id = :storeId and e.eventEndDate < :now order by e.eventDate desc")
	public List<Event> findEventsByStoreIdOrderByDesc(@Param("storeId") Long id, @Param("now") LocalDateTime now);

	// StoreのIDから現在時刻以降のイベントを店舗ごとに取得する
	@Query(value = "select e from Event e join EventSpace sp on e.eventspace = sp.id join Store st on sp.store = st.id where st.id = :storeId and e.eventEndDate between :start and :end order by e.eventDate")
	public List<Event> findEventsByStoreIdBetween(@Param("storeId") Long id, @Param("start") LocalDateTime now,
			@Param("end") LocalDateTime end);

}
