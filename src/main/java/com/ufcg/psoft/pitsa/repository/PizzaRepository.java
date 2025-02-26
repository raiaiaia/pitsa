package com.ufcg.psoft.pitsa.repository;

import com.ufcg.psoft.pitsa.model.Pizza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PizzaRepository extends JpaRepository<Pizza, Long> {
}

