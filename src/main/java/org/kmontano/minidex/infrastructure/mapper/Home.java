package org.kmontano.minidex.infrastructure.mapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Home {
    @JsonProperty("front_default")
    private String frontDefault;

    @JsonProperty("front_shiny")
    private String frontShiny;
}