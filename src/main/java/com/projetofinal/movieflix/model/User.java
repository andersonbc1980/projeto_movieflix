package com.projetofinal.movieflix.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer user_id;
    private String country;
    private Integer birth_year;

    
    public Integer getUser_id() {
		return user_id;
	}
	public void setUser_id(Integer user_id) {
		this.user_id = user_id;
	}
	public String getCountry() { 
    	return country; 
    }
    public void setCountry(String country) { 
    	this.country = country; 
    }
	public Integer getBirth_year() {
		return birth_year;
	}
	public void setBirth_year(Integer birth_year) {
		this.birth_year = birth_year;
	}
    
}
