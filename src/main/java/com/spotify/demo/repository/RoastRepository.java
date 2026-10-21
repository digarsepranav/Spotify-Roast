package com.spotify.demo.repository;

import com.spotify.demo.entity.Roast;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoastRepository extends JpaRepository<Roast, Long> {
}
