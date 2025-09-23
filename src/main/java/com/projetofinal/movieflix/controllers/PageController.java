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
                           @RequestParam Integer year,
                           @RequestParam String genres) {
        Movie m = new Movie();
        //m.setMovieId((int)(Math.random() * 100000));
       // m.setMovieId(31);
        m.setTitle(title);
        m.setYear(year);
        m.setGenres(genres);
        m.setImdbId("tt0111161");
        movieRepo.save(m);
        return "redirect:/";
    }

    @PostMapping("/addRating")
    public String addRating(@RequestParam Integer userId,
                            @RequestParam Integer movieId,
                            @RequestParam Double rating) {
        Rating r = new Rating();
        r.setUserId(userId);
        r.setMovieId(movieId);
        r.setRating(rating);
        r.setRatingTs(java.time.Instant.now());
        ratingRepo.save(r);
        return "redirect:/";
    }
}
