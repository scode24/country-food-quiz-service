package com.quiz.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.quiz.model.ResponseStatus;

@ControllerAdvice
public class ServiceExceptionHandler {

	@ExceptionHandler(value = AdminConfigurationException.class)
	public ResponseEntity<ResponseStatus> handleAdminConfigurationException(AdminConfigurationException ex) {
		return new ResponseEntity<ResponseStatus>(new ResponseStatus(ex.getMessage()),
				HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(value = FoodConfigurationException.class)
	public ResponseEntity<ResponseStatus> handleFoodConfigurationException(FoodConfigurationException ex) {
		return new ResponseEntity<ResponseStatus>(new ResponseStatus(ex.getMessage()),
				HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(value = ServiceException.class)
	public ResponseEntity<ResponseStatus> handleServiceException(FoodConfigurationException ex) {
		return new ResponseEntity<ResponseStatus>(new ResponseStatus(ex.getMessage()),
				HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
