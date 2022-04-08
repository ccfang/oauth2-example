package com.example.demo.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "USER_INFO")
public class UserInfo {

	@Id
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	@Column(name = "ID", length = 36)
	private String id;

	@Column(name = "USER_ID", length = 100)
	private String userId;

	@Column(name = "USER_NAME", length = 100)
	private String userName;

	@Column(name = "ACCESS_TOKEN", length = 500)
	private String accessToken;

	@Column(name = "NOTIFY_ACCESS_TOKEN", length = 500)
	private String notifyAccessToken;

	public UserInfo() {

	}

	public UserInfo(String userId, String userName, String accessToken) {
		this.userId = userId;
		this.userName = userName;
		this.accessToken = accessToken;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getNotifyAccessToken() {
		return notifyAccessToken;
	}

	public void setNotifyAccessToken(String notifyAccessToken) {
		this.notifyAccessToken = notifyAccessToken;
	}

	public boolean isSubscribed() {
		return this.getNotifyAccessToken() != null;
	}
}
