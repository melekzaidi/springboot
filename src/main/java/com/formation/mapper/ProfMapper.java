package com.formation.mapper;

import com.formation.dto.ProfDTO;
import com.formation.entity.Prof;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProfMapper {

    public ProfDTO toDTO(Prof prof) {
        if (prof == null) return null;
        
        return new ProfDTO(
            prof.getId(),
            prof.getMatricule(),
            prof.getNom(),
            prof.getPrenom(),
            prof.getEmail(),
            prof.getSpecialite(),
            prof.getTelephone(),
            prof.getDateEmbauche(),
            prof.getDateCreation()
        );
    }

    public Prof toEntity(ProfDTO dto) {
        if (dto == null) return null;
        
        Prof prof = new Prof();
        prof.setId(dto.getId());
        prof.setMatricule(dto.getMatricule());
        prof.setNom(dto.getNom());
        prof.setPrenom(dto.getPrenom());
        prof.setEmail(dto.getEmail());
        prof.setSpecialite(dto.getSpecialite());
        prof.setTelephone(dto.getTelephone());
        prof.setDateEmbauche(dto.getDateEmbauche());
        prof.setDateCreation(dto.getDateCreation());
        
        return prof;
    }

    public List<ProfDTO> toDTOList(List<Prof> profs) {
        return profs.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public void updateEntityFromDTO(ProfDTO dto, Prof prof) {
        if (dto == null || prof == null) return;
        
        prof.setMatricule(dto.getMatricule());
        prof.setNom(dto.getNom());
        prof.setPrenom(dto.getPrenom());
        prof.setEmail(dto.getEmail());
        prof.setSpecialite(dto.getSpecialite());
        prof.setTelephone(dto.getTelephone());
        prof.setDateEmbauche(dto.getDateEmbauche());
    }
}