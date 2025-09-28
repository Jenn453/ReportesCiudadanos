package co.edu.uniquindio.proyecto.dto.login;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record PasswordOlvidadoDTO(
        @Email @NotBlank String email
) {}