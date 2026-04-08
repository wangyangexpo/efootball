package com.example.demo.service.executor;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.api.PlayerDTO;
import com.example.demo.api.PlayerEnumsResponse;
import com.example.demo.api.request.PlayerQueryRequest;
import com.example.demo.common.PageResult;
import com.example.demo.domain.entity.PlayerEntity;
import com.example.demo.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.TreeSet;

/**
 * Player query executor
 */
@Component
public class PlayerQueryExe {

    @Autowired
    private PlayerRepository playerRepository;

    public PlayerDTO getById(Long id) {
        PlayerEntity entity = playerRepository.selectById(id);
        if (entity == null) {
            return null;
        }
        return convertToDTO(entity);
    }

    public PageResult<PlayerDTO> list(PlayerQueryRequest request) {
        LambdaQueryWrapper<PlayerEntity> wrapper = new LambdaQueryWrapper<>();

        // ID精确查询
        if (request.getId() != null) {
            wrapper.eq(PlayerEntity::getId, request.getId());
        }

        // 姓名模糊查询
        if (request.getName() != null && !request.getName().isEmpty()) {
            wrapper.like(PlayerEntity::getName, request.getName());
        }

        // Filter conditions
        if (request.getPosition() != null) {
            wrapper.eq(PlayerEntity::getPosition, request.getPosition());
        }
        if (request.getStatus() != null) {
            wrapper.eq(PlayerEntity::getStatus, request.getStatus());
        }
        if (request.getNumber() != null) {
            wrapper.eq(PlayerEntity::getNumber, request.getNumber());
        }
        if (request.getLeague() != null) {
            wrapper.eq(PlayerEntity::getLeague, request.getLeague());
        }
        if (request.getClub() != null) {
            wrapper.eq(PlayerEntity::getClub, request.getClub());
        }
        if (request.getCountry() != null) {
            wrapper.eq(PlayerEntity::getCountry, request.getCountry());
        }
        if (request.getFoot() != null) {
            wrapper.eq(PlayerEntity::getFoot, request.getFoot());
        }

        // Height filter with operator
        if (request.getHeight() != null) {
            String operator = request.getHeightOperator();
            if (operator == null || operator.equals("=")) {
                wrapper.eq(PlayerEntity::getHeight, request.getHeight());
            } else if (operator.equals("+")) {
                wrapper.gt(PlayerEntity::getHeight, request.getHeight());
            } else if (operator.equals("-")) {
                wrapper.lt(PlayerEntity::getHeight, request.getHeight());
            }
        }

        // Pagination
        Page<PlayerEntity> page = new Page<>(request.getPageNum(), request.getPageSize());
        Page<PlayerEntity> result = playerRepository.selectPage(page, wrapper);

        // Convert to DTOs
        List<PlayerDTO> dtoList = result.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return PageResult.of(dtoList, result.getTotal(), request.getPageNum(), request.getPageSize());
    }

    /**
     * Get all enums from player data
     */
    public PlayerEnumsResponse getEnums() {
        List<PlayerEntity> allPlayers = playerRepository.selectAll();

        PlayerEnumsResponse response = new PlayerEnumsResponse();

        // Use TreeSet for sorted unique values
        response.setPositions(allPlayers.stream()
                .map(PlayerEntity::getPosition)
                .filter(p -> p != null)
                .collect(Collectors.toCollection(TreeSet::new))
                .stream().collect(Collectors.toList()));

        response.setStatuses(List.of("现役", "历史"));

        response.setLeagues(allPlayers.stream()
                .map(PlayerEntity::getLeague)
                .filter(l -> l != null && !l.isEmpty())
                .collect(Collectors.toCollection(TreeSet::new))
                .stream().collect(Collectors.toList()));

        response.setClubs(allPlayers.stream()
                .map(PlayerEntity::getClub)
                .filter(c -> c != null && !c.isEmpty())
                .collect(Collectors.toCollection(TreeSet::new))
                .stream().collect(Collectors.toList()));

        response.setCountries(allPlayers.stream()
                .map(PlayerEntity::getCountry)
                .filter(c -> c != null && !c.isEmpty())
                .collect(Collectors.toCollection(TreeSet::new))
                .stream().collect(Collectors.toList()));

        response.setFoots(List.of("左", "右"));

        return response;
    }

    public void deleteById(Long id) {
        playerRepository.deleteById(id);
    }

    private PlayerDTO convertToDTO(PlayerEntity entity) {
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