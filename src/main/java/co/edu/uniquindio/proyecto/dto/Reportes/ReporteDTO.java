package co.edu.uniquindio.proyecto.dto.Reportes;

import co.edu.uniquindio.proyecto.dto.UbicacionDTO;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record ReporteDTO(
        @NotBlank String usuario,
        @NotBlank String titulo,
        @NotBlank String categoria,
        @NotBlank String descripcion,
        UbicacionDTO ubicacion,
        @NotBlank String estadoActual,
        List<String> imagenes,
        String fechaCreacion,
        Integer cantidadImportante

) {}
