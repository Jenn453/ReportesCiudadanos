package co.edu.uniquindio.proyecto.dto.notificaciones;

import jakarta.validation.constraints.NotBlank;
import co.edu.uniquindio.proyecto.dto.UbicacionDTO;

public record NotificacionUbicacionDTO(
        @NotBlank String idReporte,
        UbicacionDTO ubicacion,
        int radio
) {}