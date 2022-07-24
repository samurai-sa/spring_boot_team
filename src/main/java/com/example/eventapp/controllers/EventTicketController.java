package com.example.eventapp.controllers;

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
import com.example.eventapp.entities.Event;
import com.example.eventapp.entities.EventTicket;
import com.example.eventapp.entities.Store;
import com.example.eventapp.form.QRForm;
import com.example.eventapp.form.TicketNumberForm;
import com.example.eventapp.services.EventService;
import com.example.eventapp.services.EventTicketService;

@Controller
@RequestMapping("/ticket")
public class EventTicketController {

	private final EventTicketService eventTicketService;
	private final EventService eventService;
	private final PostSlackMessage postSlackMessage;

	public EventTicketController(
			EventTicketService eventTicketService,
			EventService eventService,
			PostSlackMessage postSlackMessage) {
		this.eventTicketService = eventTicketService;
		this.eventService = eventService;
		this.postSlackMessage = postSlackMessage;
	}

	// 整理券番号の生成/登録処理 → 整理券の印刷用画面
	@PostMapping("/generateNumber/{id}")
	public String generateNumber(
			@AuthenticationPrincipal(expression = "store") Store store,
			@PathVariable("id") Long id,
			RedirectAttributes redirectAttributes,
			Model model) {
		Event event = eventService.findById(id);
		// 整理券作成 → 番号のみ生成に変更
		// EventTicket newEventTicket = eventTicketService.generate(event);
		String ticketNum = eventTicketService.generateTicketNum(id);
		redirectAttributes.addAttribute("ticketNum", ticketNum);
		model.addAttribute("store", store);
		// model.addAttribute("ticket", newEventTicket);
		return "redirect:/ticket/printEventTicket/{id}";
	}

	// 整理券印刷画面表示
	@GetMapping("/printEventTicket/{id}")
	public String printEventTicket(
			@AuthenticationPrincipal(expression = "store") Store store,
			@PathVariable("id") Long id,
			@ModelAttribute("ticketNum") String ticketNum,
			Model model) {
		Event event = eventService.findById(id);

		byte[] byteImage = eventTicketService.qrCreate(ticketNum);
		String image = eventTicketService.convertByteToBase64(byteImage);

		model.addAttribute("title", "整理券番号印刷");
		model.addAttribute("image", image);
		model.addAttribute("ticketNum", ticketNum);
		model.addAttribute("event", event);
		model.addAttribute("store", store);
		model.addAttribute("main", "tickets/printEventTicket::main");
		return "layout/layout";
	}

	// 登録処理
	@PostMapping("/{eventId}/{ticketNum}/register")
	public String eventTicketRegister(@PathVariable("eventId") Long eventId,
			@PathVariable("ticketNum") String ticketNum, Model model) {
		Event event = eventService.findById(eventId);
		eventTicketService.register(eventId, ticketNum);

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

	// QR認証画面
	@GetMapping("/qr/{id}")
	public String qrForm(
			@AuthenticationPrincipal(expression = "store") Store store,
			@ModelAttribute QRForm qrForm,
			@PathVariable("id") Long id,
			String authenticationResult,
			Model model) {
		model.addAttribute("title", "QR認証");
		model.addAttribute("qrForm", qrForm);
		model.addAttribute("id", id);
		model.addAttribute("store", store);
		model.addAttribute("authenticationResult", authenticationResult);
		model.addAttribute("main", "tickets/qrForm::main");
		return "layout/layout";
	}

	// QR認証 → QR認証実行（QR認証画面）
	@PostMapping("/qrToNumber/{id}")
	public String qrToNumber(
			@AuthenticationPrincipal(expression = "store") Store store,
			@ModelAttribute TicketNumberForm ticketNumberForm,
			@Valid QRForm qrForm,
			BindingResult bindingResult,
			@PathVariable("id") Long id,
			Model model) {
		if (bindingResult.hasErrors()) {
			return qrForm(store, qrForm, id, null, model);
		}

		// QRからURLを取得するメソッド
		String urlFromQr = eventTicketService.qrRead(qrForm.getQr());

		// ticketNumberForm.getTicketNum()とDBのデータを比較して認証するメソッド
		String authenticationResult = eventTicketService.authentication(urlFromQr, id);

		if (urlFromQr.equals("nullError")) {
			return qrForm(store, qrForm, id, null, model);
		} else if (urlFromQr.equals("error")) {
			return authenticateNumber(store, ticketNumberForm, id, authenticationResult, model);
		} else if (!urlFromQr.isEmpty()) {
			ticketNumberForm.setTicketNum(urlFromQr);
		}

		// 認証成功でQR認証画面、認証失敗で番号認証画面へ遷移
		if (authenticationResult == "認証できました") {
			return qrForm(store, qrForm, id, authenticationResult, model);
		} else {
			return qrForm(store, qrForm, id, authenticationResult, model);
		}
	}

	// 整理券認証画面
	@GetMapping("/authenticateNumber/{id}")
	public String authenticateNumber(
			@AuthenticationPrincipal(expression = "store") Store store,
			@ModelAttribute TicketNumberForm ticketNumberForm,
			@PathVariable("id") Long id,
			String authenticationResult,
			Model model) {
		model.addAttribute("title", "整理券番号認証");
		model.addAttribute("ticketNumberForm", ticketNumberForm);
		model.addAttribute("authenticationResult", authenticationResult);
		model.addAttribute("id", id);
		model.addAttribute("store", store);
		model.addAttribute("main", "tickets/ticketNumberForm::main");
		return "layout/layout";
	}

	// 入力整理番号の認証
	@PostMapping("/authentication/{id}")
	public String authentication(
			@AuthenticationPrincipal(expression = "store") Store store,
			@Valid TicketNumberForm ticketNumberForm,
			RedirectAttributes redirectAttributes,
			@PathVariable("id") Long id,
			Model model) {
		// ticketNumberForm.getTicketNum()とDBのデータを比較して認証するメソッド
		String authenticationResult = eventTicketService.authentication(ticketNumberForm.getTicketNum(), id);
		return authenticateNumber(store, ticketNumberForm, id, authenticationResult, model);
	}
}
