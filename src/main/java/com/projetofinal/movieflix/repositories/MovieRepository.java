package com.projetofinal.movieflix.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.projetofinal.movieflix.model.Movie;

public interface MovieRepository extends JpaRepository<Movie, Integer> {

}
