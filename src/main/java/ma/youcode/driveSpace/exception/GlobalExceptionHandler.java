package ma.youcode.driveSpace.exception;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
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
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(problem(HttpStatus.NOT_FOUND, "Not found", ex.getMessage(), request));
  }

  @ExceptionHandler(BusinessRuleException.class)
  public ResponseEntity<ProblemDetail> handleBusinessRule(BusinessRuleException ex, WebRequest request) {
    log.warn("Conflict: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(problem(HttpStatus.CONFLICT, "Conflict", ex.getMessage(), request));
  }

  @ExceptionHandler(ForbiddenActionException.class)
  public ResponseEntity<ProblemDetail> handleForbidden(ForbiddenActionException ex, WebRequest request) {
    log.warn("Forbidden: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(problem(HttpStatus.FORBIDDEN, "Forbidden", ex.getMessage(), request));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ProblemDetail> handleValidation(MethodArgumentNotValidException ex, WebRequest request) {
    Map<String, String> errors = new LinkedHashMap<>();
    ex.getBindingResult().getFieldErrors()
        .forEach(e -> errors.putIfAbsent(e.getField(), e.getDefaultMessage()));
    log.warn("Validation failed: {}", errors);
    ProblemDetail detail = problem(HttpStatus.BAD_REQUEST, "Bad request", "Invalid request", request);
    detail.setProperty("errors", errors);
    return ResponseEntity.badRequest().body(detail);
  }

  @ExceptionHandler(MissingRequestHeaderException.class)
  public ResponseEntity<ProblemDetail> handleMissingHeader(MissingRequestHeaderException ex, WebRequest request) {
    log.warn("Missing header: {}", ex.getHeaderName());
    return ResponseEntity.badRequest()
        .body(problem(HttpStatus.BAD_REQUEST, "Bad request", "Missing header: " + ex.getHeaderName(), request));
  }

  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<ProblemDetail> handleNoRoute(NoResourceFoundException ex, WebRequest request) {
    log.warn("No endpoint: {}", ex.getResourcePath());
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(problem(HttpStatus.NOT_FOUND, "Not found", "No endpoint at /" + ex.getResourcePath(), request));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ProblemDetail> handleUnexpected(Exception ex, WebRequest request) {
    log.error("Unexpected error", ex);
    return ResponseEntity.internalServerError()
        .body(problem(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", "Unexpected error", request));
  }

  private ProblemDetail problem(HttpStatus status, String title, String detail, WebRequest request) {
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
    problem.setTitle(title);
    problem.setProperty("timestamp", Instant.now());
    problem.setProperty("path", ((ServletWebRequest) request).getRequest().getRequestURI());
    return problem;
  }
}
