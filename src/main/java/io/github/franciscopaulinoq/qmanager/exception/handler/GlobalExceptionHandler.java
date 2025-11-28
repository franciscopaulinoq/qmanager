package io.github.franciscopaulinoq.qmanager.exception.handler;

import io.github.franciscopaulinoq.qmanager.exception.CategoryAlreadyExistsException;
import io.github.franciscopaulinoq.qmanager.exception.CategoryNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(CategoryNotFoundException.class)
    public ProblemDetail handleCategoryNotFoundException(CategoryNotFoundException e) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
        problem.setTitle("Category not found");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(CategoryAlreadyExistsException.class)
    public ProblemDetail handleCategoryAlreadyExistsException(CategoryAlreadyExistsException e) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.getMessage());
        problem.setTitle("Category already exists");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationErrors(MethodArgumentNotValidException e) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
        problem.setTitle("Validation error");

        List<ValidationProblem> errors = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> new ValidationProblem(
                        fieldError.getField(),
                        fieldError.getDefaultMessage()
                ))
                .toList();

        problem.setProperty("errors", errors);
        return problem;
    }

    private record ValidationProblem(String field, String message) {}
}
