package co.edu.uniquindio.proyecto.dto.moderadores;

import jakarta.validation.constraints.NotBlank;

public record ObtenerCategoriaDTO(
        String id,
        @NotBlank String nombre,
        String color,
        String descripcion) {

}
