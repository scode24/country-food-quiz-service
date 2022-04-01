package com.quiz.model;

public class NextQuestionResponse {

	private Long questionId;
	private String question;
	private String[] options;

	public NextQuestionResponse(Long questionId, String question, String[] options) {
		super();
		this.questionId = questionId;
		this.question = question;
		this.options = options;
	}

	public Long getQuestionId() {
		return questionId;
	}

	public void setQuestionId(Long questionId) {
		this.questionId = questionId;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String[] getOptions() {
		return options;
	}

	public void setOptions(String[] options) {
		this.options = options;
	}
}
