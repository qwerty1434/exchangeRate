package com.bolta.exchange.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.function.client.WebClientRequestException;

import java.util.NoSuchElementException;

import static com.bolta.exchange.global.exception.ErrorMessage.*;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiCallFailedException.class)
    protected ResponseEntity ApiCallFailedException(ApiCallFailedException e){
        log.error("api server에서 값을 제대로 받아오지 못했습니다.", e);
        return ResponseEntity.internalServerError()
                .body(ErrorResponse.of(INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler(NoSuchElementException.class)
    protected ResponseEntity NoSuchElementException(NoSuchElementException e){
        log.error("api server의 quotes에서 값을 찾을 수 없습니다.", e);
        return ResponseEntity.internalServerError()
                .body(ErrorResponse.of(RATE_NOT_FOUND_ERROR));
    }

    @ExceptionHandler(WebClientRequestException.class)
    protected ResponseEntity WebClientRequestException(WebClientRequestException e){
        log.error("WebClient의 요청에 문제가 발생했습니다.", e);
        return ResponseEntity.internalServerError()
                .body(ErrorResponse.of(INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity IllegalArgumentException(IllegalArgumentException e){
        log.error("사용자가 올바르지 않은 값을 입력했습니다.", e);
        return ResponseEntity.badRequest()
                .body(ErrorResponse.of(HttpStatus.BAD_REQUEST,e.getMessage()));
    }

    @ExceptionHandler(ConversionFailedException.class)
    protected ResponseEntity ConversionFailedException(ConversionFailedException e){
        log.error("사용자가 올바르지 않은 Currency로 요청을 전송했습니다.", e);
        return ResponseEntity.badRequest()
                .body(ErrorResponse.of(INVALID_CURRENCY_VALUE_ERROR));
    }


}
