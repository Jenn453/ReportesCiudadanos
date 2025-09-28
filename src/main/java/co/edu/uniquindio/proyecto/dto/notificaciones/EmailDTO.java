package co.edu.uniquindio.proyecto.dto.notificaciones;

public record EmailDTO(
        String asunto,
        String cuerpo,
        String destinatario
) {
}
