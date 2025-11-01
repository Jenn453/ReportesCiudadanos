package co.edu.uniquindio.proyecto.mapper;

import co.edu.uniquindio.proyecto.dto.reportes.HistorialReporteDTO;
import co.edu.uniquindio.proyecto.modelo.vo.HistorialReporte;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Mapper(componentModel = "spring")
public interface HistorialReporteMapper {

    @Mapping(source = "fecha", target = "fecha", qualifiedByName = "formatearFecha")
    @Mapping(source = "fechaLimiteEdicion", target = "fechaLimiteEdicion", qualifiedByName = "formatearFecha")
    HistorialReporteDTO toDTO(HistorialReporte historial);

    @Named("formatearFecha")
    default String formatearFecha(LocalDateTime fecha) {
        if (fecha == null) return null;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                "d 'de' MMMM 'de' yyyy h:mm a", new Locale("es", "CO")
        );

        return fecha.format(formatter)
                .replace("AM", "a.m.")
                .replace("PM", "p.m.");
    }
}