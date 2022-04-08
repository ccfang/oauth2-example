package com.example.demo.service;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.UserInfo;
import com.example.demo.repository.UserInfoRepository;

@Service
public class UserInfoService {

	private UserInfoRepository userInfoRepository;

	public UserInfoService(@Autowired UserInfoRepository userInfoRepository) {
		this.userInfoRepository = userInfoRepository;
	}

	@Transactional
	public UserInfo save(UserInfo userInfo) {
		return userInfoRepository.save(userInfo);
	}

	public Optional<UserInfo> findByUserId(String userId) {
		return userInfoRepository.findByUserId(userId);
	}
}
