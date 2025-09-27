package com.projetofinal.movieflix.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.projetofinal.movieflix.model.Movie;
import com.projetofinal.movieflix.model.Rating;
import com.projetofinal.movieflix.model.User;
import com.projetofinal.movieflix.repositories.MovieRepository;
import com.projetofinal.movieflix.repositories.RatingRepository;
import com.projetofinal.movieflix.repositories.UserRepository;


@Controller
public class PageController {

    private final MovieRepository movieRepo;
    private final RatingRepository ratingRepo;
    private final UserRepository userRepo;

    public PageController(MovieRepository movieRepo, RatingRepository ratingRepo, UserRepository userRepo) {
        this.movieRepo = movieRepo;
        this.ratingRepo = ratingRepo;
        this.userRepo = userRepo;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("movies", movieRepo.findAll());
        model.addAttribute("ratings", ratingRepo.findAll());
        model.addAttribute("users", userRepo.findAll());
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
    public String addRating(@RequestParam Integer user_id,
                            @RequestParam Integer movie_id,
                            @RequestParam Double rating) {
        Rating r = new Rating();
        r.setUser_id(user_id);
        r.setMovie_id(movie_id);
        r.setRating(rating);
        r.setRating_ts(java.time.Instant.now());
        ratingRepo.save(r);
        return "redirect:/";
    }  
        
      @PostMapping("/addUser")
      public String addUser(@RequestParam String country,
                            @RequestParam Integer birth_year) {
            User u = new User();
            u.setCountry(country);
            u.setBirth_year(birth_year);
            userRepo.save(u);
            return "redirect:/";
        }
}
