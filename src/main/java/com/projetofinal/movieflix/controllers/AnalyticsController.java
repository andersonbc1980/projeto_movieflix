package com.projetofinal.movieflix.controllers;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.projetofinal.movieflix.repositories.MovieRepository;
import com.projetofinal.movieflix.repositories.RatingRepository;
import com.projetofinal.movieflix.repositories.UserRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Controller
public class AnalyticsController {

    @PersistenceContext
    private EntityManager em;

    private final MovieRepository movieRepo;
    private final RatingRepository ratingRepo;
    private final UserRepository userRepo;

    public AnalyticsController(MovieRepository movieRepo, RatingRepository ratingRepo, UserRepository userRepo) {
        this.movieRepo = movieRepo;
        this.ratingRepo = ratingRepo;
        this.userRepo = userRepo;
    }

    @GetMapping("/analytics")
    public String analytics(Model model) {
    	Instant sevenDaysAgo = Instant.now().minus(7, ChronoUnit.DAYS);
    	// Filmes mais bem avaliados da última semana
    	List<Object[]> topMoviesLastWeek = em.createQuery(
        		"SELECT m.title, AVG(r.rating) as avgRating " +
        				"FROM Rating r JOIN Movie m ON r.movie_id = m.movie_id " +
        				"WHERE r.rating_ts >= :sevenDaysAgo " +
        				"GROUP BY m.title ORDER BY avgRating DESC", Object[].class)
    					.setParameter("sevenDaysAgo", sevenDaysAgo)
        				.setMaxResults(5)
        				.getResultList();

    	
        // Top 5 filmes
        List<Object[]> topMovies = em.createQuery(
        		"SELECT m.title, AVG(r.rating) as avgRating, COUNT(r) as total " +
        				"FROM Rating r JOIN Movie m ON r.movie_id = m.movie_id " +
        				"GROUP BY m.title ORDER BY avgRating DESC", Object[].class)
        				.setMaxResults(5)
        				.getResultList();
        
        // Média de Avaliação por Gênero 
        List<Object[]> avgRatingByGenre = em.createQuery(
              "SELECT u.genres, AVG(r.rating) " +
              "FROM Rating r JOIN Movie u ON r.movie_id = u.movie_id " +
              "GROUP BY u.genres ORDER BY AVG(r.rating) DESC", Object[].class)
        	  .getResultList();

                
        // Média por país
        List<Object[]> avgByCountry = em.createQuery(
        		"SELECT u.country, AVG(r.rating) " +
        				"FROM Rating r JOIN User u ON r.user_id = u.user_id " +
        				"GROUP BY u.country ORDER BY AVG(r.rating) DESC", Object[].class)
        				.getResultList();

        // Qtd avaliações por país
        List<Object[]> ratingsByCountry = em.createQuery(
        		"SELECT u.country, COUNT(r) FROM Rating r JOIN User u ON r.user_id = u.user_id " +
        				"GROUP BY u.country ORDER BY COUNT(r) DESC", Object[].class)
        				.getResultList();

        model.addAttribute("topMoviesLastWeek", topMoviesLastWeek);
        model.addAttribute("topMovies", topMovies);
        model.addAttribute("avgRatingByGenre", avgRatingByGenre);
        model.addAttribute("avgByCountry", avgByCountry);
        model.addAttribute("ratingsByCountry", ratingsByCountry);

        return "analytics";
    }
}
