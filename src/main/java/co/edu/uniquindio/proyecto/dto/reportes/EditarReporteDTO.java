package co.edu.uniquindio.proyecto.dto.reportes;

import co.edu.uniquindio.proyecto.dto.UbicacionDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record EditarReporteDTO(
        @NotBlank String titulo,
        @NotBlank String categoria,
        @NotBlank String ciudad,
        @NotBlank String descripcion,
        @NotNull UbicacionDTO ubicacion,
        @NotEmpty List<String> imagen
) {}