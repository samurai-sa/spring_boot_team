package com.example.eventapp.controllers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.eventapp.api.PostSlackMessage;
import com.example.eventapp.calendar.Month;
import com.example.eventapp.entities.Event;
import com.example.eventapp.entities.EventSpace;
import com.example.eventapp.entities.Store;
import com.example.eventapp.form.EventForm;
import com.example.eventapp.form.SelectStoreForm;
import com.example.eventapp.form.search.SearchByDateForm;
import com.example.eventapp.services.EventService;
import com.example.eventapp.services.EventSpaceService;
import com.example.eventapp.services.StoreService;

@Controller
@RequestMapping("/event")
public class EventController {

	private final EventService eventService;
	private final EventSpaceService eventSpaceService;
	private final StoreService storeService;
	private final PostSlackMessage postSlackMessage;

	public EventController(
			EventService eventService,
			EventSpaceService eventSpaceService,
			StoreService storeService,
			PostSlackMessage postSlackMessage) {
		this.eventService = eventService;
		this.eventSpaceService = eventSpaceService;
		this.storeService = storeService;
		this.postSlackMessage = postSlackMessage;
	}

	// イベント一覧
	@GetMapping("/")
	public String eventList(
			@AuthenticationPrincipal(expression = "store") Store store,
			@ModelAttribute SelectStoreForm selectStoreForm,
			Long storeId,
			Long eventSpaceId,
			Model model) {
		List<Store> stores = storeService.findAll();
		List<Event> EventsAfterToday = eventService.findEventAfterToday();
		List<Event> EventsAfterTodayParStore = new ArrayList<>();
		Long storeNum = store.getId();
		Long storeNow = store.getId();
		if(eventSpaceId==null || eventSpaceId==0) {		
			if (storeId == null) {
				EventsAfterTodayParStore = eventService.findEventsByStoreId(store.getId());
			} else if (storeId != null && storeId != 0) {
				storeNum = storeId;
				EventsAfterTodayParStore = eventService.findEventsByStoreId(storeNum);
			} else if (storeId != null && storeId == 0) {
				storeNum = storeId;
				EventsAfterTodayParStore = EventsAfterToday;
			}
		}else if(eventSpaceId!=null) {
			storeNum = storeId;
			EventsAfterTodayParStore = eventService.findEventsByEventSpaceId(eventSpaceId, LocalDateTime.now());
		}
		
		List<EventSpace> eventSpaceList = new ArrayList<>();
		if (storeId == null) {
			eventSpaceList = eventSpaceService.findByStoreId(store.getId());
		}else if (storeId != null && storeId != 0) {
			eventSpaceList = eventSpaceService.findByStoreId(storeId);
		}
		model.addAttribute("storeName", store.getName());
		model.addAttribute("storeId", storeNow);
		model.addAttribute("title", "イベント一覧");
		model.addAttribute("eventSpaceId", eventSpaceId);
		model.addAttribute("eventSpaceList", eventSpaceList);
		model.addAttribute("storeId", storeNow);
		model.addAttribute("selectStoreForm", selectStoreForm);
		model.addAttribute("storeNum", storeNum);
		model.addAttribute("stores", stores);
		model.addAttribute("store", store);
		model.addAttribute("events", EventsAfterTodayParStore);
		model.addAttribute("main", "events/eventList::main");
		return "layout/layout";
	}

	// スラックでお知らせ
	@PostMapping("/slack/{eventId}")
	public String postSlack(
			@PathVariable("eventId") Long eventId) {
		Event event = eventService.findById(eventId);
		// チケットの参加上限と発行済み数が同じ場合、スラックでお知らせする
		if (event.getPrintedTicket() >= event.getMaxTicket()) {
			try {
				postSlackMessage.postMessage(event.getName(), event.getEventspace().getStore().getName());
			} catch (Exception e) {
				System.out.println("Slackにメッセージを送れませんでした");
			}
		}
		return "redirect:/event/";
	}

