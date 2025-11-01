package co.edu.uniquindio.proyecto.dto.comentarios;

import jakarta.validation.constraints.NotBlank;

public record ComentarioDTO(
        @NotBlank String nombreUsuario,
        @NotBlank String mensaje,
        String fechaCreacion
) {}
