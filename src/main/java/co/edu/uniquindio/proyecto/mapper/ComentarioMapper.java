package co.edu.uniquindio.proyecto.mapper;

import co.edu.uniquindio.proyecto.dto.comentarios.ComentarioDTO;
import co.edu.uniquindio.proyecto.dto.comentarios.CrearComentarioDTO;
import co.edu.uniquindio.proyecto.modelo.documentos.Comentario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface ComentarioMapper {

    @Mapping(target = "fechaCreacion", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "nombreUsuario", ignore = true) // Se obtiene del token
    @Mapping(target = "clienteId", ignore = true) // Se obtiene del token
    @Mapping(target = "reporteId", ignore = true) // Se asigna manualmente
    Comentario toDocument(CrearComentarioDTO crearComentarioDTO);

    ComentarioDTO toDTO(Comentario comentario);

    default String map(LocalDateTime fecha) {
        if (fecha == null) return null;

        java.time.format.DateTimeFormatter formatter =
                java.time.format.DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy h:mm a", new java.util.Locale("es", "CO"));

        // Convertir "PM" a "pm", etc.
        return fecha.format(formatter).replace("AM", "am").replace("PM", "pm");
    }
}