package co.edu.uniquindio.proyecto.dto.notificaciones;

import jakarta.validation.constraints.NotBlank;

public record NotificacionDTO(
        @NotBlank String titulo,
        @NotBlank String cuerpo,
        @NotBlank String topic
) {}