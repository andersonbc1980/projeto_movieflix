package com.projetofinal.movieflix.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.projetofinal.movieflix.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {
}
