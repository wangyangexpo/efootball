package com.example.demo.api;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * Player enums response for filter options
 */
@Data
public class PlayerEnumsResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<String> positions;
    private List<String> statuses;
    private List<String> leagues;
    private List<String> clubs;
    private List<String> countries;
    private List<String> foots;
}