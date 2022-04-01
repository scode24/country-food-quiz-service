package com.quiz.entity.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "session_tracker", schema = "public")
public class SessionTracker {

	@Id
	private Long id;
	private String username;
	private Timestamp lastUpdateTimestamp;
	@Column(length = 1091)
	private String processIndexList;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getProcessIndexList() {
		return processIndexList;
	}

	public void setProcessIndexList(String processIndexList) {
		this.processIndexList = processIndexList;
	}

	public Timestamp getLastUpdateTimestamp() {
		return lastUpdateTimestamp;
	}

	public void setLastUpdateTimestamp(Timestamp lastUpdateTimestamp) {
		this.lastUpdateTimestamp = lastUpdateTimestamp;
	}
}