	// 過去のイベント一覧
	@GetMapping("/pastList")
	public String eventPastList(
			@AuthenticationPrincipal(expression = "store") Store store,
			@ModelAttribute SelectStoreForm selectStoreForm,
			Long storeId,
			Model model) {
		List<Store> stores = storeService.findAll();
		List<Event> EventsBeforeToday = eventService.findEventBeforeToday();
		List<Event> EventsBeforeTodayParStore = new ArrayList<>();
		Long storeNum = store.getId();
		if (storeId == null) {
			EventsBeforeTodayParStore = eventService.findEventsByStoreIdOrderByDesc(store.getId());
		} else if (storeId != null && storeId != 0) {
			storeNum = storeId;
			EventsBeforeTodayParStore = eventService.findEventsByStoreIdOrderByDesc(storeNum);
		} else if (storeId != null && storeId == 0) {
			storeNum = storeId;
			EventsBeforeTodayParStore = EventsBeforeToday;
		}
		model.addAttribute("title", "過去のイベント一覧");
		model.addAttribute("selectStoreForm", selectStoreForm);
		model.addAttribute("storeNum", storeNum);
		model.addAttribute("stores", stores);
		model.addAttribute("store", store);
		model.addAttribute("events", EventsBeforeTodayParStore);
		model.addAttribute("main", "events/eventPastList::main");
		return "layout/layout";
	}

	// イベント作成(店舗一覧の詳細のハンドラ)
	@GetMapping({ "/eventCreate", "/eventCreate/{eventSpaceId}" })
	public String eventCreateForm(
			@AuthenticationPrincipal(expression = "store") Store store,
			@PathVariable(name = "eventSpaceId", required = false) Long eventSpaceId,
			@ModelAttribute EventForm eventForm,
			Model model) {
		List<String> timeList = new ArrayList<>();
		timeList = eventService.timeList();
		List<EventSpace> eventSpaces = eventSpaceService.findAllByOrderByStoreId();
		model.addAttribute("eventSpaces", eventSpaces);
		model.addAttribute("timeList", timeList);
		model.addAttribute("title", "イベント作成");
		model.addAttribute("eventSpaceId", eventSpaceId);
		model.addAttribute("store", store);
		model.addAttribute("eventForm", eventForm);
		model.addAttribute("main", "events/eventCreate::main");
		return "layout/layout";
	}

	@PostMapping("/eventCreate")
	public String eventCreate(
			@AuthenticationPrincipal(expression = "store") Store store,
			@Valid EventForm eventForm,
			BindingResult bindingResult,
			RedirectAttributes redirectAttributes,
			Model model) {
		// 開催時間と終了時間の営業時間内指定のバリデーションはHTMLでやっています
		if (bindingResult.hasErrors()) {
			return eventCreateForm(store, eventForm.getEventspaceId(), eventForm, model);
		}
		// イベント時間重複確認
		try {
			eventService.checkDuplicate(
					eventForm.getEventDate(),
					eventForm.getEventTime(),
					eventForm.getEventEndTime(),
					eventForm.getEventspaceId(),
					null,
					eventForm.getMaxTicket());

		} catch (IllegalArgumentException e) {
			bindingResult.rejectValue("maxTicket", "eventForm.maxTicket.overEventSpaceCapacity");
			return eventCreateForm(store, eventForm.getEventspaceId(), eventForm, model);
		} catch (RuntimeException e) {
			bindingResult.rejectValue("eventEndTime", "eventForm.eventEndTime.sameTime");
			return eventCreateForm(store, eventForm.getEventspaceId(), eventForm, model);
		} catch (Exception e) {
			bindingResult.rejectValue("eventTime", "eventForm.eventTime.duplicate");
			bindingResult.rejectValue("eventEndTime", "eventForm.eventEndTime.duplicate");
			return eventCreateForm(store, eventForm.getEventspaceId(), eventForm, model);
		}

		Event event = eventService.register(
				store.getName(),
				eventForm.getEventspaceId(),
				eventForm.getName(),
				eventForm.getDescription(),
				eventForm.getEventDate(),
				eventForm.getEventTime(),
				eventForm.getEventEndTime(),
				eventForm.getMaxTicket());

		redirectAttributes.addFlashAttribute("message", "イベント： " + event.getName() + " を作成しました");
		return "redirect:/event/";
	}

