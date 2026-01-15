package org.kmontano.minidex.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class EnvelopeLimitReachedException extends RuntimeException {
    public EnvelopeLimitReachedException(){
        super("Daily envelope limit already reached");
    }
}
