package com.formation.service;

import com.formation.entity.Niveau;
import com.formation.repository.NiveauRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class NiveauServiceImpl implements NiveauService {

    @Autowired
    private NiveauRepository niveauRepository;

    @Override
    public Niveau saveNiveau(Niveau niveau) {
        return niveauRepository.save(niveau);
    }

    @Override
    public Niveau updateNiveau(Long id, Niveau niveauDetails) {
        Niveau niveau = getNiveauById(id);
        niveau.setCode(niveauDetails.getCode());
        niveau.setNom(niveauDetails.getNom());
        niveau.setDescription(niveauDetails.getDescription());
        return niveauRepository.save(niveau);
    }

    @Override
    public void deleteNiveau(Long id) {
        Niveau niveau = getNiveauById(id);
        niveauRepository.delete(niveau);
    }

    @Override
    public Niveau getNiveauById(Long id) {
        return niveauRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Niveau non trouvé avec l'ID: " + id));
    }

    @Override
    public List<Niveau> getAllNiveaux() {
        return niveauRepository.findAllByOrderByNomAsc();
    }

    @Override
    public boolean isCodeUnique(String code) {
        return !niveauRepository.existsByCode(code);
    }
}