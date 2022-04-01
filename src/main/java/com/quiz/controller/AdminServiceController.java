package com.quiz.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.quiz.entity.model.Food;
import com.quiz.entity.model.User;
import com.quiz.exception.handler.AdminConfigurationException;
import com.quiz.exception.handler.FoodConfigurationException;
import com.quiz.jpa.repository.FoodRepository;
import com.quiz.jpa.repository.UserRepository;
import com.quiz.model.ResponseStatus;
import com.quiz.model.ServiceConstants;

@RestController
@RequestMapping("/admin")
public class AdminServiceController {

	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private FoodRepository foodRepository;

	@RequestMapping("")
	public String sayAdminDefaultMessage() {
		return ServiceConstants.ADMIN_DEFAULT_MSG;
	}

	@PostMapping(value = "/addAdmin", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseStatus> addAdmin(@RequestBody User user) throws AdminConfigurationException {
		if (user.getPassword() == null || user.getRole() == null || user.getUsername() == null) {
			throw new AdminConfigurationException(
					ServiceConstants.REQUIRED_FIELDS_MISSING + "{username, password, role}");
		}
		if (userRepository.findUserByUsername(user.getUsername()) != null) {
			throw new AdminConfigurationException(ServiceConstants.ADMIN_ALREADY_PRESENT);
		} else {
			user.setId(getLastCount("user") + 1);
			user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
			userRepository.save(user);
			return new ResponseEntity<ResponseStatus>(new ResponseStatus(ServiceConstants.ADMIN_DETAILS_ADD_SUCCESSFUL),
					HttpStatus.OK);
		}
	}

	@PostMapping(value = "/updateAdmin", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseStatus> updateAdmin(@RequestBody User user) throws AdminConfigurationException {
		if (user.getUsername() == null || user.getPassword() == null) {
			throw new AdminConfigurationException(ServiceConstants.REQUIRED_FIELDS_MISSING + "{username, password}");
		}
		if (userRepository.findUserByUsername(user.getUsername()) == null) {
			throw new AdminConfigurationException(ServiceConstants.ADMIN_NOT_PRESENT);
		} else {
			User existingUser = userRepository.findUserByUsername(user.getUsername());
			existingUser.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
			userRepository.save(existingUser);
			return new ResponseEntity<ResponseStatus>(
					new ResponseStatus(ServiceConstants.ADMIN_DETAILS_UPDATE_SUCCESSFUL), HttpStatus.OK);
		}
	}

	@PostMapping(value = "/deleteAdmin", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseStatus> deleteAdmin(@RequestBody User user) throws AdminConfigurationException {
		if (user.getUsername() == null) {
			throw new AdminConfigurationException(ServiceConstants.REQUIRED_FIELDS_MISSING + "{username}");
		}
		if (userRepository.findUserByUsername(user.getUsername()) == null) {
			if (userRepository.count() == 1) {
				throw new AdminConfigurationException(ServiceConstants.ATLEAST_ONE_ADMIN_SHOULD_PRESENT);
			}
			throw new AdminConfigurationException(ServiceConstants.ADMIN_NOT_PRESENT);
		} else {
			User existingUser = userRepository.findUserByUsername(user.getUsername());
			userRepository.delete(existingUser);
			return new ResponseEntity<ResponseStatus>(
					new ResponseStatus(ServiceConstants.ADMIN_DETAILS_DELETE_SUCCESSFUL), HttpStatus.OK);
		}
	}

	@GetMapping(value = "/getAllAdmin", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<User>> getAllAdmin() {
		List<User> userList = userRepository.findAll();
		return new ResponseEntity<List<User>>(userList, HttpStatus.OK);
	}

	@PostMapping(value = "/insertAllFoodData", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseStatus> insertAllFoodData() throws IOException {
		foodRepository.deleteAll();
		InputStream dataInputStream = new ClassPathResource("indian_food.csv").getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(dataInputStream));
		long foodId = 1;
		String[] foodDetails = null;
		while (reader.readLine() != null) {
			foodDetails = reader.readLine().split(",");
			if (!foodDetails[1].equalsIgnoreCase("-1")) {
				foodRepository.save(new Food(foodId++, foodDetails[0], foodDetails[1]));
			}
		}
		return new ResponseEntity<ResponseStatus>(new ResponseStatus(ServiceConstants.FOOD_DATA_ADD_SUCCESSFUL),
				HttpStatus.OK);
	}

	@PostMapping(value = "/insertFoodData", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseStatus> insertFoodData(@RequestBody Food food)
			throws FoodConfigurationException, AdminConfigurationException {
		if (food.getFoodname() == null || food.getOriginplace() == null) {
			throw new FoodConfigurationException(ServiceConstants.REQUIRED_FIELDS_MISSING + "{foodname, originplace}");
		}
		food.setFoodname(food.getFoodname());
		if (foodRepository.findFoodByName(food.getFoodname()) != null) {
			throw new FoodConfigurationException(ServiceConstants.FOOD_DATA_ALREADY_EXISTS);
		}
		food.setId(getLastCount("food") + 1);
		foodRepository.save(food);
		return new ResponseEntity<ResponseStatus>(new ResponseStatus(ServiceConstants.FOOD_DATA_ADD_SUCCESSFUL),
				HttpStatus.OK);
	}

	@PostMapping(value = "/deleteFoodData", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseStatus> deleteFoodData(@RequestBody Food food) throws FoodConfigurationException {
		if (food.getFoodname() == null) {
			throw new FoodConfigurationException(ServiceConstants.REQUIRED_FIELDS_MISSING + "{foodname, originplace}");
		}
		Food foodToBeDeleted = foodRepository.findFoodByName(food.getFoodname());
		if (foodToBeDeleted == null) {
			throw new FoodConfigurationException(ServiceConstants.FOOD_DATA_NOT_EXISTS);
		}
		foodRepository.delete(foodToBeDeleted);
		return new ResponseEntity<ResponseStatus>(new ResponseStatus(ServiceConstants.FOOD_DATA_DELETE_SUCCESSFUL),
				HttpStatus.OK);
	}

	@GetMapping(value = "/getAllFoodData", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Food>> getAllFoodData() throws IOException {
		List<Food> foodList = foodRepository.findAll();
		return new ResponseEntity<List<Food>>(foodList, HttpStatus.OK);
	}

	private Long getLastCount(String entity) throws AdminConfigurationException {
		if (entity.equalsIgnoreCase("food")) {
			Page<Food> page = foodRepository.findAll(PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "id")));
			return page.getContent().size() == 0 ? 0l : page.getContent().get(0).getId();
		} else if (entity.equalsIgnoreCase("user")) {
			Page<User> page = userRepository.findAll(PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "id")));
			return page.getContent().size() == 0 ? 0l : page.getContent().get(0).getId();
		}
		throw new AdminConfigurationException(ServiceConstants.INVALID_ENTITY_TYPE);
	}
}
