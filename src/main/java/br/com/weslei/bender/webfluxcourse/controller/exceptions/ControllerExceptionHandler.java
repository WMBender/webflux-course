package br.com.weslei.bender.webfluxcourse.controller.exceptions;


import br.com.weslei.bender.webfluxcourse.service.exception.ObjectNotFoundException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(DuplicateKeyException.class)
    ResponseEntity<Mono<StandardError>> duplicatedKeyException(
            DuplicateKeyException ex, ServerHttpRequest request
    ){
        return ResponseEntity.badRequest()
            .body(Mono.just(
                StandardError.builder()
                    .timestamp(this.getCurrentTimeTruncatedToMillis())
                    .status(BAD_REQUEST.value())
                    .error(BAD_REQUEST.getReasonPhrase())
                    .message(this.verifyDupKey(ex.getMessage()))
                    .path(request.getPath().value())
                    .build()
                )
            );
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<Mono<ValidationError>> validationError(
        WebExchangeBindException ex, ServerHttpRequest request
    ){
        ValidationError error = new ValidationError(
                this.getCurrentTimeTruncatedToMillis(), request.getPath().toString(), BAD_REQUEST.value(), "Validation Error", "Error on validation attributes"
        );

        for(FieldError x : ex.getBindingResult().getFieldErrors()){
            error.addError(x.getField(), x.getDefaultMessage());
        }

        return ResponseEntity.status(BAD_REQUEST).body(Mono.just(error));
    }

    @ExceptionHandler(ObjectNotFoundException.class)
    ResponseEntity<Mono<StandardError>>objectNotFoundException(
            ObjectNotFoundException ex, ServerHttpRequest request
    ){
        return ResponseEntity.status(NOT_FOUND)
                .body(Mono.just(
                                StandardError.builder()
                                        .timestamp(this.getCurrentTimeTruncatedToMillis())
                                        .status(NOT_FOUND.value())
                                        .error(NOT_FOUND.getReasonPhrase())
                                        .message(ex.getMessage())
                                        .path(request.getPath().value())
                                        .build()
                        )
                );
    }

    private String verifyDupKey(String message){
        if(message.contains("email dup key")){
            return "E-mail already registered";
        }
        return "Dup key exception";
    }

    private LocalDateTime getCurrentTimeTruncatedToMillis(){
        return now().truncatedTo(ChronoUnit.MILLIS);
    }
}
