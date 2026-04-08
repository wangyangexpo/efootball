package com.example.demo.service.executor;

import com.example.demo.api.PlayerDTO;
import com.example.demo.api.request.PlayerUpdateRequest;
import com.example.demo.domain.entity.PlayerEntity;
import com.example.demo.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Player update command executor
 */
@Component
public class PlayerUpdateExe {

    @Autowired
    private PlayerRepository playerRepository;

    public PlayerDTO execute(Long id, PlayerUpdateRequest request) {
        PlayerEntity entity = playerRepository.selectById(id);
        if (entity == null) {
            throw new RuntimeException("Player not found with id: " + id);
        }

        // Update fields if provided
        if (request.getName() != null) {
            entity.setName(request.getName());
        }
        if (request.getPosition() != null) {
            entity.setPosition(request.getPosition());
        }
        if (request.getStatus() != null) {
            entity.setStatus(request.getStatus());
        }
        if (request.getNumber() != null) {
            entity.setNumber(request.getNumber());
        }
        if (request.getClub() != null) {
            entity.setClub(request.getClub());
        }
        if (request.getLeague() != null) {
            entity.setLeague(request.getLeague());
        }
        if (request.getCountry() != null) {
            entity.setCountry(request.getCountry());
        }
        if (request.getHeight() != null) {
            entity.setHeight(request.getHeight());
        }
        if (request.getFoot() != null) {
            entity.setFoot(request.getFoot());
        }
        if (request.getCardImage() != null) {
            entity.setCardImage(request.getCardImage());
        }
        entity.setUpdateTime(LocalDateTime.now());

        // Save
        playerRepository.updateById(entity);

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