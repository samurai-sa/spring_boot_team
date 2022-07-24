package com.example.eventapp.controllers;

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

import com.example.eventapp.entities.EventSpace;
import com.example.eventapp.entities.Store;
import com.example.eventapp.form.EventSpaceForm;
import com.example.eventapp.services.EventSpaceService;

@Controller
@RequestMapping("/space")
public class EventSpaceController {

	private final EventSpaceService eventSpaceService;

	public EventSpaceController(
			EventSpaceService eventSpaceService) {
		this.eventSpaceService = eventSpaceService;
	}

	// イベントスペース一覧
	@GetMapping("/eventSpaceList")
	public String eventSpaceList(
			@AuthenticationPrincipal(expression = "store") Store store,
			Model model) {
		List<EventSpace> eventSpaces = eventSpaceService.findAll();
		model.addAttribute("title", "イベントスペース一覧");
		model.addAttribute("eventSpaces", eventSpaces);
		model.addAttribute("store", store);
		model.addAttribute("main", "spaces/eventSpaceList::main");
		return "layout/layout";
	}

	// イベントスペース作成
	@GetMapping("/eventSpaceCreate/{storeId}")
	public String eventSpaceCreate(
			@AuthenticationPrincipal(expression = "store") Store store,
			@ModelAttribute EventSpaceForm eventSpaceForm,
			@PathVariable("storeId") Long storeId,
			Model model) {
		model.addAttribute("title", "イベントスペース作成");
		model.addAttribute("storeId", storeId);
		model.addAttribute("store", store);
		model.addAttribute("eventSpaceForm", eventSpaceForm);
		model.addAttribute("main", "spaces/eventSpaceCreate::main");
		return "layout/layout";
	}

	@PostMapping("/eventSpaceCreate/{storeId}")
	public String eventSpaceCreateProcess(
			@AuthenticationPrincipal(expression = "store") Store store,
			@Valid EventSpaceForm eventSpaceForm,
			BindingResult bindingResult,
			RedirectAttributes redirectAttributes,
			@PathVariable("storeId") Long storeId,
			Model model) {
		if (bindingResult.hasErrors()) {
			return eventSpaceCreate(store, eventSpaceForm, storeId, model);
		}
		EventSpace eventSpace = eventSpaceService.register(
				storeId, eventSpaceForm.getName(),
				eventSpaceForm.getCapacity());
		redirectAttributes.addFlashAttribute("message", "イベントスペース: " + eventSpace.getName() + " を作成しました");
		return "redirect:/store/";
	}

	// イベントスペース編集画面
	@GetMapping("/eventSpaceEdit/{eventSpaceId}")
	public String eventSpaceEdit(
			@AuthenticationPrincipal(expression = "store") Store store,
			@PathVariable("eventSpaceId") Long eventSpaceId,
			@ModelAttribute EventSpaceForm eventSpaceForm,
			Model model) {
		EventSpace eventSpace = eventSpaceService.findById(eventSpaceId);
		eventSpaceForm.setName(eventSpace.getName());
		eventSpaceForm.setCapacity(eventSpace.getCapacity());
		model.addAttribute("eventSpace", eventSpace);
		model.addAttribute("title", "イベントスペース編集");
		model.addAttribute("eventSpaceForm", eventSpaceForm);
		model.addAttribute("eventSpaceId", eventSpaceId);
		model.addAttribute("store", store);
		model.addAttribute("main", "spaces/eventSpaceEdit::main");
		return "layout/layout";
	}

	// イベントスペース編集POST先
	@PostMapping("/eventSpaceEdit/{eventSpaceId}")
	public String eventSpaceEditProcess(
			@AuthenticationPrincipal(expression = "store") Store store,
			@PathVariable("eventSpaceId") Long eventSpaceId,
			@Valid EventSpaceForm eventSpaceForm,
			RedirectAttributes redirectAttributes,
			BindingResult bindingResult,
			Model model) {
		if (bindingResult.hasErrors()) {
			return eventSpaceEdit(store, eventSpaceId, eventSpaceForm, model);
		}
		EventSpace eventSpace = eventSpaceService.update(eventSpaceId, eventSpaceForm.getName(), eventSpaceForm.getCapacity());
		redirectAttributes.addFlashAttribute("message", "イベントスペース: " + eventSpace.getName() + " を更新しました");
		return "redirect:/store/";
	}

	// イベントスペース削除
	@PostMapping("/eventSpaceDelete/{eventSpaceId}")
	public String eventSpaceDelete(@PathVariable("eventSpaceId") Long eventSpaceId,
			RedirectAttributes redirectAttributes,
			Model model) {
		EventSpace eventSpace = eventSpaceService.findById(eventSpaceId);
		eventSpaceService.delete(eventSpaceId);
		redirectAttributes.addFlashAttribute("message", "イベントスペース: " + eventSpace.getName() + " を削除しました");
		return "redirect:/store/";
	}
}
