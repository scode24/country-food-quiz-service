package com.quiz.cron;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.quiz.entity.model.SessionTracker;
import com.quiz.jpa.repository.SessionTrackerRepository;

@Configuration
@EnableScheduling
public class SessionTrackerCron {

	@Autowired
	private SessionTrackerRepository sessionTrackerRepository;

	@Scheduled(fixedRate = 20000)
	public void checkAndClearSessionTracker() {
		List<SessionTracker> inactiveSessionTrackerList = sessionTrackerRepository.getInactiveSessionTrackerList();
		if (inactiveSessionTrackerList.size() > 0) {
			sessionTrackerRepository.deleteAllInBatch(inactiveSessionTrackerList);
		}
	}
}