	// イベントの編集画面
	@GetMapping("/eventEditing/{eventId}")
	// 指定のイベントのIDを取得
	public String eventEdit(
			@AuthenticationPrincipal(expression = "store") Store store,
			@PathVariable("eventId") Long eventId,
			@ModelAttribute EventForm eventForm,
			Model model) {

		// 指定のイベントのIDを取得
		Event event = eventService.findById(eventId);
		List<EventSpace> eventSpaces = eventSpaceService.findAllByOrderByStoreId();

		List<String> timeList = new ArrayList<>();
		timeList = eventService.timeList();

		// 取得した指定のイベント情報をフォームにSet
		eventForm.setEventspaceId(event.getEventspace().getId());
		eventForm.setName(event.getName());
		eventForm.setDescription(event.getDescription());
		eventForm.setEventDate(event.getEventDate().toLocalDate());
		eventForm.setEventTime(event.getEventDate().toLocalTime());
		eventForm.setEventEndTime(event.getEventEndDate().toLocalTime());
		eventForm.setMaxTicket(event.getMaxTicket());
		// HTMLに表示
		model.addAttribute("title", "イベントの編集");
		model.addAttribute("event", event);
		model.addAttribute("eventSpaceId", event.getEventspace().getId());
		model.addAttribute("eventSpaces", eventSpaces);
		model.addAttribute("store", store);
		model.addAttribute("timeList", timeList);
		model.addAttribute("eventForm", eventForm);
		model.addAttribute("main", "events/eventEdit::main");
		return "layout/layout";
	}

	// イベント更新処理
	@PostMapping("/eventEditing/{eventId}")
	public String eventEditing(
			@AuthenticationPrincipal(expression = "store") Store store,
			@PathVariable("eventId") Long eventId,
			@Valid EventForm eventForm,
			BindingResult bindingResult,
			RedirectAttributes redirectAttributes,
			Model model) {
		if (bindingResult.hasErrors()) {
			return eventEdit(store, eventId, eventForm, model);
		}
		// イベント時間重複確認
		try {
			eventService.checkDuplicate(
					eventForm.getEventDate(),
					eventForm.getEventTime(),
					eventForm.getEventEndTime(),
					eventForm.getEventspaceId(),
					eventId,
					eventForm.getMaxTicket());

		} catch (IllegalArgumentException e) {
			bindingResult.rejectValue("maxTicket", "eventForm.maxTicket.overEventSpaceCapacity");
			return eventEdit(store, eventId, eventForm, model);
		} catch (RuntimeException e) {
			bindingResult.rejectValue("eventEndTime", "eventForm.eventEndTime.sameTime");
			return eventEdit(store, eventId, eventForm, model);
		} catch (Exception e) {
			bindingResult.rejectValue("eventTime", "eventForm.eventTime.duplicate");
			bindingResult.rejectValue("eventEndTime", "eventForm.eventEndTime.duplicate");
			return eventEdit(store, eventId, eventForm, model);
		}

		eventService.eventUpdate(
				store.getName(),
				eventId,
				eventForm.getEventspaceId(),
				eventForm.getName(),
				eventForm.getDescription(),
				eventForm.getEventDate(),
				eventForm.getEventTime(),
				eventForm.getEventEndTime(),
				eventForm.getMaxTicket());

		Event event = eventService.findById(eventId);
		redirectAttributes.addFlashAttribute("message", "イベント: " + event.getName() + " を更新しました");
		return "redirect:/event/";
	}

	// イベント削除処理
	@PostMapping("/eventDelete/{eventId}")
	public String eventDelete(
			RedirectAttributes redirectAttributes,
			@PathVariable("eventId") Long eventId,
			Model model) {
		Event event = eventService.findById(eventId);
		eventService.delete(eventId);
		redirectAttributes.addFlashAttribute("message", "イベント: " + event.getName() + " を削除しました");
		return "redirect:/event/";
	}

	// 表示切替処理
	@PostMapping("/switchList")
	public String switchList(
			@Valid SelectStoreForm selectStoreForm,
			RedirectAttributes redirectAttributes) {
		List<EventSpace> eventSpaces;
		Long storeId;
		Long eventSpaceId = selectStoreForm.getEventspaceId();
		if( eventSpaceId==null) {
			eventSpaceId = (long) 0;
		}
		else if(eventSpaceId==0) {
			eventSpaceId = (long) 0;
		}else {
			eventSpaceId = selectStoreForm.getEventspaceId();
		}
		eventSpaces = eventSpaceService.findByStoreId(selectStoreForm.getStoreId());
		if(selectStoreForm.getStoreId()==null) {
		}else if(selectStoreForm.getStoreId()!=null) {
			storeId = selectStoreForm.getStoreId();
			
			if(eventSpaceId==0) {
				
			}else if(eventSpaceId!=0) {
				
				if(eventSpaceId!=0 && eventSpaces.contains(eventSpaceService.findById(eventSpaceId))) {
				}else {
					eventSpaceId = (long) 0;
				}
			}

			redirectAttributes.addAttribute("storeId", storeId);
		}
	
		if(eventSpaceId==null || eventSpaceId==0) {
			eventSpaceId = (long) 0;
			redirectAttributes.addAttribute("eventSpaceId", eventSpaceId);
		}else if(selectStoreForm.getEventspaceId()!=null) {
			eventSpaceId = selectStoreForm.getEventspaceId();
			redirectAttributes.addAttribute("eventSpaceId", eventSpaceId);
		}
		
		return "redirect:/event/";
	}

