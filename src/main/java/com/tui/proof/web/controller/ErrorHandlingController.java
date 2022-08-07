package com.tui.proof.web.controller;

import com.tui.proof.web.model.ValidationErrorResponse;
import com.tui.proof.web.model.Violation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @author akazmierczak
 * @create 07.08.2022
 */
@Slf4j
@ControllerAdvice
@Validated
public class ErrorHandlingController {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationError(MethodArgumentNotValidException exception) {
        log.debug("Handling validation exception.");

        ValidationErrorResponse error = new ValidationErrorResponse();
        exception.getBindingResult().getFieldErrors().forEach(fieldError ->
                error.violations.add(
                        new Violation(fieldError.getField(), fieldError.getDefaultMessage())
                )
        );

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }


}
