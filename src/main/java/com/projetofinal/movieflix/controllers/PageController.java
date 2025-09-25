package com.projetofinal.movieflix.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.projetofinal.movieflix.model.Movie;
import com.projetofinal.movieflix.model.Rating;
import com.projetofinal.movieflix.repositories.MovieRepository;
import com.projetofinal.movieflix.repositories.RatingRepository;


@Controller
public class PageController {

    private final MovieRepository movieRepo;
    private final RatingRepository ratingRepo;

    public PageController(MovieRepository movieRepo, RatingRepository ratingRepo) {
        this.movieRepo = movieRepo;
        this.ratingRepo = ratingRepo;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("movies", movieRepo.findAll());
        model.addAttribute("ratings", ratingRepo.findAll());
        return "index";
    }

    @PostMapping("/addMovie")
    public String addMovie(@RequestParam String title,
                           @RequestParam Integer movie_year,
                           @RequestParam String genres) {
        Movie m = new Movie();
        m.setTitle(title);
        m.setMovie_year(movie_year);
        m.setGenres(genres);
        movieRepo.save(m);
        return "redirect:/";
    }

    @PostMapping("/addRating")
    public String addRating(@RequestParam Integer userId,
                            @RequestParam Integer movie_id,
                            @RequestParam Double rating) {
        Rating r = new Rating();
        r.setUserId(userId);
        r.setMovie_id(movie_id);
        r.setRating(rating);
        ratingRepo.save(r);
        return "redirect:/";
    }
}
