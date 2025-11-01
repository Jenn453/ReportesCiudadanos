package co.edu.uniquindio.proyecto.excepciones;

public class ReporteNoEncontradoException extends RuntimeException {
    public ReporteNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}