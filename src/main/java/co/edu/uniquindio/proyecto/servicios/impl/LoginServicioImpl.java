package co.edu.uniquindio.proyecto.servicios.impl;

import co.edu.uniquindio.proyecto.dto.TokenDTO;
import co.edu.uniquindio.proyecto.dto.login.LoginDTO;
import co.edu.uniquindio.proyecto.dto.login.PasswordNuevoDTO;
import co.edu.uniquindio.proyecto.dto.login.PasswordOlvidadoDTO;
import co.edu.uniquindio.proyecto.dto.notificaciones.EmailDTO;
import co.edu.uniquindio.proyecto.dto.usuarios.UsuarioNuevoCodigoDTO;
import co.edu.uniquindio.proyecto.excepciones.DatosInvalidosException;
import co.edu.uniquindio.proyecto.excepciones.EstadoInvalidoException;
import co.edu.uniquindio.proyecto.excepciones.UsuarioNoEncontradoException;
import co.edu.uniquindio.proyecto.modelo.documentos.Usuario;
import co.edu.uniquindio.proyecto.modelo.enums.EstadoUsuario;
import co.edu.uniquindio.proyecto.modelo.vo.CodigoValidacion;
import co.edu.uniquindio.proyecto.repositorios.UsuarioRepo;
import co.edu.uniquindio.proyecto.seguridad.JWTUtils;
import co.edu.uniquindio.proyecto.servicios.EmailServicio;
import co.edu.uniquindio.proyecto.servicios.LoginServicio;
import lombok.RequiredArgsConstructor;
import co.edu.uniquindio.proyecto.dto.usuarios.UsuarioNuevoCodigoDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class LoginServicioImpl implements LoginServicio {

    private final UsuarioRepo usuarioRepo;
    private final UsuarioServicioImpl usuarioServicioImpl;
    private final JWTUtils jwtUtils;
    private final BCryptPasswordEncoder passwordEncoder=new BCryptPasswordEncoder();
    private final EmailServicio emailServicio;

    @Override
    public TokenDTO login(LoginDTO loginDTO) throws Exception {


        Optional<Usuario> optionalUsuario = usuarioRepo.findByEmail(loginDTO.email());


        if(optionalUsuario.isEmpty()){
            throw new UsuarioNoEncontradoException("El usuario no existe");
        }


        Usuario usuario = optionalUsuario.get();

        // Verificar si el usuario est Activo

        if(usuario.getEstado()!= EstadoUsuario.ACTIVO){
            throw new EstadoInvalidoException("El usuario no esta activo");
        }


        // Verificar si la contraseña es correcta usando el PasswordEncoder
        if(!passwordEncoder.matches(loginDTO.password(), usuario.getPassword())){
            throw new DatosInvalidosException("Contraseña incorrecta");
        }


        String token = jwtUtils.generateToken(usuario.getId().toString(), crearClaims(usuario));
        return new TokenDTO(token);
    }


    private Map<String, String> crearClaims(Usuario usuario){
        return Map.of(
                "email", usuario.getEmail(),
                "nombre", usuario.getNombre(),
                "rol", "ROLE_"+usuario.getRol().name()
        );
    }



    @Override
    public void recuperarPassword(UsuarioNuevoCodigoDTO usuarioNuevoCodigoDTO) throws Exception {

        // 1. Generar un nuevo código de activación
        String nuevoCodigo = usuarioServicioImpl.generarCodigoAleatorio();

        // 2. Buscar el usuario por su correo
        Optional<Usuario> usuarioOptional = usuarioRepo.findByEmail(usuarioNuevoCodigoDTO.email());
        if (usuarioOptional.isEmpty()) {
            throw new UsuarioNoEncontradoException("No se encontró un usuario con el email " + usuarioNuevoCodigoDTO.email());
        }

        // 3. Obtener el usuario
        Usuario usuario = usuarioOptional.get();

        // 4. Crear un nuevo código de validación
        CodigoValidacion codigo = new CodigoValidacion(
                LocalDateTime.now(),
                nuevoCodigo
        );

        // 5. Asignar el nuevo código al usuario
        usuario.setCodigoValidacion(codigo);
        usuarioRepo.save(usuario); // Guardar los cambios en la base de datos

        // 6. Enviar el código de activación por correo
        String cuerpoCorreo = "Tu codigo de recuperacion de password es: " + nuevoCodigo;
        EmailDTO emailDTO = new EmailDTO("Código de Recuperacion de Password", cuerpoCorreo, usuarioNuevoCodigoDTO.email());
        emailServicio.enviarCorreo(emailDTO); // Enviar el correo con el código
    }

    @Override
    public void actualizarPassword(PasswordNuevoDTO passwordNuevoDTO) throws Exception {
        Optional<Usuario> usuarioOptional = usuarioRepo.findByEmail(passwordNuevoDTO.email());
        if (usuarioOptional.isEmpty()) {
            throw new UsuarioNoEncontradoException("No se encontró un usuario con el email " + passwordNuevoDTO.email());
        }

        Usuario usuario = usuarioOptional.get();

        if(!usuario.getCodigoValidacion().getCodigo().equals(passwordNuevoDTO.codigo())){
            throw new DatosInvalidosException("El codigo no coincide");
        }

        usuario.setPassword(passwordEncoder.encode(passwordNuevoDTO.nuevoPassword()));
        usuario.setCodigoValidacion(null);
        usuarioRepo.save(usuario);

    }


}
