package co.edu.uniquindio.proyecto.dto.login;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record PasswordNuevoDTO(
        @NotBlank String codigo,
        @Email @NotBlank  String email,
        @NotBlank @Length(min = 7, max = 20) String nuevoPassword
) {}