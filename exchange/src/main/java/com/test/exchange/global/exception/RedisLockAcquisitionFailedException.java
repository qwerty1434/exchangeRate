package com.test.exchange.global.exception;

public class RedisLockAcquisitionFailedException extends RuntimeException{
    ErrorMessage errorMessage;
    public RedisLockAcquisitionFailedException(ErrorMessage errorMessage) {
        super(errorMessage.getMessage());
        this.errorMessage = errorMessage;
    }
}
