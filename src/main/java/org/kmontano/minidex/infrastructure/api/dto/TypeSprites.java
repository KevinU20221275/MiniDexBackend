package org.kmontano.minidex.infrastructure.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TypeSprites {
    @JsonProperty("generation-ix")
    private GenerationIx generationIx;
}
