package co.edu.uniquindio.proyecto.servicios;

import co.edu.uniquindio.proyecto.dto.usuarios.*;
import org.bson.types.ObjectId;
import org.springframework.web.bind.annotation.*;


public interface UsuarioServicio {

    void crearUsuario( CrearUsuarioDTO cuenta) throws Exception;

    void enviarCodigoActivacion( UsuarioNuevoCodigoDTO usuarioNuevoCodigoDTO) throws Exception ;

    void activarCuenta( UsuarioActivacionDTO usuarioActivacionDTO) throws Exception;


    void editar( EditarUsuarioDTO cuenta) throws Exception;

    void cambiarPassword(CambiarPasswordDTO cambiarPasswordDTO) throws Exception ;


    void eliminar() throws Exception;

    UsuarioDTO obtener() throws Exception;

}
