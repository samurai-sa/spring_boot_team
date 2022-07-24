package com.example.eventapp.controllers;

import java.util.List;

import javax.validation.Valid;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.eventapp.entities.Store;
import com.example.eventapp.form.SignUpForm;
import com.example.eventapp.services.StoreService;

@Controller
@RequestMapping("/store")
public class StoreController {

	private final StoreService storeService;

	public StoreController(StoreService storeService) {
		this.storeService = storeService;
	}

	@GetMapping("/signUp")
	public String signUpForm(
			@ModelAttribute("signUp") SignUpForm signUpForm,
			Model model) {
		model.addAttribute("title", "サインアップ");
		String notLogin = "notLogin";
		model.addAttribute("notLogin", notLogin);
		model.addAttribute("signUpForm", signUpForm);
		model.addAttribute("main", "stores/signUp::main");
		return "layout/layout";
	}

	@PostMapping("/signUp")
	public String signUp(
			@ModelAttribute("signUpForm") @Valid SignUpForm signUpForm,
			BindingResult bindingResult,
			RedirectAttributes redirectAttributes,
			Model model) {
		if (bindingResult.hasErrors()) {
			return signUpForm(signUpForm, model);
		}
		String[] roles = { "ROLE_USER", "ROLE_ADMIN" };
		Store store = storeService.register(signUpForm.getName(), signUpForm.getPassword(), roles);
		redirectAttributes.addFlashAttribute("message", "店舗ID： " + store.getId());
		return "redirect:/store/login";
	}

	@GetMapping("/login")
	public String login(Model model) {
		model.addAttribute("title", "ログイン");
		String notLogin = "notLogin";
		model.addAttribute("notLogin", notLogin);
		model.addAttribute("main", "stores/login::main");
		return "layout/layout";
	}

	// 店舗の一覧表示
	// 店舗の詳細はhtmlで
	@GetMapping("/")
	public String storeList(
			@AuthenticationPrincipal(expression = "store") Store store,
			Model model) {
		List<Store> stores = storeService.findAll();
		model.addAttribute("storeId", store.getId());
		model.addAttribute("stores", stores);
		model.addAttribute("store", store);
		model.addAttribute("title", "店舗一覧");
		model.addAttribute("main", "stores/storeList::main");
		return "layout/layout";
	}
}
