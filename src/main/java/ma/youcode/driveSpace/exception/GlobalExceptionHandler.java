package ma.youcode.driveSpace.exception;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ProblemDetail> handleNotFound(ResourceNotFoundException ex, WebRequest request) {
    log.warn("Not found: {}", ex.getMessage());
    return build(HttpStatus.NOT_FOUND, "Resource not found", ex.getMessage(), request);
  }

  @ExceptionHandler(BusinessRuleException.class)
  public ResponseEntity<ProblemDetail> handleBusinessRule(BusinessRuleException ex, WebRequest request) {
    log.warn("Business rule refused: {}", ex.getMessage());
    return build(HttpStatus.CONFLICT, "Business rule violated", ex.getMessage(), request);
  }

  @ExceptionHandler(ForbiddenActionException.class)
  public ResponseEntity<ProblemDetail> handleForbidden(ForbiddenActionException ex, WebRequest request) {
    log.warn("Forbidden action: {}", ex.getMessage());
    return build(HttpStatus.FORBIDDEN, "Forbidden action", ex.getMessage(), request);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ProblemDetail> handleValidation(MethodArgumentNotValidException ex, WebRequest request) {
    Map<String, String> errors = new LinkedHashMap<>();
    ex.getBindingResult().getFieldErrors()
        .forEach(e -> errors.putIfAbsent(e.getField(), e.getDefaultMessage()));
    log.warn("Validation failed: {}", errors);
    ProblemDetail problem = problem(HttpStatus.BAD_REQUEST, "Validation failed", "Invalid request body", request);
    problem.setProperty("errors", errors);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
  }

  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<ProblemDetail> handleNoRoute(NoResourceFoundException ex, WebRequest request) {
    log.warn("No endpoint: {}", ex.getResourcePath());
    return build(HttpStatus.NOT_FOUND, "Endpoint not found",
        "No endpoint exists at /" + ex.getResourcePath(), request);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ProblemDetail> handleUnexpected(Exception ex, WebRequest request) {
    log.error("Unexpected error", ex);
    return build(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error",
        "An unexpected error occurred", request);
  }

  private ResponseEntity<ProblemDetail> build(HttpStatus status, String title, String detail, WebRequest request) {
    return ResponseEntity.status(status).body(problem(status, title, detail, request));
  }

  private ProblemDetail problem(HttpStatus status, String title, String detail, WebRequest request) {
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
    problem.setTitle(title);
    problem.setProperty("timestamp", Instant.now());
    problem.setProperty("path", ((ServletWebRequest) request).getRequest().getRequestURI());
    return problem;
  }
}
