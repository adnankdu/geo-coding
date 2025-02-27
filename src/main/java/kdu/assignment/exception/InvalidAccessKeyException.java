package kdu.assignment.exception;

import kdu.assignment.logs.Logs;

public class InvalidAccessKeyException extends NullPointerException{
    public InvalidAccessKeyException(String message){
        super(message);
        Logs.logger.error(message);
    }
}
