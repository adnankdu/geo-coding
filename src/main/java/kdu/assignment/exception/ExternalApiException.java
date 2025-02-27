package kdu.assignment.exception;

import kdu.assignment.logs.Logs;

public class ExternalApiException extends RuntimeException {
    public ExternalApiException(String message, Throwable cause) {
        super(message, cause);
    }
    public ExternalApiException(String message){
        super(message);
        Logs.logger.error(message);
    }
}
