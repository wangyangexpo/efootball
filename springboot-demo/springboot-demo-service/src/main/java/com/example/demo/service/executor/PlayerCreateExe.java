package com.example.demo.service.executor;

import com.example.demo.api.PlayerDTO;
import com.example.demo.api.request.PlayerCreateRequest;
import com.example.demo.domain.entity.PlayerEntity;
import com.example.demo.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Player create command executor
 */
@Component
public class PlayerCreateExe {

    @Autowired
    private PlayerRepository playerRepository;

    public PlayerDTO execute(PlayerCreateRequest request) {
        // Create entity
        PlayerEntity entity = new PlayerEntity();
        entity.setName(request.getName());
        entity.setPosition(request.getPosition());
        entity.setStatus(request.getStatus());
        entity.setNumber(request.getNumber());
        entity.setClub(request.getClub());
        entity.setLeague(request.getLeague());
        entity.setCountry(request.getCountry());
        entity.setHeight(request.getHeight());
        entity.setFoot(request.getFoot());
        entity.setCardImage(request.getCardImage());
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        entity.setDeleted(0);

        // Save
        playerRepository.insert(entity);

        // Convert to DTO
        PlayerDTO dto = new PlayerDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setPosition(entity.getPosition());
        dto.setStatus(entity.getStatus());
        dto.setNumber(entity.getNumber());
        dto.setClub(entity.getClub());
        dto.setLeague(entity.getLeague());
        dto.setCountry(entity.getCountry());
        dto.setHeight(entity.getHeight());
        dto.setFoot(entity.getFoot());
        dto.setCardImage(entity.getCardImage());
        dto.setCreateTime(entity.getCreateTime());
        dto.setUpdateTime(entity.getUpdateTime());

        return dto;
    }
}