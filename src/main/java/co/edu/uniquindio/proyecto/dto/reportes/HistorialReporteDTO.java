package co.edu.uniquindio.proyecto.dto.reportes;

import co.edu.uniquindio.proyecto.modelo.enums.EstadoReporte;

public record HistorialReporteDTO(
        String motivo,
        EstadoReporte estado,
        String fecha,
        String fechaLimiteEdicion
)
{}
