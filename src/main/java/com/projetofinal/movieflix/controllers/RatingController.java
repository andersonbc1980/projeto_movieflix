package com.projetofinal.movieflix.controllers;

import java.time.Instant;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projetofinal.movieflix.model.Rating;
import com.projetofinal.movieflix.repositories.RatingRepository;

@RestController
@RequestMapping("/ratings")
public class RatingController {

    private final RatingRepository repo;

    public RatingController(RatingRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<Rating> all() {
        return repo.findAll();
    }

    @PostMapping
    public Rating create(@RequestBody Rating r) {
        if (r.getRatingTs() == null) r.setRatingTs(Instant.now());
        return repo.save(r);
    }
}
