package com.quiz.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.quiz.entity.model.SessionTracker;

public interface SessionTrackerRepository extends JpaRepository<SessionTracker, Long> {

	@Query("select st from SessionTracker st where st.username = :username")
	public SessionTracker getSessionTrackerByUsername(String username);

	@Query(nativeQuery = true, value = "select * from public.session_tracker s where s.username in (select st.username from public.session_tracker st group by st.last_update_timestamp, st.username \r\n"
			+ "having CURRENT_TIMESTAMP - st.last_update_timestamp > make_interval(0, 0, 0, 0, 0, 0, 10))")
	public List<SessionTracker> getInactiveSessionTrackerList();
}
