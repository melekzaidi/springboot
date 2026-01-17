package com.formation.service;

import com.formation.entity.Niveau;
import java.util.List;

public interface NiveauService {
    Niveau saveNiveau(Niveau niveau);
    Niveau updateNiveau(Long id, Niveau niveau);
    void deleteNiveau(Long id);
    Niveau getNiveauById(Long id);
    List<Niveau> getAllNiveaux();
    boolean isCodeUnique(String code);
}