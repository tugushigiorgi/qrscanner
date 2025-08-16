package com.asterbit.qrscanner.exceptions;

import static com.asterbit.qrscanner.util.ConstMessages.AUTHENTICATION_FAILED;
import static com.asterbit.qrscanner.util.ConstMessages.EMAIL_ALREADY_EXISTS;
import static com.asterbit.qrscanner.util.ConstMessages.TIMESTAMP;
import static com.asterbit.qrscanner.util.ConstMessages.UNEXPECTED_ERROR;
import static com.asterbit.qrscanner.util.ConstMessages.USER_NOT_FOUND;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import java.time.LocalDateTime;
import java.util.HashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ProblemDetail handleValidationExceptions(MethodArgumentNotValidException ex) {
    log.warn("Validation failed: {}", ex.getMessage());

    var fieldErrors = new HashMap<>();
    ex.getBindingResult().getFieldErrors().forEach(error ->
        fieldErrors.put(error.getField(), error.getDefaultMessage())
    );

    var problem = ProblemDetail.forStatusAndDetail(BAD_REQUEST,
        "Validation failed for one or more fields");
    problem.setTitle("Validation Error");
    problem.setProperty("fieldErrors", fieldErrors);
    problem.setProperty(TIMESTAMP, LocalDateTime.now());
    return problem;
  }

  @ExceptionHandler(ResponseStatusException.class)
  public ProblemDetail handleResponseStatusException(ResponseStatusException ex) {
    log.warn("ResponseStatusException: {}", ex.getMessage());

    var problem = ProblemDetail.forStatusAndDetail(ex.getStatusCode(), ex.getReason());
    problem.setTitle("Business Error");
    problem.setProperty(TIMESTAMP, LocalDateTime.now());
    return problem;
  }

  @ExceptionHandler(InvalidTokenException.class)
  public ProblemDetail handleInvalidTokenException(InvalidTokenException ex) {
    log.warn("Invalid token: {}", ex.getMessage());

    var problem = ProblemDetail.forStatusAndDetail(BAD_REQUEST, ex.getMessage());
    problem.setTitle("Invalid Token");
    problem.setProperty(TIMESTAMP, LocalDateTime.now());
    return problem;
  }

  @ExceptionHandler(InvalidCredentialsException.class)
  public ProblemDetail handleInvalidCredentials(InvalidCredentialsException ex) {
    log.warn("Invalid credentials: {}", ex.getMessage());

    var problem = ProblemDetail.forStatusAndDetail(UNAUTHORIZED, ex.getMessage());
    problem.setTitle(AUTHENTICATION_FAILED);
    problem.setProperty(TIMESTAMP, LocalDateTime.now());
    return problem;
  }

  @ExceptionHandler(EmailAlreadyExistsException.class)
  public ProblemDetail handleEmailExists(EmailAlreadyExistsException ex) {
    log.warn("Email conflict: {}", ex.getMessage());

    var problem = ProblemDetail.forStatusAndDetail(BAD_REQUEST, ex.getMessage());
    problem.setTitle(EMAIL_ALREADY_EXISTS);
    problem.setProperty(TIMESTAMP, LocalDateTime.now());
    return problem;
  }

  @ExceptionHandler(UsernameNotFoundException.class)
  public ProblemDetail handleUsernameNotFoundException(UsernameNotFoundException ex) {
    log.warn("User not found: {}", ex.getMessage());

    var problem = ProblemDetail.forStatusAndDetail(NOT_FOUND, ex.getMessage());
    problem.setTitle(USER_NOT_FOUND);
    problem.setProperty(TIMESTAMP, LocalDateTime.now());
    return problem;
  }

  @ExceptionHandler(Exception.class)
  public ProblemDetail handleGenericException(Exception ex) {
    log.error("Unexpected error", ex);

    ProblemDetail problem = ProblemDetail.forStatusAndDetail(
        INTERNAL_SERVER_ERROR,
        UNEXPECTED_ERROR
    );
    problem.setTitle("Internal Server Error");
    problem.setProperty(TIMESTAMP, LocalDateTime.now());
    problem.setProperty("exception", ex.getClass().getSimpleName());
    return problem;
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ProblemDetail handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
    log.warn("Required request body is missing or unreadable: {}", ex.getMessage());

    var problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
        "Required request body is missing or malformed");
    problem.setTitle("Invalid Request Body");
    problem.setProperty(TIMESTAMP, LocalDateTime.now());
    return problem;
  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ProblemDetail handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
    log.warn("Request method not supported: {}", ex.getMethod());

    var problem = ProblemDetail.forStatusAndDetail(HttpStatus.METHOD_NOT_ALLOWED,
        "Request method '" + ex.getMethod() + "' is not supported for this endpoint");
    problem.setTitle("Method Not Allowed");
    problem.setProperty(TIMESTAMP, LocalDateTime.now());
    return problem;
  }


  @ExceptionHandler(NoResourceFoundException.class)
  public ProblemDetail handleNoResourceFound(NoResourceFoundException ex) {
    String path = ex.getResourcePath(); // Correct method
    log.warn("Static resource not found: {}", path);

    var problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND,
        "Requested static resource not found: " + path);
    problem.setTitle("Resource Not Found");
    problem.setProperty(TIMESTAMP, LocalDateTime.now());
    return problem;
  }
}
