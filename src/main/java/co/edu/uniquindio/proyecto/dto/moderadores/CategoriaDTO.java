package co.edu.uniquindio.proyecto.dto.moderadores;

import jakarta.validation.constraints.NotBlank;

public record CategoriaDTO(
        @NotBlank String nombre,
        String color,
        String descripcion) {

}
