package com.projetofinal.movieflix.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {

    @Id
    private Integer userId;
    private String country;
    private Integer birthYear;

    public Integer getUserId() { 
    	return userId; 
    }
    public void setUserId(Integer userId) { 
    	this.userId = userId; 
    }
    public String getCountry() { 
    	return country; 
    }
    public void setCountry(String country) { 
    	this.country = country; 
    }
    public Integer getBirthYear() { 
    	return birthYear; 
    }
    public void setBirthYear(Integer birthYear) { 
    	this.birthYear = birthYear; 
    }
}
