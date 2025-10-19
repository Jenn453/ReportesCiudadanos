package co.edu.uniquindio.proyecto.dto.reportes;

import jakarta.validation.constraints.NotNull;

public record UbicacionDTO(
        @NotNull Double latitud,
        @NotNull Double longitud
) {}