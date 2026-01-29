package com.formation.mapper;

import com.formation.dto.NiveauDTO;
import com.formation.entity.Niveau;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class NiveauMapper {

    public NiveauDTO toDTO(Niveau niveau) {
        if (niveau == null) return null;
        
        return new NiveauDTO(
            niveau.getId(),
            niveau.getCode(),
            niveau.getNom(),
            niveau.getDescription(),
            niveau.getDateCreation()
        );
    }

    public Niveau toEntity(NiveauDTO dto) {
        if (dto == null) return null;
        
        Niveau niveau = new Niveau();
        niveau.setId(dto.getId());
        niveau.setCode(dto.getCode());
        niveau.setNom(dto.getNom());
        niveau.setDescription(dto.getDescription());
        niveau.setDateCreation(dto.getDateCreation());
        
        return niveau;
    }

    public List<NiveauDTO> toDTOList(List<Niveau> niveaux) {
        return niveaux.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public void updateEntityFromDTO(NiveauDTO dto, Niveau niveau) {
        if (dto == null || niveau == null) return;
        
        niveau.setCode(dto.getCode());
        niveau.setNom(dto.getNom());
        niveau.setDescription(dto.getDescription());
    }
}