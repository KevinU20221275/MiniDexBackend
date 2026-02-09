package org.kmontano.minidex.infrastructure.mapper;

import lombok.Data;

@Data
public class MoveResponse {
    private String name;
    private MoveType type;
    private Integer power;
    private Integer accuracy;
}
