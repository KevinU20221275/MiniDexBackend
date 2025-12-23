package org.kmontano.minidex.domain.trainer;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.util.List;

public class DailyPackStatus {

    private Integer numEnvelopes;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate lastResetDate;
    private List<Envelope> envelopes;

    public Integer getNumEnvelopes() {
        return numEnvelopes;
    }

    public DailyPackStatus setNumEnvelopes(Integer numEnvelopes) {
        this.numEnvelopes = numEnvelopes;
        return this;
    }

    public LocalDate getLastResetDate() {
        return lastResetDate;
    }

    public DailyPackStatus setLastResetDate(LocalDate lastResetDate) {
        this.lastResetDate = lastResetDate;
        return this;
    }

    public List<Envelope> getEnvelopes() {
        return envelopes;
    }

    public DailyPackStatus setEnvelopes(List<Envelope> envelopes) {
        this.envelopes = envelopes;
        return this;
    }
}