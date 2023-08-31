package com.example.Blog_API.exception;

import com.example.Blog_API.enums.ResponseCode;
import com.example.Blog_API.payload.ErrorDetails;
import com.example.Blog_API.payload.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalException {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> globleExcpetionHandler(Exception e, WebRequest request){
        ErrorDetails errorDetails=new ErrorDetails(new Date(),e.getMessage(),request.getDescription(false));
        ErrorResponse errorResponse=new ErrorResponse();
        errorResponse.setErrorDetails(errorDetails);
        errorResponse.setResponseCode(ResponseCode.ERROR.getCode());
        errorResponse.setResponseStatus(ResponseCode.ERROR.name());
        return ResponseEntity.ok(errorResponse);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex){
        Map<String, String> errors=new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> errors.put(error.getField(),error.getDefaultMessage()));
        return errors;
    }


    @ExceptionHandler(NotFoundException.class)
    public final ResponseEntity<ErrorResponse> handleNotFoundException(Exception ex, WebRequest request) throws Exception {
        ErrorDetails errorDetails = new ErrorDetails(
                new Date(),
                ex.getMessage(),
                request.getDescription(false));
        ErrorResponse errorResponse=new ErrorResponse();
        errorResponse.setErrorDetails(errorDetails);
        errorResponse.setResponseStatus(ResponseCode.RECORD_NOT_FOUND.name());
        errorResponse.setResponseCode(ResponseCode.RECORD_NOT_FOUND.getCode());
        return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class NotFoundException extends RuntimeException {
        public NotFoundException(String message) {
            super(message);
        }
    }

    @ExceptionHandler(DuplicateValueException.class)
    public final ResponseEntity<ErrorResponse> handleDuplicateValueException(Exception ex, WebRequest request) throws Exception {
        ErrorDetails errorDetails = new ErrorDetails(
                new Date(),
                ex.getMessage(),
                request.getDescription(false));
        ErrorResponse errorResponse=new ErrorResponse();
        errorResponse.setErrorDetails(errorDetails);
        errorResponse.setResponseStatus(ResponseCode.DUPLICATED.name());
        errorResponse.setResponseCode(ResponseCode.DUPLICATED.getCode());
        return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class DuplicateValueException extends RuntimeException {
        public DuplicateValueException(String message) {
            super(message);
        }
    }
}
