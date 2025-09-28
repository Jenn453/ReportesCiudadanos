package co.edu.uniquindio.proyecto.dto.usuarios;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record CambiarPasswordDTO(
        @NotBlank String actualPassword,
        @NotBlank @Length(min = 7, max = 20) String nuevoPassword
) {
}

