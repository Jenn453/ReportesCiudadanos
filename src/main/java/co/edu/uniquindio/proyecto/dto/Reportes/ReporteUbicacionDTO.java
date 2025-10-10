package co.edu.uniquindio.proyecto.dto.Reportes;

import jakarta.validation.constraints.NotNull;

public record ReporteUbicacionDTO(
        @NotNull Double latitud,
        @NotNull Double longitud

) {
}
