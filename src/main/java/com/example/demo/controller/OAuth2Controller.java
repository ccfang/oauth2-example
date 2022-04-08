package com.example.demo.controller;

import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import com.example.demo.entity.UserInfo;
import com.example.demo.model.LineNotifyResponse;
import com.example.demo.model.LineProfileResponse;
import com.example.demo.model.LineTokenResponse;
import com.example.demo.properties.LineNotifyProperties;
import com.example.demo.properties.LineProperties;
import com.example.demo.service.UserInfoService;

@Controller
@RequestMapping("/oauth2")
public class OAuth2Controller {

	private RestTemplate restTemplate;

	private LineProperties lineProperties;

	private LineNotifyProperties lineNotifyProperties;

	private UserInfoService userInfoService;

	@Autowired
	public OAuth2Controller(LineProperties lineProperties, UserInfoService userInfoService,
			LineNotifyProperties lineNotifyProperties, RestTemplateBuilder restTemplateBuilder) {
		this.lineProperties = lineProperties;
		this.lineNotifyProperties = lineNotifyProperties;
		this.userInfoService = userInfoService;
		this.restTemplate = restTemplateBuilder.build();
	}

	@GetMapping("/callback")
	public String callback(@RequestParam("code") String code, @RequestParam("state") String state,
			HttpSession session) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add("grant_type", "authorization_code");
		map.add("code", code);
		map.add("client_id", lineProperties.getClientId());
		map.add("client_secret", lineProperties.getClientSecret());
		map.add("redirect_uri", lineProperties.getRedirectUri());

		HttpEntity<MultiValueMap<String, String>> tokenRequest = new HttpEntity<>(map, headers);
		ResponseEntity<LineTokenResponse> tokenResponse = restTemplate.postForEntity(lineProperties.getTokenUrl(),
				tokenRequest, LineTokenResponse.class);
		String accessToken = tokenResponse.getBody().getAccess_token();

		headers = new HttpHeaders();
		headers.setBearerAuth(accessToken);
		HttpEntity<String> request = new HttpEntity<>(headers);
		ResponseEntity<LineProfileResponse> profileResponse = restTemplate.exchange("https://api.line.me/v2/profile",
				HttpMethod.GET, request, LineProfileResponse.class);
		LineProfileResponse lineProfile = profileResponse.getBody();
		UserInfo userInfo;
		Optional<UserInfo> optional = userInfoService.findByUserId(lineProfile.getUserId());
		if (optional.isPresent()) {
			userInfo = optional.get();
			userInfo.setAccessToken(accessToken);
		} else {
			userInfo = new UserInfo(lineProfile.getUserId(), lineProfile.getDisplayName(), accessToken);
		}
		userInfoService.save(userInfo);
		session.setAttribute("userInfo", userInfo);
		return "redirect:/ok";
	}

	@GetMapping("/notify/callback")
	public String notifyCallback(@RequestParam("code") String code, @RequestParam("state") String state,
			HttpSession session) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add("grant_type", "authorization_code");
		map.add("code", code);
		map.add("client_id", lineNotifyProperties.getClientId());
		map.add("client_secret", lineNotifyProperties.getClientSecret());
		map.add("redirect_uri", lineNotifyProperties.getRedirectUri());

		HttpEntity<MultiValueMap<String, String>> tokenRequest = new HttpEntity<>(map, headers);
		ResponseEntity<LineTokenResponse> tokenResponse = restTemplate.postForEntity(lineNotifyProperties.getTokenUrl(),
				tokenRequest, LineTokenResponse.class);
		String accessToken = tokenResponse.getBody().getAccess_token();

		UserInfo userInfo = (UserInfo) session.getAttribute("userInfo");
		userInfo.setNotifyAccessToken(accessToken);
		session.setAttribute("userInfo", userInfo);
		userInfoService.save(userInfo);
		return "redirect:/ok";
	}

	@PostMapping("/notify")
	public ResponseEntity<LineNotifyResponse> notify(HttpSession session, @RequestBody String message) {
		UserInfo userInfo = (UserInfo) session.getAttribute("userInfo");
		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add("message", message);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.setBearerAuth(userInfo.getNotifyAccessToken());
		HttpEntity<MultiValueMap<String, String>> notifyRequest = new HttpEntity<>(map, headers);
		return restTemplate.postForEntity("https://notify-api.line.me/api/notify", notifyRequest,
				LineNotifyResponse.class);
	}
}
