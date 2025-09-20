package com.projetofinal.movieflix.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.projetofinal.movieflix.model.Rating;

public interface RatingRepository extends JpaRepository<Rating, Long> {

}
