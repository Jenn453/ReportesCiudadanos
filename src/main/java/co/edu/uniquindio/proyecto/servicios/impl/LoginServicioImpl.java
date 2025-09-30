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





}
