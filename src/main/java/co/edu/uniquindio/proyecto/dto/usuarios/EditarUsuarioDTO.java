package co.edu.uniquindio.proyecto.dto.usuarios;

import co.edu.uniquindio.proyecto.modelo.vo.Ubicacion;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record EditarUsuarioDTO (
        @NotBlank @Length(max = 100) String nombre,
        @Length(max = 10) String telefono,
        @NotBlank @Length(max = 100) String ciudad,
        @NotBlank @Length(max = 100) String direccion,
        Ubicacion ubicacion
) {
}