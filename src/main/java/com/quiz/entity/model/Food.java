package com.quiz.entity.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "food", schema = "public")
public class Food {
	
	@Id
	private Long id;
	private String foodname;
	private String originplace;
	
	public Food() {
	}
	
	public Food(Long id, String foodname, String originplace) {
		super();
		this.id = id;
		this.foodname = foodname;
		this.originplace = originplace;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFoodname() {
		return foodname;
	}

	public void setFoodname(String foodname) {
		this.foodname = foodname;
	}

	public String getOriginplace() {
		return originplace;
	}

	public void setOriginplace(String originplace) {
		this.originplace = originplace;
	}
}
