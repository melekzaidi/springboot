package com.formation.service;

import com.formation.entity.Prof;
import java.util.List;

public interface ProfService {
    Prof saveProf(Prof prof);
    Prof updateProf(Long id, Prof prof);
    void deleteProf(Long id);
    Prof getProfById(Long id);
    List<Prof> getAllProfs();
    boolean isMatriculeUnique(String matricule);
    boolean isEmailUnique(String email);
}