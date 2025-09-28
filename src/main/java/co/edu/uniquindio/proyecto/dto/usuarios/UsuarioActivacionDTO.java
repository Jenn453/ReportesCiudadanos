package co.edu.uniquindio.proyecto.dto.usuarios;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

public record UsuarioActivacionDTO (
        @Email @NotBlank  String email,
        @NotBlank String codigo
){
}