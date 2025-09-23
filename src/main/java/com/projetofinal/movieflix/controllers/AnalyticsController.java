package com.projetofinal.movieflix.controllers;

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
        // Top 5 filmes
        List<Object[]> topMovies = em.createQuery(
        		"SELECT m.title, AVG(r.rating) as avgRating, COUNT(r) as total " +
        				"FROM Rating r JOIN Movie m ON r.movieId = m.movieId " +
        				"GROUP BY m.title ORDER BY avgRating DESC", Object[].class)
        				.setMaxResults(5)
        				.getResultList();

        // Média por país
        List<Object[]> avgByCountry = em.createQuery(
        		"SELECT u.country, AVG(r.rating) " +
        				"FROM Rating r JOIN User u ON r.userId = u.userId " +
        				"GROUP BY u.country ORDER BY AVG(r.rating) DESC", Object[].class)
        				.getResultList();

        // Qtd avaliações por país
        List<Object[]> ratingsByCountry = em.createQuery(
        		"SELECT u.country, COUNT(r) FROM Rating r JOIN User u ON r.userId = u.userId " +
        				"GROUP BY u.country ORDER BY COUNT(r) DESC", Object[].class)
        				.getResultList();

        model.addAttribute("topMovies", topMovies);
        model.addAttribute("avgByCountry", avgByCountry);
        model.addAttribute("ratingsByCountry", ratingsByCountry);


        return "analytics";
    }
}