	@PostMapping("/switchPastList")
	public String switchPastList(
			@Valid SelectStoreForm selectStoreForm,
			RedirectAttributes redirectAttributes) {
		Long storeId = selectStoreForm.getStoreId();
		redirectAttributes.addAttribute("storeId", storeId);
		return "redirect:/event/pastList";
	}

	// 検索
	@GetMapping("/eventSearch")
	public String eventSearch(
			@AuthenticationPrincipal(expression = "store") Store store,
			@ModelAttribute SearchByDateForm searchByDateForm,
			Model model) {
		model.addAttribute("title", "イベント検索");
		model.addAttribute("searchByDateForm", searchByDateForm);
		model.addAttribute("store", store);
		model.addAttribute("storeName", store.getName());
		model.addAttribute("main", "events/eventSearch::main");
		return "layout/layout";
	}

	// 検索処理
	@PostMapping("/eventSearch")
	public String eventSearchProcess(
			@AuthenticationPrincipal(expression = "store") Store store,
			@Valid SearchByDateForm searchByDateForm, BindingResult bindingResult,
			Long storeId,
			Model model) {
		if (bindingResult.hasErrors()) {
			return eventSearch(store, searchByDateForm, model);
		}

		storeId = store.getId();

		List<Event> eventsInformation = eventService.findByEventRange(searchByDateForm,
				searchByDateForm.getTargetDateStart(),
				searchByDateForm.getTargetDateEnd());

		model.addAttribute("title", "イベント検索");
		model.addAttribute("rangeForm", searchByDateForm);
		model.addAttribute("storeId", storeId);
		model.addAttribute("store", store);
		model.addAttribute("storeName", store.getName());
		model.addAttribute("events", eventsInformation);
		model.addAttribute("main", "events/eventSearch::searched");

		return "layout/layout";

	}

	@GetMapping("/calendar/{storeId}/{year}/{month}")
	public String eventcalendar(@AuthenticationPrincipal(expression = "store") Store store,
			@PathVariable("storeId") Long storeId, @PathVariable("year") int year, @PathVariable("month") int month,
			Model model) {

		Store targetStore = storeService.findById(storeId).orElseThrow();
		List<Store> stores = storeService.findAll();
		// パスからカレンダー生成
		Month calendar = new Month(year, month);
		calendar.setDate();

		// 指定された月のイベントを検索
		List<Event> events = eventService.findByStoreIdBetWeen(storeId, calendar.getFirstLocalDate(),
				calendar.getLastLocalDate());
		calendar.setEvent(events);

		model.addAttribute("stores", stores);
		model.addAttribute("title", "カレンダー - " + year + "年" + month + "月");
		model.addAttribute("store", store);
		model.addAttribute("targetStore", targetStore);
		model.addAttribute("calendar", calendar);
		model.addAttribute("main", "events/eventCalendar::main");

		return "layout/layout";
	}

	@GetMapping("/calendar/{storeId}/{year}/{month}/{date}")
	public String timeline(@AuthenticationPrincipal(expression = "store") Store store, @PathVariable("year") int year,
			@PathVariable("storeId") Long storeId,
			@PathVariable("month") int month,
			@PathVariable("date") int date, Model model) {

		Store targetStore = storeService.findById(storeId).orElseThrow();
		LocalDate targetDate = LocalDate.of(year, month, date);
		// イベントスペース取得
		List<EventSpace> eventSpaces = eventSpaceService.findByStoreId(storeId);
		// 店舗IDからイベント取得
		List<Event> events = eventService.findByStoreIdBetWeen(storeId, targetDate);
		model.addAttribute("title", "イベントタイムライン - " + year + "年" + month + "月" + date + "日");
		model.addAttribute("store", store);
		model.addAttribute("targetStore", targetStore);
		model.addAttribute("targetDate", targetDate);
		model.addAttribute("nextDate", targetDate.plusDays(1));
		model.addAttribute("previousDate", targetDate.minusDays(1));
		model.addAttribute("spaces", eventSpaces);
		model.addAttribute("events", events);
		model.addAttribute("main", "events/eventTimeLine::main");
		return "layout/layout";
	}
}
