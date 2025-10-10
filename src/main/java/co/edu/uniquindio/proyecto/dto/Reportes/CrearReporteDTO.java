package co.edu.uniquindio.proyecto.dto.Reportes;

import co.edu.uniquindio.proyecto.dto.UbicacionDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;


public record CrearReporteDTO(
        @NotBlank String titulo,
        @NotBlank String descripcion,
        @NotNull UbicacionDTO ubicacion,
        @NotBlank String ciudad,//  Aquí agregamos el objeto de ubicación
        List<String>imagenes,
        @NotBlank String categoria
) {}