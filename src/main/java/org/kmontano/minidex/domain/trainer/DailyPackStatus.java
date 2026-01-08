package org.kmontano.minidex.domain.trainer;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.util.Objects;


@Data
public class DailyPackStatus {
    private Integer numEnvelopes;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate lastResetDate;

    public DailyPackStatus() {
        this.numEnvelopes = 3;
        this.lastResetDate = LocalDate.now();
    }

    public void onOpenEnvelope(){
        resetIfNeeded();

        if (Objects.equals(this.numEnvelopes, 0)){
            throw new IllegalStateException("Ya reclamaste el numero maximo de sobres");
        }
        this.numEnvelopes--;
    }

    public void resetIfNeeded() {
        LocalDate today = LocalDate.now();

        if (lastResetDate == null || lastResetDate.isBefore(today)) {
            lastResetDate = today;
            numEnvelopes = 3;
        }
    }

}