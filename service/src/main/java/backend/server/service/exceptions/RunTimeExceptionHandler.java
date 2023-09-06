package backend.server.service.exceptions;

import backend.server.service.security.POJOs.responses.MessageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class RunTimeExceptionHandler {
    @ExceptionHandler(value = {RuntimeException.class})
    public ResponseEntity<Object> handelApiRunTimeException(RuntimeException e){
        throw e;
        // return handler exception
        //return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
    }
}
