package com.quiz.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.quiz.entity.model.Food;
import com.quiz.entity.model.SessionTracker;
import com.quiz.exception.handler.AdminConfigurationException;
import com.quiz.exception.handler.ServiceException;
import com.quiz.jpa.repository.FoodRepository;
import com.quiz.jpa.repository.SessionTrackerRepository;
import com.quiz.model.NextQuestionResponse;
import com.quiz.model.ServiceConstants;
import com.quiz.security.configuration.OptionsConfiguration;

@RestController
@RequestMapping("/v1")
@CrossOrigin("*")
public class ServiceController {

	@Autowired
	private SessionTrackerRepository sessionTrackerRepository;

	@Autowired
	private FoodRepository foodRepository;

	@Autowired
	private OptionsConfiguration optionConfiguration;

	@GetMapping("")
	public String sayAdminDefaultMessage() {
		return ServiceConstants.DEFAULT_MSG;
	}

	@GetMapping(value = "/validateAndInsertUsername", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> validateAndInsertUsername(@RequestHeader("username") String username)
			throws AdminConfigurationException, ServiceException {
		if (sessionTrackerRepository.getSessionTrackerByUsername(username) == null) {
			SessionTracker sessionTracker = new SessionTracker();
			sessionTracker.setId(getLastCount("sessionTracker") + 1);
			sessionTracker.setUsername(username);
			sessionTracker.setLastUpdateTimestamp(new Timestamp(System.currentTimeMillis()));
			sessionTrackerRepository.save(sessionTracker);
			return new ResponseEntity<Boolean>(true, HttpStatus.OK);
		}
		return new ResponseEntity<Boolean>(false, HttpStatus.OK);
	}

	@GetMapping(value = "/checkUserActive", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> checkUserActive(@RequestHeader("username") String username) {
		SessionTracker sessionTracker = sessionTrackerRepository.getSessionTrackerByUsername(username);
		if (sessionTracker != null) {
			return new ResponseEntity<Boolean>(true, HttpStatus.OK);
		}
		return new ResponseEntity<Boolean>(false, HttpStatus.OK);
	}

	@GetMapping(value = "/nextQuestion", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<NextQuestionResponse> nextQuestion(@RequestHeader("username") String username) {
		SessionTracker sessionTracker = sessionTrackerRepository.getSessionTrackerByUsername(username);
		String processStr = sessionTracker.getProcessIndexList();

		List<Long> processedFoodIndexList = processStr == null ? new ArrayList()
				: Stream.of(processStr.split(",")).map(String::trim).map(Long::parseLong).collect(Collectors.toList());

		List<Food> remainingFoodList = null;

		if (processedFoodIndexList.isEmpty()) {
			remainingFoodList = foodRepository.findAll();
		} else {
			remainingFoodList = foodRepository.getRemainingFoodList(processedFoodIndexList);
		}

		if (remainingFoodList.size() == 0) {
			return new ResponseEntity<NextQuestionResponse>(new NextQuestionResponse(-1l, null, null), HttpStatus.OK);
		}

		Random random = new Random();
		int newIndex = random.nextInt(remainingFoodList.size() - 1 - 0) + 0;

		Food newFood = remainingFoodList.get(newIndex);
		String[] optionList = optionConfiguration.getOptionList(newFood.getOriginplace());
		NextQuestionResponse nextQuestion = new NextQuestionResponse(newFood.getId(), newFood.getFoodname(),
				optionList);

		processedFoodIndexList.add(Long.parseLong(String.valueOf(newIndex)));
		sessionTracker.setProcessIndexList(processedFoodIndexList.toString().replace("[", "").replace("]", ""));
		sessionTracker.setLastUpdateTimestamp(new Timestamp(System.currentTimeMillis()));
		sessionTrackerRepository.save(sessionTracker);

		return new ResponseEntity<NextQuestionResponse>(nextQuestion, HttpStatus.OK);
	}

	@PostMapping(value = "/initGame", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> initGame(@RequestHeader("username") String username) {
		SessionTracker sessionTracker = sessionTrackerRepository.getSessionTrackerByUsername(username);
		sessionTrackerRepository.delete(sessionTracker);
		return new ResponseEntity<Boolean>(true, HttpStatus.OK);
	}

	@PostMapping(value = "/checkAnswer/{questionId}/{answer}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> checkAnswer(@PathVariable Long questionId, @PathVariable String answer) {
		Food food = foodRepository.getByQuestionId(questionId);
		return new ResponseEntity<Boolean>(answer.toLowerCase().equalsIgnoreCase(food.getOriginplace().toLowerCase()),
				HttpStatus.OK);
	}

	private Long getLastCount(String entity) throws ServiceException {
		if (entity.equalsIgnoreCase("sessionTracker")) {
			Page<SessionTracker> page = sessionTrackerRepository
					.findAll(PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "id")));
			return page.getContent().size() == 0 ? 0l : page.getContent().get(0).getId();
		}
		throw new ServiceException(ServiceConstants.INVALID_ENTITY_TYPE);
	}

}
