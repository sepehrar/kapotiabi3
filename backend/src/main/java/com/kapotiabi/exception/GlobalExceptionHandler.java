package com.kapotiabi.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(DuplicateEmailException.class)
  public ResponseEntity<Map<String, Object>> handleDuplicateEmail(
      DuplicateEmailException ex, HttpServletRequest req) {
    return error(HttpStatus.CONFLICT, ex.getMessage(), req.getRequestURI());
  }

  @ExceptionHandler(AlreadySavedException.class)
  public ResponseEntity<Map<String, Object>> handleAlreadySaved(
      AlreadySavedException ex, HttpServletRequest req) {
    return error(HttpStatus.CONFLICT, ex.getMessage(), req.getRequestURI());
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<Map<String, Object>> handleDataIntegrityViolation(
      DataIntegrityViolationException ex, HttpServletRequest req) {
    return error(HttpStatus.CONFLICT, "Shop already saved", req.getRequestURI());
  }

  @ExceptionHandler(ForbiddenException.class)
  public ResponseEntity<Map<String, Object>> handleForbidden(
      ForbiddenException ex, HttpServletRequest req) {
    return error(HttpStatus.FORBIDDEN, ex.getMessage(), req.getRequestURI());
  }

  @ExceptionHandler(InvalidCredentialsException.class)
  public ResponseEntity<Map<String, Object>> handleInvalidCredentials(
      InvalidCredentialsException ex, HttpServletRequest req) {
    return error(HttpStatus.UNAUTHORIZED, ex.getMessage(), req.getRequestURI());
  }

  @ExceptionHandler(InvalidTokenException.class)
  public ResponseEntity<Map<String, Object>> handleInvalidToken(
      InvalidTokenException ex, HttpServletRequest req) {
    return error(HttpStatus.BAD_REQUEST, ex.getMessage(), req.getRequestURI());
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Map<String, Object>> handleIllegalArgument(
      IllegalArgumentException ex, HttpServletRequest req) {
    return error(HttpStatus.BAD_REQUEST, ex.getMessage(), req.getRequestURI());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleValidation(
      MethodArgumentNotValidException ex, HttpServletRequest req) {
    String message =
        ex.getBindingResult().getFieldErrors().stream()
            .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
            .collect(Collectors.joining(", "));
    return error(HttpStatus.BAD_REQUEST, message, req.getRequestURI());
  }

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<Map<String, Object>> handleNotFound(
      NotFoundException ex, HttpServletRequest req) {
    return error(HttpStatus.NOT_FOUND, ex.getMessage(), req.getRequestURI());
  }

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<Map<String, Object>> handleConflict(
      IllegalStateException ex, HttpServletRequest req) {
    return error(HttpStatus.CONFLICT, ex.getMessage(), req.getRequestURI());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex, HttpServletRequest req) {
    return error(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", req.getRequestURI());
  }

  private ResponseEntity<Map<String, Object>> error(
      HttpStatus status, String message, String path) {
    return ResponseEntity.status(status)
        .body(
            Map.of(
                "timestamp",
                Instant.now().toString(),
                "status",
                status.value(),
                "message",
                message,
                "path",
                path));
  }
}
