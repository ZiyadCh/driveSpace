package ma.youcode.driveSpace.exception;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ProblemDetail> handleNotFound(ResourceNotFoundException ex, WebRequest request) {
    return build(HttpStatus.NOT_FOUND, "Resource not found", ex.getMessage(), request);
  }

  @ExceptionHandler({ BusinessRuleException.class, DataIntegrityViolationException.class })
  public ResponseEntity<ProblemDetail> handleConflict(RuntimeException ex, WebRequest request) {
    return build(HttpStatus.CONFLICT, "Business rule violated", ex.getMessage(), request);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ProblemDetail> handleValidation(MethodArgumentNotValidException ex, WebRequest request) {
    Map<String, String> errors = new LinkedHashMap<>();
    ex.getBindingResult().getFieldErrors()
        .forEach(error -> errors.putIfAbsent(error.getField(), error.getDefaultMessage()));

    ProblemDetail problem = problem(HttpStatus.BAD_REQUEST, "Validation failed", "Invalid request body", request);
    problem.setProperty("errors", errors);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ProblemDetail> handleUnexpected(Exception ex, WebRequest request) {
    return build(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", "An unexpected error occurred", request);
  }

  private ResponseEntity<ProblemDetail> build(HttpStatus status, String title, String detail, WebRequest request) {
    return ResponseEntity.status(status).body(problem(status, title, detail, request));
  }

  private ProblemDetail problem(HttpStatus status, String title, String detail, WebRequest request) {
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
    problem.setTitle(title);
    problem.setProperty("path", request.getDescription(false).replaceFirst("^uri=", ""));
    return problem;
  }
}
