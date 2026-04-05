package co.edu.uniquindio.proyecto.dto.comentarios;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ComentarioDTO {
    private String id;
    private String clienteId;
    @NotBlank private String nombreUsuario;
    @NotBlank private String mensaje;
    private String fechaCreacion;
}
