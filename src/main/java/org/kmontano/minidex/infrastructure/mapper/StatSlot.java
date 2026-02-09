package org.kmontano.minidex.infrastructure.mapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class StatSlot {
    @JsonProperty("base_stat")
    private int baseStat;

    private Stat stat;
}
