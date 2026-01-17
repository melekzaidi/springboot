package com.formation.repository;

import com.formation.entity.Niveau;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NiveauRepository extends JpaRepository<Niveau, Long> {
    Optional<Niveau> findByCode(String code);
    List<Niveau> findAllByOrderByNomAsc();
    boolean existsByCode(String code);
}