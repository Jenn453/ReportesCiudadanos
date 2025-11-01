package co.edu.uniquindio.proyecto.excepciones;

import co.edu.uniquindio.proyecto.dto.MensajeDTO;
import co.edu.uniquindio.proyecto.dto.ValidacionDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.ArrayList;
import java.util.List;


@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(DatosInvalidosException.class)
    public ResponseEntity<String> manejarDatosInvalidosException(DatosInvalidosException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
    @ExceptionHandler(EstadoInvalidoException.class)
    public ResponseEntity<String> manejarEstadoInvalidoException(EstadoInvalidoException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(ex.getMessage());
    }

    @ExceptionHandler(CodigoExpiradoException.class)
    public ResponseEntity<String> manejarCodigoExpiradoException(CodigoExpiradoException ex) {
        return ResponseEntity.status(HttpStatus.GONE).body(ex.getMessage());
    }


    @ExceptionHandler(DatoRepetidoException.class)
    public ResponseEntity<String> manejarDatoRepetidoException(DatoRepetidoException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(UsuarioNoEncontradoException.class)
    public ResponseEntity<String> manejarUsuarioNoEncontradoException(UsuarioNoEncontradoException ex) {
        return ResponseEntity.status(404).body(ex.getMessage());
    }

    @ExceptionHandler(EmailNoEncontradoException.class)
    public ResponseEntity<String> manejarEmailNoEncontradoException(EmailNoEncontradoException ex) {
        return ResponseEntity.status(404).body(ex.getMessage());
    }

    @ExceptionHandler(ComentarioNoEncontradoException.class)
    public ResponseEntity<String> manejarComentarioNoEncontradoException(ComentarioNoEncontradoException ex) {
        return ResponseEntity.status(404).body(ex.getMessage());
    }


    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<MensajeDTO<String>> noResourceFoundExceptionHandler (NoResourceFoundException ex){
        return ResponseEntity.status(404).body( new MensajeDTO<>(true, "El recurso no fue encontrado") );
    }

    @ExceptionHandler(EmailRepetidoException.class)
    public ResponseEntity<MensajeDTO<String>> noResourceFoundExceptionHandler (EmailRepetidoException ex){
        return ResponseEntity.status(409).body( new MensajeDTO<>(true, ex.getMessage()) );
    }

    @ExceptionHandler(AccesoNoPermitidoException.class)
    public ResponseEntity<MensajeDTO<String>> accesoNoPermitidoExceptionHandler (AccesoNoPermitidoException ex){
        return ResponseEntity.status(403).body( new MensajeDTO<>(true, ex.getMessage()) );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<MensajeDTO<String>> generalExceptionHandler (Exception e){
        e.printStackTrace();
        return ResponseEntity.internalServerError().body( new MensajeDTO<>(true, e.getMessage()) );
    }

    @ExceptionHandler(CategoriaNoEncontradaException.class)
    public ResponseEntity<String> manejarCategoriaNoEncontradaException(CategoriaNoEncontradaException ex) {
        return ResponseEntity.status(404).body(ex.getMessage());
    }

    @ExceptionHandler(ReporteNoEncontradoException.class)
    public ResponseEntity<MensajeDTO<String>> manejarReporteNoEncontradoException(ReporteNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new MensajeDTO<>(true, ex.getMessage()));
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<MensajeDTO<List<ValidacionDTO>>> validationExceptionHandler ( MethodArgumentNotValidException ex ) {
        List<ValidacionDTO> errores = new ArrayList<>();
        BindingResult results = ex.getBindingResult();


        for (FieldError e: results.getFieldErrors()) {
            errores.add( new ValidacionDTO(e.getField(), e.getDefaultMessage()) );
        }


        return ResponseEntity.badRequest().body( new MensajeDTO<>(true, errores) );
    }


}

