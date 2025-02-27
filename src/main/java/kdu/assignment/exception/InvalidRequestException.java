package kdu.assignment.exception;

import kdu.assignment.logs.Logs;

public class InvalidRequestException extends RuntimeException {
    public InvalidRequestException(String message){
        super(message);
        Logs.logger.error(message);
    }
}
