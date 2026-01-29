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

    @Autowired
    private com.formation.repository.AppUserRepository appUserRepository;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Override
    public Prof saveProf(Prof prof) {
        Prof savedProf = profRepository.save(prof);
        
        // Sync AppUser
        com.formation.entity.AppUser appUser = new com.formation.entity.AppUser();
        appUser.setEmail(savedProf.getEmail());
        appUser.setPassword(passwordEncoder.encode(savedProf.getMatricule())); // Password = Matricule
        appUser.setRole("PROF");
        appUserRepository.save(appUser);
        
        return savedProf;
    }

    @Override
    public Prof updateProf(Long id, Prof profDetails) {
        Prof prof = getProfById(id);
        
        String oldEmail = prof.getEmail();
        
        prof.setMatricule(profDetails.getMatricule());
        prof.setNom(profDetails.getNom());
        prof.setPrenom(profDetails.getPrenom());
        prof.setEmail(profDetails.getEmail());
        prof.setSpecialite(profDetails.getSpecialite());
        prof.setTelephone(profDetails.getTelephone());
        
        Prof updatedProf = profRepository.save(prof);
        
        // Update AppUser if email changed
        if (!oldEmail.equals(updatedProf.getEmail())) {
            appUserRepository.findByEmail(oldEmail).ifPresent(appUser -> {
                appUser.setEmail(updatedProf.getEmail());
                appUserRepository.save(appUser);
            });
        }
        
        return updatedProf;
    }

    @Override
    public void deleteProf(Long id) {
        Prof prof = getProfById(id);
        // Delete AppUser
        appUserRepository.findByEmail(prof.getEmail()).ifPresent(appUserRepository::delete);
        profRepository.delete(prof);
    }

    @Override
    public Prof getProfById(Long id) {
        return profRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Professeur non trouvé avec l'ID: " + id));
    }

    @Override
    public Prof getProfByEmail(String email) {
        return profRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Professeur non trouvé avec l'email: " + email));
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