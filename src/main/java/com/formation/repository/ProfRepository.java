package com.formation.repository;

import com.formation.entity.Prof;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfRepository extends JpaRepository<Prof, Long> {
    Optional<Prof> findByMatricule(String matricule);
    Optional<Prof> findByEmail(String email);
    List<Prof> findAllByOrderByNomAsc();
    boolean existsByMatricule(String matricule);
    boolean existsByEmail(String email);
}