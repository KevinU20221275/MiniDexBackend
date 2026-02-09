package org.kmontano.minidex.infrastructure.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GenerationIx {
    @JsonProperty("scarlet-violet")
    private ScarletViolet scarletViolet;
}
