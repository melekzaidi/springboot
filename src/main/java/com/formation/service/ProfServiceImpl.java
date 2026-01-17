package com.formation.service;

import com.formation.entity.Prof;
import com.formation.repository.ProfRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProfServiceImpl implements ProfService {

    @Autowired
    private ProfRepository profRepository;

    @Override
    public Prof saveProf(Prof prof) {
        return profRepository.save(prof);
    }

    @Override
    public Prof updateProf(Long id, Prof profDetails) {
        Prof prof = getProfById(id);
        prof.setMatricule(profDetails.getMatricule());
        prof.setNom(profDetails.getNom());
        prof.setPrenom(profDetails.getPrenom());
        prof.setEmail(profDetails.getEmail());
        prof.setSpecialite(profDetails.getSpecialite());
        prof.setTelephone(profDetails.getTelephone());
        return profRepository.save(prof);
    }

    @Override
    public void deleteProf(Long id) {
        Prof prof = getProfById(id);
        profRepository.delete(prof);
    }

    @Override
    public Prof getProfById(Long id) {
        return profRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Professeur non trouvé avec l'ID: " + id));
    }

    @Override
    public List<Prof> getAllProfs() {
        return profRepository.findAllByOrderByNomAsc();
    }

    @Override
    public boolean isMatriculeUnique(String matricule) {
        return !profRepository.existsByMatricule(matricule);
    }

    @Override
    public boolean isEmailUnique(String email) {
        return !profRepository.existsByEmail(email);
    }
}