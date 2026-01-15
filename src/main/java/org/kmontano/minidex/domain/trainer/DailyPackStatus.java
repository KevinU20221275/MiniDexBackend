package org.kmontano.minidex.domain.trainer;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.kmontano.minidex.exception.EnvelopeLimitReachedException;

import java.time.LocalDate;

/**
 * Represents the daily envelope status of a trainer.
 *
 * This class is responsible for:
 * - Tracking how many envelopes are available per day
 * - Enforcing the daily envelope limit
 * - Resetting the envelope count when a new day starts
 *
 * This is a domain object and should not contain any HTTP-specific logic.
 */
@Data
public class DailyPackStatus {
    /**
     * Number of envelopes available for the current day.
     */
    private Integer numEnvelopes;

    /**
     * Date of the last daily reset.
     * Used to determine when the envelope count must be reset.
     *
     * Serialized as yyyy-MM-dd for frontend compatibility.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate lastResetDate;

    /**
     * Creates a new DailyPackStatus with the default daily values.
     * A trainer starts with 3 envelopes available for the current day.
     */
    public DailyPackStatus() {
        this.numEnvelopes = 3;
        this.lastResetDate = LocalDate.now();
    }


    /**
     * Handles the logic for opening an envelope.
     *
     * - Resets the daily counter if a new day has started
     * - Decreases the available envelope count
     * - Throws a domain conflict exception if no envelopes are available
     *
     * @throws EnvelopeLimitReachedException if the daily envelope limit
     *         has already been reached
     */
    public void onOpenEnvelope(){
        resetIfNeeded();

        if (this.numEnvelopes <= 0){
            throw new EnvelopeLimitReachedException();
        }
        this.numEnvelopes--;
    }

    /**
     * Resets the daily envelope count if the last reset date
     * is before the current date.
     *
     * This method ensures that each trainer receives a fresh
     * set of envelopes once per day.
     */
    public void resetIfNeeded() {
        LocalDate today = LocalDate.now();

        if (lastResetDate == null || lastResetDate.isBefore(today)) {
            lastResetDate = today;
            numEnvelopes = 3;
        }
    }

}