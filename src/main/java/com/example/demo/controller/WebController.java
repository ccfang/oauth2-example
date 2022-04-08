package com.example.demo.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.entity.UserInfo;
import com.example.demo.properties.LineNotifyProperties;
import com.example.demo.properties.LineProperties;

@Controller
@RequestMapping("/")
public class WebController {

	private LineProperties lineProperties;

	private LineNotifyProperties lineNotifyProperties;

	@Autowired
	public WebController(LineProperties lineProperties, LineNotifyProperties lineNotifyProperties) {
		this.lineProperties = lineProperties;
		this.lineNotifyProperties = lineNotifyProperties;
	}

	@GetMapping("/login")
	public String login(ModelMap model) {
		model.addAttribute("authUrl", lineProperties.getAuthUrl());
		model.addAttribute("clientId", lineProperties.getClientId());
		model.addAttribute("redirectUri", lineProperties.getRedirectUri());
		return "login.html";
	}

	@GetMapping("/")
	public String index(HttpSession session, ModelMap model) {
		UserInfo userInfo = (UserInfo) session.getAttribute("userInfo");
		if (userInfo != null) {
			model.addAttribute("userName", userInfo.getUserName());
			model.addAttribute("authUrl", lineNotifyProperties.getAuthUrl());
			model.addAttribute("clientId", lineNotifyProperties.getClientId());
			model.addAttribute("redirectUri", lineNotifyProperties.getRedirectUri());
			model.addAttribute("isSubscribed", userInfo.isSubscribed());
			return "index.html";
		} else {
			return "redirect:/login";
		}
	}

	@GetMapping("/ok")
	public String ok() {
		return "ok.html";
	}
}
