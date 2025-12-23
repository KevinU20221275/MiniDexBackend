package org.kmontano.minidex.dto.request;

import jakarta.validation.constraints.NotBlank;

public class OpenEnvelopeRequest {
    @NotBlank
    private String envelopeId;

    public String getEnvelopeId() {
        return envelopeId;
    }

    public void setEnvelopeId(String envelopeId) {
        this.envelopeId = envelopeId;
    }
}
