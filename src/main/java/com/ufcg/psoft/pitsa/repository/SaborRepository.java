package com.ufcg.psoft.pitsa.repository;

import com.ufcg.psoft.pitsa.model.Sabor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SaborRepository extends JpaRepository<Sabor, Long> {
    Optional<Sabor> findByNome(String nome);
}
