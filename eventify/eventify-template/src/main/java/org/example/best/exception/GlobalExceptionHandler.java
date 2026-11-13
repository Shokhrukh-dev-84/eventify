package org.example.best.exception;

import jakarta.persistence.EntityNotFoundException;
import org.example.best.exception.type.ErrorLevel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler{

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse>  handleNotFound(EntityNotFoundException ex) {
        ErrorResponse error = createError("NOT_FOUND", ex.getMessage(), ErrorLevel.ERROR,
                Collections.emptyList());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        ErrorResponse error = createError("BAD_REQUEST", ex.getMessage(), ErrorLevel.ERROR,
                Collections.emptyList());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        ErrorResponse error = createError("FORBIDDEN", ex.getMessage(), ErrorLevel.ERROR,
                Collections.emptyList());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException ex) {
        ErrorResponse error = createError("CONFLICT", ex.getMessage(), ErrorLevel.ERROR,
                Collections.emptyList());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleArgumentNotValidException(
            MethodArgumentNotValidException ex) {
        ErrorResponse error = createError("BAD_REQUEST", ex.getMessage(), ErrorLevel.ERROR,
                Collections.emptyList());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(UserNotAuthenticatedException.class)
    public ResponseEntity<ErrorResponse> handleHttpClientErrorException(UserNotAuthenticatedException ex) {
        ErrorResponse error = createError("UNAUTHORIZED", ex.getMessage(), ErrorLevel.ERROR,
                Collections.emptyList());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    private ErrorResponse createError(String code,
                                      String message,
                                      ErrorLevel level,
                                      List<ErrorDetail> details) {
        ErrorResponse error = new ErrorResponse();
        error.setCode(code);
        error.setLevel(level);
        error.setMessage(message);
        error.setDetails(details);
        return error;
    }
}
