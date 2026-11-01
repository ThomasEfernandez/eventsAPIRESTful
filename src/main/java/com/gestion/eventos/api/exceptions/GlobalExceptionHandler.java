package com.gestion.eventos.api.exceptions;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

  
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneralException(Exception ex) {
        System.err.println("Ocurrió un error inesperado: " + ex.getMessage());
        ex.printStackTrace(); // En producción, se loguea, no se imprime a consola
        
        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put("error", "Error Interno del Servidor");
        errorDetails.put("message", "Ocurrió un error inesperado. Por favor, inténtalo de nuevo más tarde.");
        
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
@ExceptionHandler(DataIntegrityViolationException.class)
public ResponseEntity<Map<String, String>> handleDataIntegrityViolationSimple(DataIntegrityViolationException ex) {
    Map<String, String> errorDetails = new HashMap<>();
    errorDetails.put("error", "Conflicto de Datos");
    errorDetails.put("message", "La operación no se pudo completar debido a un conflicto de datos. Asegúrate de que los valores sean únicos y las referencias existan.");
    // O puedes usar el mensaje original de la causa raíz si no te importa exponerlo (no recomendado)
 //    errorDetails.put("message", ex.getRootCause() != null ? ex.getRootCause().getMessage() : "Error de integridad de datos.");
    return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT);
}
}