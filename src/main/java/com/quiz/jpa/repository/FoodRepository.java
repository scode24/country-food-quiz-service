package com.quiz.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.quiz.entity.model.Food;

@Repository
public interface FoodRepository extends JpaRepository<Food, Long> {

	@Query("select f from Food f where lower(f.foodname) like lower(concat('%', :foodname,'%'))")
	public Food findFoodByName(String foodname);

	@Query("select f from Food f where f.id not in :processedFoodIndexList")
	public List<Food> getRemainingFoodList(List<Long> processedFoodIndexList);

	@Query("select f from Food f where f.id = :questionId")
	public Food getByQuestionId(Long questionId);
}
