package org.kmontano.minidex.infrastructure.mapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Showdown {
    @JsonProperty("front_default")
    private String frontDefault;

    @JsonProperty("front_shiny")
    private String frontShiny;

    @JsonProperty("back_default")
    private String backDefault;

    @JsonProperty("back_shiny")
    private String backShiny;
}
