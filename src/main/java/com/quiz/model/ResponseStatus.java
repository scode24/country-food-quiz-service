package com.quiz.model;

public class ResponseStatus {
	private String statusMsg;
	
	public ResponseStatus(String statusMsg) {
		this.statusMsg = statusMsg;
	}

	public String getStatusMsg() {
		return statusMsg;
	}

	public void setStatusMsg(String statusMsg) {
		this.statusMsg = statusMsg;
	}
}
